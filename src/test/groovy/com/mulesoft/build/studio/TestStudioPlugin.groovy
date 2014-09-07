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


    @Test
    void testGenerateProyectVersion() {

        String muleVersion = '3.5.0'
        String newerVersion = '3.5.1'
        MulePluginExtension pluginConfig = new MulePluginExtension()

        pluginConfig.version = muleVersion
        pluginConfig.muleEnterprise = true

        StudioProject testSubject = new StudioProject(project: new ProjectBuilder().build(), muleConfig: pluginConfig)

        //3.5.0 EE should produce 3.5.ee
        assertEquals("Should produce 3.5.ee", "3.5.ee", testSubject.generateRuntimeVersion())

        pluginConfig.muleEnterprise = false

        //3.5.0 CE should produce 3.5.CE
        assertEquals("Should produce 3.5.CE", "3.5.CE", testSubject.generateRuntimeVersion())

        pluginConfig.version = newerVersion
        pluginConfig.muleEnterprise = true

        //3.5.1 EE should produce 3.5.1.ee
        assertEquals("Should produce 3.5.1.ee", "3.5.1.ee", testSubject.generateRuntimeVersion())
    }
}
