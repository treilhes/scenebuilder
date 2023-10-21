package com.oracle.javafx.scenebuilder.core.loader.content;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;



class FolderExtensionProviderTest {

    private static File resourceFolder = new File("./src/test/resources/com/oracle/javafx/scenebuilder/core/loader/content");
    private static File file1 = new File(resourceFolder, "somefile1.txt");
    private static File file2 = new File(resourceFolder, "somefile2.txt");

    @TempDir
    Path storeDir;

    @Test
    void must_detect_need_update() {
        FolderExtensionProvider fep = new FolderExtensionProvider(resourceFolder);
        assertFalse(fep.isUpToDate(storeDir));
    }

    @Test
    void must_detect_dont_need_update() throws IOException {
        Files.copy(file1.toPath(), storeDir.resolve(file1.getName()));
        Files.copy(file2.toPath(), storeDir.resolve(file2.getName()));
        FolderExtensionProvider fep = new FolderExtensionProvider(resourceFolder);
        assertTrue(fep.isUpToDate(storeDir));
    }

    @Test
    void must_update_target_folder() throws IOException {
        FolderExtensionProvider fep = new FolderExtensionProvider(resourceFolder);
        assertTrue(fep.update(storeDir));
        assertTrue(Files.exists(storeDir.resolve(file1.getName())));
        assertTrue(Files.exists(storeDir.resolve(file2.getName())));
        assertTrue(fep.isUpToDate(storeDir));
    }

    @Test
    void source_content_must_be_valid() throws IOException {
        FolderExtensionProvider fep = new FolderExtensionProvider(resourceFolder);
        assertTrue(fep.isValid());
    }
}
