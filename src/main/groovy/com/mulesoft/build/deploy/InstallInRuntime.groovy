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

        //do this as soon as we have the effective config.
        doInstallInRuntime()

    }


    void doInstallInRuntime() {

        //this could be a standalone project or a domain.
        //if it is a domain, then the deployment is different.

        boolean isDomain = project.plugins.hasPlugin(MuleDomainPlugin)
        def mule = project.mule

        String archiveName = isDomain ? mule.resolveDomainName() : project.name
        String targetLocation = isDomain ? DOMAINS_DIR : APPS_DIR

        //determine where to copy the file
        //if the prject includes the setting, then it is easy.
        def folderName = resolveTargetLocation(mule)

        if (folderName) {
            File destination = new File(folderName).listFiles().find { File child ->
                child.isDirectory() && child.name.equals(targetLocation)
            }

            if (!destination.exists()) {
                throw new IllegalStateException("Selected runtime does not have the $targetLocation deployment directory.")
            }

            //should copy the files in the build directory to the
            //configured runtime
            from project.buildDir

            include "${archiveName}.zip"

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
