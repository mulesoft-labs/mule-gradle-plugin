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

    @Override
    public void apply(Project project) {

        project.afterEvaluate { proj ->
            //define the task
            Task task = proj.tasks.create('install', InstallInRuntime)

            //depends on the generic build
            task.dependsOn 'build'
            task.description = InstallInRuntime.TASK_DESC;
            task.group = MulePluginConstants.MULE_GROUP
        }
    }
}



