package com.mulesoft.build.studio

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * This plugin is a customization of the eclipse plugin that adds specific
 * configurations for MuleStudio projects.
 *
 * Created by juancavallotti on 04/01/14.
 */
class StudioPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        //the studio plugin also adds the mule nature.
        project.apply(plugin: 'mule')

        //apply the base plugin and then customize.
        project.apply(plugin: 'eclipse')

        //initialize the studio dependencies.
        //TODO - this might be better if delayed until last moment for better flexibility
        StudioDependencies deps = new StudioDependencies()

        //add the compile dependencies
        project.dependencies {
            compile(deps.listCompileDeps(project))
        }

        //TODO - Add specific studio customizations
    }
}
