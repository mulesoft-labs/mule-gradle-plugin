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
package com.mulesoft.build.dependency

import com.mulesoft.build.MulePluginExtension
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by juancavallotti on 24/05/14.
 */
class MuleProjectDependenciesConfigurer implements DependenciesConfigurer {

    static final MULE_DEPS_PREFIX = 'mule-'
    static final MULE_MODULES_PREFIX = MULE_DEPS_PREFIX + 'module-'
    static final MULE_TRANSPORTS_PREFIX = MULE_DEPS_PREFIX + 'transport-'
    static final COMMUNITY_GROUPID = 'org.mule'
    static final COMMUNITY_MODULES_GROUPID  = COMMUNITY_GROUPID + '.modules'
    static final COMMUNITY_TRANSPORTS_GROUPID  = COMMUNITY_GROUPID + '.transports'
    static final EE_GROUPID = 'com.mulesoft.muleesb'
    static final EE_MODULES_GROUPID = EE_GROUPID + '.modules'
    static final EE_TRANSPORTS_GROUPID = EE_GROUPID + '.transports'



    Project project
    MulePluginExtension mule


    final static Logger logger = LoggerFactory.getLogger(MuleProjectDependenciesConfigurer.class)


    @Override
    void applyDependencies() {
        applyDefaults()
        addMuleDependencies()
    }

    void applyDefaults() {

        logger.debug('Apply defaults phase for mule dependencies...')

        //core libs, could be more
        if (mule.coreLibs.empty) {
            logger.debug('Applying default core libs..')
            mule.coreLibs.addAll(['core'])
        }

        if (mule.eeCoreLibs.empty && mule.muleEnterprise) {
            logger.debug('Applying default ee libs...')
            mule.eeCoreLibs.addAll(['core-ee'])
        }


        if (mule.modules.empty) {
            logger.debug('Applying default modules...')
            mule.modules.addAll([
                    'spring-config',
                    'client',
                    'cxf',
                    'json',
                    'management',
                    'scripting',
                    'sxc',
                    'xml'
            ])
            logger.debug("Modules enabled: ${mule.modules}")
        }

        if (mule.transports.empty) {
            logger.debug('Applying default transports...')
            mule.transports.addAll([
                    'file',
                    'http',
                    'jdbc',
                    'jms',
                    'vm'
            ])
            logger.debug("Transports enabled: ${mule.transports}")
        }

        if (mule.eeModules.empty && mule.muleEnterprise) {
            logger.debug('Applying default Enterprise modules...')
            mule.eeModules.addAll([
                    'spring-config-ee',
                    'data-mapper',
                    'boot-ee'
            ])

            logger.debug("Enterprise modules enabled: ${mule.modules}")
        }

        if (mule.eeTransports.empty && mule.muleEnterprise) {
            logger.debug('Applying default Enterprise transports...')
            //none for the time being.
            logger.debug("Enterprise modules enabled: ${mule.modules}")
        }


        //finally call the closure to finalize the customization of the modules.
        if (mule.components) {
            mule.components()
        }
    }

    void addMuleDependencies() {
        //we need to decide into which configuration we want to add the deps.
        //this could be embedded or mule app

        List<Map> deps = buildDependencies()


        project.dependencies {
            if (project.configurations.findByName('providedCompile')) {
                logger.debug('Adding dependencies in MuleApp mode...')
                providedCompile(deps)
            } else {
                logger.debug('Adding dependencies in embedded mode...')
                compile(deps)

            }

            if (mule.disableJunit) {
                return
            }

            def testDeps = [
                    [group: 'org.mule.tests', name: 'mule-tests-functional', version: mule.version],
                    [group: 'junit', name: 'junit', 'version': mule.junitVersion]
            ];

            if (project.configurations.findByName('providedTestCompile')) {
                providedTestCompile(testDeps)
            } else {
                testCompile(testDeps)
            }

        }
    }

    List<Map> buildDependencies() {

        List<Map> ret = []

        ret.addAll mule.coreLibs.collectAll {String element ->
            return [group: COMMUNITY_GROUPID, name: MULE_DEPS_PREFIX + element, version: mule.version]
        }

        ret.addAll mule.modules.collectAll {String element ->
            return [group: COMMUNITY_MODULES_GROUPID, name: MULE_MODULES_PREFIX + element, version: mule.version]
        }
        ret.addAll mule.transports.collectAll {String element ->
            return [group: COMMUNITY_TRANSPORTS_GROUPID, name: MULE_TRANSPORTS_PREFIX + element, version: mule.version]
        }

        if (!mule.muleEnterprise) {
            return ret;
        }

        ret.addAll mule.eeCoreLibs.collectAll {String element ->
            return [group: EE_GROUPID, name: MULE_DEPS_PREFIX + element, version: mule.version]
        }
        ret.addAll mule.eeModules.collectAll {String element ->
            return [group: EE_MODULES_GROUPID, name: MULE_MODULES_PREFIX + element, version: mule.version]
        }
        ret.addAll mule.eeTransports.collectAll {String element ->
            return [group: EE_TRANSPORTS_GROUPID, name: MULE_TRANSPORTS_PREFIX + element, version: mule.version]
        }

    }
}