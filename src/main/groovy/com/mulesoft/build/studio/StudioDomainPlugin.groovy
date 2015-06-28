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
package com.mulesoft.build.studio

import com.mulesoft.build.MulePluginExtension
import com.mulesoft.build.domain.MuleDomainPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.eclipse.model.BuildCommand
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This plugin is a customization of the eclipse plugin that adds specific
 * configurations for MuleStudio projects.
 * Created by juancavallotti on 6/28/15.
 */
class StudioDomainPlugin implements Plugin<Project> {


    private static final Logger logger = LoggerFactory.getLogger(StudioPlugin.class)


    @Override
    void apply(Project project) {
        logger.debug('Applying domain studio plugin...')

        //apply the mule domain plugin.
        project.apply(plugin: MuleDomainPlugin)

        //this is as well an eclipse plugin.
        project.apply(plugin: EclipsePlugin)


        EclipseModel eclipseConfig = project.extensions.getByType(EclipseModel)

        //use the DSL to customize how the eclipse project is created.
        eclipseConfig.project {
            natures = [
                    'org.mule.tooling.core.muleNature',
                    'org.mule.tooling.core.muleDomainNature',
                    'org.eclipse.jdt.core.javanature'
            ]
            buildCommands = [
                    new BuildCommand('org.eclipse.jdt.core.javabuilder'),
                    new BuildCommand('org.mule.tooling.core.muleBuilder'),
                    new BuildCommand('org.mule.tooling.core.muleDomainBuilder')
            ]
        }

        eclipseConfig.classpath {
            //remove provided configurations from the eclipse classpath
            //since they will be in the mule runtime anyway, and if not
            //the user should install it on the mule runtime to preserve semantics
            minusConfigurations += [project.configurations.providedCompile]

            containers 'org.eclipse.jdt.launching.JRE_CONTAINER', 'MULE_RUNTIME'

        }

        Task currentTask = project.task('studio') << {
            logger.info('Updating mule studio project...')

            //get the mule project configuration
            MulePluginExtension mule = project.extensions.getByType(MulePluginExtension)

            //create the mule-project.xml file if it does not exist
            StudioProject studioProject = new StudioProject(project: project, muleConfig: mule)

            //create the mule-project if needed
            studioProject.createStudioProjectIfNecessary()
        }

        currentTask.description = 'Update MuleStudio project metadata.'
        currentTask.group = "IDE"
        currentTask.dependsOn 'eclipse'
    }
}
