package com.oracle.javafx.scenebuilder.core.loader.api;

import java.io.File;
import java.nio.file.Path;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class AbstractExtension implements Extension {

    private static File rootDirectory = new File(".");

    private File pluginsDirectory;

    private ApplicationContext parentContext;

    private AnnotationConfigApplicationContext extensionContext;

    public static void load(ApplicationContext parentContext, Class<? extends AbstractExtension> extensionClass) {
        try {
            AbstractExtension extension = extensionClass.getDeclaredConstructor().newInstance();
            extension.createContext(parentContext);
            extension.createPluginsDirectory();
            extension.createLayer();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createContext(ApplicationContext parentContext) {
        this.parentContext = parentContext;
        extensionContext = new AnnotationConfigApplicationContext();
        extensionContext.setParent(parentContext);
        extensionContext.register(explicitClassToRegister().toArray(new Class[0]));
        extensionContext.refresh();
        extensionContext.start();
    }

    private void createPluginsDirectory() {
        pluginsDirectory = new File(rootDirectory, id().toString());
        if (pluginsDirectory.exists()) {
            pluginsDirectory.mkdirs();
        }
    }

    private void createLayer() {
        Path repoPath = Path.of("C:/Users/ptreilhes/.m2/repository");
        Path pluginsPath = pluginsDirectory.toPath();

        String pluginLayerName = id().toString() + "_plugins";
        String pluginApiLayerName = id().toString() + "_api";

        System.out.println("LAUNCH:" + pluginsDirectory.getAbsolutePath());
//        Layers layers = Layers.builder()
//                .resolve(new LocalResolveImpl().withLocalRepo("local", repoPath, "default"))
//                .pluginsDirectory(pluginLayerName, pluginsPath, List.of(pluginApiLayerName))
//                .layer("platform")
//                    .withModule("org.moditect.layrry:layrry-platform:1.0.0.Alpha2")
//                .layer(pluginApiLayerName)
//                    .withParent("platform")
////                    .withModule("it.core.loader:api:jar:1.0.0-SNAPSHOT")
//                .layer(pluginLayerName)
//                    .withParent(pluginApiLayerName)
//                    .withModulesIn(pluginsPath)
//                 .build();
//
//        System.out.println("EndBuild");
//        layers.run("api/com.oracle.javafx.scenebuilder.core.loader.api.App", "Alice");


    }
}
