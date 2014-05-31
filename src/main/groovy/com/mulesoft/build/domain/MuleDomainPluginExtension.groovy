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

import com.mulesoft.build.MulePluginExtension

/**
 * Created by juancavallotti on 30/05/14.
 */
class MuleDomainPluginExtension extends MulePluginExtension {

    MuleDomainPluginExtension(String projectName) {
        this.projectName = projectName
    }

    /**
     * Configuration parameter for domain name. This setting, if remains null will take the value from the main project
     * name.
     */
    String domainName = null

    /**
     * For internal use only, please do not modify.
     */
    private String projectName = null;


    String resolveDomainName() {
        domainName ?: projectName
    }
}
