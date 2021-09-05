package com.oracle.javafx.scenebuilder.fs.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class ImportFxmlAction extends AbstractFxmlAction {

    private final DocumentManager documentManager;

    public ImportFxmlAction(
            @Autowired Api api, 
            @Autowired DocumentWindow documentWindow,
            @Autowired DocumentManager documentManager, 
            @Autowired Editor editor,
            @Autowired FileSystem fileSystem
            ) {
        super(api, fileSystem, documentWindow, editor);
        this.documentManager = documentManager;
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