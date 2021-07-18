package com.oracle.javafx.scenebuilder.fs.action;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class RevealFxmlFileAction extends AbstractAction {

    private final DocumentManager documentManager;
    private final DocumentWindow documentWindow;
    private final FileSystem fileSystem;
    private final Dialog dialog;
    
    public RevealFxmlFileAction(
            @Autowired Api api,
            @Autowired DocumentManager documentManager,
            @Autowired DocumentWindow documentWindow,
            @Autowired FileSystem fileSystem,
            @Autowired Dialog dialog) {
        super(api);
        this.documentManager = documentManager;
        this.documentWindow = documentWindow;
        this.fileSystem = fileSystem;
        this.dialog = dialog;
    }

    @Override
    public boolean canPerform() {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        return fxomDocument != null && fxomDocument.getLocation() != null;
    }

    @Override
    public ActionStatus perform() {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        final URL location = fxomDocument.getLocation();

        try {
            fileSystem.revealInFileBrowser(new File(location.toURI()));
        } catch (IOException | URISyntaxException x) {
            dialog.showErrorAndWait("",
                    I18N.getString("alert.reveal.failure.message", documentWindow.getStage().getTitle()),
                    I18N.getString("alert.reveal.failure.details"), x);
            return ActionStatus.FAILED;
        }
        
        return ActionStatus.DONE;
    }
    
}