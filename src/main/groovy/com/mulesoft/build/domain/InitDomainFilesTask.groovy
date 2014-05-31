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

import com.mulesoft.build.MulePluginConvention
import com.mulesoft.build.util.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by juancavallotti on 31/05/14.
 */
class InitDomainFilesTask extends DefaultTask {

    /**
     * Initialize the domain project with the appropriate structure.
     */
    @TaskAction
    void copyStarterFiles() {

        //get the convention
        MulePluginConvention convention = project.convention.getByType(MulePluginConvention)

        File domainDir = project.file(convention.domainSourceDir)

        if (domainDir.exists()) {
            logger.warn("Directory ${convention.domainSourceDir} already exists not executing further actions.")
            return
        } else {

            if (domainDir.mkdirs()) {
                logger.debug("Successfully created ${convention.domainSourceDir}")
            } else {
                logger.warn("Could not create ${convention.domainSourceDir}")
            }
        }

        InputStream domainStarter;

        if (project.mule.muleEnterprise) {
            domainStarter = getClass().getResourceAsStream('/starters/starters-domain-ee.xml')
        } else {
            domainStarter = getClass().getResourceAsStream('/starters/starters-domain-ce.xml')
        }

        //and write it
        String fileName = "${convention.domainSourceDir}/mule-domain-config.xml"

        FileUtils.writeFile(project, fileName, domainStarter)
    }

}
