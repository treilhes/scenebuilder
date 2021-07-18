package com.oracle.javafx.scenebuilder.fs.action;

import java.io.IOException;
import java.net.URL;

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
import com.oracle.javafx.scenebuilder.api.preferences.Preferences;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SbPlatform;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class LoadUrlAction extends AbstractAction {

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
    private final Preferences preferences;

    private URL fxmlURL;
    private boolean keepTrackOfLocation;

    public LoadUrlAction(@Autowired Api api, @Autowired Document document, @Autowired DocumentManager documentManager,
            @Autowired DocumentWindow documentWindow, @Autowired Preferences preferences, @Autowired Editor editor,
            @Autowired InlineEdit inlineEdit, @Autowired Dialog dialog, @Autowired FileSystem fileSystem,
            @Autowired ActionFactory actionFactory, @Autowired Main main, @Autowired MessageLogger messageLogger,
            @Autowired RecentItemsPreference recentItemsPreference) {
        super(api);
        this.document = document;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.preferences = preferences;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.fileSystem = fileSystem;
        this.actionFactory = actionFactory;
        this.main = main;
        this.messageLogger = messageLogger;
        this.recentItemsPreference = recentItemsPreference;
    }

    public URL getFxmlURL() {
        return fxmlURL;
    }

    public void setFxmlURL(URL fxmlURL) {
        this.fxmlURL = fxmlURL;
    }

    public boolean isKeepTrackOfLocation() {
        return keepTrackOfLocation;
    }

    public void setKeepTrackOfLocation(boolean keepTrackOfLocation) {
        this.keepTrackOfLocation = keepTrackOfLocation;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        assert fxmlURL != null;
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editor.setFxmlTextAndLocation(fxmlText, keepTrackOfLocation ? fxmlURL : null, false);
            document.updateLoadFileTime();
            documentWindow.updateStageTitle(); // No-op if fxml has not been loaded yet

            documentWindow.untrack();
            preferences.readFromJavaPreferences();

            SbPlatform.runLater(() -> {
                documentWindow.apply();
                documentWindow.track();
            });
            // TODO remove after checking the new watching system is operational in
            // EditorController or in filesystem
            // watchingController.update();
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
        return ActionStatus.DONE;
    }
}