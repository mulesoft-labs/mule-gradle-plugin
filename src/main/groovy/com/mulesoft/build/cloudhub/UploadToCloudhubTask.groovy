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

import com.mulesoft.build.util.HttpUtils
import org.apache.commons.io.IOUtils
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

        //build the target uri
        String chApi = conv.clouduhbApiEndpoint

        //get the configuration where to upload the app.
        CloudhubEnvironment env = ext.resolveTargetDomain()

        if (env == null && ext.domains.isEmpty()) {
            throw new IllegalStateException('Could not find a configured cloudhub environment.')
        }

        if (env == null) {
            throw new IllegalStateException("Multiple cloudhub domains found but none selected as default: ${ext.domains.keySet()}")
        }

        File uploadedFile = project.configurations.archives.allArtifacts.files.singleFile

        //try and upload the app to cloudhub
        String url = "$chApi/applications/$env.domainName/deploy"

        //build an url connection.
        HttpURLConnection conn = url.toURL().openConnection()

        try {
            //set the headers
            String credentials = HttpUtils.buildBasicAuthHeader(env.username, env.password)
            String basicAuth = "Basic $credentials"

            conn.doOutput = true
            conn.useCaches = false
            conn.setRequestMethod('POST')
            conn.setRequestProperty('Authorization', basicAuth)
            conn.setRequestProperty('Content-Type', 'application/octet-stream')

            //write the payload
            OutputStream os = conn.getOutputStream()

            IOUtils.copy(uploadedFile.newInputStream(), os)

            os.flush()
            os.close()

            //check the response
            if (conn.responseCode != 200) {
                throw new IllegalStateException('Deployment to cloudhub failed.')
            }

        } finally {
            conn.disconnect()
        }

    }

}
