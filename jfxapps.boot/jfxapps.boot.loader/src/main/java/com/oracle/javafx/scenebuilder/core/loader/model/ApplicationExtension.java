package com.oracle.javafx.scenebuilder.core.loader.model;

import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionContentProvider;

public class ApplicationExtension extends Extension {

    public ApplicationExtension(UUID id, ExtensionContentProvider contentProvider) {
        super(id, contentProvider);
    }

    protected ApplicationExtension() {
        this(null, null);
    }

    @Override
    public ApplicationExtension clone() throws CloneNotSupportedException {
        return (ApplicationExtension)super.clone();
    }
}
