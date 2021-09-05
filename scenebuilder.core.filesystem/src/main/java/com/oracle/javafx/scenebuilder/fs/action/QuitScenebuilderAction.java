package com.oracle.javafx.scenebuilder.fs.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.lifecycle.DisposeWithSceneBuilder;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.Alert;

import javafx.application.Platform;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@Lazy
@ActionMeta(nameKey = "action.name.toggle.dock", descriptionKey = "action.description.toggle.dock")
public class QuitScenebuilderAction extends AbstractAction {
    
    private final static Logger logger = LoggerFactory.getLogger(QuitScenebuilderAction.class);
    
    private final Main main;
    private final Dialog dialog;
    private final List<DisposeWithSceneBuilder> finalizations;
    private final FileSystem fileSystem;

    public QuitScenebuilderAction(
            @Autowired Api api,
            @Autowired Main main,
            @Autowired FileSystem fileSystem,
            @Autowired Dialog dialog,
            @Lazy @Autowired(required = false) List<DisposeWithSceneBuilder> finalizations) {
        super(api);
        this.main = main;
        this.fileSystem = fileSystem;
        this.dialog = dialog;
        this.finalizations = finalizations;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {


        // Check if an editing session is on going
        for (Document dwc : main.getDocumentWindowControllers()) {
            if (dwc.getEditorController().isTextEditingSessionOnGoing()) {
                // Check if we can commit the editing session
                if (dwc.getEditorController().canGetFxmlText() == false) {
                    // Commit failed
                    return ActionStatus.CANCELLED;
                }
            }
        }

        // Collects the documents with pending changes
        final List<Document> pendingDocs = new ArrayList<>();
        for (Document dwc : main.getDocumentWindowControllers()) {
            if (dwc.isDocumentDirty()) {
                pendingDocs.add(dwc);
            }
        }

        // Notifies the user if some documents are dirty
        final boolean exitConfirmed;
        switch (pendingDocs.size()) {
        case 0: {
            exitConfirmed = true;
            break;
        }

        case 1: {
            final Document dwc0 = pendingDocs.get(0);
            exitConfirmed = dwc0.performCloseAction() == ActionStatus.DONE;
            break;
        }

        default: {
            assert pendingDocs.size() >= 2;

            final Alert d = dialog.customAlert();
            d.setMessage(I18N.getString("alert.review.question.message", pendingDocs.size()));
            d.setDetails(I18N.getString("alert.review.question.details"));
            d.setOKButtonTitle(I18N.getString("label.review.changes"));
            d.setActionButtonTitle(I18N.getString("label.discard.changes"));
            d.setActionButtonVisible(true);

            switch (d.showAndWait()) {
            default:
            case OK: { // Review
                int i = 0;
                ActionStatus status;
                do {
                    status = pendingDocs.get(i++).performCloseAction();
                } while ((status == ActionStatus.DONE) && (i < pendingDocs.size()));
                exitConfirmed = (status == ActionStatus.DONE);
                break;
            }
            case CANCEL: {
                exitConfirmed = false;
                break;
            }
            case ACTION: { // Do not review
                exitConfirmed = true;
                break;
            }
            }
            break;
        }
        }

        // Exit if confirmed
        if (exitConfirmed) {
            for (Document dwc : main.getDocumentWindowControllers()) {
                // Write to java preferences before closing
                dwc.updatePreferences();
                main.documentWindowRequestClose(dwc);
            }
            fileSystem.stopWatcher();

            finalizations.forEach(a -> a.dispose());
            // TODO (elp): something else here ?
            logger.info(I18N.getString("log.stop"));
            Platform.exit();
        }
    
        
        return ActionStatus.DONE;
    }
}