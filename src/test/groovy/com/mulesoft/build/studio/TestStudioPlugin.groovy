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
import com.mulesoft.build.util.FileUtils
import groovy.xml.XmlUtil
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.*
import static org.hamcrest.Matchers.*

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
        InputStream xmlFileStream = getClass().getResourceAsStream('/blank-mule-project.xml')

        String tempDir = System.getProperty('java.io.tmpdir')

        Project studioProject = new ProjectBuilder().withName(projectName).withProjectDir(new File(tempDir)).build();

        StudioProject project = new StudioProject(project: studioProject, muleConfig: pluginConfig)

        def rootNode = project.generateProjectXml(xmlFileStream)

        StringWriter out = new StringWriter()
        XmlUtil.serialize(rootNode, out)

        String xml = out.toString()

        //do a smoke test on the generated XML
        assertTrue("project name should be on the xml", xml.contains(projectName))
        assertTrue("version should be on the xml", xml.contains(project.generateRuntimeVersion()))
    }


    @Test
    void testGenerateProjectVersion() {

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


    @Test
    void testUpdateProjectVersion() {

        //the key is to have a project with existing file.
        //first, copy mule-project.xml to the temp dir
        InputStream origFile = getClass().getResourceAsStream('/mule-project.xml')

        File outputFile = new File(System.getProperty('java.io.tmpdir') + '/mule-project.xml')

        OutputStream os = outputFile.newOutputStream()

        FileUtils.copyStream(origFile, os)
        os.close()

        //mark the file for deletion after we don't need it
        outputFile.deleteOnExit()

        //now we can start the test.
        Project mockProj = new ProjectBuilder().withName('mockStudio')
                .withProjectDir(new File(System.getProperty('java.io.tmpdir'))).build()


        MulePluginExtension pluginConfig = new MulePluginExtension()
        //set the mule version
        pluginConfig.version = '3.5.0'
        pluginConfig.muleEnterprise = true

        //run the test on the subject
        StudioProject testSubject = new StudioProject(project: mockProj, muleConfig: pluginConfig)

        testSubject.createStudioProjectIfNecessary()

        //read the file to a string and assert.
        String file = new String(outputFile.readBytes())

        //should contain the string org.mule.tooling.server.3.5.ee

        assertThat(file, containsString('org.mule.tooling.server.3.5.ee'))
    }

    @Test
    void testSingleProjectName() {
        String projectName = 'my-project'
        MulePluginExtension pluginConfig = new MulePluginExtension()
        InputStream xmlFileStream = getClass().getResourceAsStream('/mule-project-with-name.xml')

        String tempDir = System.getProperty('java.io.tmpdir')
        Project studioProject = new ProjectBuilder().withName(projectName).withProjectDir(new File(tempDir)).build();
        StudioProject project = new StudioProject(project: studioProject, muleConfig: pluginConfig)

        def rootNode = project.generateProjectXml(xmlFileStream)

        StringWriter out = new StringWriter()
        XmlUtil.serialize(rootNode, out)
        String xml = out.toString()
        assertEquals("project name should be updated and appear only once", 1, (xml =~ /<name>my-project<\/name>/).size())
    }

}
