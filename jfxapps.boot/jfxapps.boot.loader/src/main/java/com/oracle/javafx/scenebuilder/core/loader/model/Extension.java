package com.oracle.javafx.scenebuilder.core.loader.model;

import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionContentProvider;

public class Extension extends AbstractExtension<Extension> {

    public Extension() {
        this(null, null);
    }

    public Extension(UUID id, ExtensionContentProvider contentProvider) {
        super(id, contentProvider);
    }

    @Override
    public Extension clone() throws CloneNotSupportedException {
        return (Extension)super.clone();
    }
}
