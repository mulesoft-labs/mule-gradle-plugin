package com.mulesoft.build.studio

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Manages the studio dependencies file.
 * Created by juancavallotti on 04/01/14.
 */
class StudioDependencies {

    Logger logger = LoggerFactory.getLogger(StudioDependencies.class)

    static String STUDIO_DEPS_FILE = 'studio-deps.xml';

    /**
     * Get all the dependencies configured on the studio
     * @return
     */
    List<Map> listCompileDeps(Project project) {

        //if the studio deps file exists, then read through it
        File deps = project.file(STUDIO_DEPS_FILE)

        if (!deps.exists()) {
            logger.warn("Studio specific dependencies file ${deps.absolutePath} does not exist")
            return []
        }


        return listCompileDeps(deps)
    }

    List<Map> listCompileDeps(File file) {

        List ret = [];

        //slurp the XML and convert it into a list of maps
        XmlSlurper slurper = new XmlSlurper();

        def dependencies = slurper.parse(file);

        //for each child of the dependencies, get the group name and version attribute
        dependencies.dependency.each {
            def dep = [group: it.@group, name: it.@name, version: it.@version]

            logger.info("Parsed ${dep} as compile-time dependency")

            ret << dep
        }

        return ret;
    }


}
