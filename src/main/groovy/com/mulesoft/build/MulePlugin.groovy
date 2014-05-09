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
package com.mulesoft.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by juancavallotti on 02/01/14.
 */
class MulePlugin implements Plugin<Project> {

    Logger logger = LoggerFactory.getLogger(MulePlugin.class)

    void apply(Project project) {

        //apply the java plugin.
        project.apply(plugin: 'java')

        //add the mule extension.
        project.extensions.create("mule", MulePluginExtension)

        //apply plugins that also read the config

        //add the tasks related to deployment
        project.apply(plugin: 'mule-deploy')

        //add the tasks related to execution
        project.apply(plugin: 'mule-run')


        //add providedCompile and providedRuntime for dependency management.
        //this is needed because we'll be generating a container - based archive.
        project.configurations {

            providedCompile {
                description = "Compile time dependencies that should not be part of the final zip file."
                visible = false
            }

            providedRuntime {
                description = "Runtime dependencies that should not be part of the final zip file."
                visible = false
                extendsFrom providedCompile
            }

            compile {
                extendsFrom providedCompile
            }

            runtime {
                extendsFrom providedRuntime
            }
        }

        project.afterEvaluate { proj -> addDependenciesToProject(proj) }

        project.repositories {

            //local maven repository
            mavenLocal()

            //central maven repository
            mavenCentral()

            //the CE mule repository.
            maven {
                url "http://repository.mulesoft.org/releases/"
            }

            //jboss repository, always useful.
            maven {
                url "https://repository.jboss.org/nexus/content/repositories/"
            }
        }

        Task ziptask = addZipDistributionTask(project)

        ArchivePublishArtifact zipArtifact = new ArchivePublishArtifact(ziptask)
        //make it believe it is a war
        zipArtifact.setType("war")

        project.extensions.getByType(DefaultArtifactPublicationSet.class).addCandidate(zipArtifact)

    }

    private Task addZipDistributionTask(Project project) {
        //the packaging logic.
        Task ziptask = project.tasks.create("mulezip", MuleZip.class);

        //add the app directory yo the root of the zip file.
        ziptask.from {
            return 'src/main/app'
        }

        //add the data-mapper mappings
        ziptask.from {
            return 'mappings'
        }

        //add the APIKit specific files.
        ziptask.from {
            return 'src/main/api'
        }

        ziptask.dependsOn {
            project.convention.getPlugin(JavaPluginConvention.class).sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).runtimeClasspath
        }

        ziptask.classpath {

            FileCollection runtimeClasspath = project.convention.getPlugin(JavaPluginConvention.class)
                    .sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).runtimeClasspath

            Configuration providedRuntime = project.configurations.getByName(
                    'providedRuntime');

            runtimeClasspath -= providedRuntime;

        }

        ziptask.description = "Generate a deployable zip archive for this Mule APP"
        ziptask.group = BasePlugin.BUILD_GROUP

        return ziptask
    }


    private void addDependenciesToProject(Project project) {

        MulePluginExtension mule = project.mule

        //get the mule version.
        project.dependencies {
            def eeDeps = [
                    [group: 'com.mulesoft.muleesb.modules', name: 'mule-module-boot-ee', version: mule.version],
                    [group: 'com.mulesoft.muleesb', name: 'mule-core-ee', version: mule.version],
                    [group: 'com.mulesoft.muleesb.modules', name: 'mule-module-data-mapper', version: mule.version],
                    [group: 'com.mulesoft.muleesb.modules', name: 'mule-module-spring-config-ee', version: mule.version]
                ]
            
            providedCompile (                    
                    (mule.muleEnterprise ? eeDeps : []) + 
                    [group: 'org.mule', name: 'mule-core', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-spring-config', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-file', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-http', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-jdbc', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-jms', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-vm', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-client', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-cxf', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-json', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-management', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-scripting', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-sxc', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-xml', version: mule.version]
            )
        }
    }


}