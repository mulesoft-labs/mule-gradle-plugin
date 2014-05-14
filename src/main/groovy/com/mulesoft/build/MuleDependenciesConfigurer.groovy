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

import org.gradle.api.Project

/**
 * Utility class to encapsulate how we add mule dependencies to a given project.
 *
 * Created by juancavallotti on 14/05/14.
 */
class MuleDependenciesConfigurer {
    private void addDependenciesToProject(Project project) {

        MulePluginExtension mule = project.mule

        //get the mule version.
        project.dependencies {
            def testDeps = [];

            if (!mule.disableJunit) {
                testDeps = [
                        [group: 'org.mule.tests', name: 'mule-tests-functional', version: mule.version],
                        [group: 'junit', name: 'junit', 'version': mule.junitVersion]
                ];
            }

            def eeDeps = [
                    [group: 'com.mulesoft.muleesb.modules', name: 'mule-module-boot-ee', version: mule.version],
                    [group: 'com.mulesoft.muleesb', name: 'mule-core-ee', version: mule.version],
                    [group: 'com.mulesoft.muleesb.modules', name: 'mule-module-data-mapper', version: mule.version],
                    [group: 'com.mulesoft.muleesb.modules', name: 'mule-module-spring-config-ee', version: mule.version]
            ]

            providedCompile (
                    (mule.muleEnterprise ? eeDeps : []) +
                            [group: 'org.mule', name: 'mule-core', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-spring-config', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-file', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-http', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-jdbc', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-jms', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-vm', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-client', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-cxf', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-json', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-management', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-scripting', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-sxc', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-xml', version: mule.version]
            )

            providedTestCompile (
                    testDeps
            )
        }
    }
}
