/*
 * Copyright 2014 juancavallotti.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mulesoft.build.mmc

import com.mulesoft.build.util.FileUtils
import com.mulesoft.build.util.HttpUtils
import com.mulesoft.build.util.MultipartOutputStream
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task to upload the generated archive of this project to MMC
 *
 * Created by juancavallotti on 04/06/14.
 */
class DeployToMMCTask extends DefaultTask {


    @TaskAction
    void deployArtifactToMMC() {

        logger.debug('Starting deployment of artifact')

        MMCPluginExtension mmc = project.extensions.getByType(MMCPluginExtension)

        logger.debug('Found mmc extension, looking for environment...')

        //resolve the environment
        MMCEnvironment env = mmc.resolveTargetEnvironment()

        logger.debug('Found MMC environment')

        if (env == null) {
            Set<String> envNames = mmc.environments.keySet()
            throw new IllegalStateException("Could not resolve target environment, choices are: $envNames")
        }

        //setup the app name and the version
        if (!env.appName) {
            logger.info("Setting $project.name as Deployed App Name")
            env.appName = project.name
        }

        if (!env.version) {
            logger.info("Setting $project.version as Deployed App Version")
            env.version = project.version
        }

        //get the list of files to produce.
        File uploadedFile = project.configurations.archives.allArtifacts.files.singleFile

        if (logger.isDebugEnabled()) {
            logger.debug("Will upload file ${uploadedFile.absolutePath}")
        }

        String uploadUrl = "$env.url/api/repository"

        logger.debug('Creating http connection to MMC')

        //configure network auth
        HttpUtils.configureNetworkAuthenticator(env.username, env.password)

        //create an url connection
        HttpURLConnection conn = uploadUrl.toURL().openConnection()

        try {

            println "\tUploading app to environment $uploadUrl"

            def mpbuilder = MultipartOutputStream.builder()

            logger.debug('Configuring http headers...')
            conn.useCaches = false
            conn.doOutput = true
            conn.setRequestMethod('POST')
            conn.setRequestProperty('Content-Type', "multipart/form-data; boundary=$mpbuilder.boundary")

            logger.debug('Sending multipart body...')

            OutputStream reqOutputStream = conn.getOutputStream()

            //create a multipart stream
            MultipartOutputStream os = mpbuilder.build(reqOutputStream)


            //send the info
            os.startPart('application/octet-stream', ["Content-Disposition: form-data; name=\"file\"; filename=\"${uploadedFile.name}\""] as String[])
            //send the file part
            FileUtils.copyStream(uploadedFile.newInputStream(), os)

            os.startPart(['Content-Disposition: form-data; name="name"'] as String[])
            os.write(env.appName.bytes)

            os.startPart(['Content-Disposition: form-data; name="version"'] as String[])
            os.write(env.version.bytes)

            os.flush()
            os.close()

            //check the response
            int status = conn.responseCode

            logger.info("Uploaded app with status $status")

            if (status != 200) {
                String message = ''

                switch (status) {
                    case 400:
                        message = 'Invalid credentials'
                        break
                    case 409:
                        message = "Application version $env.version already exists"
                        break
                    case 501:
                        message = 'Uploaded artifact should be a Zip File'
                        break
                    default:
                        message = "Request failed with status: $status"
                }
                logger.warn("[FAILED!!] $message")
                throw new IllegalStateException(message)
            }

            println '\tDone!'
        } finally {
            conn.disconnect()
        }

    }

}
