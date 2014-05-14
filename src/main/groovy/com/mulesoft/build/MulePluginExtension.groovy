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
 * POGO with default configurations for the mule plugin.
 * Created by juancavallotti on 02/01/14.
 */
class MulePluginExtension {

    /**
     * The mule version against we want to build.
     */
    String version = '3.4.0'

    /**
     * The standalone path.
     */
    String installPath = null

    /**
     * Enterprise edition, by default is true, disable this if you wish to have a lighter build. By making it false
     * it will disable all the enterprise features in your project build, if you plan to use MuleStudio, then leave it
     * as true
     */
    boolean muleEnterprise = true

    /**
     * Mule test facilities are based on JUnit so this should keep always default value, nevertheless providing a backdoor
     * for other test backends.
     */
    boolean disableJunit = false

    /**
     * The version of JUnit used to run tests.
     */
    String junitVersion = '4.9'

    /**
     * Credentials for the EE customer repository, this takes effect if muleEnterprise = true
     * These credentials may be obtained through a support ticket.
     */
    String enterpriseRepoUsername = ''

    /**
     * Password for customer repository.
     */
    String enterpriseRepoPassword = ''


}
