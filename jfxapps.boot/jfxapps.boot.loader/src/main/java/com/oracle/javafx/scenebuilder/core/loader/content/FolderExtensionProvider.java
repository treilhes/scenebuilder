package com.oracle.javafx.scenebuilder.core.loader.content;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oracle.javafx.scenebuilder.core.loader.util.FolderSync;

/**
 * Folder must contains only jar files
 */
public class FolderExtensionProvider implements ExtensionContentProvider {

    private static final Logger logger = LoggerFactory.getLogger(FolderExtensionProvider.class);

    @JsonProperty("folder")
    private File sourceFolder;

    public FolderExtensionProvider() {
        super();
    }

    public FolderExtensionProvider(File sourceFolder) {
        this();
        this.sourceFolder = sourceFolder;
    }

    @Override
    public boolean isUpToDate(Path targetFolder) {
        return Arrays.stream(sourceFolder.listFiles()).allMatch(f -> {
            Path target = targetFolder.resolve(f.getName());
            return Utils.isFileUpToDate(f, target.toFile());
        });
    }

    @Override
    public boolean update(Path targetFolder) throws IOException {
//        for (File f:sourceFolder.listFiles()) {
//            Path target = targetFolder.resolve(f.getName());
//            if (!Utils.isFileUpToDate(f, target.toFile())) {
//                Files.copy(f.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
//            }
//        }
        targetFolder.toFile().mkdirs();
        FolderSync.syncDirectories(sourceFolder.toPath(), targetFolder);
        return true;
    }

    @Override
    public boolean isValid() {

        if (!sourceFolder.exists()) {
            logger.error("source folder does not exists : {}", sourceFolder);
        }
        return Arrays.stream(sourceFolder.listFiles()).allMatch(Utils::isFileValid);
    }

    public File getSourceFolder() {
        return sourceFolder;
    }

    protected void setSourceFolder(File sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    @Override
    public String toString() {
        return "FolderExtensionProvider [sourceFolder=" + sourceFolder + "]";
    }


}
