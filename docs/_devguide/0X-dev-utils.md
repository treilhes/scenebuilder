---
title: "The dev.utils project"
permalink: /devguide/dev-utils/
excerpt: "Notes about the dev.utils project tools"
last_modified_at: 2021-06-07T08:48:05-04:00
toc: true
---

## The dev.utils project

The *dev.utils* project contains some tools to manage and validate the scenebuilder project

- com.oracle.javafx.scenebuilder.devutils.jfxconfig.OpenJavaFxToAllUnnamed
- com.oracle.javafx.scenebuilder.devutils.strchk.StringCheckerApp

### com.oracle.javafx.scenebuilder.devutils.jfxconfig.OpenJavaFxToAllUnnamed

When launched, it will generate/update the file "/dev.utils/module.config" with all the JPMS directive needed to open all the packages of the JavaFx modules to the UNNAMED module. The file can then be used in any launchers;

### com.oracle.javafx.scenebuilder.devutils.strchk.StringCheckerApp

The StringCheckerApp will search for all String occurences in all projects and then will:

- ignore it if the line ends with *//NOCHECK* or *//NOCHECK* (.java files only)
- if it is a path/filename it will check if it exists at the right place in the same project resource folder or mark an error and will try to find it in other projects
- if it is an i18n key  it will check if it exists in the i18n file of the module or mark an error and will try to find it in other projects