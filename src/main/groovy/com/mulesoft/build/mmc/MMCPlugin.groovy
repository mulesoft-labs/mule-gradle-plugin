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

import com.mulesoft.build.MulePluginConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This plugin allows easy deployment of the app in MMC through its REST API.
 * Created by juancavallotti on 04/06/14.
 */
class MMCPlugin implements Plugin<Project> {

    private static final Logger logger = LoggerFactory.getLogger(MMCPlugin)

    @Override
    void apply(Project project) {

        logger.debug('Apply the MMC plugin')

        //create the MMC plugin extension.
        project.extensions.create('mmc', MMCPluginExtension)


        //register the plugin
        Task t =  project.tasks.create('uploadToRepository', DeployToMMCTask)

        t.description = 'Upload the resulting application to the app repository of the Mule Management Console'
        t.group = MulePluginConstants.MULE_GROUP

        t.dependsOn project.build
    }
}
