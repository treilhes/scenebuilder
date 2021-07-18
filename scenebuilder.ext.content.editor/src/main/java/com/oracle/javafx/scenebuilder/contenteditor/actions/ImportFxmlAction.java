package com.oracle.javafx.scenebuilder.contenteditor.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class ImportFxmlAction extends AbstractFxmlAction {

    private final DocumentWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    private final Dialog dialog;
    private final Editor editor;
    private final JobManager jobManager;

    public ImportFxmlAction(
            @Autowired Api api, 
            @Autowired DocumentWindow documentWindow,
            @Autowired DocumentManager documentManager, 
            @Autowired InlineEdit inlineEdit,
            @Autowired Editor editor,
            @Autowired JobManager jobManager,
            @Autowired FileSystem fileSystem,
            @Autowired Dialog dialog) {
        super(api, fileSystem, documentWindow, editor);
        this.documentWindow = documentWindow;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.jobManager = jobManager;
        this.dialog = dialog;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        fetchFXMLFile().ifPresent(fxmlFile -> performImport(fxmlFile));
        return ActionStatus.DONE;
    }
}