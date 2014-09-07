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

    static final String MULE_APP_PROPS = APP_DIR + 'mule-app.properties'


    static final String CONFIG_RESOURCES_KEY = 'config.resources'

    MuleRunTask() {
        setMain 'org.mule.MuleServer'

        def args = []

        //check inf mule-app.properties exist
        File muleAppProps = project.file(MULE_APP_PROPS)

        if (muleAppProps.exists()) {
            args.add('-props')
            args.add(MULE_APP_PROPS)
        }

        args.add('-config')

        //check if mule-config exists
        File deployProps = project.file(MULE_DEPLOY)



        if (!deployProps.exists()) {
            args.add(MULE_DEFAULT_CONFIG);
            this.args = args
            logger.info("JVM Arguments for running the app: $args")
            return
        }

        Properties props = new Properties()
        props.load(deployProps.newInputStream())

        args.addAll(props.getProperty(CONFIG_RESOURCES_KEY).split(',').collect({APP_DIR + it}))
        logger.info("JVM Arguments for running the app: $args")
        this.args = args
    }
}
