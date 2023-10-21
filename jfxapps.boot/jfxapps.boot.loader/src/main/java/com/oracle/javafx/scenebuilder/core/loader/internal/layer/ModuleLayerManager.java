package com.oracle.javafx.scenebuilder.core.loader.internal.layer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.internal.layer.impl.InvalidLayerException;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.impl.ModuleLayerManagerImpl;

public interface ModuleLayerManager {

    public static ModuleLayerManager get() {
        return ModuleLayerManagerImpl.get();
    }

    boolean remove(Layer layer) throws IOException;
    //boolean remove(EditorLayer layer) throws IOException;
    boolean removeAllLayers() throws IOException;

    Layer create(Layer parent, UUID layerId, Path layerDirectory) throws IOException, InvalidLayerException;

    boolean unload(UUID layerId) throws IOException;

    boolean remove(UUID layerId) throws IOException;

    Layer get(UUID id);

}
