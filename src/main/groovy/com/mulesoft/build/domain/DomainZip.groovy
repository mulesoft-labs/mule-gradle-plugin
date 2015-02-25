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
package com.mulesoft.build.domain

import com.mulesoft.build.MulePluginConvention
import org.gradle.api.Project
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Zip

/**
 * Created by juancavallotti on 30/05/14.
 */
class DomainZip extends Zip {


    DomainZip() {
        configureArtifacts()
    }


    void configureArtifacts() {

        String archiveName = project.mule.resolveDomainName()

        //we'd like to place the results on the build dir.
        destinationDir = project.buildDir

        //the zip name of the domain is the name of the project.
        baseName = archiveName

        //get the plugin convention
        from {
            MulePluginConvention convention = project.convention.getByType(MulePluginConvention)
            return convention.domainSourceDir
        }

        //add the logic for copying the apps.
        rootSpec.into('apps') {
            from {
                getModuleArchives()
            }
        }

        //compile-time deps will go to the domain's lib directory
        rootSpec.into('lib') {
            from {
                getLibs()
            }
            include '**.jar'
        }
    }

    @InputFiles
    public Set<File> getLibs() {
        Set<File> providedFiles = project.configurations.providedCompile.files
        Set<File> compileFiles = project.configurations.compile.files
        return compileFiles - providedFiles
    }

    @InputFiles
    public Set<File> getModuleArchives() {
        Set<File> files = []
        project.subprojects.each { subproj ->
            files.addAll(subproj.configurations.archives.allArtifacts.files.files)
        }
        return files
    }
}
