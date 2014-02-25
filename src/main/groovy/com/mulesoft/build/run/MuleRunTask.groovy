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


package com.mulesoft.build.run

import org.gradle.api.tasks.JavaExec


/**
 * Created by juancavallotti on 16/02/14.
 */
class MuleRunTask extends JavaExec {

    static final String TASK_DESC = 'Executes the project for testing in an embedded Mule Server'

    static final String APP_DIR = 'src/main/app/'

    static final String MULE_DEFAULT_CONFIG = APP_DIR + 'mule-config.xml'

    static final String MULE_DEPLOY = APP_DIR + 'mule-deploy.properties'

    static final String CONFIG_RESOURCES_KEY = 'config.resources'

    MuleRunTask() {
        main = 'org.mule.MuleServer'

        def config = ['-config']

        //check if mule-config exists
        File deployProps = new File(MULE_DEPLOY)

        if (!deployProps.exists()) {
            config.add(MULE_DEFAULT_CONFIG);
            this.args = config
            logger.info("JVM Arguments for running the app: $config")
            return
        }

        Properties props = new Properties()
        props.load(deployProps.newInputStream())

        config.addAll(props.getProperty(CONFIG_RESOURCES_KEY).split(' ').collect({APP_DIR + it}))
        logger.info("JVM Arguments for running the app: $config")
        this.args = config
    }
}
