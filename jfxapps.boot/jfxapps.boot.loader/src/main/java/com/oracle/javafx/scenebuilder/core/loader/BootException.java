package com.oracle.javafx.scenebuilder.core.loader;

public class BootException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BootException(String message, ExtensionReport rootReport) {
        super(message, rootReport.getThrowable().get());
    }

}
