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
package com.mulesoft.build

import com.mulesoft.build.domain.MuleDomainPlugin
import com.mulesoft.build.studio.StudioPlugin
import com.mulesoft.build.util.FileUtils
import com.mulesoft.build.util.GradleProjectUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

/**
 * This task allows the creation of a simple mule project that can be used as starter project. This project is intended
 * as a Quickstart tool for new MuleESB users.
 * Created by juancavallotti on 25/05/14.
 */
class InitProjectTask extends DefaultTask {

    static final String STARTER_XML_FILENAME = ''

    @TaskAction
    def initStarterProject() {
        logger.debug('Creating a starter project...')

        checkAndCreatePaths()
        copyInitialFiles()
        printInstructions()

        logger.debug('Starter project created.')

    }

    void checkAndCreatePaths() {

        def paths = [
                'src/main/app',
                'src/main/java',
                'src/test/java',
                'src/main/resources',
                'src/test/resources'
        ]

        paths.each {
            project.mkdir(it)
        }

    }

    void copyInitialFiles() {

        String version = project.mule.version

        boolean legacy = GradleProjectUtils.isLegacyVersion(version)
        boolean hasDomainParent = GradleProjectUtils.hasDomainParent(project)

        InputStream contents = null
        String filename = null

        if (legacy) {
            //legacy projects use log4j.properties
            filename = 'src/main/resources/log4j.properties'
            contents = getClass().getResourceAsStream('/starters/starters-log4j.properties')
        } else {
            //use log4j2.xml
            filename = 'src/main/resources/log4j2.xml'
            contents = getClass().getResourceAsStream('/starters/starters-log4j2.xml')
        }

        writeFile(filename, contents)

        //app env properties
        filename = 'src/main/app/mule-app.properties'
        contents = getClass().getResourceAsStream('/starters/starters-mule-app.properties')

        writeFile(filename, contents)

        //deployment descriptor
        filename = 'src/main/app/mule-deploy.properties'
        contents = getClass().getResourceAsStream('/starters/starters-mule-deploy.properties')

        saveDeployProperties(filename, contents)

        filename = 'src/main/app/mule-config.xml'
        if (legacy) {
            //mule app configuration
            contents = getClass().getResourceAsStream('/starters/starters-mule-config-legacy.xml')
        } else {
            contents = getClass().getResourceAsStream('/starters/starters-mule-config.xml')
        }
        writeFile(filename, contents)


        //if not legacy, copy the starters globals
        if (!legacy && !hasDomainParent) {
            filename = 'src/main/app/globals.xml'
            contents =  getClass().getResourceAsStream('/starters/starters-mule-globals.xml')
            writeFile(filename, contents)
        }
    }

    void writeFile(String filename, InputStream contents) {
        FileUtils.writeFile(project, filename, contents)
    }

    void printInstructions() {

        String header  = ' DETAIL '

        (50 - header.length() / 2).times {print '-'}
        print header
        (50 - header.length() / 2).times {print '-'}
        print '\n'


        println 'Succeeded creating project.'
        println 'You can run it by invoking the \'runApp\' task.'

        if (project.plugins.hasPlugin(StudioPlugin)) {
            println 'You can convert this project into a MuleStudio project by invoking the \'studio\' task.'
        }

        println 'Visit: https://github.com/mulesoft-labs/mule-gradle-plugin to learn about this plugin.'

        100.times {print '-'}
        print '\n'

    }


    void saveDeployProperties(String filename, InputStream contents) {

        String version = project.mule.version
        boolean legacy = GradleProjectUtils.isLegacyVersion(version)
        boolean hasDomainParent = GradleProjectUtils.hasDomainParent(project)

        if (contents == null) {
            throw new IllegalArgumentException("Cannot write $filename, contents not found.")
        }

        logger.debug('Applying proper domain, if needed.')

        //check if I have parent and if my parent is a domain
        if (!hasDomainParent) {
            logger.debug('The project is not part of any domain, leaving as it is')
            writeFile(filename, contents)
            return
        }

        def parentMule = project.parent.mule

        //find the domain name.
        String domainName = parentMule.resolveDomainName()

        Properties deployProps = new Properties()

        deployProps.load(contents)

        deployProps['domain'] = domainName

        if (!legacy) {
            //this is a non legacy domain so we exclude the globals.xml
            deployProps['config.resources'] = 'mule-config.xml'
        }

        deployProps.store(project.file(filename).newOutputStream(), 'Created by mule gradle plugin')
    }
}
