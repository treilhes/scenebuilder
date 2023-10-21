package com.oracle.javafx.scenebuilder.core.loader.internal.context;

public class InvalidExtensionException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidExtensionException(String message) {
        super(message);
    }

    public InvalidExtensionException(Throwable cause) {
        super(cause);
    }


    public static class Unchecked extends RuntimeException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public Unchecked(String message) {
            super(message);
        }

    }
}
