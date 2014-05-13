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
import com.mulesoft.build.MulePluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.plugins.ide.eclipse.model.BuildCommand
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


        EclipseModel eclipseConfig = project.extensions.getByType(EclipseModel)

        //use the DSL to customize how the eclipse project is created.
        eclipseConfig.project {
            natures = [
                    'org.mule.tooling.core.muleNature',
                    'org.eclipse.jdt.core.javanature'
            ]
            buildCommands = [
                    new BuildCommand('org.mule.tooling.core.muleBuilder'),
                    new BuildCommand('org.eclipse.jdt.core.javabuilder')
            ]
        }

        eclipseConfig.classpath {
            //remove provided configurations from the eclipse classpath
            //since they will be in the mule runtime anyway, and if not
            //the user should install it on the mule runtime to preserve semantics
            minusConfigurations += project.configurations.providedCompile
            minusConfigurations += project.configurations.providedRuntime
            minusConfigurations += project.configurations.providedTestCompile
            minusConfigurations += project.configurations.providedTestRuntime

            containers 'org.eclipse.jdt.launching.JRE_CONTAINER', 'MULE_RUNTIME'

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

            //get the mule project configuration
            MulePluginExtension mule = project.extensions.getByType(MulePluginExtension)

            //create the mule-project.xml file if it does not exist
            StudioProject studioProject = new StudioProject(projectName: project.name, muleConfig: mule)

            //create the mule-project if needed
            studioProject.createStudioProjectIfNecessary()
        }

        currentTask.description = 'Update MuleStudio project metadata.'
        currentTask.group = "IDE"
        currentTask.dependsOn 'eclipse'

    }


}
