package com.oracle.javafx.scenebuilder.fs.action;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SbPlatform;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.DocumentScope;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderLoadingProgress;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

import javafx.stage.FileChooser;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save",
        accelerator = "CTRL+O")
public class OpenFilesAction extends AbstractAction {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenFilesAction.class);

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

    public OpenFilesAction(@Autowired Api api, @Autowired Document document, @Autowired DocumentManager documentManager,
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
        final FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"),
                "*.fxml")); //NOCHECK
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());
        final List<File> fxmlFiles = fileChooser.showOpenMultipleDialog(null);
        if (fxmlFiles != null) {
            assert fxmlFiles.isEmpty() == false;
            fileSystem.updateNextInitialDirectory(fxmlFiles.get(0));
            performOpenFiles(fxmlFiles, document);
        }
        return ActionStatus.DONE;
    }
    
    
    private void performOpenFiles(List<File> fxmlFiles,
                                  Document fromWindow) {
        assert fxmlFiles != null;
        assert fxmlFiles.isEmpty() == false;

        final Map<File, Document> documents = new HashMap<>();
        
        final Map<File, IOException> exceptions = new HashMap<>();
        
        //build dependency injections first
        for (File fxmlFile : fxmlFiles) {
                try {
                    final Document dwc = main.lookupDocumentWindowControllers(fxmlFile.toURI().toURL());
                    if (dwc != null) {
                        // fxmlFile is already opened
                        dwc.getDocumentWindow().getStage().toFront();
                    } else {
                        // Open fxmlFile
                        final Document hostWindow;
                        final Document unusedWindow = main.lookupUnusedDocumentWindowController(documents.values());
                        if (unusedWindow != null) {
                            hostWindow = unusedWindow;
                        } else {
                            hostWindow = main.makeNewWindow();
                        }
                        documents.put(fxmlFile, hostWindow);
                    }
                } catch (IOException e) {
                    exceptions.put(fxmlFile, e);
                }
        }

        SceneBuilderLoadingProgress.get().end();
        
        // execute ui related loading now
        SbPlatform.runLater(() -> {
            
            
            for (Entry<File, Document> entry:documents.entrySet()) {
                File file = entry.getKey();
                Document hostWindow = entry.getValue();
                hostWindow.onFocus();
                Document scope = DocumentScope.getCurrentScope();
                try {
                    hostWindow.loadFromFile(file);
                    hostWindow.openWindow();
                } catch (IOException xx) {
                    hostWindow.closeWindow();
                    exceptions.put(file, xx);
                }
                
                switch (exceptions.size()) {
                    case 0: { // Good
                        // Update recent items with opened files
                        recentItemsPreference.addRecentItems(fxmlFiles);
                        break;
                    }
                    case 1: {
                        final File fxmlFile = exceptions.keySet().iterator().next();
                        final Exception x = exceptions.get(fxmlFile);
                        dialog.showErrorAndWait(
                                I18N.getString("alert.title.open"), 
                                I18N.getString("alert.open.failure1.message", displayName(fxmlFile.getPath())), 
                                I18N.getString("alert.open.failure1.details"), 
                                x);
                        break;
                    }
                    default: {
                        if (exceptions.size() == fxmlFiles.size()) {
                            // Open operation has failed for all the files
                            dialog.showErrorAndWait(
                                    I18N.getString("alert.title.open"), 
                                    I18N.getString("alert.open.failureN.message"), 
                                    I18N.getString("alert.open.failureN.details")
                                    );
                        } else {
                            // Open operation has failed for some files
                            dialog.showErrorAndWait(
                                    I18N.getString("alert.title.open"), 
                                    I18N.getString("alert.open.failureMofN.message", exceptions.size(), fxmlFiles.size()), 
                                    I18N.getString("alert.open.failureMofN.details")
                                    );
                        }
                        break;
                    }
                }
            }
        });
    }
    
    private static String displayName(String pathString) {
        return Paths.get(pathString).getFileName().toString();
    }
}