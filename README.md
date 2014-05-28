<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](http://doctoc.herokuapp.com/)*

- [Gradle Plugin for Building Mule Apps](#gradle-plugin-for-building-mule-apps)
  - [Install the plugin locally](#install-the-plugin-locally)
  - [Enabling your project to build through the Command Line](#enabling-your-project-to-build-through-the-command-line)
  - [Start a new Project](#start-a-new-project)
  - [Enterprise Features](#enterprise-features)
  - [Working with MuleStudio](#working-with-mulestudio)
  - [Fine-grained Control over Mule Components](#fine-grained-control-over-mule-components)
  - [Special Features](#special-features)
  - [Mule Embedded in Java Apps](#mule-embedded-in-java-apps)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

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

Start a new Project
----

If you create an empty folder and inside an initial `build.gradle` like the one shown in 
[Enabling your project to build through the Command Line](#enabling-your-project-to-build-through-the-command-line), you
can create the full project structure automatically, you simply need to run the `initMuleProject` task and the plugin
 will create the necessary directories and files for you. After the task completes, you'll have a working example project
 suitable for running with `runApp`, deploying it on a standalone server or in CloudHub.

    $ gradle initMuleProject
    
You can as well easily convert it into a MuleStudio project, following the instructions discussed in 
[Working with MuleStudio](#working-with-mulestudio).

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

By default, this gradle plugin has configured a fair number of mule components that should satisfy the majority of standard
app builds, nevertheless this can be tuned at will i.e. to remove modules that older versions of mule didn't have or to add
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
Mule provides many pluggable components, mainly in the form of external modules and cloud connectors. These modules can
 be built either with mule's devkit or created manually. The mule plugin extension adds DSL to easily add these elements
 to the build through 3 main DSL calls:
 
- The method `plugin(group: , name:, version:, noClassifier:, noExt:)` is the main call of specifying these external 
 plugins.
- The method `connector(name:, version:, noClassifier:, noExt:)` is a shortcut that includes the de-facto group for most
 cloud connectors.
- The method `module(name:, version:, noClassifier:, noExt:)` is a shortcut that includes the de-facto group for most
 devkit (and non-devkit) modules.
 
The arguments for the three methods except `noClassifier` and `noExt` are equal in meaning to the ones used when 
 specifying dependencies, the remaining are used for:
 
- `noClassifier`: This is a `boolean` value indicating that we don't want to use the 'plugin' classifier when including
 the dependency.
- `noExt`: This is a `boolean` value indicating that we we don't want to use a 'zip' plugin in our distribution but to
use the default 'jar' with dependencies approach. This maximizes the compatibility with IDE's including Mule Studio 
or IntelliJ with the downside of adding unwanted jars into our build.

Now let's see an example of how this DSL would look like:

```groovy
mule.components {
    
    //add a cloud connector
    connector name: 'mule-module-cors', version: '1.1'

    //add an external module
    module name: 'mule-module-apikit-plugin', version: '1.3', noClassifier: true
}
```

NOTE: When adding dependencies through this method, the final place where these will be on the archive depends on whether
 the component is packaged as a jar library or an app plugin, in the first case, the jar and its dependencies will end
 up in the archive's `lib/` directory and in the second case, the zip will end in the archive's `plugins/` directory.  

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