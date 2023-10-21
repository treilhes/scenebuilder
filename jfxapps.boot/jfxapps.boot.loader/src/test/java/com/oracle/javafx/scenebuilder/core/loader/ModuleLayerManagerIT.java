package com.oracle.javafx.scenebuilder.core.loader;

import static com.oracle.javafx.scenebuilder.core.loader.TestUtils.copy;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.oracle.javafx.scenebuilder.core.loader.internal.layer.Layer;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.ModuleLayerManager;

class ModuleLayerManagerIT {

    private final static UUID ROOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final static UUID EXT_ID = UUID.randomUUID();

    @TempDir
    Path rootDir;

    @Test
    void must_create_the_root_layer() throws Exception {
        ModuleLayerManager manager = ModuleLayerManager.get();
        manager.create(null, ROOT_ID, rootDir);
        manager.removeAllLayers();
    }

    @Test
    void create_duplicate_root_layer_return_the_same_one() throws Exception {
        ModuleLayerManager manager = ModuleLayerManager.get();
        Layer layer1 = manager.create(null, ROOT_ID, rootDir);
        Layer layer2 = manager.create(null, ROOT_ID, rootDir);

        assertEquals(layer1, layer2);

        manager.removeAllLayers();
    }

    @Test
    void must_delete_the_root_layer() throws Exception {
        Path subDir = rootDir.resolve("must_delete_the_root_layer");
        Files.createDirectory(subDir);

        copy(subDir, Constants.IT_MODULE_JAR);

        ModuleLayerManager manager = ModuleLayerManager.get();
        Layer layer = manager.create(null, ROOT_ID, subDir);

        boolean deleted = manager.remove(layer);

        assertTrue(deleted);
    }


    @Test
    void must_create_the_root_extension_layer() throws Exception {

        Path rootLayerDir = rootDir.resolve("must_create_the_root_extension_layer");
        Path extDir = rootDir.resolve("must_create_the_root_extension_layer_ext");
        Files.createDirectory(rootLayerDir);
        Files.createDirectory(extDir);

        copy(extDir, Constants.IT_MODULE_JAR);

        ModuleLayerManager manager = ModuleLayerManager.get();
        Layer root = manager.create(null, ROOT_ID, rootLayerDir);
        Layer ext = manager.create(root, EXT_ID, extDir);

        manager.removeAllLayers();
    }

    @Test
    void must_delete_the_root_extension__layer() throws Exception {
        Path subDir = rootDir.resolve("must_delete_the_root_layer");
        Files.createDirectory(subDir);

        copy(subDir, Constants.IT_MODULE_JAR);

        ModuleLayerManager manager = ModuleLayerManager.get();
        Layer layer = manager.create(null, ROOT_ID, subDir);

        boolean deleted = manager.remove(layer);

        assertTrue(deleted);
    }

}
