Gradle Plugin for Building Mule Apps
==================

This plugin allows the user to build mule applications with the gradle build system.

How to use
----

Build the plugin and install it to your local maven repository:

   $ gradle gradle clean publishToMavenLocal

After this, add into an existing project's root a `build.gradle` file with the following contents:

```groovy

buildscript {
	dependencies {
		classpath group: 'com.mulesoft.build', name: 'mule-gradle-plugin', version: '1.0.0'
	}

	repositories {
		mavenLocal()
	}
}

apply plugin: 'mule'

mule.version = '3.5.0-bighorn'

```

This plugin also adds two dependency scopes to gradle that are important for container-based type of projects:

  - providedCompile: similar to the `provided` scope in maven.
  - providedRuntime: Libraries that are available on the runtime itself.
  
New features will be added in the future.
