package com.gluonhq.jfxapps.boot.loader.model;

import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.content.ExtensionContentProvider;

public class Application extends AbstractExtension<ApplicationExtension> {

    protected Application() {
        this(null, null);
    }

    public Application(UUID id, ExtensionContentProvider contentProvider) {
        super(id, contentProvider);
    }

    @Override
    public Application clone() throws CloneNotSupportedException {
        return (Application)super.clone();
    }
}
