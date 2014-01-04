package com.mulesoft.build.studio

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

}
