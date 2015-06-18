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

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
/**
 * Created by juancavallotti on 2/26/15.
 */
class TestMulePlugin {

    @Test
    public void checkTestEnvironment() {

        Project proj = ProjectBuilder.builder().withName('test-proj').build()

        proj.apply plugin: MulePlugin

        proj.evaluate()

        Task test = proj.tasks.test

        assertNotNull('Test task should not be null')

        def unpackTasks = proj.tasks.withType(UnpackPluginJarsTask)

        assertThat('Should have the unpack task.', unpackTasks, hasSize(1))

        UnpackPluginJarsTask unpackTask = unpackTasks.find()

        assertThat('Test task should depend on unpack plugins', test.dependsOn, hasItem(unpackTask))
    }


    @Test
    public void verifyDeploymentTask() {

        Project p = ProjectBuilder.builder().withName('test-project').build()


        //we need to apply the mule plugin
        p.apply plugin: MulePlugin

        p.evaluate()

        //at this point we should have a deploy task.
        assertNotNull('Should have a deploy task', p.tasks.findByName('deploy'))
    }

}
