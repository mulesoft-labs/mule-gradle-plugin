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

package com.mulesoft.build.run

import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.InputFiles
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by juancavallotti on 2/26/15.
 */
class CopyAdditionalResourcesTask extends Copy {

    private static final Logger logger = LoggerFactory.getLogger(CopyAdditionalResourcesTask)

    CopyAdditionalResourcesTask() {
        logger.debug('Configure copy additional resources task')

        from resourcesDirectories()
        into "${project.buildDir}/resources/main"
    }


    @InputFiles
    public Collection resourcesDirectories() {
        def convention = project.convention.getByName('muleConvention')
        return convention.appResourcesDirectory()
    }

}
