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

import com.mulesoft.build.MulePlugin
import com.mulesoft.build.MulePluginExtension
import com.mulesoft.build.domain.MuleDomainPlugin
import com.mulesoft.build.studio.StudioPlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.*
import static org.hamcrest.Matchers.*

/**
 * Created by juancavallotti on 24/05/14.
 */
class TestDependenciesConfigurer {


    @Test
    public void testAddDataMapperDependencies() {

        //the project should contain the data mapper dependencies
        //only if it is a mule (or studio) enterprise project, this plugin gets added to
        //domains and could be added to build an embedded app, those cases should not
        //get the clover dependencies for running unit tests.
        Project proj = ProjectBuilder.builder().withName('mule').build()

        proj.apply plugin: StudioPlugin

        proj.mule.muleEnterprise = true

        proj.evaluate()

        Configuration config = proj.configurations.getByName(MuleProjectDependenciesConfigurer.TEST_RUNTIME_PLUGINS_CONFIGURATION)

        //check the config is there.
        assertThat('Project should contain the config', config, not( nullValue() ))

        //the config should have 2 configured elements.
        assertThat('Should have 2 configured artifacts.', config.getAllDependencies(), hasSize(2))
    }


    @Test
    public void testDataMapperNotPresent() {
        Project proj = ProjectBuilder.builder().withName('domain').build()

        proj.apply plugin: MuleDomainPlugin

        proj.mule.muleEnterprise = true

        proj.evaluate()

        Configuration config = proj.configurations.getByName(MuleProjectDependenciesConfigurer.TEST_RUNTIME_PLUGINS_CONFIGURATION)

        //at this point if there is no config, no harm
        //I will not enforce this because the deps plugin adds it for all projects
        //and it might be useful on the future.
        if (config == null) {
            return //PASS
        }

        //the config should have 2 configured elements.
        assertThat('Should not have test runtime artifacts.', config.getAllDependencies(), hasSize(0))
    }


    @Test
    public void testDataMapperNotPresentInEmbedded() {
        Project proj = ProjectBuilder.builder().withName('embedded').build()

        proj.apply plugin: JavaPlugin
        proj.apply plugin: MuleDependencyPlugin

        proj.mule.muleEnterprise = true

        proj.evaluate()

        Configuration config = proj.configurations.getByName(MuleProjectDependenciesConfigurer.TEST_RUNTIME_PLUGINS_CONFIGURATION)

        //at this point if there is no config, no harm
        //I will not enforce this because the deps plugin adds it for all projects
        //and it might be useful on the future.
        if (config == null) {
            return //PASS
        }

        //the config should have 2 configured elements.
        assertThat('Should not have test runtime artifacts.', config.getAllDependencies(), hasSize(0))
    }


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

    @Test
    public void testMuleDependenciesConfigurerBuildDependencies() throws Exception {
        MuleProjectDependenciesConfigurer configurer = new MuleProjectDependenciesConfigurer()


        MulePluginExtension extension = new MulePluginExtension()


        //inject dependencies.
        configurer.mule = extension

        configurer.applyDefaults()
        List<Map> deps = configurer.buildDependencies()

        assertThat(deps.size(), greaterThan(0))

        //check for some values
        assertThat(deps, hasItem([group:'org.mule', name:'mule-core', version:extension.version]))
        assertThat(deps, hasItem([group:'org.mule.modules', name:'mule-module-spring-config', version:extension.version]))
        assertThat(deps, hasItem([group:'com.mulesoft.muleesb.modules', name:'mule-module-spring-config-ee', version:extension.version]))

    }

    @Test
    public void testMultipleComponentClosures() throws Exception {

        MuleProjectDependenciesConfigurer configurer = new MuleProjectDependenciesConfigurer()
        MulePluginExtension extension = new MulePluginExtension()

        extension.components {
            modules += 'ws'
        }

        extension.components {
            modules += 'db'
        }

        configurer.mule = extension
        configurer.applyDefaults()

        assertThat(extension.modules, hasItem('ws'))
        assertThat(extension.modules, hasItem('db'))
    }
}
