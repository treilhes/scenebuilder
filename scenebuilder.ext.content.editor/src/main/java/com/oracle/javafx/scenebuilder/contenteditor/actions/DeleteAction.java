package com.oracle.javafx.scenebuilder.contenteditor.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.DeleteSelectionJob;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class DeleteAction extends AbstractAction {

    private final DocumentWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    private final Dialog dialog;
    private final Editor editor;
    private final JobManager jobManager;

    public DeleteAction(
            @Autowired Api api, 
            @Autowired DocumentWindow documentWindow,
            @Autowired DocumentManager documentManager, 
            @Autowired InlineEdit inlineEdit,
            @Autowired Editor editor,
            @Autowired JobManager jobManager,
            @Autowired Dialog dialog) {
        super(api);
        this.documentWindow = documentWindow;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.jobManager = jobManager;
        this.dialog = dialog;
    }

    @Override
    public boolean canPerform() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            result = tic.getCaretPosition() < tic.getLength();
        } else {
            final Job job = new DeleteSelectionJob(getApi().getContext(), editor).extend();
            result = job.isExecutable();
        }
        return result;
    }

    @Override
    public ActionStatus perform() {
        assert canPerform();

        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.deleteNextChar();
        } else {
            final List<FXOMObject> selectedObjects = editor.getSelectedObjects();

            // Collects fx:ids in selected objects and their descendants.
            // We filter out toggle groups because their fx:ids are managed automatically.
            final Map<String, FXOMObject> fxIdMap = new HashMap<>();
            for (FXOMObject selectedObject : selectedObjects) {
                fxIdMap.putAll(selectedObject.collectFxIds());
            }
            FXOMNodes.removeToggleGroups(fxIdMap);

            // Checks if deleted objects have some fx:ids and ask for confirmation.
            final boolean deleteConfirmed;
            if (fxIdMap.isEmpty()) {
                deleteConfirmed = true;
            } else {
                final String message;

                if (fxIdMap.size() == 1) {
                    if (selectedObjects.size() == 1) {
                        message = I18N.getString("alert.delete.fxid1of1.message");
                    } else {
                        message = I18N.getString("alert.delete.fxid1ofN.message");
                    }
                } else {
                    if (selectedObjects.size() == fxIdMap.size()) {
                        message = I18N.getString("alert.delete.fxidNofN.message");
                    } else {
                        message = I18N.getString("alert.delete.fxidKofN.message");
                    }
                }

                final Alert d = dialog.customAlert(documentWindow.getStage());
                d.setMessage(message);
                d.setDetails(I18N.getString("alert.delete.fxid.details"));
                d.setOKButtonTitle(I18N.getString("label.delete"));

                deleteConfirmed = (d.showAndWait() == AbstractModalDialog.ButtonID.OK);
            }

            if (deleteConfirmed) {
                final Job job = new DeleteSelectionJob(getApi().getContext(), editor).extend();
                jobManager.push(job);
            }
        }

        return ActionStatus.DONE;
    }
}