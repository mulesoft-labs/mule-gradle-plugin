package com.mulesoft.build.studio

import com.mulesoft.build.MulePluginExtension
import groovy.xml.XmlUtil
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

        StudioProject project = new StudioProject(projectName: projectName, muleConfig: pluginConfig)

        def rootNode = project.generateProjectXml()

        StringWriter out = new StringWriter()
        XmlUtil.serialize(rootNode, out)

        String xml = out.toString()

        //do a smoke test on the generated XML
        assertTrue("project name should be on the xml", xml.contains(projectName))
        assertTrue("version should be on the xml", xml.contains(project.generateRuntimeVersion()))
    }

}
