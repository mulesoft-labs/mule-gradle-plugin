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

mule.version = '3.5.0'

```

How to build your app:

    $ gradle build

This plugin also adds two dependency scopes to gradle that are important for container-based type of projects:

  - providedCompile: similar to the `provided` scope in maven.
  - providedRuntime: Libraries that are available on the runtime itself.
  
New features will be added in the future.

Enterprise Features
----

By default gradle projects are enterprise-enabled, this is controlled by `mule.muleEnterprise` project configuration.

Mulesoft's enterprise artifacts are deployed in a password-protected nexus instance. The plugin allows you to specify the
 credentials so gradle can resolve these dependencies.
 
In order to use this repository, an enterprise user can specify these credentials in the build script using the following
 settings:
 
```groovy

mule.enterpriseRepoUsername = 'your-username'
mule.enterpriseRepoPassword = 'your-password'

```
Currently, externalization of these credentials is left to the user's preferred method.

This repo will not be enabled if `mule.muleEnterprise` is set to false.

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

Fine-grained Control over Mule Components
----

By default, the gradle plugin has configured a fair number of mule components that should satisfy the majority of standard
build apps, nevertheless this can be tuned at will i.e to remove modules older versions of mule didn't have or to add
newer modules unknown at the time of building this plugin. Finally, this is useful to troubleshoot build issues, that may
happen upon unpublished artifacts or restrictive company policies.

NOTE: Newer modules can be added as well as standard dependencies but this method offers a less-verbose approach.

To verify which mule components are installed, just run

    $ gradle muleDeps

Now let's see an example on how to remove the deprecated jdbc transport from the standard build and add the new DB module.

```groovy
mule.components {

    //exclude jdbc transport, deprecated in 3.5.0
    transports -= 'jdbc'
   
    //include DB module.
    modules += 'db'
}
```
Special Features
----

Many apps can be run and tested directly by executing:

    $ gradle runApp

The build can be configured to deploy the resulting artifact on a mule standalone server:

    mule.installPath = '/path/to/mule/home'

Alternatively it can be configured through the MULE_HOME environment variable. Finally to deploy:

    $ gradle install

Mule Embedded in Java Apps
----

This plugin provides a way for adding mule modules to embedded java apps, this is particularly useful when embedding
mule integrations inside a Java App. In order to add mule dependencies automatically to the compile configuration:

```groovy

buildscript {
	dependencies {
		classpath group: 'org.mulesoft.build', name: 'mule-gradle-plugin', version: '1.0.0-SNAPSHOT'
	}

	repositories {
		mavenLocal()
	}
}

apply plugin: 'java'
apply plugin: 'mule-dependencies' 

```