package com.oracle.javafx.scenebuilder.api.clipboard;

public interface ClipboardHandler {
    
    public void performCopy();
    public void performCut();
    public void performPaste();
    
    public boolean canPerformCopy();
    public boolean canPerformCut();
    public boolean canPerformPaste();
    
}