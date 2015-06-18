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
package com.mulesoft.build.mmc

import org.gradle.util.ConfigureUtil

/**
 * Configuration parameters for the Mule Management Console connection.
 * Created by juancavallotti on 04/06/14.
 */
class MMCPluginExtension {

    /**
     * The configured environments.
     */
    Map<String, MMCEnvironment> environments = [:] as Map

    /**
     * The environment in which to deploy by default. If null, this will be inferred by certain rules, checking if there
     * is only one environment configured.
     */
    String defaultEnvironment = null

    /**
     * The environment to pick. This forces the resolution of environment to the one specified by this property.
     * This property takes precedence over any other configuration.
     */
    String forceEnvironment = null;

    /**
     * Add an environment with the given name to the deployments.
     * @param name the name of the environment
     * @param environment the environment configuration
     */
    void addEnvironment(String name, MMCEnvironment environment) {
        environments[name] = environment
    }

    void environments(Closure<Void> closure) {
        ConfigureUtil.configure(closure, this)
    }

    /**
     * DSL implementation for adding new environments
     * @param name
     * @param args
     * @return
     */
    def methodMissing(String name, def args) {
        addEnvironment(name, args as MMCEnvironment)
    }

    /**
     * Logic for resolving the target environment
     * @return null if no environment is configured.
     */
    MMCEnvironment resolveTargetEnvironment() {
        if (environments.isEmpty()) {
            return null
        }

        if (forceEnvironment != null) {
            return environments[forceEnvironment]
        }

        if (defaultEnvironment != null) {
            return environments[defaultEnvironment]
        }

        if (environments.size() > 1) {
            //cannot infer enviromnent
            return null
        }

        //return any environment
        return environments.find {return true}.value

    }

}
