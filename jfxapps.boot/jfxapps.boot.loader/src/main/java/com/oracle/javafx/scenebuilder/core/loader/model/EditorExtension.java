package com.oracle.javafx.scenebuilder.core.loader.model;

import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionContentProvider;

public class EditorExtension extends Extension {

    protected EditorExtension() {
        this(null, null);
    }

    public EditorExtension(UUID id, ExtensionContentProvider contentProvider) {
        super(id, contentProvider);
    }

    @Override
    public EditorExtension clone() throws CloneNotSupportedException {
        return (EditorExtension)super.clone();
    }
}
