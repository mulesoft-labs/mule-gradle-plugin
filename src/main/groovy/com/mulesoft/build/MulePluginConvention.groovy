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
 * Created by juancavallotti on 28/05/14.
 */
class MulePluginConvention {

    /**
     * Where to read the mule configurations.
     */
    String appSourceDir = 'src/main/app'

    /**
     * Where to read data-mapper mappings.
     */
    String dataMapperMappingsDir = 'mappings'

    /**
     * Where to read APIKit, RAML files.
     */
    String apiKitApiDir = 'src/main/api'

    /**
     * Where to read MuleDomain sources.
     */
    String domainSourceDir = 'src/main/domain'

    /**
     * Where to read MuleDomain resources.
     * TODO - this might be best implemented using source sets.
     */
    String domainResourcesDir = 'src/main/resources'

    /**
     * Extension point for additional paths to be configured.
     */
    Set<String> additionalPaths = []

    public final Set<String> appResourcesDirectory() {
        return ([appSourceDir, dataMapperMappingsDir, apiKitApiDir] + additionalPaths)
    }

}
