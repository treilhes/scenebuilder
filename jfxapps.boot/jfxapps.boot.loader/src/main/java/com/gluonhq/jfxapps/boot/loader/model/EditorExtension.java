package com.gluonhq.jfxapps.boot.loader.model;

import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.content.ExtensionContentProvider;

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
