package com.mulesoft.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
/**
 * Created by juancavallotti on 02/01/14.
 */
class MulePlugin implements Plugin<Project> {
    void apply(Project project) {

        //apply the java plugin.
        project.apply(plugin: 'java')

        //add the mule extension.
        project.extensions.create("mule", MulePluginExtension)

        MulePluginExtension mule = project.mule

        //add providedCompile and providedRuntime for dependency management.
        //this is needed because we'll be generating a container - based archive.
        project.configurations {

            providedCompile {
                description = "Compile time dependencies that should not be part of the final zip file."
                visible = false
            }

            providedRuntime {
                description = "Runtime dependencies that should not be part of the final zip file."
                visible = false
                extendsFrom providedCompile
            }

            compile {
                extendsFrom providedCompile
            }

            runtime {
                extendsFrom providedRuntime
            }
        }


        //get the mule version.
        project.dependencies {
            providedCompile (
                    [group: 'org.mule', name: 'mule-core', version: mule.version],
                    [group: 'com.mulesoft.muleesb.modules', name: 'mule-module-boot-ee', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-spring-config', version: mule.version],
                    [group: 'com.mulesoft.muleesb', name: 'mule-core-ee', version: mule.version],
                    [group: 'com.mulesoft.muleesb.modules', name: 'mule-module-data-mapper', version: mule.version],
                    [group: 'com.mulesoft.muleesb.modules', name: 'mule-module-spring-config-ee', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-file', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-http', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-jdbc', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-jms', version: mule.version],
                    [group: 'org.mule.transports', name: 'mule-transport-vm', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-client', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-cxf', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-json', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-management', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-scripting', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-sxc', version: mule.version],
                    [group: 'org.mule.modules', name: 'mule-module-xml', version: mule.version]
            )
        }

        project.repositories {
            mavenLocal()

            mavenCentral()

            maven {
                url "https://repository-master.mulesoft.org/nexus/content/repositories/public/"
            }

            maven {
                url "https://repository.jboss.org/nexus/content/repositories/"
            }
        }

        //the packaging logic.
        def ziptask = project.getTasks().create("mulezip", MuleZip.class);

        //add the app directory yo the root of the zip file.
        ziptask.from {
            return "src/main/app"
        }

        //add the data-mapper mappings
        ziptask.from {
            return "mappings"
        }

        //add the APIKit specific files.
        ziptask.from {
            return "src/main/api"
        }

        ziptask.dependsOn {
            project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath()
        }

        ziptask.classpath {
            FileCollection runtimeClasspath = project.getConvention().getPlugin(JavaPluginConvention.class)
                    .getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath()
            Configuration providedRuntime = project.getConfigurations().getByName(
                    'providedRuntime');
            runtimeClasspath.minus(providedRuntime);
        }

        ziptask.setDescription("Generate the MuleApp deployable zip archive")
        ziptask.setGroup(BasePlugin.BUILD_GROUP)
        ArchivePublishArtifact zipArtifact = new ArchivePublishArtifact(ziptask)
        project.getExtensions().getByType(DefaultArtifactPublicationSet.class).addCandidate(zipArtifact)
    }
}