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
package com.mulesoft.build.mmc

import com.mulesoft.build.MulePlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.*

/**
 * Created by juancavallotti on 04/06/14.
 */
class TestEnvironmentsDSL {

    @Test
    void testAddDevelopmentEnvironment() {

        MMCPluginExtension extension = new MMCPluginExtension()

        extension.environments {
            test url: 'http://localhost:8081/mmc', username: 'test', password: 'testpw'
        }

        assertTrue(extension.environments.containsKey('test'))
        MMCEnvironment env = extension.environments['test']

        assertEquals('host should be the one configured', 'http://localhost:8081/mmc' , env.url)
        assertEquals('username should be the same', 'test', env.username)
        assertEquals('password should be the same', 'testpw', env.password)
    }


    @Test
    void testInferEnvironment() {
        MMCPluginExtension extension = new MMCPluginExtension()

        extension.environments {
            test url: 'http://localhost:8081/mmc', username: 'test', password: 'testpw'
        }

        MMCEnvironment env = extension.resolveTargetEnvironment()

        assertNotNull('Target environment should not be null', env)

        assertSame('Resolved environment should be the same one defined', extension.environments['test'], env)

        //add a new environment
        extension.environments {
            prod url: 'http://prodhost:8081/mmc', username: 'produsername', password: 'prodpw'
        }

        env = extension.resolveTargetEnvironment()

        assertNull('When two envs and no default, it should be null', env)

        //configured the default one

        extension.defaultEnvironment = 'prod'

        env = extension.resolveTargetEnvironment()
        assertNotNull('Environment should have been resolved when default is configured', env)


        assertSame('Resolved environment should be the configured one', extension.environments['prod'], env)
    }


    @Test
    void testExternalEnvironment() {

        Project p = ProjectBuilder.builder().withName('testProject').build()

        p.ext.setProperty(MMCPlugin.FORCE_ENVIRONMENT_PROPERTY, 'dev')

        //configure the project
        p.apply plugin: MulePlugin
        p.apply plugin: MMCPlugin

        //configure a couple of environments
        p.mmc.environments {
            dev url: 'http://localhost:8081/mmc', username: 'test', password: 'testpw'
            prod url: 'http://localhost:8082/mmc', username: 'prod', password: 'prodpw'

            defaultEnvironment = 'prod'
        }

        p.evaluate()


        def env = p.mmc.resolveTargetEnvironment()

        assertNotNull('Environment should have been resolved since there is configuration', env)
        assertSame('Resolved environment should be the configured by external property', p.mmc.environments['dev'], env)
    }

    @Test
    void testMMCDslContribution() {

        Project p = ProjectBuilder.builder().withName('testProject').build()

        p.apply plugin: MulePlugin
        p.apply plugin: MMCPlugin

        p.mule {
            mmc {
                dev url: 'http://localhost:8081/mmc', username: 'test', password: 'testpw'
                defaultEnvironment = 'dev'
            }
        }

        p.evaluate()

        assertNotNull('Should have the dev environment', p.mmc.environments['dev'])
        assertEquals('Should have a default environment set', 'dev', p.mmc.defaultEnvironment)
    }

}
