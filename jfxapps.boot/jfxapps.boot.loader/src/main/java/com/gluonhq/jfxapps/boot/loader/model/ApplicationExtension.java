package com.gluonhq.jfxapps.boot.loader.model;

import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.content.ExtensionContentProvider;

public class ApplicationExtension extends Extension {

    protected ApplicationExtension() {
        this(null, null);
    }

    public ApplicationExtension(UUID id, ExtensionContentProvider contentProvider) {
        super(id, contentProvider);
    }

    @Override
    public ApplicationExtension clone() throws CloneNotSupportedException {
        return (ApplicationExtension)super.clone();
    }
}
