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

package com.mulesoft.build.cloudhub

import com.mulesoft.build.MulePlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by juancavallotti on 6/18/15.
 */
class TestCloudhubPlugin {

    @Test
    public void checkDeploymentTaskName() {

        Project p = ProjectBuilder.builder().withName('testProject').build()

        p.apply plugin: MulePlugin
        p.apply plugin: CloudhubPlugin

        p.evaluate()

        assertNotNull('Should contain a cloudhubDeploy task', p.tasks.findByName('cloudhubDeploy'))

        assertThat('Deploy should depend on cloudhub deploy', p.deploy.dependsOn, hasItem(p.cloudhubDeploy))
    }


}
