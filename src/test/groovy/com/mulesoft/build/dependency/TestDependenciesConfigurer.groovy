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
package com.mulesoft.build.dependency

import com.mulesoft.build.MulePluginExtension
import org.junit.Test

import static org.junit.Assert.*
import static org.hamcrest.Matchers.*

/**
 * Created by juancavallotti on 24/05/14.
 */
class TestDependenciesConfigurer {


    @Test
    public void testMuleDependenciesConfigurerApplyDefaults() throws Exception {

        MuleProjectDependenciesConfigurer configurer = new MuleProjectDependenciesConfigurer()
        MulePluginExtension extension = new MulePluginExtension()

        configurer.mule = extension

        configurer.applyDefaults()

        //try and find one of each group
        assertThat(extension.coreLibs, hasItem('core'))
        assertThat(extension.modules, hasItem('spring-config'))
        assertThat(extension.transports, hasItem('http'))
        assertThat(extension.eeCoreLibs, hasItem('core-ee'))
        assertThat(extension.eeModules, hasItem('spring-config-ee'))
    }

}
