package com.oracle.javafx.scenebuilder.core.loader.model;

import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionContentProvider;

public class Editor extends AbstractExtension<EditorExtension> {

    protected Editor() {
        this(null, null);
    }

    public Editor(UUID id, ExtensionContentProvider contentProvider) {
        super(id, contentProvider);
    }

    @Override
    public Editor clone() throws CloneNotSupportedException {
        return (Editor)super.clone();
    }
}
