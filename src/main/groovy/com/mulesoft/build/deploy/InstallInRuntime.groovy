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

package com.mulesoft.build.deploy

import com.mulesoft.build.MulePluginConstants
import com.mulesoft.build.MulePluginExtension
import com.mulesoft.build.domain.MuleDomainPlugin
import org.gradle.api.tasks.Copy

/**
 * Created by juancavallotti on 16/02/14.
 */
class InstallInRuntime extends Copy {

    static final String MULE_HOME_VAR = 'MULE_HOME'

    static final String APPS_DIR = 'apps'

    static final String DOMAINS_DIR = 'domains'

    static final String TASK_DESC = 'Install the generated archive into the location defined in mule.installPath ' +
            'or MULE_HOME environment variable'


    InstallInRuntime() {
        //this task should always run
        outputs.upToDateWhen {false}
    }


    void doInstallInRuntime() {

        //this could be a standalone project or a domain.
        //if it is a domain, then the deployment is different.

        boolean isDomain = project.plugins.hasPlugin(MuleDomainPlugin)
        def mule = project.mule

        //this plugin needs the mule plugin in order to be correctly executed
        //so we hope project.mulezip won't fail.
        String archiveName = isDomain ? mule.resolveDomainName() + '.zip' : project.mulezip.archiveName

        String targetLocation = isDomain ? DOMAINS_DIR : APPS_DIR

        //determine where to copy the file
        //if the prject includes the setting, then it is easy.
        def folderName = resolveTargetLocation(mule)

        if (folderName) {

            File runtimeInstallPath = new File(folderName)

            if (!runtimeInstallPath.exists()) {
                String message = "Runtime not found in location: $runtimeInstallPath"
                logger.error(message)
                throw new IllegalArgumentException(message)
            }

            File destination = runtimeInstallPath.listFiles().find { File child ->
                child.isDirectory() && child.name.equals(targetLocation)
            }

            if (!destination.exists()) {
                String message = "Selected runtime location ($runtimeInstallPath) does not have the $targetLocation deployment directory."
                logger.error(message)
                throw new IllegalArgumentException(message)
            }

            logger.debug("Archive name to copy: $archiveName")

            //should copy the files in the build directory to the
            //configured runtime
            from project.buildDir

            include archiveName

            destinationDir = destination
        }

    }

    private String resolveTargetLocation(MulePluginExtension mule) {

        if (mule.installPath) {
            logger.info("Found configured mule home: $mule.installPath")
            return mule.installPath
        }

        //ok install path not defined
        def muleHome = System.env[MULE_HOME_VAR]

        if (muleHome) {
            logger.info("Found MULE_HOME environment variable: $muleHome");
            return muleHome
        }

        return null;
    }
}
