---
title: "IDE Integration"
permalink: /devguide/ide-integration/
excerpt: "Notes about the IDE integrations"
last_modified_at: 2021-06-07T08:48:05-04:00
toc: true
---

## IDE Integrations

### Maven

#### javafx.platform

To be able to build properly the javafx.platform variable must be set to `win`|`mac`|`linux`<br/>

The prefered way to do this is to add the following into the maven `settings.xml` file<br/>

```xml
<properties>
    <javafx.platform>win</javafx.platform>
</properties>
```

### Eclipse

Eclipse integration is rather good but m2e still lacks some features about JPMS for a perfect integration

Maven compiler arguments like `patch-module` are not synchronized with the eclipse project classpath.<br/>

In order to work properly spring.core was patched to allow aspect to properly function in a multi ModuleLayer architecture. Those patch aren't correctly handled in Eclipse and prevent successfull compilation.

To run the application from inside Eclipse projects "jfxapps.boot.jpms.patches" and nested projects must be removed from the workspace.

#### Provided launchers 

Some Eclipse launchers are provided in "dev.utils\ide\eclipse\launchers"

### IDEA

fill it if you're using it

### Netbeans

fill it if you're using it
