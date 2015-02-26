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

package com.mulesoft.build.run

import com.mulesoft.build.MulePluginConstants
import com.mulesoft.build.UnpackPluginJarsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by juancavallotti on 16/02/14.
 */
class MuleRunPlugin implements Plugin<Project> {

    private static final Logger logger = LoggerFactory.getLogger(MuleRunPlugin)

    @Override
    void apply(Project project) {

        logger.debug('Applying Run Plugin')

        logger.debug('Adding copy resources task...')
        CopyAdditionalResourcesTask copyAdditional = project.tasks.create('copyResourcesForRunningApp', CopyAdditionalResourcesTask)

        logger.debug('Creating mule run task...')
        MuleRunTask task = project.tasks.create("runApp", MuleRunTask)

        logger.debug('Configuring task...')
        task.dependsOn copyAdditional

        //unpack plugins is only available after the project evaluation has been completed.
        project.afterEvaluate {

            logger.debug('Checking availability of unpackPluginJars task...')
            UnpackPluginJarsTask unpackPlugins = project.tasks.getByName('unpackPluginJars')

            task.dependsOn unpackPlugins

            unpackPlugins << {
                logger.debug('Adding plugins to the runApp classpath...')
                task.classpath = task.classpath + unpackPlugins.pluginJars
            }

        }


        task.classpath = project.sourceSets.main.runtimeClasspath
        task.description = MuleRunTask.TASK_DESC;
        task.group = MulePluginConstants.MULE_GROUP
    }
}
