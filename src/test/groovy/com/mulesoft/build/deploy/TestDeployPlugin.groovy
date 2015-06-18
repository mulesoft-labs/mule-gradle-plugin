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

import com.mulesoft.build.MulePlugin
import com.mulesoft.build.MulePluginConvention
import com.mulesoft.build.MulePluginExtension
import com.mulesoft.build.MuleZip
import junit.framework.Assert
import org.gradle.api.Project
import org.gradle.api.Task
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

        //add the mulezip task
        proj.tasks.create('mulezip', MuleZip.class)

        //configure the install directory
        proj.mule.installPath = '/non/existing/path'

        try {

            InstallInRuntime task = proj.tasks.create('install', InstallInRuntime)
            task.doInstallInRuntime()

            //this MUST have thrown an exception
            fail('Must have thrown exception but didnt')

        } catch (IllegalArgumentException ex) {
            //this is what is expected.
            assertTrue('Test passed', true)
        } catch (Exception ex) {
            fail("Wrong type of exception ${ex.getClass()}")
        }
    }

    @Test
    void checkLocalDeploymentTaskName() {
        Project p = ProjectBuilder.builder().withName('test-project').build()
        p.apply plugin: MulePlugin

        p.evaluate()

        //assert
        assertNotNull('should have a deployLocally task', p.tasks.findByName('deployLocally'))
    }
}
