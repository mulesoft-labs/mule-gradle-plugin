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
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Create the contents of mule-project.xml. This file
 *
 * Created by juancavallotti on 27/02/14.
 */
class StudioProject {

    private String projectName
    private MulePluginExtension muleConfig

    private static final String PROJECT_FILENAME = "mule-project.xml"

    private static final Logger logger = LoggerFactory.getLogger(StudioProject)

    protected GPathResult generateProjectXml() {

        XmlSlurper slurper = new XmlSlurper(false, false)

        //load the classpath resource.
        def project = slurper.parse(getClass().getResourceAsStream('/blank-mule-project.xml'))

        String runtimeVersion = generateRuntimeVersion()

        //set the appropriate runtime
        project.@runtimeId="org.mule.tooling.server.$runtimeVersion"

        //set the correct name
        project.appendNode {
            name(projectName)
        }


        return project
    }

    /**
     * Create the mule-project.xml file in the root folder of the project if it does not exist.
     */
    void createStudioProjectIfNecessary() {
        File projFile = new File(PROJECT_FILENAME)

        if (projFile.exists()) {
            logger.debug("$PROJECT_FILENAME already exists, not creating it")
            return
        }

        def xml = generateProjectXml()

        XmlUtil.serialize(xml, projFile.newWriter('UTF-8', false))
    }

    /**
     * Estimate the runtime version. This is a best effort approach.
     * @return the version of the runtime in mule studio
     */
    protected String generateRuntimeVersion() {
        String ee = muleConfig.muleEnterprise ? '.ee' : ''
        String version = muleConfig.version
        return version + ee
    }
}
