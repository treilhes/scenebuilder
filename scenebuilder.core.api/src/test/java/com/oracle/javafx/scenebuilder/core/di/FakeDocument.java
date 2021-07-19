package com.oracle.javafx.scenebuilder.core.di;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.attribute.FileTime;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.action.Action.ActionStatus;

@Component(DocumentScope.SCOPE_OBJECT_NAME)
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class FakeDocument implements Document {

    private final DocumentScopedObject documentScopedObject;

    public FakeDocument(DocumentScopedObject documentScopedObject) {
        super();
        this.documentScopedObject = documentScopedObject;
    }

    public DocumentScopedObject getDocumentScopedObject() {
        return documentScopedObject;
    }

    @Override
    public boolean isInited() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isUnused() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDocumentDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasContent() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasName() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void loadFromFile(File fxmlFile) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadFromURL(URL url, boolean keepTrackOfLocation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void openWindow() {
        // TODO Auto-generated method stub

    }

    @Override
    public void updatePreferences() {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateWithDefaultContent() {
        // TODO Auto-generated method stub

    }

    @Override
    public void performImportFxml() {
        // TODO Auto-generated method stub

    }

    @Override
    public void performIncludeFxml() {
        // TODO Auto-generated method stub

    }

    @Override
    public void performRevealAction() {
        // TODO Auto-generated method stub

    }

    @Override
    public void performImportMedia() {
        // TODO Auto-generated method stub

    }

    @Override
    public void performControlAction(DocumentControlAction toggleRightPanel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCloseRequest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public DocumentWindow getDocumentWindow() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canPerformEditAction(DocumentEditAction editAction) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void performEditAction(DocumentEditAction editAction) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canPerformControlAction(DocumentControlAction controlAction) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public URL getFxmlLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void closeWindow() {
        // TODO Auto-generated method stub

    }

    @Override
    public Editor getEditorController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FileTime getLoadFileTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateLoadFileTime() {
        // TODO Auto-generated method stub

    }

    @Override
    public ActionStatus performCloseAction() {
        // TODO Auto-generated method stub
        return null;
    }

}
