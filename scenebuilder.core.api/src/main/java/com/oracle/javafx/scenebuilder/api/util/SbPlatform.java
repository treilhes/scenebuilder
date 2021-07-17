package com.oracle.javafx.scenebuilder.api.util;

import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.DocumentScope;

import javafx.application.Platform;

public final class SbPlatform {

    /**
     * Same as {@link Platform#runLater(Runnable)}
     * @param runnable
     */
    public static void runLater(Runnable runnable) {
        SbPlatform.runLater(runnable);
    }
    
    /**
     * Same as {@link Platform#runLater(Runnable)} but will also ensure execution
     * with the specified {@link Document} scope
     * @param scope
     * @param runnable
     */
    public static void runForDocumentLater(Document scope, Runnable runnable) {
        DocumentScope.executeWithScope(scope, runnable);
    }
    
    /**
     * Same as {@link Platform#runLater(Runnable)} but will also ensure execution
     * with the currently active {@link Document} scope
     * @param runnable
     */
    public static void runForDocumentLater(Runnable runnable) {
        runForDocumentLater(DocumentScope.getActiveScope(), runnable);
    }
}
