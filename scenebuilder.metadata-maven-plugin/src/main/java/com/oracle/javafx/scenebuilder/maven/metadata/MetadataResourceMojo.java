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
    @Parameter(property = "rootClassesString", required = true, alias="rootClasses")
    List<String> rootClassesString;
    List<Class<?>> rootClasses = new ArrayList<>();

    /** The input file. */
    @Parameter(property = "excludeClassesString", required = false, alias = "excludeClasses")
    List<String> excludeClassesString;
    List<Class<?>> excludeClasses = new ArrayList<>();

    /** The input file. */
    @Parameter(property = "jarFilterPatternsString", required = false, alias = "jarFilterPatterns")
    List<String> jarFilterPatternsString;
    List<Pattern> jarFilterPatterns = new ArrayList<>();

    /** The input root classes. */
    @Parameter(property = "includePackages", required = false)
    List<String> includePackages;

    /** The input file. */
    @Parameter(property = "excludePackages", required = false)
    List<String> excludePackages;

    /** The backup file. */
    @Parameter(property = "output", required = false, defaultValue = "${project.build.directory}/generated-resources")
    File output;

    /** The backup file. */
    @Parameter(property = "input", required = false, defaultValue = "${basedir}/src/main/resources")
    File input;

//    @Parameter(defaultValue = "${project}", readonly = true, required = true)
//    private MavenProject project;

    @Parameter(property = "constructorOverrides", required = false)
    List<ConstructorOverride> constructorOverrides;

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
        initJFX();
        SearchContext searchContext = prepareParameters();

        PluginDescriptor pluginDescriptor = (PluginDescriptor)getPluginContext().get("pluginDescriptor");
        List<File> cp = pluginDescriptor.getArtifacts().stream()
            .map(a -> a.getFile())
            .collect(Collectors.toList());

        try {
            List<Descriptor> descriptors = new ArrayList<>();
            Set<Path> jars = JarFinder.listJarsInClasspath(cp, jarFilterPatterns, descriptors);
            ClassCrawler crawler = new ClassCrawler();

            final CompletableFuture<Map<Class<?>, BeanMetaData<?>>> returnValue = new CompletableFuture<>();

            Platform.runLater(() -> {
                crawler.crawl(jars, searchContext);
                returnValue.complete(crawler.getClasses());
            });

            Map<Class<?>, BeanMetaData<?>> found = returnValue.get();

            Resources.save(output);

            if (Report.flush(getLog().isDebugEnabled())) {
                throw new MojoExecutionException("Some errors occured during the generation process, please see the logs!");
            }
        } catch (Exception e) {
            getLog().error("Failed to complete the generating process! " + e.getMessage(), e);
            throw new MojoExecutionException("Failed to complete the generating process!", e);
        }

    }

    private SearchContext prepareParameters() throws MojoExecutionException {

        SearchContext searchContext = new SearchContext();

        for (String s:rootClassesString) {
            try {
                Class<?> cls = Class.forName(s);
                rootClasses.add(cls);
                searchContext.addRootClass(cls);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to load root class : " + s, e);
            }
        }

        for (String s:excludeClassesString) {
            try {
                Class<?> cls = Class.forName(s);
                excludeClasses.add(cls);
                searchContext.addExcludeClass(cls);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to load excluded class : " + s, e);
            }
        }

        for (String s:jarFilterPatternsString) {
            try {
                Pattern pattern = Pattern.compile(s);
                jarFilterPatterns.add(pattern);
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

        //searchContext.setInputFolder(input);

        return searchContext;
    }

    public static class DummyApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // noop
        }
    }

    public static void initJFX() {
        Thread t = new Thread("JavaFX Init Thread") {
            @Override
            public void run() {
                Application.launch(DummyApp.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
    }

}
