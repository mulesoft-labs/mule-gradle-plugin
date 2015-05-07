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

/**
 * Encapsulates the information of an environment from the perspective of the mule agent.
 * Created by juancavallotti on 5/6/15.
 */
class MuleEnvironment {

    /**
     * The base url of the mule environment where the agent's
     * HTTP listener is waiting for requests.
     */
    String baseUrl = 'http://localhost:9999/mule'


}
