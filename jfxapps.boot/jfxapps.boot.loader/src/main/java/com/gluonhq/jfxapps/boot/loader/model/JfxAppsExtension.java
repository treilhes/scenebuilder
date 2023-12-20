package com.gluonhq.jfxapps.boot.loader.model;

import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.content.ExtensionContentProvider;

public class JfxAppsExtension extends Extension {

    public JfxAppsExtension(UUID id, ExtensionContentProvider contentProvider) {
        super(id, contentProvider);
    }

    protected JfxAppsExtension() {
        this(null, null);
    }

    @Override
    public JfxAppsExtension clone() throws CloneNotSupportedException {
        return (JfxAppsExtension)super.clone();
    }
}
