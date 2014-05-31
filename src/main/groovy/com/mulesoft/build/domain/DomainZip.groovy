package com.mulesoft.build.domain

import com.mulesoft.build.MulePluginConvention
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip

/**
 * Created by juancavallotti on 30/05/14.
 */
class DomainZip extends Zip {

    DomainZip() {

        project.afterEvaluate {
            configureArtifacts()
        }
    }



    void configureArtifacts() {
        //we'd like to place the results on the build dir.
        destinationDir = project.buildDir

        //the zip name of the domain is the name of the project.
        baseName = project.name

        //get the plugin convention
        from {
            MulePluginConvention convention = project.convention.getByType(MulePluginConvention)
            return convention.domainSourceDir
        }

        //add the logic for copying the apps.
        into('apps') {
            from project.buildDir
            exclude "${project.name}.zip"
            include '**.zip'
        }

        //compile-time deps will go to the domain's lib directory
        into('lib') {

            Set<File> providedFiles = project.configurations.providedCompile.files
            Set<File> compileFiles = project.configurations.compile.files

            from (compileFiles - providedFiles)
            include '**.jar'
        }
    }

}
