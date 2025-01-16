---
title: "JPA"
permalink: /userguide/jfxapps-jpa/
excerpt: "JPA Java Persistence API"
last_modified_at: 2021-06-07T08:48:05-04:00
toc: true
---

## Overview

JfxApps offers JPA out of the box but:

- Entity scanning is forbiden. @EntityScan has been disabled
- Repository scanning is forbiden @EnableJpaRepositories(basePackages, basePackageClasses) has been disabled

Scanning is disabled because it cost a lot to boot (+- 250 ms per extension). Not realy a big deal for a web application but
an unbearable cost for a local application that need to boot the faster possible

All of your entities and repositories simply must be declared in your extension descriptor Extension#localContextClasses() or Extension#exportedContextClasses() to be loaded