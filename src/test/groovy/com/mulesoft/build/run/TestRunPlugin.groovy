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

package com.mulesoft.build.run

import com.mulesoft.build.MulePlugin
import com.mulesoft.build.UnpackPluginJarsTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by juancavallotti on 2/26/15.
 */
class TestRunPlugin {

    @Test
    public void testRunpluginConfiguration() {

        //apply the mule plugin
        Project proj = ProjectBuilder.builder().withName('test-run').build()

        proj.apply plugin: MulePlugin

        proj.evaluate()

        //verify the state of the project.

        //should have the mule run task
        def runTasks = proj.tasks.withType(MuleRunTask)

        assertThat('Should have one run task.',runTasks, hasSize(1))

        //should have the unpack plugin jars
        def unpackPluginTasks = proj.tasks.withType(UnpackPluginJarsTask)

        assertThat('Should have one unpack plugins task', unpackPluginTasks, hasSize(1))

        //should have one instance of copy resources
        def copyResourcesTasks = proj.tasks.withType(CopyAdditionalResourcesTask)

        assertThat('Should have one copy resources task', copyResourcesTasks, hasSize(1))


        //run task should depend on unpack and copy resources
        MuleRunTask runTask = runTasks.find()

        UnpackPluginJarsTask unpackTask = unpackPluginTasks.find()

        CopyAdditionalResourcesTask copyTask = copyResourcesTasks.find()

        assertThat('run task should depend on unpack and copy', runTask.dependsOn, hasItems(unpackTask, copyTask))
    }


}
