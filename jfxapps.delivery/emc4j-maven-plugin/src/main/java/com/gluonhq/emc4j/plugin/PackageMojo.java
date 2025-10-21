/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.emc4j.plugin;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map.Entry;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import com.gluonhq.emc4j.plugin.javaconfig.JavaProcessConfig;

@Mojo(name = "package", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class PackageMojo extends Emc4jAbstractMojo {

//    @Component
//    private MavenProject project;

    @Component
    private BuildPluginManager pluginManager;

//    /**
//     * The current repository/network configuration of Maven.
//     */
//    @Parameter(defaultValue = "${repositorySystemSession}")
//    private RepositorySystemSession repositorySession;

    /**
     * The project's remote repositories to use for the resolution of project
     * dependencies.
     */
    @Parameter(defaultValue = "${project.remoteProjectRepositories}")
    private List<RemoteRepository> projectRepositories;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        /*
         <groupId>com.github.akman</groupId>
                <artifactId>jpackage-maven-plugin</artifactId>
                <version>0.1.5</version>
                <executions>
                    <execution>
                        <phase>verify</phase>
                        <goals>
                            <goal>jpackage</goal>
                        </goals>
                        <configuration>
                            <toolhome>${java.home}</toolhome>
                            <appversion>${app.version}</appversion>
                            <input>${project.build.directory}/application</input>
                            <licensefile>${basedir}/src/assets/LICENSE.txt</licensefile>
                            <module>${main.module.name}/${main.class.name}</module>
                            <name>${app.name}</name>
                            <description>${app.description}</description>
                            <vendor>${app.vendor}</vendor>
                            <verbose>true</verbose>
                            <runtimeimage>${project.build.directory}/runtime</runtimeimage>
                            <dest>${project.build.directory}/package</dest>
                            <!-- <type>PLATFORM</type> -->
                            <javaoptions>-Djavafx.allowjs=true</javaoptions>
                            <javaoptions>--add-opens=javafx.fxml/javafx.fxml=ALL-UNNAMED</javaoptions>
                            <javaoptions>--enable-native-access=javafx.graphics</javaoptions>
                            <javaoptions>--sun-misc-unsafe-memory-access=allow</javaoptions>
                            <javaoptions>-Djava.library.path=runtime\bin;runtime\lib</javaoptions>
                            <icon>${basedir}/src/assets/windows/icon-windows.ico</icon>

                            <!-- Windows -->
                            <windirchooser>true</windirchooser>
                            <winmenu>true</winmenu>
                            <winmenugroup>Scene Builder</winmenugroup>
                            <winperuserinstall>true</winperuserinstall>
                            <winshortcut>true</winshortcut>

                        </configuration>
                    </execution>
                </executions>
         */
        try {
            cleanOutputDirectory();

            var jcfg = initializeJavaProcessConfig();

            File destinationDir = new File(getOutputDirectory(), "generated");
            File appImageDir = new File(destinationDir, "app-image");
            File packagedDir = new File(destinationDir, "packaged");
            File runtimeDir = prepareRuntime();
            File inputDir = prepareInput(jcfg);


            String toolHome = System.getProperty("java.home").replace("\\", "/");
            String runtimeimage = runtimeDir.getAbsolutePath().replace("\\", "/");
            String appImageDest = appImageDir.getAbsolutePath().replace("\\", "/");
            String packagedDest = packagedDir.getAbsolutePath().replace("\\", "/");
            String input = inputDir.getAbsolutePath().replace("\\", "/");

            String appversion = "1.0.0";// "${app.version}";
            String licensefile = "${basedir}/src/assets/LICENSE.txt";
            String name = "FAKEAppName"; //"${app.name}";
            String description = "description";// "${app.description}";
            String vendor = "Someone"; // "${app.vendor}";

            //String mainjar = "$APPDIR/" + jcfg.getMainJar().getName();
            String mainjar = jcfg.getMainJar().getName();//new File(inputDir, jcfg.getMainJar().getName()).getAbsolutePath().replace("\\", "/");
            String mainclass = jcfg.getMainClass();


            Boolean verbose = Boolean.TRUE;
            String icon = "${basedir}/src/assets/windows/icon-windows.ico";
            Boolean windirchooser = Boolean.TRUE;
            Boolean winmenu = Boolean.TRUE;
            String winmenugroup = "Scene Builder Xxxxxxxxxxxx";
            Boolean winperuserinstall = Boolean.TRUE;
            Boolean winshortcut = Boolean.TRUE;

//            String javaOptionsFormat = """
//                -Djavafx.allowjs=true \
//                -Djava.library.path=runtime/bin;runtime/lib \
//                --add-opens=javafx.fxml/javafx.fxml=ALL-UNNAMED \
//                --enable-native-access=javafx.graphics \
//                --sun-misc-unsafe-memory-access=allow \
//                --module-path "%s" \
//                --class-path "%s" \
//                -m %s""";
//
//            String modPathArgValue = FsUtil.toPathesString(jcfg.getModules(), jcfg.getAutomaticModules());
//            String clsPathArgValue = FsUtil.toPathesString(jcfg.getClasspath());
//            String mStartArgValue = jcfg.getMainModule() + "/" + jcfg.getMainClass();
//
//            String javaOptions = String.format(javaOptionsFormat, modPathArgValue, clsPathArgValue, mStartArgValue);
            String javaOptions = "@$APPDIR\\app\\options.config";

            /*
                <name>launcher1</name>
                <file>config/jpackage/launcher1.properties</file>
                <module>mainModule1Name/mainClass1Name</module>
                <mainjar>mainJar1.jar</mainjar>
                <mainclass>mainClass1Name</mainclass>
                <arguments>--arg11 --arg12</arguments>
                <javaoptions>-Xms128m -Xmx1024m</javaoptions>
                <appversion>1.0.1</appversion>
                <icon>config/jpackage/launcher1.ico</icon>
                <winconsole>true</winconsole>
             */

            Element[] debugLauncher = new Element[] {
                    // app info
                    element(name("appversion"), appversion),
                    element(name("name"), "DebgLaunch"),

                    // launch info
                    element(name("mainjar"), mainjar),
                    element(name("mainclass"), mainclass),

                    element(name("javaoptions"), javaOptions),

                    element(name("winconsole"), "true")
            };

            Xpp3Dom configuration = configuration(
                    // folder structure
                    element(name("toolhome"), toolHome),
                    element(name("runtimeimage"), runtimeimage),
                    element(name("dest"), appImageDest),
                    element(name("input"), input),

                    // app info
                    element(name("appversion"), appversion),
                    //element(name("licensefile"), licensefile),
                    element(name("name"), name),
                    element(name("description"), description),
                    element(name("vendor"), vendor),

                    // launch info
                    element(name("mainjar"), mainjar),
                    element(name("mainclass"), mainclass),

                    element(name("javaoptions"), javaOptions),

                    element(name("type"), "IMAGE"),
                    element(name("verbose"), verbose.toString()),
                    element(name("addlaunchers"),
                            element(name("addlauncher"), debugLauncher))


                    // Common values
                    //element(name("icon"), icon),

                    // Windows specifics
                    //element(name("windirchooser"), windirchooser.toString()),
                    //element(name("winmenu"), winmenu.toString()),
                    //element(name("winmenugroup"), winmenugroup),
                    //element(name("winperuserinstall"), winperuserinstall.toString()),
                    //element(name("winshortcut"), winshortcut.toString())
                );


            //create app-image
            executeJpackage(configuration);


            File appImageFolder = new File(appImageDest, name);
            File appImageApp = new File(appImageFolder, "app");

            String appImageSrc = appImageFolder.getAbsolutePath().replace("\\", "/");

            generateConfigFile(jcfg, new File(appImageApp, "options.config"));
            generateJPackageConfig(jcfg, new File(appImageApp, "DebgLaunch.cfg"));
            generateJPackageConfig(jcfg, new File(appImageApp, name + ".cfg"));
            preparePackage(jcfg, appImageApp);

            Xpp3Dom packagingConfiguration = configuration(
                    // folder structure
                    element(name("toolhome"), toolHome),
                    element(name("appimage"), appImageSrc),
                    element(name("dest"), packagedDest),

                    // app info
                    //element(name("appversion"), appversion),
                    //element(name("licensefile"), licensefile),
                    //element(name("name"), name),
                    //element(name("description"), description),
                    //element(name("vendor"), vendor),

                    // Common values
                    //element(name("icon"), icon),

                    // Windows specifics
                    element(name("windirchooser"), windirchooser.toString()),
                    element(name("winmenu"), winmenu.toString()),
                    element(name("winmenugroup"), winmenugroup),
                    element(name("winperuserinstall"), winperuserinstall.toString()),
                    element(name("winshortcut"), winshortcut.toString())
                );

            //create installer
            executeJpackage(packagingConfiguration);

        } catch (Exception e) {
            throw new MojoExecutionException("Error executing jpackage goal", e);
        }
        System.out.println("PackageMojo executed");
    }

    private void executeJpackage(Xpp3Dom configuration) throws MojoExecutionException {
        executeMojo(
                plugin(
                    groupId("com.github.akman"),
                    artifactId("jpackage-maven-plugin"),
                    version("0.1.5")
                ),
                goal("jpackage"),
                configuration,
                executionEnvironment(
                    getProject(),
                    mavenSession,
                    pluginManager
                )
        );
    }

    private File prepareInput(JavaProcessConfig jcfg) throws Exception {

        File inputDir = new File(getOutputDirectory(), "input");

        inputDir.mkdirs();

        File mainDir = inputDir;// new File(inputDir, "libs");
        mainDir.mkdirs();
        Files.copy(jcfg.getMainJar().toPath(), mainDir.toPath().resolve(jcfg.getMainJar().getName()));

        return inputDir;
    }

    private void preparePackage(JavaProcessConfig jcfg, File generatedAppFolder) throws Exception {

        File modulesDir = new File(generatedAppFolder, "mp");
        modulesDir.mkdirs();

        for (File module:jcfg.getModules()) {
            Files.copy(module.toPath(), modulesDir.toPath().resolve(module.getName()));
        }
        for (File automod:jcfg.getAutomaticModules()) {
            Files.copy(automod.toPath(), modulesDir.toPath().resolve(automod.getName()));
        }

        File cpDir = new File(generatedAppFolder, "cp");
        cpDir.mkdirs();

        for (File cpjar:jcfg.getClasspath()) {
            Files.copy(cpjar.toPath(), cpDir.toPath().resolve(cpjar.getName()));
        }

        File patchDir = new File(generatedAppFolder, "patch");
        patchDir.mkdirs();
        for (Entry<String, List<File>> patch : jcfg.getPatchModules().entrySet()) {
            for (File f : patch.getValue()) {
                Files.copy(f.toPath(), patchDir.toPath().resolve(f.getName()));
            }
        }
    }

    private File prepareRuntime() throws Exception {

        var factory = getPluginArtifactFactory();
        var runtimePluginArtifact = factory.createArtifact(DELIVERY_RUNTIME_GROUP_ID, DELIVERY_RUNTIME_ARTIFACT_ID,
                getEmc4jVersion(), DELIVERY_RUNTIME_CLASSIFIER);

        Artifact runtimeArtifact = runtimePluginArtifact.resolve();

        File runtimeDir = new File(getOutputDirectory(), "runtime");
        runtimeDir.mkdirs();

        unjar(runtimeArtifact.getFile(), runtimeDir);

        return runtimeDir;
    }

    private void unjar(File source, File target) throws IOException {
        Path jarPath = source.toPath();
        Path outputDir = target.toPath();

        try (FileSystem fs = FileSystems.newFileSystem(jarPath)) {
            for (Path root : fs.getRootDirectories()) {
                Files.walk(root).filter(Files::isRegularFile).forEach(p -> {
                    try {
                        Path dest = outputDir.resolve(root.relativize(p).toString());
                        Files.createDirectories(dest.getParent());
                        Files.copy(p, dest, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    private void generateJPackageConfig(JavaProcessConfig jcfg, File target) throws IOException {
        /*
         [Application]
        app.classpath=$APPDIR\cp\*
        app.modulepath=$APPDIR\mp
        app.mainmodule=jfxapps.boot.main/com.gluonhq.jfxapps.boot.main.Main

        [JavaOptions]
        java-options=-Djpackage.app-version=1.0.0

         */
        StringBuilder sb = new StringBuilder();
        String ls = System.lineSeparator();
        sb.append("[Application]").append(ls);
        if (!jcfg.getClasspath().isEmpty()) {
            sb.append("app.classpath=$APPDIR\\cp\\*").append(ls);
        }
        if (!jcfg.getModules().isEmpty() || !jcfg.getAutomaticModules().isEmpty()) {
            sb.append("app.modulepath=$APPDIR\\mp").append(ls);
        }
        if (jcfg.getMainModule() != null) {
            sb.append("app.mainmodule=").append(jcfg.getMainModule()).append("/").append(jcfg.getMainClass())
                    .append(ls);
        }
        sb.append(ls);
        sb.append("[JavaOptions]").append(ls);

        for (String jvmArg : jcfg.getJvmArgs()) {
            sb.append("java-options=").append(jvmArg).append(ls);
        }

        for (String addRead : jcfg.getAddReads()) {
            sb.append("java-options=--add-reads").append("\n").append("java-options=").append(addRead).append(ls);
        }
        for (String addOpen : jcfg.getAddOpens()) {
            sb.append("java-options=--add-opens").append("\n").append("java-options=").append(addOpen).append(ls);
        }
        for (String addExport : jcfg.getAddExports()) {
            sb.append("java-options=--add-exports").append("\n").append("java-options=").append(addExport).append(ls);
        }

        String patchFormat = "%s=%s/%s";
        for (Entry<String, List<File>> patch : jcfg.getPatchModules().entrySet()) {
            for (File f : patch.getValue()) {
                sb.append("java-options=--patch-module").append("\n");
                sb.append("java-options=")
                        .append(String.format(patchFormat, patch.getKey(), "$APPDIR\\patch", f.getName())).append("\n");
            }
        }
        Files.writeString(target.toPath(), sb.toString());
    }

}