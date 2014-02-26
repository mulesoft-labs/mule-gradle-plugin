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
package com.mulesoft.build.studio

import com.mulesoft.build.MulePluginConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * This plugin is a customization of the eclipse plugin that adds specific
 * configurations for MuleStudio projects.
 *
 * Created by juancavallotti on 04/01/14.
 */
class StudioPlugin implements Plugin<Project> {

    private static final Logger logger = LoggerFactory.getLogger(StudioPlugin.class)

    @Override
    void apply(Project project) {

        //the studio plugin also adds the mule nature.
        project.apply(plugin: 'mule')

        //apply the base plugin and then customize.
        project.apply(plugin: 'eclipse')


        EclipseModel eclipseConfig = project.extensions.getByType(EclipseModel);

        //use the DSL to customize how the eclipse project is created.
        eclipseConfig.project {
            natures = ['org.mule.tooling.core.muleNature']
        }

        //initialize the studio dependencies.
        //TODO - this might be better if delayed until last moment for better flexibility
        StudioDependencies deps = new StudioDependencies()

        //add the compile dependencies
        project.dependencies {
            compile(deps.listCompileDeps(project))
        }

        Task currentTask

        currentTask = project.task('addDependency') << {
            deps.addDependency(project);
        }

        currentTask.description = 'Add a dependency on the studio file descriptor. - Intended for use with tooling'
        currentTask.group = MulePluginConstants.STUDIO_GROUP

        currentTask = project.task('studio') << {
            logger.info('Updating mule studio project...')
            //TODO - Add specific studio customizations
        }

        currentTask.description = 'Update MuleStudio project metadata.'
        currentTask.group = "IDE"
        currentTask.dependsOn 'eclipse'


    }
}
