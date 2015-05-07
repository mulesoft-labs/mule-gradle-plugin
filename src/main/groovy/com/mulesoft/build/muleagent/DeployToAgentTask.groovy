/*
 * Copyright 2015 juancavallotti.
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

package com.mulesoft.build.muleagent

import com.mulesoft.build.util.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task to deploy artifacts to the new Mule Agent.
 *
 * Created by juancavallotti on 5/6/15.
 */
class DeployToAgentTask extends DefaultTask {

    @TaskAction
    void deployArtifactToAgent() {

        logger.debug('Starting deployment of artifact')

        MuleAgentPluginExtension extension = project.extensions.getByType(MuleAgentPluginExtension)

        List<MuleEnvironment> envs = extension.resolveTargetEnvironments()

        if (logger.isDebugEnabled()) {
            logger.debug("Will deploy to the following environments: $envs")
        }

        if (envs.isEmpty()) {
            logger.error('There are no environments configured for deployment.')
            throw new IllegalStateException('There are no environments configured for deployment.')
        }

        String appName = project.name

        File uploadedFile = project.configurations.archives.allArtifacts.files.singleFile

        if (logger.isDebugEnabled()) {
            logger.debug("Will upload file ${uploadedFile.absolutePath}")
        }

        //go through each environment and deploy.
        envs.each { MuleEnvironment env ->


            String url = "$env.baseUrl/applications/$appName"

            logger.debug("Will deploy to $url")

            //build an url connection.
            HttpURLConnection conn = url.toURL().openConnection()

            try {

                println "\t Uploading file $uploadedFile.name to URL: $url"

                //set the headers

                conn.doOutput = true
                conn.useCaches = false
                conn.setRequestMethod('PUT')
                conn.setRequestProperty('Content-Type', 'application/octet-stream')

                //write the payload
                OutputStream os = conn.getOutputStream()

                FileUtils.copyStream(uploadedFile.newInputStream(), os)

                os.flush()
                os.close()

                //check the response
                if (conn.responseCode != 202) {

                    switch (conn.responseCode) {
                        case 406:
                            logger.warn("Invalid Request")
                            break;
                        default:
                            logger.warn("Agent responded with status code: $conn.responseCode")
                    }
                    throw new IllegalStateException("Deployment to Mule Agent failed. Status Code: $conn.responseCode" )
                }

                println "\t Done!"

            } finally {
                conn.disconnect()
            }


        }

    }

}
