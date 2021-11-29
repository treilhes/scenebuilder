package com.oracle.javafx.scenebuilder.maven.metadata;

import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.oracle.javafx.scenebuilder.maven.metadata.data.ConstructorOverride;
import com.oracle.javafx.scenebuilder.metadata.bean.BeanMetaData;
import com.oracle.javafx.scenebuilder.metadata.finder.ClassCrawler;
import com.oracle.javafx.scenebuilder.metadata.finder.JarFinder;
import com.oracle.javafx.scenebuilder.metadata.finder.SearchContext;
import com.oracle.javafx.scenebuilder.metadata.finder.api.Executor;
import com.oracle.javafx.scenebuilder.metadata.model.Component;
import com.oracle.javafx.scenebuilder.metadata.model.Descriptor;
import com.oracle.javafx.scenebuilder.metadata.model.MetadataFromJavafx;
import com.oracle.javafx.scenebuilder.metadata.model.Property;
import com.oracle.javafx.scenebuilder.metadata.util.Report;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

@Mojo(name = "metadataSource", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class MetadataSourceMojo extends AbstractMojo {

    /** The input root classes. */
    @Parameter(property = "rootClasses", required = true, alias = "rootClasses")
    List<String> rootClasses;

    /** The input file. */
    @Parameter(property = "excludeClasses", required = false, alias = "excludeClasses")
    List<String> excludeClasses;

    /** The input file. */
    @Parameter(property = "jarFilterPatterns", required = false, alias = "jarFilterPatterns")
    List<String> jarFilterPatterns;

    /** The input root classes. */
    @Parameter(property = "includePackages", required = false)
    List<String> includePackages;

    /** The input file. */
    @Parameter(property = "excludePackages", required = false)
    List<String> excludePackages;

    /** The backup file. */
    @Parameter(property = "resourceFolder", required = false, defaultValue = "${project.build.directory}/generated-resources/scenebuilder")
    File resourceFolder;

    @Parameter(property = "sourceFolder", required = false, defaultValue = "${project.build.directory}/generated-sources/scenebuilder")
    File sourceFolder;

    @Parameter(property = "constructorOverrides", required = false)
    List<ConstructorOverride> constructorOverrides;

    @Parameter(property = "targetPackage", required = true)
    String targetPackage;

    @Parameter(property = "moduleName", required = false, defaultValue = "${project.artifactId}")
    String moduleName;

    @Parameter(property = "uuid", required = true)
    String uuid;

    /** The input root classes. */
    @Parameter(property = "moduleRequires", required = false)
    List<String> moduleRequires;

    @Parameter(property = "executorClass", required = true)
    String executorClass;

    @Parameter(property = "extensionName", required = true)
    String extensionName;

    @Parameter(property = "failOnError", required = false, defaultValue = "true")
    boolean failOnError = true;

    @Parameter(property = "javafxVersion", required = true)
    String javafxVersion;

    @Parameter(property = "metadataPrefix", required = false, defaultValue = "")
    String metadataPrefix = "";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Default constructor.
     */
    public MetadataSourceMojo() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException {
        initJFX(javafxVersion);

        PluginDescriptor pluginDescriptor = (PluginDescriptor) getPluginContext().get("pluginDescriptor");
        List<File> cp = pluginDescriptor.getArtifacts().stream().map(a -> a.getFile()).collect(Collectors.toList());

        try {

            final SearchContext searchContext = new SearchContext();
            prepareParameters(searchContext);

            Executor executor = searchContext.getExecutorClass().getConstructor().newInstance();
            List<Descriptor> descriptors = new ArrayList<>();
            Set<Path> jars = JarFinder.listJarsInClasspath(cp, searchContext.getJarFilterPatterns(), descriptors);
            ClassCrawler crawler = new ClassCrawler();

            final CompletableFuture<Map<Class<?>, BeanMetaData<?>>> returnValue = new CompletableFuture<>();
            final CompletableFuture<Map<Class<?>, Component>> descriptorReturnValue = new CompletableFuture<>();

            executor.preExecute(searchContext);

            Runnable runnable = () -> {
                crawler.crawl(jars, searchContext);
                descriptorReturnValue.complete(descriptorLoad(descriptors));
                returnValue.complete(crawler.getClasses());
            };

            Platform.runLater(runnable);

            Map<Class<?>, BeanMetaData<?>> found = returnValue.get();
            Map<Class<?>, Component> othersFound = descriptorReturnValue.get();
            try {

                Map<Component, Set<Property>> components = MetadataFromJavafx.load(found, othersFound);

                executor.execute(searchContext, components, othersFound);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to execute Executor program", e);
            }

            // Resources.save(output);

            updateProject();

            if (Report.flush(getLog().isDebugEnabled()) && failOnError) {
                throw new MojoExecutionException(
                        "Some errors occured during the generation process, please see the logs!");
            }
        } catch (Exception e) {
            getLog().error("Failed to complete the generation process! " + e.getMessage(), e);
            Report.flush(getLog().isDebugEnabled());
            throw new MojoExecutionException("Failed to complete the generation process!", e);
        }

    }

    private Map<Class<?>, Component> descriptorLoad(List<Descriptor> descriptors) {

        Map<Class<?>, Component> result = new HashMap<>();

        if (descriptors != null && !descriptors.isEmpty()) {
            for (Descriptor extDescriptor : descriptors) {
                for (Entry<Class<?>, String> e : extDescriptor.getClassToMetaClass().entrySet()) {
                    Class<?> componentClass = e.getKey();
                    String metaClassName = e.getValue();
                    try {
                        Component c = new Component(new BeanMetaData<>(componentClass, null));
                        c.getCustom().put("className", metaClassName);
                        result.put(componentClass, c);
                    } catch (Exception ex) {
                    }
                }
            }
        }

        return result;
    }

    private void updateProject() {
        project.addCompileSourceRoot(sourceFolder.getPath());
        project.addCompileSourceRoot(resourceFolder.getPath());
    }

    private SearchContext prepareParameters(SearchContext searchContext) throws MojoExecutionException {

        for (String s : rootClasses) {
            try {
                Class<?> cls = Class.forName(s);
                searchContext.addRootClass(cls);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to load root class : " + s, e);
            }
        }

        for (String s : excludeClasses) {
            try {
                Class<?> cls = Class.forName(s);
                searchContext.addExcludeClass(cls);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to load excluded class : " + s, e);
            }
        }

        for (String s : jarFilterPatterns) {
            try {
                Pattern pattern = Pattern.compile(s);
                searchContext.addJarFilterPattern(pattern);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to compile jar filter pattern : " + s, e);
            }
        }

        for (String s : includePackages) {
            searchContext.addIncludedPackage(s);
        }

        for (String s : excludePackages) {
            searchContext.addExcludedPackage(s);
        }

        try {
            if (constructorOverrides != null) {
                for (ConstructorOverride cto : constructorOverrides) {
                    Class<?> cls = Class.forName(cto.getCls());
                    Class<?>[] originalParameters = cto.getParameterOverrides().stream().map(p -> {
                        try {
                            return Class.forName(p.getCls());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList()).toArray(new Class<?>[0]);

                    Class<?>[] newParameters = cto.getParameterOverrides().stream().map(p -> {
                        try {
                            return Class.forName(p.getOverridedBy());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList()).toArray(new Class<?>[0]);

                    Constructor<?> constructor = cls.getConstructor(originalParameters);

                    searchContext.addAltConstructor(constructor, newParameters);
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to override constructors", e);
        }

        try {
            searchContext.setExecutorClass((Class<Executor>) Class.forName(executorClass));
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to find executor class", e);
        }

        if (sourceFolder != null && !sourceFolder.exists()) {
            sourceFolder.mkdirs();
        }
        searchContext.setSourceFolder(sourceFolder);

        if (resourceFolder != null && !resourceFolder.exists()) {
            resourceFolder.mkdirs();
        }
        searchContext.setResourceFolder(resourceFolder);

        searchContext.setTargetPackage(targetPackage);

        searchContext.setModuleName(moduleName);

        for (String s : moduleRequires) {
            searchContext.addModuleRequire(s);
        }

        searchContext.setUuid(uuid);

        searchContext.setExtensionName(extensionName);

        searchContext.setMetadataPrefix(metadataPrefix);

        return searchContext;
    }

    public static void initJFX(String javafxVersion) {
        try {
            System.out.println("Initializing JavaFX thread");
            String fxv = System.getProperty("javafx.version", "versionless");
            System.out.println("JavaFX version : " + fxv);
            // System.setProperty("javafx.version", javafxVersion);
            System.setProperty("javafx.version", "versionless");
            fxv = System.getProperty("javafx.version", "versionless");
            System.out.println("JavaFX version : " + fxv);

            Thread t = newJfxThread();
            t.setDaemon(true);
            t.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static class DummyApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // noop
        }
    }

    private static Thread newJfxThread() {
        return new Thread("JavaFX Init Thread") {
            @Override
            public void run() {
                Application.launch(DummyApp.class, new String[0]);
            }
        };
    }
}
