/*
 * Copyright 2015 juancavallotti.
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

package com.mulesoft.build.muleagent

import org.gradle.util.ConfigureUtil

/**
 * Created by juancavallotti on 5/6/15.
 */
class MuleAgentPluginExtension {

    /**
     * The configured environments.
     */
    Map<String, List<MuleEnvironment>> environments = [:] as Map

    /**
     * The environment in which to deploy by default. If null, this will be inferred by certain rules, checking if there
     * is only one environment configured.
     */
    String defaultEnvironment = null

    /**
     * Add an environment with the given name to the deployments.
     * @param name the name of the environment
     * @param environment the environment configuration
     */
    void addEnvironment(String name, MuleEnvironment environment) {
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

        if (args.length > 0 && args[0] instanceof List) {
            environments[name] = args[0]
        } else {
            addEnvironment(name, args as MuleEnvironment)
        }
    }

    /**
     * DSL implementation for adding an environment with all default values.
     * @param name the name of the environment.
     */
    def propertyMissing(String name) {
        addEnvironment(name, new MuleEnvironment())
    }


    List<MuleEnvironment> cluster(Closure<Void> closure) {
        def context = new ClusterDelegate()
        ConfigureUtil.configure(closure, context)
        return context.envs
    }


    /**
     * Logic for resolving the target environment
     * @return null if no environment is configured.
     */
    List<MuleEnvironment> resolveTargetEnvironments() {
        if (environments.isEmpty()) {
            return []
        }

        if (defaultEnvironment != null) {
            return environments[defaultEnvironment]
        }

        if (environments.size() > 1) {
            //cannot infer enviromnent
            return []
        }

        //return any environment
        return environments.find {return true}.value

    }
}
