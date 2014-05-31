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
 * This is the version of the plugin extension that modules of the domain will use, this is just to ensure consistency
 * and at the same time enable custom settings per-module.
 * Created by juancavallotti on 31/05/14.
 */
class MuleDomainModulePluginExtension extends MulePluginExtension {

    /**
     * The parent domain extension.
     */
    private MuleDomainPluginExtension parent


    //override crucial getters

    /**
     * Mule version should be the same across the domain for consistency.
     * @return
     */
    String getVersion() {
        return parent.getVersion()
    }

    /**
     * There is no way a domain is enterprise and modules are not.
     * @return
     */
    boolean isMuleEnterprise() {
        return parent.isMuleEnterprise()
    }

    /**
     * We need to share credentials across modules so all can build properly.
     * @return
     */
    String getEnterpriseRepoUsername() {
        return parent.getEnterpriseRepoUsername()
    }

    /**
     * We need to share credentials across modules so all can build properly.
     * @return
     */
    String getenterpriseRepoPassword() {
        return parent.getEnterpriseRepoPassword()
    }
}
