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
package com.mulesoft.build.domain

import com.mulesoft.build.MulePluginConstants
import com.mulesoft.build.MulePluginConvention
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Fix the domain modules by configuring the proper domain name in their mule-deploy.properties file.
 *
 * Created by juancavallotti on 31/05/14.
 */
class FixDomainTask extends DefaultTask {
    private static final Logger logger = LoggerFactory.getLogger(CheckDomainTask)

    FixDomainTask() {
        description = "Update all modules in the project so they are correctly configured for the Domain."
        group = MulePluginConstants.MULE_GROUP
    }

    @TaskAction
    void checkSubprojects() {

        String domainName = project.mule.resolveDomainName()
        MulePluginConvention convention  = null

        project.subprojects.each {Project subproj ->
            convention = subproj.convention.findByType(MulePluginConvention)

            //where the app resides?
            File deployProps = subproj.file("${convention.appSourceDir}").listFiles().find({File file ->
                file.name.equals('mule-deploy.properties')
            })

            if (!deployProps || !deployProps.exists()) {
                logger.warn("Cannot verify module ${subproj.name}, file mule-deploy.properties not present.")
                throw new IllegalStateException("Module ${subproj.name} does not have a mule-deploy.properties file!")
            }

            Properties props = new Properties()
            props.load(deployProps.newInputStream())

            String domain = props['domain']

            if (domainName.equals(domain)) {
                logger.info("Module ${subproj.name} is correctly configured")
                return
            }

            logger.warn("Updating module ${subproj.name}...")
            props['domain'] = domainName

            props.store(deployProps.newOutputStream(), 'Updated by gradle mule plugin.')
        }
    }

}
