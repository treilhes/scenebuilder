package app.ext;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;
import com.oracle.javafx.scenebuilder.core.loader.extension.OpenExtension;

import app.ext.exported.ExportedRootServiceComponent;
import app.ext.internal.LocalExtServiceComponent;


public class ExtExtension implements OpenExtension {

    public final static UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public UUID getParentId() {
        return Extension.ROOT_ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of(LocalExtServiceComponent.class);
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
        return List.of(ExportedRootServiceComponent.class);
    }

    @Override
    public InputStream getLicense() {
        return null;
    }

    @Override
    public InputStream getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getLoadingImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getIconX2() {
        // TODO Auto-generated method stub
        return null;
    }

}
