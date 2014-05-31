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
package com.mulesoft.build.domain

import com.mulesoft.build.MulePluginConvention
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Starting from 3.5.0, mule supports the ability to deploy apps into domains.
 * This plugin tackles the creation and deployment of these domains.
 *
 * This takes advantage of gradle's multi-module build.
 *
 * Created by juancavallotti on 30/05/14.
 */
class MuleDomainPlugin implements Plugin<Project> {

    private static final Logger logger = LoggerFactory.getLogger(MuleDomainPlugin)

    @Override
    void apply(Project project) {

        //the base plugin will allow us to define target artifacts.
        project.apply plugin: BasePlugin

        project.apply plugin: 'mule-dependencies'

        //add the mule plugin convention.
        project.convention.create('muleConvention', MulePluginConvention)

        //add the runtime configurations

        //since domains are much simpler, we don't actually need more configurations.
        //but in the future we might consider adding a 'container configurations' plugin to make these configurations
        //reusable
        project.configurations {
            providedCompile {
                description = 'Compile time dependencies that should not be part of the final zip file.'
                visible = false
            }

            compile {
                extendsFrom providedCompile
            }

            //test compile is required by the dependencies plugin, this needs to be improved.
            //but for the time being there is no harm in adding this configuration.
            testCompile {
                extendsFrom compile
            }

        }

        //apply the mule plugin to each subproject
        //mule studio could be applied by the user later, but we might consider making it default.
        project.subprojects {

            //configure the plugin extension on the mule project.
            extensions.add('mule', project.mule)

            //apply the mule plugin to the subproject
            apply plugin: 'mule'

            //change the outputs of the task so it checks correctly if it is up-to-date
            mulezip.outputs.dir project.buildDir
            mulezip.destinationDir = project.buildDir
        }

        //add domain-specific tasks.
        //the domain package depends on subprojects to build successfullty
        project.tasks.create('domainZip', DomainZip)

        project.domainZip.dependsOn project.subprojects.tasks['build']

        //we need to trigger the assemble phase upon biuld.
        project.tasks.create('build').dependsOn project.assemble

        //declare the output artifacts
        project.artifacts {
            archives project.domainZip
        }
    }
}
