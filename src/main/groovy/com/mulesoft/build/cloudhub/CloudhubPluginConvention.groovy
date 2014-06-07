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

/**
 * Convention class for configuring variables related to cloudhub deployment.
 * Created by juancavallotti on 06/06/14.
 */
class CloudhubPluginConvention {

    /**
     * This points where the REST API of cloudhub is. This requires basic authentication.
     */
    String clouduhbApiEndpoint = 'https://cloudhub.io/api'

}
