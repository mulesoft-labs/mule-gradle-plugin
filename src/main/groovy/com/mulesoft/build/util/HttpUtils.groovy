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
package com.mulesoft.build.util

import org.apache.commons.codec.binary.Base64

/**
 * Created by juancavallotti on 04/06/14.
 */
class HttpUtils {

    static void configureNetworkAuthenticator(String username, String password) {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray())
            }
        })
    }

    static String generateAuthenticationHeader(String username, String password) {
        String authString = "$username:$password"
        return 'Basic ' + authString.bytes.encodeBase64().toString()
    }

}
