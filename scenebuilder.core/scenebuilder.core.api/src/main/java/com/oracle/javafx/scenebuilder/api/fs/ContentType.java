package com.oracle.javafx.scenebuilder.api.fs;

import java.util.List;

public interface ContentType {

    String getMimeType();

    List<String> getExtensions();

    String getLabel();

    public default boolean match(String extension, String mimeType) {
        if (extension == null && mimeType == null) {
            return false;
        }
        boolean matchExtension = extension == null ? false
                : getExtensions().stream().map(s -> s.toLowerCase()).anyMatch(extension.toLowerCase()::equals);
        boolean matchMime = mimeType == null ? false : getMimeType().toLowerCase().equals(mimeType.toLowerCase());

        return matchExtension || matchMime;
    }

}
