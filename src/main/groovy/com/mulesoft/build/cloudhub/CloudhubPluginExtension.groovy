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
package com.mulesoft.build.cloudhub

import org.gradle.util.ConfigureUtil

/**
 * Created by juancavallotti on 06/06/14.
 */
class CloudhubPluginExtension {

    /**
     * The configured domains.
     */
    Map<String, CloudhubEnvironment> domains = [:] as Map

    /**
     * The domain in which to deploy by default. If null, this will be inferred by certain rules, checking if there
     * is only one environment configured.
     */
    String defaultDomain = null

    /**
     * Add a domain with the given name to the domains.
     * @param name the name of the environment
     * @param environment the environment configuration
     */
    void addDomain(String name, CloudhubEnvironment environment) {
        environment.domainName = name
        domains[name] = environment
    }

    void domains(Closure<Void> closure) {
        ConfigureUtil.configure(closure, this)
    }

    /**
     * DSL implementation for adding new domains
     * @param name
     * @param args
     * @return
     */
    def methodMissing(String name, def args) {
        addDomain(name, args as CloudhubEnvironment)
    }

    /**
     * Logic for resolving the target environment
     * @return null if no environment is configured.
     */
    CloudhubEnvironment resolveTargetDomain() {
        if (domains.isEmpty()) {
            return null
        }

        if (defaultDomain != null) {

            return domains[defaultDomain]
        }

        if (domains.size() > 1) {
            //cannot infer enviromnent
            return null
        }

        //return any environment
        return domains.find {return true}.value
    }
}
