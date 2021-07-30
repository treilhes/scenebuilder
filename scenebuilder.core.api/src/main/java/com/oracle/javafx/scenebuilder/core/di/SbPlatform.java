package com.oracle.javafx.scenebuilder.core.di;

import com.oracle.javafx.scenebuilder.api.Document;

import javafx.application.Platform;

public final class SbPlatform {

    /**
     * Same as {@link Platform#runLater(Runnable)}
     * @param runnable
     */
    public static void runLater(Runnable runnable) {
        Platform.runLater(runnable);
    }
    
    /**
     * Same as {@link Platform#runLater(Runnable)} but will also ensure execution
     * with the specified {@link Document} scope
     * @param scope
     * @param runnable
     */
    public static void runForDocumentLater(Document scope, Runnable runnable) {
        DocumentScope.executeLaterWithScope(scope, runnable);
    }
    
    /**
     * Same as {@link Platform#runLater(Runnable)} but will also ensure execution
     * with the currently active {@link Document} scope
     * @param runnable
     */
    public static void runForDocumentLater(Runnable runnable) {
        DocumentScope.executeLaterWithScope(DocumentScope.getActiveScopeUUID(), runnable);
    }
    
    /**
     * Execute the Runnable with the provided {@link Document} scope
     * @param scope
     * @param runnable
     */
    public static void runForDocument(Document scope, Runnable runnable) {
        DocumentScope.executeWithScope(scope, runnable);
    }
    
    /**
     * Execute the Runnable with the {@link Document} scope from {@link DocumentScope#getActiveScope()} 
     * @param runnable
     */
    public static void runForDocument(Runnable runnable) {
        DocumentScope.executeWithScope(DocumentScope.getActiveScopeUUID(), runnable);
    }
    
    /**
     * Execute the Runnable on a dedicated thread with the provided {@link Document} scope
     * @param scope
     * @param runnable
     */
    public static void runOnThreadForDocument(Document scope, Runnable runnable) {
        DocumentScope.executeOnThreadWithScope(scope, runnable);
    }
    
    /**
     * Execute the Runnable on a dedicated thread with the {@link Document} scope from {@link DocumentScope#getActiveScope()}
     * @param runnable
     */
    public static void runOnThreadForDocument(Runnable runnable) {
        DocumentScope.executeOnThreadWithScope(DocumentScope.getActiveScopeUUID(), runnable);
    }
}
