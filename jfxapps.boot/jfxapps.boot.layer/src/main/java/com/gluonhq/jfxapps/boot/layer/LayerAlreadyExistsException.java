package com.gluonhq.jfxapps.boot.layer;

import java.util.UUID;

public class LayerAlreadyExistsException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private UUID layerId;

    public LayerAlreadyExistsException(UUID layerId, String messageFormat) {
        super(String.format(messageFormat, layerId));
        this.layerId = layerId;
    }

    public UUID getLayerId() {
        return layerId;
    }

}
