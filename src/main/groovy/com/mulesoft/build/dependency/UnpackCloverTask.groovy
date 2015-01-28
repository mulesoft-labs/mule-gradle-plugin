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

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction

/**
 * Expands the CloverETL Engine that allows unit tests against datamapper.
 * Created by juancavallotti on 11/06/14.
 */
class UnpackCloverTask extends DefaultTask {


    //clover plugins.
    public static final String CLOVER_GROUP = 'com.cloveretl'
    public static final String CLOVER_NAME = 'cloveretl-engine'

    //mule clover custom plugins
    public static final String MULE_CLOVER_GROUP = 'com.mulesoft.muleesb.datamapper'
    public static final String MULE_CLOVER_NAME = 'mule-clover-plugins'

    @TaskAction
    void doUnpackClover() {

        logger.debug('Trying to unpack Clover ETL for unit testing...')

        //smoke test for this being an embedded app
        if (!project.configurations.findByName('providedTestCompile')) {
            logger.error('Unit testing datamapper is not supported on embedded projects.')
            throw new IllegalStateException('Unit testing datamapper is not supported on embedded projects.')
        }

        //define an intermediate copy task
        def copyTask = project.tasks.create('copyClover', Copy)


        Set<File> providedTestRuntime = project.configurations.providedTestRuntime.files
        Set<File> providedTestCompile = project.configurations.providedTestCompile.files

        Set<File> finalFiles = providedTestRuntime - providedTestCompile

        File clover = finalFiles.find({
            it.name.startsWith(CLOVER_NAME)
        })

        File muleClover = finalFiles.find({
            it.name.startsWith(MULE_CLOVER_NAME)
        })

        if (!clover || !muleClover) {
            logger.error('Could not find Clover ETL engine, required to run the tests...')
            throw new IllegalStateException('Could not find Clover ETL engine, required to run the tests...')
        }

        if (logger.debugEnabled) logger.debug("Found Clover ETL engine in $clover")
        if (logger.debugEnabled) logger.debug("Found Mule Clover Plugins in $muleClover")


        JavaPluginConvention convention = project.convention.findPlugin(JavaPluginConvention)

        if (logger.debugEnabled) logger.debug("Clover will be unzipped into: ${convention.sourceSets.test.output.classesDir}")

        copyTask.from project.zipTree(clover)
        copyTask.into convention.sourceSets.test.output.classesDir
        copyTask.from project.zipTree(muleClover)
        copyTask.into convention.sourceSets.test.output.classesDir
        copyTask.execute()
    }

}
