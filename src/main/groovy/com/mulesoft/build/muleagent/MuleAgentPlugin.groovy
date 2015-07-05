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

import com.mulesoft.build.MulePluginConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This plugin allows easy deployment of the app to the Mule Agent through its REST API.
 * Created by juancavallotti on 5/6/15.
 */
class MuleAgentPlugin implements Plugin<Project> {

    private static final Logger logger = LoggerFactory.getLogger(MuleAgentPlugin)

    public static final String FORCE_ENVIRONMENT_PROPERTY = 'muleAgentEnvironment'

    @Override
    void apply(Project project) {
        logger.debug('Apply the Mule Agent plugin')

        //create the Mule Agent plugin extension.
        def agentExt = project.extensions.create('muleAgent', MuleAgentPluginExtension)

        if (project.hasProperty(FORCE_ENVIRONMENT_PROPERTY)) {
            project.muleAgent.forceEnvironment = project.property(FORCE_ENVIRONMENT_PROPERTY)
        }

        //register the plugin
        Task agentDeployTask =  project.tasks.create('muleAgentDeploy', DeployToAgentTask)

        if (project.tasks.findByName('deploy')) {
            project.deploy.dependsOn agentDeployTask
        } else {
            logger.error('Project does not contain the \'deploy\' task, you must apply the \'mule\' plugin!!')
        }

        //contribute to the DSL as an extension.
        if (project.hasProperty('mule')) {
            project.mule.ext.agent = agentExt.&environments
        } else {
            logger.warn('Could not find mule plugin extension, mule plugin might not be applied.')
        }

        agentDeployTask.description = 'Deploy the resulting application to Mule through the Mule Agent'
        agentDeployTask.group = MulePluginConstants.MULE_GROUP

        agentDeployTask.dependsOn project.build
    }
}
