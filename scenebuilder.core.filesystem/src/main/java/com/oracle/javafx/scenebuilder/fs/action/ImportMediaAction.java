package com.oracle.javafx.scenebuilder.fs.action;

import java.io.File;

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
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.util.ResourceUtils;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class ImportMediaAction extends AbstractFxmlAction {

    private final DocumentWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    private final Dialog dialog;
    private final Editor editor;
    private final JobManager jobManager;
    private final FileSystem fileSystem;

    public ImportMediaAction(
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
        this.fileSystem = fileSystem;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    /**
     * Performs the 'import' media edit action.
     * This action creates an object matching the type of the selected
     * media file (either ImageView or MediaView) and insert it in the document
     * (either as root if the document is empty or under the selection common
     * ancestor node otherwise).
     *
     * @param mediaFile the media file to be imported
     */
    @Override
    public ActionStatus perform() {

        final FileChooser fileChooser = new FileChooser();
        final ExtensionFilter imageFilter = new ExtensionFilter(I18N.getString("file.filter.label.image"),
                ResourceUtils.getSupportedImageExtensions());
        final ExtensionFilter audioFilter = new ExtensionFilter(I18N.getString("file.filter.label.audio"),
                ResourceUtils.getSupportedAudioExtensions());
        final ExtensionFilter videoFilter = new ExtensionFilter(I18N.getString("file.filter.label.video"),
                ResourceUtils.getSupportedVideoExtensions());
        final ExtensionFilter mediaFilter = new ExtensionFilter(I18N.getString("file.filter.label.media"),
                ResourceUtils.getSupportedMediaExtensions());

        fileChooser.getExtensionFilters().add(mediaFilter);
        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.getExtensionFilters().add(audioFilter);
        fileChooser.getExtensionFilters().add(videoFilter);

        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        File mediaFile = fileChooser.showOpenDialog(documentWindow.getStage());
        if (mediaFile != null) {

            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(mediaFile);

            performImport(mediaFile);
        }

        return ActionStatus.DONE;
    }
}