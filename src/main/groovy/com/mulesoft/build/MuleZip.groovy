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