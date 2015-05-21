package com.mulesoft.build.cloudhub

import groovyx.net.http.HTTPBuilder
import org.gradle.api.GradleException

import static groovyx.net.http.ContentType.BINARY
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Sion Williams
 */
class DeployToCloudhubTask extends DefaultTask{

    @TaskAction
    def run() {
        logger.debug 'Uploading archive to cloudhub...'

        //get the plugin convention
        CloudhubPluginConvention conv = project.convention.getByType(CloudhubPluginConvention)

        //get the extension
        CloudhubPluginExtension ext = project.extensions.getByType(CloudhubPluginExtension)

        //build the target uri
        String baseUrl = conv.clouduhbApiEndpoint

        //get the configuration where to upload the app.
        CloudhubEnvironment env = ext.resolveTargetDomain()

        if (env == null && ext.domains.isEmpty()) {
            logger.error 'No environment has been configured, aborting...'
            throw GradleException('Could not find a configured cloudhub environment.')
        }

        if (env == null) {
            logger.error 'Multiple environments found but none defined as default...'
            throw GradleException("Multiple cloudhub domains found but none selected as default: ${ext.domains.keySet()}")
        }

        //display the environment that will be used.
        if (logger.isInfoEnabled()) {
            logger.info "About to deploy to environment $env"
        }

        File uploadedFile = project.configurations.archives.allArtifacts.files.singleFile

        def service = new HTTPBuilder(baseUrl)

        service.auth.basic(env.username, env.password)

        service.post(path:"/applications/$env.domainName/deploy", body: uploadedFile.bytes, requestContentType: BINARY) { response ->
            logger.info response.responseLine
        }
    }
}