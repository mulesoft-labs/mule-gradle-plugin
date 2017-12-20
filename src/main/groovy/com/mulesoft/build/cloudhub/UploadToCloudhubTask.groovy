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
package com.mulesoft.build.cloudhub

import com.mulesoft.build.MulePluginExtension
import com.mulesoft.build.util.FileUtils
import com.mulesoft.build.util.HttpUtils
import com.mulesoft.build.util.MultipartOutputStream
import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by juancavallotti on 06/06/14.
 */
class UploadToCloudhubTask extends DefaultTask {

    @TaskAction
    public void uploadToCloudhub() {

        logger.debug('Uploading archive to cloudhub...')

        //get the plugin convention
        CloudhubPluginConvention conv = project.convention.getByType(CloudhubPluginConvention)

        //get the extension
        CloudhubPluginExtension ext = project.extensions.getByType(CloudhubPluginExtension)

        //get the mule plugin extension
        MulePluginExtension muleExt = project.extensions.getByType(MulePluginExtension)

        //build the target uri
        String chApi = conv.clouduhbApiEndpoint

        //get the configuration where to upload the app.
        CloudhubEnvironment env = ext.resolveTargetDomain()

        if (env == null && ext.domains.isEmpty()) {
            logger.error('No environment has been configured, aborting...')
            throw new IllegalStateException('Could not find a configured cloudhub environment.')
        }

        if (env == null) {
            logger.error('Multiple environments found but none defined as default...')
            throw new IllegalStateException("Multiple cloudhub domains found but none selected as default: ${ext.domains.keySet()}")
        }

        //display the environment that will be used.
        if (logger.isInfoEnabled()) {
            logger.info("About to deploy to environment $env")
        }

        File uploadedFile = project.configurations.archives.allArtifacts.files.singleFile


        //try and upload the app to cloudhub
        String url = "$chApi/v2/applications"

        if (logger.isInfoEnabled()){
            logger.info("Will deploy file: $uploadedFile.absolutePath")
            logger.info("Will upload to artifact to url: $url")
        }

        //configure authentication
        HttpUtils.configureNetworkAuthenticator(env.username, env.password)

        //build an url connection.
        HttpURLConnection conn = url.toURL().openConnection()

        try {

            println "\t Uploading file $uploadedFile.name to URL: $url"

            def mpbuilder = MultipartOutputStream.builder()


            //set the headers

            conn.doOutput = true
            conn.useCaches = false
            conn.setRequestMethod('POST')
            conn.setRequestProperty('Authorization', HttpUtils.generateAuthenticationHeader(env.username, env.password))
            conn.setRequestProperty('Content-Type', "multipart/form-data; boundary=$mpbuilder.boundary")

            logger.debug('Sending multipart body...')

            OutputStream reqOutputStream = conn.getOutputStream()

            //create a multipart stream
            MultipartOutputStream os = mpbuilder.build(reqOutputStream)

            //start sending the parts
            //send the info
            os.startPart('application/zip', ["Content-Disposition: form-data; name=\"file\"; filename=\"${uploadedFile.name}\""] as String[])
            //send the file part
            FileUtils.copyStream(uploadedFile.newInputStream(), os)

            //say that we want to auto start
            os.startPart(['Content-Disposition: form-data; name="autoStart"'] as String[])
            os.write('true'.bytes)


            //the app info json
            os.startPart(['Content-Disposition: form-data; name="appInfoJson"'] as String[])
            os.write(buildAppJson(muleExt, conv, ext, env, uploadedFile).bytes)

            os.flush()
            os.close()

            //check the response
            if (conn.responseCode != 200) {

                switch (conn.responseCode) {
                    case 401:
                        logger.warn("Invalid credentials were used to upload the artifact, please verify your username and password and retry.")
                        break;
                    default:
                        logger.warn("Cloudhub responded with status code: $conn.responseCode")
                        logger.warn("Response Message: $conn.responseMessage")
                        logger.warn("Response Body: $conn.inputStream.text")
                }
                throw new IllegalStateException('Deployment to cloudhub failed.')
            }

            println "\t Done!"

        } finally {
            conn.disconnect()
        }

    }

    String buildAppJson(MulePluginExtension muleExt, CloudhubPluginConvention conv, CloudhubPluginExtension ext, CloudhubEnvironment env, File uploadedFile) {

        def config = [
            fileChecksum: '',
            fileSource: '',
            fileName: uploadedFile.name,
            properties:[:],
            logLevels:[],
            muleVersion: [
                    version: muleExt.version
            ],
            trackingSettings: [
                trackingLevel: "DISABLED"
            ],
            monitoringEnabled: true,
            monitoringAutoRestart :true,
            persistentQueues :false,
            persistentQueuesEncrypted :false,
            workers:[
                amount:1,
                type:[
                    name:"Medium",
                    weight:1,
                    cpu:"1 vCore",
                    memory:"1.5 GB memory"
                ]
            ],
            objectStoreV1:true,
            loggingNgEnabled:true,
            loggingCustomLog4JEnabled:false,
            staticIPsEnabled:false,
            domain: env.domainName

        ]

        return JsonOutput.toJson(config)
    }

}
