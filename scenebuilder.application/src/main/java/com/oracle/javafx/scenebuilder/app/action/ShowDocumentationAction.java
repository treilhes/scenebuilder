package com.oracle.javafx.scenebuilder.app.action;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about",
        accelerator = "F1")
public class ShowDocumentationAction extends AbstractAction {

    private final Dialog dialog;
    
    public ShowDocumentationAction(
            @Autowired Api api,
            @Autowired Dialog dialog) {
        super(api);
        this.dialog = dialog;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        try {
            getApi().getFileSystem().open(EditorPlatform.DOCUMENTATION_URL);
        } catch (IOException ioe) {
            dialog.showErrorAndWait("", I18N.getString("alert.help.failure.message", EditorPlatform.DOCUMENTATION_URL),
                    I18N.getString("alert.messagebox.failure.details"), ioe);
            return ActionStatus.FAILED;
        }
        return ActionStatus.DONE;
    }
}