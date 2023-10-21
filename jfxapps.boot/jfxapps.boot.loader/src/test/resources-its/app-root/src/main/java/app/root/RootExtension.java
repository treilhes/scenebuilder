package app.root;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;
import com.oracle.javafx.scenebuilder.core.loader.extension.SealedExtension;

import app.root.internal.LocalRootService;


public class RootExtension implements SealedExtension {

    @Override
    public UUID getId() {
        return Extension.ROOT_ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of(LocalRootService.class);
    }

    @Override
    public UUID getParentId() {
        return Extension.ROOT_ID;
    }

    @Override
    public InputStream getLicense() {
        return null;
    }

    @Override
    public InputStream getDescription() {
        return null;
    }

    @Override
    public InputStream getLoadingImage() {
        return null;
    }

    @Override
    public InputStream getIcon() {
        return null;
    }

    @Override
    public InputStream getIconX2() {
        return null;
    }

}
