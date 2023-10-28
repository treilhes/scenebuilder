package com.gluonhq.jfxapps.boot.loader.model;

import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.content.ExtensionContentProvider;

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
