[Please leave your feedback](http://goo.gl/forms/1JW51iuaQi) by completing this survey!
======================

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](http://doctoc.herokuapp.com/)*

- [Gradle Plugin for Building Mule Apps](#gradle-plugin-for-building-mule-apps)
  - [Install the plugin locally](#install-the-plugin-locally)
  - [Enabling your project to build through the Command Line](#enabling-your-project-to-build-through-the-command-line)
  - [Start a new Project](#start-a-new-project)
  - [Enterprise Features](#enterprise-features)
    - [Uploading your apps to the Management Console](#uploading-your-apps-to-the-management-console)
    - [Uploading your apps to Cloudhub](#uploading-your-apps-to-cloudhub)
  - [Working with MuleStudio](#working-with-mulestudio)
  - [Fine-grained Control over Mule Components](#fine-grained-control-over-mule-components)
  - [Special Features](#special-features)
  - [Mule Embedded in Java Apps](#mule-embedded-in-java-apps)
  - [Mule Domain Apps](#mule-domain-apps)
    - [Definition Mechanics](#definition-mechanics)
    - [Restrictions in the configuration](#restrictions-in-the-configuration)
    - [Shared Libraries in Domains](#shared-libraries-in-domains)
    - [Domain Goodies](#domain-goodies)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

Gradle Plugin for Building Mule Apps
==================

[![Build Status](https://travis-ci.org/mulesoft-labs/mule-gradle-plugin.svg)](https://travis-ci.org/mulesoft-labs/mule-gradle-plugin)

This plugin allows the user to build mule applications with the gradle build system.

**Important Note: Starting from published version 1.1.0, the plugin supports both gradle 1.x and 2.x.**

Install the plugin locally
----

**Optional:** The plugin needs to reside in some place that gradle is able to find. We will use the maven local repository as most of
the Mule artifacts are hosted in a maven repository.

Build the plugin and install it to your local maven repository:

    $ gradle clean publishToMavenLocal

**Note:** Since 1.0.0 the plugin has been published into MuleSoft's community maven repository, so you can simply 
proceed to the next step.  

Enabling your project to build through the Command Line
----

Add into an existing project's root a `build.gradle` file with the following contents:

```groovy

buildscript {
	dependencies {
		classpath group: 'org.mulesoft.build', name: 'mule-gradle-plugin', version: '1.2.0'
	}

	repositories {
		maven {
		    url 'http://repository.mulesoft.org/releases'
		}
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

### Uploading your apps to the Management Console

This set of plugins ship as well with one for the Mule Management Console. This plugin currently allows to upload to the 
management console the resulting artifact and define several environments through a concise DSL.

In order to use this, your project needs to apply the `mmc` plugin:

```groovy
apply plugin: 'mmc'
```

After that simply define your MMC environments, with their url, username and passwords.

```groovy
mmc.environments {
    dev url: 'http://managmentConsole:8080/mmc', appName:'myApp', version:'1.0.2'
}
```

The DSL allows 5 configuration parameters.
 - The name of the method (in the previous example `dev` is the name of the environment, this is not restricted, you can
  use whatever name you'd like as a description for your env.
 - `url`: Is the url where the management console is deployed. This url needs to be without the  /api path.
 - `username` and `password`:  The credentials to log in to the MMC API.
 - `appName`: The name of the app in the repository, by default it will be the name of the project.
 - `version`: The version of the app in the repository, by default it will be the project's version.

A couple more examples to illustrate the DSL's usage:
 
```groovy
mmc.environments {
    dev url: 'http://managmentConsole:8080/mmc', appName:'myApp', version:'1.0.2'
    prod url: 'http://prodEnv:8080/mmc', username: 'admin', password: 'test'
    'pre-prod' url: 'http://preprodEnv:8080/mmc', username: 'admin', password: 'test'
    
    defaultEnvironment = 'prod'
}
```

NOTE: Use the mmc.defaultEnvironment property to control where the built app will be deployed.

Finally, to upload the app to the target repository, run the `uploadToRepository` task.

    $ gradle uploadToRepository


### Uploading your apps to Cloudhub

This set of plugins ship as well with one for Cloudhub. This plugin currently allows to deploy the resulting app to a 
cloudhub domain and and define several environments through a concise DSL.

In order to use this, your project needs to apply the `cloudhub` plugin:

```groovy
apply plugin: 'cloudhub'
```

After that simply define your Cloudhub environments, with their domain name, username and password.

```groovy
cloudhub.domains {
    myapp  username: 'login-username', password: 'pass'
}
```

In the dsl, the name of the method is the domain where to deploy your app. If your domain contains characters that are
not valid for method names, you can simply use a string as method name, the following examples illustrate further the 
configuration:

```groovy
cloudhub.domains {
    myapp  username: 'login-username', password: 'pass'
    'myapp-dev' username: 'dev-username', password: 'pass'
    
    defaultDomain = 'myapp-dev'
}
```
In order to perform the deployment, simply call the `deploy` task:

    $ gradle deploy
 
The build will succeed if the app is correctly uploaded. 
 
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
		classpath group: 'org.mulesoft.build', name: 'mule-gradle-plugin', version: '1.2.0'
	}

	repositories {
		mavenLocal()
	}
}

apply plugin: 'java'
apply plugin: 'mule-dependencies' 

```

Mule Domain Apps
----

Starting from Mule ESB 3.5.0, both community and enterprise received the 
['Shared resources'](http://www.mulesoft.org/documentation/display/current/Shared+Resources) feature, this entails a new
  way of packaging apps and a new file where shared resources are declared.

This process is analogous to a multi-module project with a root descriptor and therefore the approach taken by this plugin
is to take advantage of [Gradle's multi-project builds](http://www.gradle.org/docs/current/userguide/multi_project_builds.html).

So the typical project structure will be the following:

  * Project Root
    * /src/main/domain/mule-domain-config.xml: Here is where the shared resources are configured. 
    * /build.gradle: This will have a slightly different structure than regular projects.
    * /settings.gradle: This is where all the modules are declared.
    * /module 1
    * /module 2
    * ...
    * /module N: Modules have the same structure as regular projects.
    
### Definition Mechanics

In order to start a domain project, you can start with a `build.gradle` similar to the following:

```groovy
buildscript {
	dependencies {
		classpath group: 'org.mulesoft.build', name: 'mule-gradle-plugin', version: '1.2.0'
	}

	repositories {
		maven {
		    url 'http://repository.mulesoft.org/releases'
		}
	}
}

apply plugin: 'mule-domain'

mule.version = '3.5.0'
```

And the following `settings.gradle` to define the modules, in this example we have defined the `api` and `backend` modules
but you can define as many as you need.
 
```groovy
include 'api', 'backend'
```

Finally, we can run the `initDomain` task, this will perform several actions for us:
 
  * Create a directory for each module.
  * Run `initMuleProject` task on each created directory.
  * Create an initial `mule-domain-config.xml` with a shared example HTTP Connector.

Once this process is done, the domain is ready to be packaged and deployed.  

### Restrictions in the configuration
  
While we try to keep configuration of the different modules as flexible as possible, there are some restrictions that
  come from the same nature of a domain:
  
  * The config parameter `mule.version` is shared and configured at the *domain project level*. The reason behind this 
  restriction is that the domain with its modules will run necessarily all in the same Mule instance.
  * The config parameter `mule.muleEnterprise` is also shared since there is no way a Mule container can be both CE and 
  EE at the same time.
  * The config parameters `mule.enterpriseRepoUsername` and `mule.enterpriseRepoPassword` are shared, this is just 
  for usability.
  
  
### Shared Libraries in Domains

Mule Domains allow us to share libraries between modules, in order to configure shared libraries, simply add dependencies
in the `compile` scope.

NOTE: A domain project is more limited than a normal mule project, no classes or unit tests are allowed, so for this reason
only two dependency scopes are defined:

  * `compile`: Use this to include shared jars
  * `providedCompile`: Use this to include any jar present in the runtime but needed in your IDE.
  
All mule dependencies present in a non-domain project are also present in the `providedCompile` scope, this is like this
so IDE's can find the XSDs shipped in the Jars that are used when editing the domain config file.

The following example shows how to add a library:

```groovy
//dependencies that are shared between the modules, installed in the domain's lib dir.
dependencies {
    compile group: 'org.apache.activemq', name: 'activemq-all', version: '5.9.1'
}
```

### Domain Goodies

  * `install` task is now 'Domain-aware' and can be used to deploy a domain with its submodules directly.
  * The domain plugin has the ability to check if all modules are correctly configured, just run the `checkDomain` task.
  * If some module is not correctly configured for the domain, the `fixDomain` task allows you to fix the module settings.
