package com.oracle.javafx.scenebuilder.fs.action;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class LoadBlankAction extends AbstractAction {

    private final Document document;
    private final DocumentWindow documentWindow;
    private final Editor editor;

    public LoadBlankAction(
    // @formatter:off
            @Autowired Api api, 
            @Autowired Document document, 
            @Autowired DocumentWindow documentWindow, 
            @Autowired Editor editor) {
    // @formatter:on
        super(api);
        this.document = document;
        this.editor = editor;
        this.documentWindow = documentWindow;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        try {
            editor.setFxmlTextAndLocation("", null, true); // NOI18N
            document.updateLoadFileTime();
            documentWindow.updateStageTitle(); // No-op if fxml has not been loaded yet
            // TODO remove after checking the new watching system is operational in
            // EditorController or in filesystem
            // watchingController.update();

            return ActionStatus.DONE;
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
    }
}