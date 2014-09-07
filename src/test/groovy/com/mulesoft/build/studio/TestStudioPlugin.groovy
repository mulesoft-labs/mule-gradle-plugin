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
package com.mulesoft.build.studio

import com.mulesoft.build.MulePluginExtension
import groovy.xml.XmlUtil
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.*

/**
 * Created by juancavallotti on 04/01/14.
 */
class TestStudioPlugin {

    @Test
    void testLoadStudioDeps() {
        File f = new File("src/test/resources/studio-deps.xml")

        assertTrue('File must exist!', f.exists())

        //parse the file
        StudioDependencies studiodeps = new StudioDependencies()

        List<Map> deps = studiodeps.listCompileDeps(f)

        //at least two deps
        assertTrue('Should have two dependencies', deps.size() == 2)

        //verify there is commons-lang and activemq
        deps.each {
            String name = it.get('name')
            assertTrue('Should have commons or activemq',name.equals('commons-lang') || name.equals('activemq-core'))
        }
    }

    @Test
    void testGenerateStudioProjectXml() {
        String projectName = 'my-project'
        MulePluginExtension pluginConfig = new MulePluginExtension()
        pluginConfig.muleEnterprise = true

        String tempDir = System.getProperty('java.io.tmpdir')

        Project studioProject = new ProjectBuilder().withName(projectName).withProjectDir(new File(tempDir)).build();

        StudioProject project = new StudioProject(project: studioProject, muleConfig: pluginConfig)

        def rootNode = project.generateProjectXml()

        StringWriter out = new StringWriter()
        XmlUtil.serialize(rootNode, out)

        String xml = out.toString()

        //do a smoke test on the generated XML
        assertTrue("project name should be on the xml", xml.contains(projectName))
        assertTrue("version should be on the xml", xml.contains(project.generateRuntimeVersion()))
    }

}
