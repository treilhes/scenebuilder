package com.oracle.javafx.scenebuilder.fs.action;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class LoadFileAction extends AbstractAction {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadFileAction.class);

    private final Document document;
    private final DocumentManager documentManager;
    private final InlineEdit inlineEdit;
    private final Dialog dialog;
    private final DocumentWindow documentWindow;
    private final FileSystem fileSystem;
    private final MessageLogger messageLogger;
    private final RecentItemsPreference recentItemsPreference;
    private final Editor editor;
    private final Main main;
    private final ActionFactory actionFactory;

    private File fxmlFile;

    public LoadFileAction(@Autowired Api api, @Autowired Document document, @Autowired DocumentManager documentManager,
            @Autowired DocumentWindow documentWindow, @Autowired Editor editor, @Autowired InlineEdit inlineEdit,
            @Autowired Dialog dialog, @Autowired FileSystem fileSystem, @Autowired ActionFactory actionFactory,
            @Autowired Main main, @Autowired MessageLogger messageLogger,
            @Autowired RecentItemsPreference recentItemsPreference) {
        super(api);
        this.document = document;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.fileSystem = fileSystem;
        this.actionFactory = actionFactory;
        this.main = main;
        this.messageLogger = messageLogger;
        this.recentItemsPreference = recentItemsPreference;
    }

    public File getFxmlFile() {
        return fxmlFile;
    }

    public void setFxmlFile(File fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        try {
            final URL fxmlURL = fxmlFile.toURI().toURL();
            //loadFromURL(fxmlURL, true);
            return actionFactory.create(LoadUrlAction.class, a -> {
                a.setFxmlURL(fxmlURL);
                a.setKeepTrackOfLocation(true);
            }).perform();

            // TODO remove after checking the new watching system is operational in
            // EditorController or in filesystem
            // watchingController.update();

            // WarnThemeAlert.showAlertIfRequired(themePreference,
            // editorController.getFxomDocument(), documentWindow.getStage());
        } catch (MalformedURLException e) {
            logger.error("Unable to load url {}", fxmlFile, e);
            return ActionStatus.FAILED;
        }
    }
}