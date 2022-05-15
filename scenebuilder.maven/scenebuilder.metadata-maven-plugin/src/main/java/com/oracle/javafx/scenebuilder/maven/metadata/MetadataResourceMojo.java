package com.oracle.javafx.scenebuilder.maven.metadata;

import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import com.oracle.javafx.scenebuilder.maven.metadata.data.ConstructorOverride;
import com.oracle.javafx.scenebuilder.metadata.bean.BeanMetaData;
import com.oracle.javafx.scenebuilder.metadata.bean.BundleValues;
import com.oracle.javafx.scenebuilder.metadata.bean.PropertyMetaData;
import com.oracle.javafx.scenebuilder.metadata.finder.ClassCrawler;
import com.oracle.javafx.scenebuilder.metadata.finder.JarFinder;
import com.oracle.javafx.scenebuilder.metadata.finder.SearchContext;
import com.oracle.javafx.scenebuilder.metadata.model.Descriptor;
import com.oracle.javafx.scenebuilder.metadata.util.Report;
import com.oracle.javafx.scenebuilder.metadata.util.Resources;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

@Mojo(name = "metadataResource", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class MetadataResourceMojo extends AbstractMojo {

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

    @Parameter(property = "constructorOverrides", required = false)
    List<ConstructorOverride> constructorOverrides;

    @Parameter(property = "failOnError", required = false, defaultValue = "true")
    boolean failOnError = true;

    @Parameter(property = "javafxVersion", required = true)
    String javafxVersion;

    /**
     * Default constructor.
     */
    public MetadataResourceMojo() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException {
        initJFX(javafxVersion);

        PluginDescriptor pluginDescriptor = (PluginDescriptor)getPluginContext().get("pluginDescriptor");
        List<File> cp = pluginDescriptor.getArtifacts().stream().map(a -> a.getFile()).collect(Collectors.toList());

        try {

            final SearchContext searchContext = new SearchContext();
            prepareParameters(searchContext);

            List<Descriptor> descriptors = new ArrayList<>();
            Set<Path> jars = JarFinder.listJarsInClasspath(cp, searchContext.getJarFilterPatterns(), descriptors);
            ClassCrawler crawler = new ClassCrawler();

            final CompletableFuture<Map<Class<?>, BeanMetaData<?>>> returnValue = new CompletableFuture<>();

            Runnable runnable = () -> {
                crawler.crawl(jars, searchContext);
                returnValue.complete(crawler.getClasses());
            };

            Platform.runLater(runnable);

            Map<Class<?>, BeanMetaData<?>> found = returnValue.get();

            for (BeanMetaData<?> bm:found.values()) {
                for (PropertyMetaData pm:bm.getProperties()) {
                    if (!pm.isHidden() && pm.isLocal()) {
                        defaultValueTo(bm, pm, BundleValues.METACLASS, "TOBEDEFINED");
                        defaultValueTo(bm, pm, BundleValues.ORDER, "TOBEDEFINED");
                    }
                }
            }
            Resources.save(found.keySet(), resourceFolder);

            if (Report.flush(getLog().isDebugEnabled()) && failOnError) {
                throw new MojoExecutionException(
                        "Some errors occured during the generation process, please see the logs!");
            }
        } catch (Exception e) {
            getLog().error("Failed to complete the generating process! " + e.getMessage(), e);
            throw new MojoExecutionException("Failed to complete the generating process!", e);
        }

    }

    private void defaultValueTo(BeanMetaData<?> bm, PropertyMetaData pm, String property, String defaultValue) {
        if (pm.getBundleValue(bm.getType(), property, null) == null) {
            pm.setBundleValue(bm.getType(), property, defaultValue);
        }
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

        for (String s:includePackages) {
            searchContext.addIncludedPackage(s);
        }

        for (String s:excludePackages) {
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

        if (resourceFolder != null && !resourceFolder.exists()) {
            resourceFolder.mkdirs();
        }
        searchContext.setResourceFolder(resourceFolder);

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
