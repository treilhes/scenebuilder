---
title: "Maven Build"
permalink: /devguide/maven-build/
excerpt: "Notes about the maven build"
last_modified_at: 2021-06-07T08:48:05-04:00
toc: true
---

## Maven Builds

### Project build

Building the project is pretty straightforward, a simple `mvn clean install` will build the project.

**Note:** In eclipse, the "scenebuilder.extension.archetype" project integration tests does not execute successfully if you're using the EMBEDDED maven. 
**Note:** In eclipse, the "scenebuilder.metadata.javafxdata" project forks a maven build and does not execute successfully if you're using the EMBEDDED maven. 
You need to use a custom install for the build to be successfull instead of the EMBEDDED maven setup.

### Javadoc build

 To generate the project aggregated javadoc execute `clean javadoc:aggregate -P javadoc`
 It will generate the up to date javadoc in "/docs/apidocs"