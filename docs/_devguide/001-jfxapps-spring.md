---
title: "Spring"
permalink: /userguide/jfxapps-spring/
excerpt: "Spring Framework"
last_modified_at: 2021-06-07T08:48:05-04:00
toc: true
---

## Overview

JfxApps use Spring as the underlying framework

### AutoConfiguration

By default autoConfiguration is disabled


### Bean naming

By default Spring uses bean class's simple name as bean name which is a valid choice when you own all the classes in your project.

JfxApps is focused on allowing extension at all levels so naming collisions is highly likely to occur.
To prevent unwanted collisions JfxApps use a different BeanNameGenerator as default.
The JfxAppsBeanNameGenerator uses bean class's fully qualified name as bean name.