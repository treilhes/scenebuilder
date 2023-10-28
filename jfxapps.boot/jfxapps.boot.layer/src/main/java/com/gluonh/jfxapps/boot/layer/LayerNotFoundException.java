package com.gluonh.jfxapps.boot.layer;

import java.util.UUID;

public class LayerNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private UUID layerId;

    public LayerNotFoundException(UUID layerId, String messageFormat) {
        super(String.format(messageFormat, layerId));
        this.layerId = layerId;
    }

    public UUID getLayerId() {
        return layerId;
    }

}
