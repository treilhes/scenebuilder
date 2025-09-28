# RunMojo - EMC4J Maven Plugin

## Overview

`RunMojo` is a Maven plugin goal for running modular Java applications using advanced JPMS (Java Platform Module System) configuration. It automates dependency resolution, JVM option generation, and application launch.

## Goals

- **run**: Launches the main application module with JPMS options and dependencies as defined in `boot-config.xml`.

## Usage

Add the plugin to your `pom.xml`:

```xml
<plugin>
    <groupId>com.gluonhq.jfxapps</groupId>
    <artifactId>emc4j-maven-plugin</artifactId>
    <version>${emc4j.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>run</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <emc4jVersion>1.0.0</emc4jVersion>
        <debug>true</debug>
        <debugPort>8000</debugPort>
        <debugSuspend>true</debugSuspend>
        <bootConfig>boot-config.xml</bootConfig>
        <outputDirectory>target/binaries</outputDirectory>
    </configuration>
</plugin>
```

## Parameters

- `emc4jVersion`: EMC4J artifacts version (required)
- `debug`: Enable JVM debugging (default: false)
- `debugPort`: Debugger port (default: 8000)
- `debugSuspend`: Suspend JVM until debugger attaches (default: true)
- `bootConfig`: Path to boot configuration XML (default: boot-config.xml)
- `outputDirectory`: Output directory for binaries/config (default: target/binaries)

## How It Works

1. Resolves main application JAR and configuration artifacts.
2. Loads `boot-config.xml` for JPMS options (patches, exports, opens, reads, etc.).
3. Generates a JVM options file (`boot.config`) with all required module/classpath settings.
4. Copies the main binary to the output directory.
5. Launches the application using the generated options and resolved dependencies.

## Example Command

After configuration, run:

```shell
mvn com.gluonhq.jfxapps:emc4j-maven-plugin:run
```

## Sample `boot-config.xml`

```xml
<boot-config>
    <debug>true</debug>
    <profile>dev</profile>
    <java-option>-Djava.util.PropertyResourceBundle.encoding=UTF-8</java-option>
    <java-option>-Dlogging.level.org.springframework.beans.factory.support=DEBUG</java-option>
    <java-option>-Dlogging.level.org.springframework.beans.factory=DEBUG</java-option>
    <java-option>-Dlogging.level.org.springframework.context.annotation=DEBUG</java-option>
    <patch-module module="java.base">
        <artifact groupId="com.gluonhq.jfxapps" artifactId="java.base.patch"/>
    </patch-module>
    <patch-module module="spring.core">
        <artifact groupId="com.gluonhq.jfxapps" artifactId="spring.core.patch"/>
    </patch-module>
    <patch-module module="org.hibernate.orm.core">
        <artifact groupId="com.gluonhq.jfxapps" artifactId="org.hibernate.orm.core.patch"/>
    </patch-module>
    <patch-module module="maven.model">
        <artifact groupId="org.apache.maven" artifactId="maven-model-builder"/>
    </patch-module>
    <add-exports module="java.base" package="javapatch.lang.module" to-module="java.base.patch.link"/>
    <add-reads module="spring.core" to-module="jfxapps.boot.layer"/>
    <add-opens module="java.base" package="java.lang" to-module="ALL-UNNAMED"/>
    <force-as-module groupId="org.apache.maven" artifactId="maven-resolver-provider"/>
    <force-as-module groupId="org.apache.maven" artifactId="maven-model"/>
    <exclude-dependency groupId="org.slf4j" artifactId="jcl-over-slf4j"/>
    <exclude-dependency groupId="org.apache.maven" artifactId="maven-model-builder"/>
</boot-config>
```

## Notes

- Supports advanced JPMS features: patching modules, exporting/opening packages, forcing JARs as modules.
- Debugging options can be enabled for remote debugging.
- All dependencies and module options are resolved/configured automatically.

## References

- [JPMS Documentation](https://openjdk.org/projects/jigsaw/)
- [Maven Plugin Development](https://maven.apache.org/guides/plugin/guide-java-plugin-development.html)
