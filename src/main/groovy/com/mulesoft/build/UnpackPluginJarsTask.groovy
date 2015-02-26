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
package com.mulesoft.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * Created by juancavallotti on 2/26/15.
 */
class UnpackPluginJarsTask extends DefaultTask {

    private static final Logger logger = LoggerFactory.getLogger(UnpackPluginJarsTask)

    def pluginJars

    @TaskAction
    public void run() {

        logger.debug('Call unpack plugin jars...')

        logger.debug('Scanning compile classpath...')

        String outputPath = "${project.buildDir}/unpackedPlugins"

        logger.debug("Output path of unpacked plugins: $outputPath")

        project.configurations.compile.each { File f ->

            if (f.path.endsWith('.zip')) {
                logger.debug("Uncompressing $f.path")

                def tree = project.zipTree(f)
                project.copy {
                    from tree
                    into project.file("$outputPath/$f.name")
                }
            }
        }

        logger.debug('Saving plugin jars...')

        pluginJars = project.fileTree(outputPath)

        if (logger.isDebugEnabled()) {
            logger.debug('Plugin jars: ')
            pluginJars.each {
                logger.debug(it.toString())
            }
        }
    }

}
