package com.gluonhq.jfxapps.boot.loader.content;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

public class FileExtensionProvider implements ExtensionContentProvider {

    private Set<File> files;

    public FileExtensionProvider() {
        super();
        files = new HashSet<>();
    }

    public FileExtensionProvider(Set<File> files) {
        this();
        this.files.addAll(files);
    }

    @Override
    public boolean isUpToDate(Path targetFolder) {
        return files.stream().allMatch(f -> {
            Path target = targetFolder.resolve(f.getName());
            return Utils.isFileUpToDate(f, target.toFile());
        });
    }

    @Override
    public boolean update(Path targetFolder) throws IOException {
        for (File f:files) {
            Path target = targetFolder.resolve(f.getName());
            if (!Utils.isFileUpToDate(f, target.toFile())) {
                Files.copy(f.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return true;
    }

    @Override
    public boolean isValid() {
        return files.stream().allMatch(Utils::isFileValid);
    }

    public Set<File> getFiles() {
        return files;
    }

    protected void setFiles(Set<File> files) {
        this.files = files;
    }

}
