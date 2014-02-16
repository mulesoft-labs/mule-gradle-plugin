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

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.bundling.Jar


/**
 * Created by juancavallotti on 02/01/14.
 */
class MuleZip extends Jar {

    private FileCollection classpath

    MuleZip() {
        extension = "zip"

        def baseDir = rootSpec.addChildBeforeSpec(mainSpec).into('')

        baseDir.into('classes') {
            from {
                def classpath = getClasspath()
                classpath ? classpath.filter {File file -> file.isDirectory()} : []
            }
        }

        baseDir.into('lib') {
            from {
                def classpath = getClasspath()
                classpath ? classpath.filter {File file -> file.isFile()} : []
            }
        }

        setDestinationDir(new File("build/"))

    }

    @InputFiles @Optional
    FileCollection getClasspath() {
        return classpath
    }

    void setClasspath(Object classpath) {
        this.classpath = project.files(classpath)
    }

    void classpath(Object... classpath) {
        FileCollection oldClasspath = getClasspath()
        this.classpath = project.files(oldClasspath ?: [], classpath)
    }
}