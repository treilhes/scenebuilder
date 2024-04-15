package com.gluonhq.jfxapps.boot.context.scope;

public class NullScopeException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    public NullScopeException(String message) {
        super(message);
    }
}