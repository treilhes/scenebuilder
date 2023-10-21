package com.oracle.javafx.scenebuilder.core.loader.internal.layer.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.core.loader.internal.layer.Layer;

public class LayerImpl implements Layer {

    private static final Logger logger = LoggerFactory.getLogger(LayerImpl.class);

    private static final String META_INF_MANIFEST_MF = "META-INF/MANIFEST.MF";

    private static final Attributes.Name MODULE_NAME_ATTRIBUTE = new Attributes.Name("Automatic-Module-Name");

    private UUID id;
    private Path directory;
    private ModuleLayer moduleLayerLock;
    private WeakReference<ModuleLayer> moduleLayer;
    private Map<String, WeakReference<ModuleReference>> moduleReferences;
    private Set<Path> jars;
    private Set<WeakReference<Module>> modules;
    private Set<WeakReference<Module>> automaticModules;
    private Set<WeakReference<Module>> unnamedModules;
    private Set<Layer> parents;
    private Set<Layer> children;

    public LayerImpl(UUID id, Path directory, ModuleLayer moduleLayer,
            Map<String, ModuleReference> moduleReferences) {
        super();
        this.id = id;
        this.directory = directory;
        this.moduleLayerLock = moduleLayer;
        this.moduleLayer = new WeakReference<ModuleLayer>(moduleLayer);

        this.parents = new HashSet<>();
        this.children = new HashSet<>();
        this.moduleReferences = new HashMap<>();
        moduleReferences.forEach((k, v) -> this.moduleReferences.put(k, new WeakReference<ModuleReference>(v)));
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Path getDirectory() {
        return directory;
    }

    @Override
    public ModuleLayer getModuleLayer() {
        return moduleLayer.get();
    }

    @Override
    public Set<Path> jars() {
        if (jars == null) {
            try {
                jars = Files.list(directory).filter(p -> p.getFileName().toString().toLowerCase().endsWith(".jar"))
                        .collect(Collectors.toSet());
            } catch (IOException e) {
                logger.error("Unable to list jars in directory {}", directory, e);
            }
        }
        return jars;
    }

    @Override
    public Set<WeakReference<Module>> modules() {
        if (modules == null) {
            populateCache();
        }
        return modules;
    }

    @Override
    public Set<WeakReference<Module>> automaticModules() {
        if (automaticModules == null) {
            populateCache();
        }
        return automaticModules;
    }

    @Override
    public Set<WeakReference<Module>> unnamedModules() {
        if (unnamedModules == null) {
            populateCache();
        }
        return unnamedModules;
    }

    @Override
    public Optional<URI> getLocation(Module module) {
        WeakReference<ModuleReference> ref = moduleReferences.get(module.getName());

        if (ref == null || ref.get() == null) {
            return null;
        } else {
            return ref.get().location();
        }
    }

    @Override
    public Future<Boolean> unlockLayer() {
        this.moduleLayerLock = null;

        FutureTask<Boolean> task = new FutureTask<>(() -> {

            while (true) {
                logger.debug("checking layer lock state");
                try {

                    if (Thread.interrupted()) {
                        logger.debug("layer unlock check interrupted");
                        return false;
                    }

                    System.gc();

                    if (jars().stream().anyMatch(LayerImpl::fileLockedCheck)) {
                        logger.debug("layer unlock check failed");
                        Thread.sleep(200);
                    } else {
                        logger.debug("layer unlocked");
                        return true;
                    }

                } catch (InterruptedException e) {
                    logger.debug("layer unlock check interrupted");
                    return false;
                }
            }

        });

        new Thread(task).start();

        return task;
    }

    private static boolean fileLockedCheck(Path path) {
        boolean locked = false;
        File file = path.toFile();

        try (RandomAccessFile fis = new RandomAccessFile(file, "rw")) {
            FileLock lck = fis.getChannel().lock();
            lck.release();
        } catch (Exception ex) {
            locked = true;
        }
        if (locked) {
            return locked;
        }

        // try further with rename
        String parent = file.getParent();
        File newName = new File(parent, file.getName() + "_lockcheck");
        if (file.renameTo(newName)) {
            newName.renameTo(file);
        } else {
            locked = true;
        }

        return locked;
    }

    private synchronized void populateCache() {
        try {
            modules = new HashSet<>();
            automaticModules = new HashSet<>();
            unnamedModules = new HashSet<>();

            for (Module m : allModules()) {
                ModuleDescriptor descriptor = m.getDescriptor();

                final Manifest manifest = new Manifest();
                try (ModuleReader reader = moduleReferences.get(m.getName()).get().open()) {
                    Optional<InputStream> iso = reader.open(META_INF_MANIFEST_MF);

                    iso.ifPresent(is -> {
                        try {
                            manifest.read(is);
                            is.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    });
                }

                boolean automatic = descriptor.isAutomatic();

                boolean hasAutomaticAttribute = manifest.getMainAttributes().get(MODULE_NAME_ATTRIBUTE) != null;

                if (!automatic) { // module
                    modules.add(new WeakReference<>(m));
                } else if (hasAutomaticAttribute) { // automatic module
                    automaticModules.add(new WeakReference<>(m));
                } else { // classpath module
                    unnamedModules.add(new WeakReference<>(m));
                }
            }

        } catch (Exception e) {
            logger.error("Unbale to populate modules cache", e);
        }
    }

    @Override
    public Set<Layer> getParents() {
        return parents;
    }

    protected void addParent(Layer parent) {
        this.parents.add(parent);
    }

    @Override
    public Set<Layer> getChildren() {
        return children;
    }

    protected void addChildren(Layer child) {
        this.children.add(child);
    }



}
