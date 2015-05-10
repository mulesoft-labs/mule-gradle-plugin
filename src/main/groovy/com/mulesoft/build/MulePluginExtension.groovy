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

/**
 * POGO with default configurations for the mule plugin.
 * Created by juancavallotti on 02/01/14.
 */
class MulePluginExtension {

    private static String DEFAULT_MODULE_GROUP = 'org.mule.modules'
    private static String DEFAULT_CONNECTOR_GROUP = 'org.mule.modules'

    static String DEFAULT_MULE_VERSION = '3.6.0'

    /**
     * The mule version against we want to build.
     */
    String version = DEFAULT_MULE_VERSION

    /**
     * The standalone path.
     */
    String installPath = null

    /**
     * Enterprise edition, by default is true, disable this if you wish to have a lighter build. By making it false
     * it will disable all the enterprise features in your project build, if you plan to use MuleStudio, then leave it
     * as true
     */
    boolean muleEnterprise = true

    /**
     * Mule test facilities are based on JUnit so this should keep always default value, nevertheless providing a backdoor
     * for other test backends.
     */
    boolean disableJunit = false

    /**
     * When mule enterprise = true, by default, the plugin will add the necessary tasks to unit-test against data mapper.
     * If this is not wanted, simply set this flag to false.
     */
    boolean disableDataMapper = false

    /**
     * The version of JUnit used to run tests.
     */
    String junitVersion = '4.9'

    /**
     * By default, the plugin includes many repositories that allow resolution of all the artifacts needed to build
     * mule Apps. In some enterprise setups, these repositories are proxied and / or cached. If this is the situation
     * this flag will disable the addition of these default repositories and give the user full responsibility of adding
     * repositories.
     */
    boolean disableDefaultRepositories = false

    /**
     * Credentials for the EE customer repository, this takes effect if muleEnterprise = true
     * These credentials may be obtained through a support ticket.
     */
    String enterpriseRepoUsername = ''

    /**
     * Password for customer repository.
     */
    String enterpriseRepoPassword = ''

    /**
     * Processed after the default libs, modules, transports has been configured, this is the preferred way of customizing
     * modules.
     */
    List<Closure<Void>> components = []

    /**
     * Mule core libraries, this normally should not be modified, if empty after project evaluation, defaults will be
     * applied.
     */
    Set<String> coreLibs = []

    /**
     * Mule enterprise core libraries, this normally should not be modified, if empty after project evaluation, defaults
     * will be applied.
     */
    Set<String> eeCoreLibs = []

    /**
     * Community open-source modules to be included. If left empty, defaults will be added.
     */
    Set<String> modules = []

    /**
     * Community open-source transports to be included. If left empty, defaults will be added.
     */
    Set<String> transports = []

    /**
     * Paid enterprise modules to be included. If left empty, defaults will be added, only included if muleEnterprise = true
     */
    Set<String> eeModules = []

    /**
     * Paid enterprise modules to be included. If left empty, defaults will be added, only included if muleEnterprise = true
     */
    Set<String> eeTransports = []

    /**
     * Maps containing the group: name: and version: of cloud connectors, devkit modules and hand-generated compatible plugins.
     */
    Set<Map<String, String>> plugins = []

    void components(Closure<Void> closure) {
        this.components << closure
    }


    /**
     * Add plugin coordinates to the connectors set.
     */
    public void plugin(def pluginData) {

        if (!pluginData.group) {
            throw new IllegalArgumentException('Plugin \'group\' attribute is mandatory')
        }

        if (!pluginData.name) {
            throw new IllegalArgumentException('Plugin \'name\' attribute is mandatory')
        }

        if (!pluginData.version) {
            throw new IllegalArgumentException('Plugin \'version\' attribute is mandatory')
        }

        Map<String, String> plugin = [group: pluginData.group,
                                      name: pluginData.name,
                                      version: pluginData.version,
                                      noClassifier: pluginData.noClassifier,
                                      noExt: pluginData.noExt
                                    ]
        plugins.add(plugin)
    }

    /**
     * Delegates to the plugin call. This is mostly reserved for future use, but takes advantage that most modules
     * are in the same group.
     * @param moduleData
     */
    public void module(moduleData) {
        if (!moduleData.group) {
            moduleData.group = DEFAULT_MODULE_GROUP
        }
        plugin(moduleData)
    }

    /**
     * Delegates to the plugin call. This is mostly reserved for future use, but takes advantage that most connectors
     * are in the same group.
     * @param moduleData
     */
    public void connector(connectorData) {
        if (!connectorData.group) {
            connectorData.group = DEFAULT_CONNECTOR_GROUP
        }
        plugin(connectorData)
    }
}
