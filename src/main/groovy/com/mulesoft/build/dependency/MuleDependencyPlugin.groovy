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
package com.mulesoft.build.dependency

import com.mulesoft.build.MulePluginConstants
import com.mulesoft.build.MulePluginExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Plugin to elegantly manage mule dependencies into this project.
 *
 * This plugin can be used on its own to produce an embedded installation of mule
 * normally wrapped in java apps.
 *
 * Created by juancavallotti on 24/05/14.
 */
class MuleDependencyPlugin implements Plugin<Project> {

    static final Logger logger = LoggerFactory.getLogger(MuleDependencyPlugin.class)

    @Override
    void apply(Project project) {

        logger.debug('Applying MuleDependenyPlugin...')

        logger.debug('Getting mule plugin extension from project...')
        //ensure mule plugin extension is present.
        def mule = project.extensions.findByName('mule')

        //if this plugin is used on its own we would need to create the extension.
        if (!mule) {
            logger.debug('Mule plugin extensions not found, creating one...')
            project.extensions.create('mule', MulePluginExtension)
        }

        //before the project is evaluated, we need to configure the special DSL for the
        //dependencies.
        project.afterEvaluate({ Project proj ->
            logger.debug('Applying dependencies after project\'s evaluation.')
            DependenciesConfigurer configurer = new MuleProjectDependenciesConfigurer(project: proj, mule: proj.mule)
            configurer.applyDependencies()

        } as Action<Project>)

        Task printMuleDependencies = project.task('muleDeps')
        printMuleDependencies << {
            MulePluginExtension muleExt = project.mule

            println "Core libs (mule.coreLibs): ${muleExt.coreLibs}"
            println "Modules (mule.modules): ${muleExt.modules}"
            println "Transports (mule.transports): ${muleExt.transports}"

            if (!muleExt.muleEnterprise) {
                return
            }

            println "Core libs EE (mule.eeCoreLibs): ${muleExt.eeCoreLibs}"
            println "Modules EE (mule.eeModules): ${muleExt.eeModules}"
            println "Transports EE (mule.eeTransports): ${muleExt.eeTransports}"

        }

        printMuleDependencies.description = "Print the list of configured mule dependencies."
        printMuleDependencies.group = MulePluginConstants.MULE_GROUP

    }
}
