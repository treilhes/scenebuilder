---
title: "IDE Integration"
permalink: /devguide/ide-integration/
excerpt: "Notes about the IDE integrations"
last_modified_at: 2021-06-07T08:48:05-04:00
toc: true
---

## IDE Integrations



### Eclipse

Eclipse integration is rather good but m2e still lacks some features about JPMS for a perfect integration

Maven compiler arguments like `add-opens,add-exports,patch-module` are not synchronized with the eclipse project classpath.<br/>
Until the following m2e-core [pull request](https://github.com/eclipse-m2e/m2e-core/pull/216) acceptance manual synchronization of the ".classpath" file is needed

sample modification:

```xml
<classpathentry kind="con" path="org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER">
	<attributes>
		<attribute name="maven.pomderived" value="true"/>
		<attribute name="add-exports" value="javafx.graphics/com.sun.javafx.geometry=scenebuilder.core.api:javafx.graphics/com.sun.javafx.scene=scenebuilder.core.api"/>
	</attributes>
</classpathentry>
```

#### Provided launchers 

Some Eclipse launchers are provided in "dev.utils\ide\eclipse\launchers"

### IDEA

fill it if you're using it

### Netbeans

fill it if you're using it
