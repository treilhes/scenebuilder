package com.oracle.javafx.scenebuilder.api.clipboard;

public interface Clipboard {

    boolean canPerformCopy();

    void performCopy();

    boolean canPerformCut();

    void performCut();

    boolean canPerformPaste();

    void performPaste();

}
