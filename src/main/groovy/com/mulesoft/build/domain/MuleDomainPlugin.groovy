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

import com.mulesoft.build.MulePluginConstants
import com.mulesoft.build.MulePluginConvention
import com.mulesoft.build.dependency.MuleDependencyPlugin
import com.mulesoft.build.deploy.MuleDeployPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
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

    public static final String DOMAIN_CONVENTION_NAME = 'muleDomainConvention'

    @Override
    void apply(Project project) {

        //the base plugin will allow us to define target artifacts.
        project.apply plugin: BasePlugin

        //create the mule plugin extension before the dependencies plguin does.
        MuleDomainPluginExtension mule = project.extensions.create('mule', MuleDomainPluginExtension, project.name)

        project.apply plugin: MuleDependencyPlugin

        //this project can be deployed in a container.
        project.apply plugin: MuleDeployPlugin


        //create the convention specific for domains if is not present.
        if (!project.convention.findByName(DOMAIN_CONVENTION_NAME)) {
            project.convention.create(DOMAIN_CONVENTION_NAME, MuleDomainPluginConvention)
        }

        //add the mule plugin convention, this is only used for convenience, design can be improved here.
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

            MuleDomainPluginConvention conv = project.convention.findByName(DOMAIN_CONVENTION_NAME)

            def spmule = new MuleDomainModulePluginExtension(parent: project.mule)

            //configure the plugin extension on the mule project.
            extensions.add('mule', spmule)

            //apply the mule plugin to the subproject
            apply plugin: conv.subprojectsPlugin

            //change the outputs of the task so it checks correctly if it is up-to-date
            mulezip.outputs.dir project.buildDir
            mulezip.destinationDir = project.buildDir
        }

        //add domain-specific tasks.
        //the domain package depends on subprojects to build successfullty
        project.tasks.create('domainZip', DomainZip)

        project.domainZip.dependsOn project.subprojects.tasks['build']

        //we need to trigger the assemble phase upon build.
        if (project.tasks.findByName('build')) {
            logger.debug('Found build task, making it dependant on assemble...')
            project.tasks.getByName('build').dependsOn project.assemble
        } else {
            logger.debug('Build task not found, will create a new one...')
            project.tasks.create('build').dependsOn project.assemble
        }

        //declare the output artifacts
        project.artifacts {
            archives project.domainZip
        }

        //add additional tasks to the plugin.
        project.tasks.create('checkDomain', CheckDomainTask)
        project.tasks.create('fixDomain', FixDomainTask)

        //we don't want to zip something that we cannot deploy correctly
        project.domainZip.dependsOn project.checkDomain

        //configure the lifecycle of domain init
        applyDomainInitTasks(project)
    }

    /**
     * Init domain task, creates and initializes any submodules the domain has.
     * @param project the domain project.
     */
    void applyDomainInitTasks(Project project) {

        //create the task init domain
        Task initDomainTask = project.tasks.create('initDomain')

        Task initDomainFilesTask = project.tasks.create('initDomainFiles', InitDomainFilesTask)

        initDomainTask.dependsOn initDomainFilesTask

        project.subprojects.each {Project subp ->

            File subpFolder = project.file(subp.name)

            if (subpFolder.exists()) {
                logger.debug("Module with name ${subp.name} already exists, skipping...")
                return
            }

            initDomainTask.dependsOn subp.initMuleProject

            Task createSubpTask = subp.task('createModule').doLast {
                if (subpFolder.mkdir()) {
                    logger.debug("Created folder for module: ${subp.name}")
                } else {
                    logger.warn("Could not create folder for module: ${subp.name}")
                }
            }

            subp.initMuleProject.dependsOn createSubpTask
        }

        initDomainTask.description = 'Initialize the domain structure and subprojects as well as populate it with initial config files'
        initDomainTask.group = MulePluginConstants.MULE_GROUP

    }
}
