package com.gluonhq.jfxapps.boot.loader;

import static com.gluonhq.jfxapps.boot.loader.TestUtils.copy;
import static com.gluonhq.jfxapps.boot.loader.TestUtils.instanciate;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;

import com.gluonh.jfxapps.boot.layer.Layer;
import com.gluonh.jfxapps.boot.layer.ModuleLayerManager;

class LayerTest {

    private final static UUID ROOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final static UUID EXT_ID = UUID.randomUUID();

    @TempDir
    Path rootDir;

    @AfterEach
    public void afterEach() throws IOException {
        ModuleLayerManager.get().removeAllLayers();
    }

    @Test
    void ext_must_access_class_in_root_extension_layer(TestInfo testInfo) throws Exception {

        Path rootLayerDir = rootDir.resolve(testInfo.getDisplayName());
        Path extDir = rootDir.resolve(testInfo.getDisplayName() + "_ext");
        Files.createDirectory(rootLayerDir);
        Files.createDirectory(extDir);

        copy(rootLayerDir, Constants.IT_MODULE_JAR);

        ModuleLayerManager manager = ModuleLayerManager.get();
        Layer root = manager.create(null, ROOT_ID, rootLayerDir);
        Layer ext = manager.create(root, EXT_ID, extDir);

        Object object = instanciate(ext, Constants.IT_MODULE_NAME + "/" + Constants.IT_MODULE_CLASS);

        assertNotNull(object);

        object = null;

        manager.removeAllLayers();
    }

    @Test
    void ext_must_access_class_in_extension_layer(TestInfo testInfo) throws Exception {

        Path rootLayerDir = rootDir.resolve(testInfo.getDisplayName());
        Path extDir = rootDir.resolve(testInfo.getDisplayName() + "_ext");
        Files.createDirectory(rootLayerDir);
        Files.createDirectory(extDir);

        copy(extDir, Constants.IT_MODULE_JAR);

        ModuleLayerManager manager = ModuleLayerManager.get();
        Layer root = manager.create(null, ROOT_ID, rootLayerDir);
        Layer ext = manager.create(root, EXT_ID, extDir);

        Object object = instanciate(ext, Constants.IT_MODULE_NAME + "/" + Constants.IT_MODULE_CLASS);

        assertNotNull(object);

        object = null;

        manager.removeAllLayers();
    }

    @Test
    void root_must_not_access_class_in_root_extension_layer(TestInfo testInfo) throws Exception {

        Path rootLayerDir = rootDir.resolve(testInfo.getDisplayName());
        Path extDir = rootDir.resolve(testInfo.getDisplayName() + "_ext");
        Files.createDirectory(rootLayerDir);
        Files.createDirectory(extDir);

        copy(extDir, Constants.IT_MODULE_JAR);

        ModuleLayerManager manager = ModuleLayerManager.get();
        Layer root = manager.create(null, ROOT_ID, rootLayerDir);
        Layer ext = manager.create(root, EXT_ID, extDir);

        assertThrows(Exception.class, () -> {
            instanciate(root, Constants.IT_MODULE_NAME + "/" + Constants.IT_MODULE_CLASS);
        });

        manager.removeAllLayers();
    } 
}
