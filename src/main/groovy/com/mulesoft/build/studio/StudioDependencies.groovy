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

import groovy.xml.XmlUtil
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

    //cache to avoid parsing the file over and over
    List<Map> cache;

    /**
     * Get all the dependencies configured on the studio
     * @return
     */
    List<Map> listCompileDeps(Project project) {

        //if the studio deps file exists, then read through it
        File deps = project.file(STUDIO_DEPS_FILE)

        if (cache != null) {
            return cache
        }

        if (!deps.exists()) {
            logger.info("Studio specific dependencies file ${deps.absolutePath} does not exist")
            return []
        }

        //cache the compile deps.
        cache = listCompileDeps(deps)

        return cache;
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

    void addDependency(Project project) {

        //this requires three system properties.
        String group = System.getProperty('group')
        String name = System.getProperty('name')
        String version = System.getProperty('version')

        if (!group?.trim() || !name?.trim() || !version?.trim()) {
            logger.error('Could not find one of group, name or version parameters')
            return
        }

        logger.info("Prepared to add studio dependency: group: $group, name: $name, version: $version")

        //verify if the dependency is already
        Map dep = [group: group, name: name, version: version]

        List<Map> deps = listCompileDeps(project)

        Map existing = deps.find {
            return it.equals(dep)
        }

        if (existing) {
            logger.info('Project already contains the same studio dependency!')
            return
        }

        //proceed to add
        File xml = project.file(STUDIO_DEPS_FILE)

        addDependencyToFile(dep, xml)
    }

    void addDependencyToFile(Map<String, String> dep, File xml) {

        def dependencies = null

        XmlSlurper slurper = new XmlSlurper()


        if (xml.exists()) {
            //parse the xml
            dependencies = slurper.parse(xml)
        } else {
            dependencies = slurper.parseText('<?xml version="1.0" encoding="UTF-8" ?><dependencies></dependencies>')
        }

        //the dependency might be there in different version
        def existing = dependencies.dependency.find {
            it.@name == dep['name'] && it.@group == dep['group']
        }

        //if it exists, then remove it
        if (existing) {
            existing.replaceNode {}
        }

        dependencies.appendNode {
            dependency(dep)
        }

        if (!xml.exists())
            xml.createNewFile()


        XmlUtil.serialize(dependencies, xml.newWriter())
    }
}
