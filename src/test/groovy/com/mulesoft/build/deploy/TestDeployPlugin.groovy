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

import com.mulesoft.build.MulePluginConvention
import com.mulesoft.build.MulePluginExtension
import org.gradle.api.Project
import org.gradle.api.tasks.TaskInstantiationException
import org.gradle.testfixtures.ProjectBuilder

import static org.junit.Assert.*
import org.junit.Test

/**
 * Created by juancavallotti on 02/06/14.
 */
class TestDeployPlugin {

    @Test
    void testUnexistentDeployDir() {

        //create as test project
        Project proj = ProjectBuilder.builder().build()

        //apply the mule plugin
        proj.extensions.create('mule', MulePluginExtension)
        proj.convention.create('muleConvention', MulePluginConvention)

        //configure the install directory
        proj.mule.installPath = '/non/existing/path'

        try {

            InstallInRuntime task = proj.tasks.create('install', InstallInRuntime)

        } catch (TaskInstantiationException ex) {
            assertTrue('Should be caused by IllegalArgumentException', ex.cause instanceof IllegalArgumentException)
        }
    }

}
