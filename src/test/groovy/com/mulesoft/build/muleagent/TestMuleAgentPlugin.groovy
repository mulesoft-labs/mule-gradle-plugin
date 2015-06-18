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

package com.mulesoft.build.muleagent

import com.mulesoft.build.MulePlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.hamcrest.Matchers.*
import static org.junit.Assert.*

/**
 * Created by juancavallotti on 6/18/15.
 */
class TestMuleAgentPlugin {

    @Test
    public void checkDeployTaskName() {

        Project p = ProjectBuilder.builder().withName('test-project').build()

        p.apply plugin: MulePlugin
        p.apply plugin: MuleAgentPlugin

        p.evaluate()

        assertNotNull('Should contain a muleAgentDeploy task', p.tasks.findByName('muleAgentDeploy'))
        assertThat('Deploy should depend on muleAgentDeploy deploy', p.deploy.dependsOn, hasItem(p.muleAgentDeploy))
    }

}
