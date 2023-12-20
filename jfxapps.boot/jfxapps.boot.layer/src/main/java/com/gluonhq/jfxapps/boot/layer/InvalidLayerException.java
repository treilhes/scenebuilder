package com.gluonhq.jfxapps.boot.layer;

public class InvalidLayerException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidLayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLayerException(String message) {
        super(message);
    }

}
