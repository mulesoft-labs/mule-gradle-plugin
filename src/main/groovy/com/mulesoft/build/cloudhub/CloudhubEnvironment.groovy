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
 * Created by juancavallotti on 06/06/14.
 */
class CloudhubEnvironment {

    /**
     * The username for logging in to this cloudhub environment.
     */
    String username;

    /**
     * The password for logging in to this cloudhub environment.
     */
    String password;

    /**
     * The domain name of this environment. in the form <domain name>.cloudhub.io, normally not configured directly.
     */
    String domainName;
}
