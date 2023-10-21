package com.oracle.javafx.scenebuilder.core.loader.internal.layer;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;

public interface Layer {
    UUID getId();
    Path getDirectory();
    ModuleLayer getModuleLayer();

    Set<Path> jars();
    Set<WeakReference<Module>> modules();
    Set<WeakReference<Module>> automaticModules();
    Set<WeakReference<Module>> unnamedModules();

    default Set<Module> allModules() {
        return getModuleLayer().modules();
    }

    Optional<URI> getLocation(Module module);

    Future<Boolean> unlockLayer();

    Set<Layer> getParents();
    Set<Layer> getChildren();

    //void bootLayer(BootProgressListener progressListener);

    default Set<Extension> extensions() {
        return ServiceLoader.load(getModuleLayer(), Extension.class).stream()
                .map(ServiceLoader.Provider::get)
                .filter(e -> e.getClass().getModule().getLayer().equals(getModuleLayer()))
                .collect(Collectors.toSet());
    }
    default Class<?> getClass(String moduleSlashClassName) throws ClassNotFoundException {
        String[] parts = moduleSlashClassName.split("/");
        try {
            ClassLoader loader = getModuleLayer().findLoader(parts[0]);
            return loader.loadClass(parts[1]);
        }
        catch (IllegalArgumentException iae) {
            // Nothing
        }
        throw new IllegalArgumentException("Module " + parts[0] + " not found");
    }

}
