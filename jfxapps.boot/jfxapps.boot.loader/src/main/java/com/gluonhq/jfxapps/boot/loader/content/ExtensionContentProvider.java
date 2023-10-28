package com.gluonhq.jfxapps.boot.loader.content;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = FileExtensionProvider.class, name = "files"),
        @Type(value = FolderExtensionProvider.class, name = "folder"),
        @Type(value = MavenExtensionProvider.class, name = "maven")
        })
public interface ExtensionContentProvider {

    @JsonIgnore
    boolean isValid();

    boolean isUpToDate(Path targetFolder);

    boolean update(Path targetFolder) throws IOException;

    class Utils {
        /**
         * file must exists and be readable to be valid
         *
         * @param file
         * @return
         */
        protected static boolean isFileValid(File file) {
            return file != null && file.exists() && file.isFile() && file.canRead();
        }

        protected static boolean isFileValid(Path file) {
            return isFileValid(file.toFile());
        }

        /**
         * At least for now, it checks both file are valid and have the same size
         *
         * @param source
         * @param target
         * @return true if same
         */
        protected static boolean isFileUpToDate(File source, File target) {
            return isFileValid(source) && isFileValid(target) && source.length() == target.length();
        }

        protected static boolean isFileUpToDate(Path source, Path target) {
            return isFileUpToDate(source.toFile(), target.toFile());
        }
    }
}
