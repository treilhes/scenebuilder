package com.gluonhq.jfxapps.boot.api.loader;

public class BootException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BootException(String message, ExtensionReport rootReport) {
        super(message, rootReport.getThrowable().get());
    }

}