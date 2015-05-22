package com.mulesoft.build.cloudhub

import org.gradle.api.GradleException
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
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

        logger.info "About to deploy to environment $env"

        File uploadedFile = project.configurations.archives.allArtifacts.files.singleFile

        try {

            def client = new RESTClient(baseUrl)
            logger.debug "baseUrl: ${baseUrl}"

            client.authorization = new HTTPBasicAuthorization(env.username, env.password)

            logger.lifecycle "Deploying application to ${env.domainName}"
            def response = client.post(path: "/applications/${env.domainName}/deploy") {
                type 'application/octet-stream'
                bytes uploadedFile.bytes
            }

            logger.lifecycle "Response status code: ${response.statusCode}"
            logger.lifecycle "Response message: ${response.statusMessage}"

        } catch (RESTClientException e) {
            throw GradleException(e.message, e)
        }
    }
}