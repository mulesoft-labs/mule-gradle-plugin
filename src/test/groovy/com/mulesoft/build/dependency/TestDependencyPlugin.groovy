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
package com.mulesoft.build.dependency

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.*;

/**
 * Created by juancavallotti on 2/24/15.
 */
class TestDependencyPlugin {

    @Test
    public void testDefaultRepositoriesEnabled() throws Exception {
        Project p = ProjectBuilder.builder()
                .withName('sampleProject').build()

        p.apply plugin: JavaPlugin
        p.apply plugin: MuleDependencyPlugin
        p.mule.disableDefaultRepositories = false
        p.mule.enterpriseRepoPassword = 'aaaa'
        p.mule.enterpriseRepoPassword = 'bbbb'
        p.evaluate()

        assertEquals('Project should have default repos', 5, p.repositories.size())

    }


    @Test
    public void testDisableDefaultRepositories() throws Exception {

        Project p = ProjectBuilder.builder()
                .withName('sampleProject').build()

        p.apply plugin: JavaPlugin
        p.apply plugin: MuleDependencyPlugin
        p.mule.disableDefaultRepositories = true
        p.evaluate()

        assertEquals('Project should have none repos', 0, p.repositories.size())
    }

}
