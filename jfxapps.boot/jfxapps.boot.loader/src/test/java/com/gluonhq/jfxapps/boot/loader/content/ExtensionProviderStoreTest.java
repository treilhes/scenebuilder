package com.gluonhq.jfxapps.boot.loader.content;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gluonhq.jfxapps.boot.loader.content.FileExtensionProvider;
import com.gluonhq.jfxapps.boot.loader.content.FolderExtensionProvider;
import com.gluonhq.jfxapps.boot.loader.model.Application;
import com.gluonhq.jfxapps.boot.loader.model.ApplicationExtension;
import com.gluonhq.jfxapps.boot.loader.model.ModelStore;

class ExtensionProviderStoreTest {

    private static File resourceFolder = new File("./src/test/resources/com/oracle/javafx/scenebuilder/core/loader/content");
    private static File file1 = new File(resourceFolder, "somefile1.txt");
    private static File file2 = new File(resourceFolder, "somefile2.txt");

    private static final String MODEL_JSON = "model.json";
    private static Application application = new Application(UUID.randomUUID(), new FileExtensionProvider(Set.of(file1, file2)));
    private static ApplicationExtension application_ext1 = new ApplicationExtension(UUID.randomUUID(), new FolderExtensionProvider(resourceFolder));

    static {
        application.addExtension(application_ext1);
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
