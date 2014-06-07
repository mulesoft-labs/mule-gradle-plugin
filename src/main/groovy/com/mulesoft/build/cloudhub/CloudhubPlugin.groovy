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

import com.mulesoft.build.MulePluginConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Plugin to perform deployments of mule applications into cloudhub.
 * Created by juancavallotti on 06/06/14.
 */
class CloudhubPlugin implements Plugin<Project> {

    public static final Logger logger = LoggerFactory.getLogger(CloudhubPlugin)

    @Override
    void apply(Project project) {

        logger.debug('Applying the CloudHub plugin')

        project.extensions.create('cloudhub', CloudhubPluginExtension)
        project.convention.create('cloudhubConvention', CloudhubPluginConvention)


        Task upload = project.tasks.create('deploy', UploadToCloudhubTask);

        upload.description = 'Deploy the application in the selected cloudhub environment.'
        upload.group = MulePluginConstants.MULE_GROUP

        upload.dependsOn project.build

    }
}
