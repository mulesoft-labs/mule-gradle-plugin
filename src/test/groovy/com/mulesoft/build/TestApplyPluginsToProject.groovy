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

import com.mulesoft.build.cloudhub.CloudhubPlugin
import com.mulesoft.build.dependency.MuleDependencyPlugin
import com.mulesoft.build.deploy.MuleDeployPlugin
import com.mulesoft.build.domain.MuleDomainPlugin
import com.mulesoft.build.mmc.MMCPlugin
import com.mulesoft.build.run.MuleRunPlugin
import com.mulesoft.build.studio.StudioPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Created by juancavallotti on 2/25/15.
 */

@RunWith(Parameterized)
class TestApplyPluginsToProject {

    @Parameterized.Parameters
    public static Collection<Object[]> getParams() {

        def classes = [
                [[MulePlugin.class]] as Object[],
                [[MulePlugin.class, MMCPlugin.class]] as Object[],
                [[MulePlugin.class, CloudhubPlugin.class]] as Object[],
                [[MulePlugin.class, MMCPlugin.class, CloudhubPlugin.class]] as Object[],
                [[MuleDomainPlugin.class]] as Object[],
                [[StudioPlugin.class]] as Object[]
        ] as List

        return classes;
    }

    @Parameterized.Parameter
    public Collection<Class> pluginClasses;


    @Test
    public void testApplyPlugin() throws Exception {


        Project proj = ProjectBuilder.builder().withName('Sample Project').build()

        pluginClasses.each {
            proj.apply plugin: it
        }

        proj.evaluate()

        //at this point everything should have gone ok.
    }
}
