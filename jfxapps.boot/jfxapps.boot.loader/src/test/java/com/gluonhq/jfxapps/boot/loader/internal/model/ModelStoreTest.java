package com.gluonhq.jfxapps.boot.loader.internal.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gluonhq.jfxapps.boot.loader.model.Application;
import com.gluonhq.jfxapps.boot.loader.model.ApplicationExtension;
import com.gluonhq.jfxapps.boot.loader.model.Editor;
import com.gluonhq.jfxapps.boot.loader.model.EditorExtension;
import com.gluonhq.jfxapps.boot.loader.model.Extension;
import com.gluonhq.jfxapps.boot.loader.model.ModelStore;

class ModelStoreTest {

    private static final String MODEL_JSON = "model.json";
    private static Application application = new Application(UUID.randomUUID(), null);
    private static ApplicationExtension application_ext1 = new ApplicationExtension(UUID.randomUUID(), null);
    private static Extension application_ext1_ext1 = new Extension(UUID.randomUUID(), null);
    private static Extension application_ext1_ext2 = new Extension(UUID.randomUUID(), null);
    private static ApplicationExtension application_ext2 = new ApplicationExtension(UUID.randomUUID(), null);
    private static Extension application_ext2_ext1 = new Extension(UUID.randomUUID(), null);
    private static Extension application_ext2_ext2 = new Extension(UUID.randomUUID(), null);
    private static Editor editor1 = new Editor(UUID.randomUUID(), null);
    private static EditorExtension editor1_ext1 = new EditorExtension(UUID.randomUUID(), null);
    private static Extension editor1_ext1_ext1 = new Extension(UUID.randomUUID(), null);
    private static Extension editor1_ext1_ext2 = new Extension(UUID.randomUUID(), null);
    private static EditorExtension editor1_ext2 = new EditorExtension(UUID.randomUUID(), null);
    private static Extension editor1_ext2_ext1 = new Extension(UUID.randomUUID(), null);
    private static Extension editor1_ext2_ext2 = new Extension(UUID.randomUUID(), null);
    private static Editor editor2 = new Editor(UUID.randomUUID(), null);
    private static EditorExtension editor2_ext1 = new EditorExtension(UUID.randomUUID(), null);
    private static Extension editor2_ext1_ext1 = new Extension(UUID.randomUUID(), null);
    private static Extension editor2_ext1_ext2 = new Extension(UUID.randomUUID(), null);
    private static EditorExtension editor2_ext2 = new EditorExtension(UUID.randomUUID(), null);
    private static Extension editor2_ext2_ext1 = new Extension(UUID.randomUUID(), null);
    private static Extension editor2_ext2_ext2 = new Extension(UUID.randomUUID(), null);

    static {
        application.addExtension(application_ext1);
        application_ext1.addExtension(application_ext1_ext1);
        application_ext1.addExtension(application_ext1_ext2);

        application.addExtension(application_ext2);
        application_ext1.addExtension(application_ext2_ext1);
        application_ext1.addExtension(application_ext2_ext2);

        application.addEditor(editor1);
        editor1.addExtension(editor1_ext1);
        editor1_ext1.addExtension(editor1_ext1_ext1);
        editor1_ext1.addExtension(editor1_ext1_ext2);

        editor1.addExtension(editor1_ext2);
        editor1_ext2.addExtension(editor1_ext2_ext1);
        editor1_ext2.addExtension(editor1_ext2_ext2);

        application.addEditor(editor2);
        editor2.addExtension(editor2_ext1);
        editor2_ext1.addExtension(editor2_ext1_ext1);
        editor2_ext1.addExtension(editor2_ext1_ext2);

        editor2.addExtension(editor2_ext2);
        editor2_ext2.addExtension(editor2_ext2_ext1);
        editor2_ext2.addExtension(editor2_ext2_ext2);
    }

    @TempDir
    Path storeDir;

    @Test
    void should_save_to_file() throws IOException {
        Path target = storeDir.resolve(MODEL_JSON);
        new ModelStore().write(target, application);
        assertTrue(Files.exists(target));
    }

    @Test
    void should_load_from_file() throws IOException {
        Path target = storeDir.resolve(MODEL_JSON);
        new ModelStore().write(target, application);
        Application app = new ModelStore().read(target);
        assertTrue(app != null);
    }

    @Test
    void models_loaded_should_be_equal_to_saved_one() throws IOException {
        Path target = storeDir.resolve(MODEL_JSON);
        new ModelStore().write(target, application);
        Application app = new ModelStore().read(target);
        assertTrue(app.equals(application));
    }

    @Test
    void model_clone_must_be_equal() throws CloneNotSupportedException  {
        Application app = application.clone();
        assertTrue(app.equals(application));
    }
}
