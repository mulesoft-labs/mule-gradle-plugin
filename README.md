Gradle Plugin for Building Mule Apps
==================

This plugin allows the user to build mule applications with the gradle build system.


Install the plugin locally
----

The plugin needs to reside in some place that gradle is able to find. We will use the maven local repository as most of
the Mule artifacts are hosted in a maven repository.

Build the plugin and install it to your local maven repository:

    $ gradle clean publishToMavenLocal


Enabling your project to build through the Command Line
----

Add into an existing project's root a `build.gradle` file with the following contents:

```groovy

buildscript {
	dependencies {
		classpath group: 'org.mulesoft.build', name: 'mule-gradle-plugin', version: '1.0.0-SNAPSHOT'
	}

	repositories {
		mavenLocal()
	}
}

apply plugin: 'mule'

mule.version = '3.5.0-bighorn'

```

How to build your app:

    $ gradle build

This plugin also adds two dependency scopes to gradle that are important for container-based type of projects:

  - providedCompile: similar to the `provided` scope in maven.
  - providedRuntime: Libraries that are available on the runtime itself.
  
New features will be added in the future.

Working with MuleStudio
----

The package itself contains as well a plugin to update MuleStudio's build path when adding dependencies.

To enable this support, edit your `build.gradle` script and change the following:

```groovy
apply plugin: 'mulestudio'
```

For the time being this combines the Eclipse Plugin and the Mule plugin, so, in order to update the studio classpath,
just run:

    $ gradle clean studio

In the future extra functionality will be added to polish the studio integration.

Special Features
----

Many apps can be run and tested directly by executing:

    $ gradle runApp

The build can be configured to deploy the resulting artifact on a mule standalone server:

    mule.installPath = /path/to/mule/home

Alternatively it can be configured through the MULE_HOME environment variable. Finally to deploy:

    $ gradle install

