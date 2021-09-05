package com.oracle.javafx.scenebuilder.fs.action;

import java.io.File;
import java.util.Optional;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.job.editor.ImportFileJob;
import com.oracle.javafx.scenebuilder.job.editor.IncludeFileJob;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class AbstractFxmlAction extends AbstractAction {
    
    private final FileSystem fileSystem;
    private final DocumentWindow documentWindow;
    private final Editor editor;

    
    public AbstractFxmlAction(Api api, FileSystem fileSystem, DocumentWindow documentWindow, Editor editor) {
        super(api);
        this.fileSystem = fileSystem;
        this.documentWindow = documentWindow;
        this.editor = editor;
    }


    protected Optional<File> fetchFXMLFile() {
        var fileChooser = new FileChooser();
        var f = new ExtensionFilter(I18N.getString("file.filter.label.fxml"), 
                "*.fxml"); // NOI18N
        fileChooser.getExtensionFilters().add(f);
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        var fxmlFile = fileChooser.showOpenDialog(documentWindow.getStage());
        if (fxmlFile != null) {
            // See DTL-5948: on Linux we anticipate an extension less path.
            final String path = fxmlFile.getPath();
            if (!path.endsWith(".fxml")) { // NOI18N
                fxmlFile = new File(path + ".fxml"); // NOI18N
            }

            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(fxmlFile);
        }
        return Optional.ofNullable(fxmlFile);
    }
    
    protected void performImport(File file) {
        JobManager jobManager = getApi().getApiDoc().getJobManager();
        MessageLogger messageLogger = getApi().getApiDoc().getMessageLogger();
        
        final ImportFileJob job = new ImportFileJob(getApi().getContext(), file, editor);
        if (job.extend().isExecutable()) {
            jobManager.push(job.extend());
        } else {
            final String target;
            if (job.getTargetObject() == null) {
                target = null;
            } else {
                final Object sceneGraphTarget
                        = job.getTargetObject().getSceneGraphObject();
                if (sceneGraphTarget == null) {
                    target = null;
                } else {
                    target = sceneGraphTarget.getClass().getSimpleName();
                }
            }
            if (target != null) {
                messageLogger.logWarningMessage(
                        "import.from.file.failed.target",
                        file.getName(), target);
            } else {
                messageLogger.logWarningMessage(
                        "import.from.file.failed",
                        file.getName());
            }
        }
    }
    
    /**
     * Performs the 'include' FXML edit action.
     * As opposed to the 'import' edit action, the 'include' action does not
     * copy the FXML content but adds an fx:include element to the FXML document.
     *
     * @param fxmlFile the FXML file to be included
     */
    protected void performIncludeFxml(File fxmlFile) {
        JobManager jobManager = getApi().getApiDoc().getJobManager();
        MessageLogger messageLogger = getApi().getApiDoc().getMessageLogger();
        
        final IncludeFileJob job = new IncludeFileJob(getApi().getContext(), fxmlFile, editor);
        if (job.extend().isExecutable()) {
            jobManager.push(job.extend());
        } else {
            final String target;
            if (job.getTargetObject() == null) {
                target = null;
            } else {
                final Object sceneGraphTarget
                        = job.getTargetObject().getSceneGraphObject();
                if (sceneGraphTarget == null) {
                    target = null;
                } else {
                    target = sceneGraphTarget.getClass().getSimpleName();
                }
            }
            if (target != null) {
                messageLogger.logWarningMessage(
                        "include.file.failed.target",
                        fxmlFile.getName(), target);
            } else {
                messageLogger.logWarningMessage(
                        "include.file.failed",
                        fxmlFile.getName());
            }
        }
    }
}
