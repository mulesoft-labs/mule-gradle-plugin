package com.mulesoft.build.deploy

import com.mulesoft.build.MulePluginExtension
import org.gradle.api.tasks.Copy

/**
 * Created by juancavallotti on 16/02/14.
 */
class InstallInRuntime extends Copy {

    static final String MULE_HOME_VAR = 'MULE_HOME'

    static final String APPS_DIR = '/apps'

    static final String TASK_DESC = 'Install the generated deployable into the location defined in mule.installPath ' +
            'or MULE_HOME environment variable'

    InstallInRuntime() {
        description = TASK_DESC;

        //should copy the files in the build directory to the
        //configured runtime
        from project.buildDir

        //I want the zip files in the build dir, TODO - copy just the appropriate output file
        include "*.zip"

        //determine where to copy the file
        //if the prject includes the setting, then it is easy.
        MulePluginExtension mule = project.mule
        def folderName = resolveTargetLocation(mule)

        if (folderName) {
            destinationDir = new File(folderName + APPS_DIR)
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
