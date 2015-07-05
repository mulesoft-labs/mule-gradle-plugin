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
package com.mulesoft.build.cloudhub

import com.mulesoft.build.MulePlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.*

/**
 * Created by juancavallotti on 04/06/14.
 */
class TestDomainsDSL {

    @Test
    void testAddDevelopmentEnvironment() {

        CloudhubPluginExtension extension = new CloudhubPluginExtension()

        extension.domains {
            test username: 'test', password: 'testpw'
        }

        assertTrue(extension.domains.containsKey('test'))
        CloudhubEnvironment env = extension.domains['test']

        assertEquals('username should match', 'test', env.username)
        assertEquals('password should match', 'testpw', env.password)
    }


    @Test
    void testInferEnvironment() {
        CloudhubPluginExtension extension = new CloudhubPluginExtension()

        extension.domains {
            test username: 'test', password: 'testpw'
        }

        CloudhubEnvironment env = extension.resolveTargetDomain()

        assertNotNull('Target domain should not be null', env)

        assertSame('Resolved domain should be the same one defined', extension.domains['test'], env)

        //add a new environment
        extension.domains {
            'env-dev' username: 'produsername', password: 'prodpw'
        }

        env = extension.resolveTargetDomain()

        assertNull('When two domains and no default, it should be null', env)

        //configured the default one

        extension.defaultDomain = 'env-dev'

        env = extension.resolveTargetDomain()
        assertNotNull('Domain should have been resolved when default is configured', env)
        assertSame('Resolved domain should be the configured one', extension.domains[extension.defaultDomain], env)
    }

    @Test
    void testExternalEnvironment() {

        Project p = ProjectBuilder.builder().withName('testProject').build()

        p.ext.setProperty(CloudhubPlugin.FORCE_ENVIRONMENT_PROPERTY, 'dev')

        //configure the project
        p.apply plugin: MulePlugin
        p.apply plugin: CloudhubPlugin

        //configure a couple of environments
        p.cloudhub.domains {
            dev username: 'a', password: 'b'
            prod username: 'c', password: 'd'

            defaultDomain = 'prod'
        }

        p.evaluate()


        def env = p.cloudhub.resolveTargetDomain()

        assertNotNull('Domain should have been resolved since there is configuration', env)
        assertSame('Resolved domain should be the configured by external property', p.cloudhub.domains['dev'], env)
    }

    @Test
    void testCloudhubDslContribution() {

        Project p = ProjectBuilder.builder().withName('testProject').build()

        p.apply plugin: MulePlugin
        p.apply plugin: CloudhubPlugin

        p.mule {
            cloudhub {
                dev username: 'c', password: 'd'
                defaultDomain = 'dev'
            }
        }

        p.evaluate()

        assertNotNull('Should have the dev domain', p.cloudhub.domains['dev'])
        assertEquals('Should have a default environment set', 'dev', p.cloudhub.defaultDomain)
    }

}
