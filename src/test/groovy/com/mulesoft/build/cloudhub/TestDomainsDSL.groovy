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
}
