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

package com.mulesoft.build.deploy

import com.mulesoft.build.MulePluginConstants
import org.gradle.api.Plugin;
import org.gradle.api.Project
import org.gradle.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by juancavallotti on 16/02/14.
 */
class MuleDeployPlugin implements Plugin<Project> {

    private static final Logger logger = LoggerFactory.getLogger(MuleDeployPlugin)

    public static final String DEPLOY_TASK_DESC = 'Perform any Mule-related deployment operations.'

    @Override
    public void apply(Project project) {

        //define the global deploy task
        final Task deployTask = project.tasks.create('deploy')
        deployTask.dependsOn 'build'
        deployTask.description = DEPLOY_TASK_DESC
        deployTask.group = MulePluginConstants.MULE_GROUP


        project.afterEvaluate { proj ->

            //NOTE: Originally this task was called install but given this conflicts with
            //the maven plugin and it is not really self-explicative, it will get renamed.
            //SEE: https://github.com/mulesoft-labs/mule-gradle-plugin/issues/26
            Task deployLocallytask = proj.tasks.create('deployLocally', InstallInRuntime)

            Task findRuntime = proj.tasks.create('configureInstall').doLast {
                //do this as soon as we have the effective config.
                deployLocallytask.doInstallInRuntime()
            }

            //we need to configure the task before.
            deployLocallytask.dependsOn findRuntime
            //depends on the generic build
            deployLocallytask.dependsOn 'build'
            deployLocallytask.description = InstallInRuntime.TASK_DESC;
            deployLocallytask.group = MulePluginConstants.MULE_GROUP


            //global deploy task should depend on deployLocally
            //so when called deploy, deploy locally is performed
            deployTask.dependsOn deployLocallytask
        }
    }
}



