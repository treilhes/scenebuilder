package app.extext;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.extension.OpenExtension;

import app.ext.ExtExtension;
import app.extext.exported.ExportedExtService;
import app.extext.internal.LocalExtExtServiceComponent;


public class ExtExtExtension implements OpenExtension {

    public final static UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public UUID getParentId() {
        return ExtExtension.ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of(LocalExtExtServiceComponent.class);
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
        return List.of(ExportedExtService.class);
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
