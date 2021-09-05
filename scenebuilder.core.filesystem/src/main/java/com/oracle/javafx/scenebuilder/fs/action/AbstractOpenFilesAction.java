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

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderLoadingProgress;
import com.oracle.javafx.scenebuilder.core.di.SbPlatform;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

public abstract class AbstractOpenFilesAction extends AbstractAction {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractOpenFilesAction.class);

    private final Dialog dialog;
    private final RecentItemsPreference recentItemsPreference;
    private final Main main;

    // @formatter:off
    public AbstractOpenFilesAction(
            @Autowired Api api, 
            @Autowired Dialog dialog, 
            @Autowired Main main, 
            @Autowired RecentItemsPreference recentItemsPreference) {
     // @formatter:on
        super(api);
        this.dialog = dialog;
        this.main = main;
        this.recentItemsPreference = recentItemsPreference;
    }

    
    protected void performOpenFiles(List<File> fxmlFiles) {
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
                            logger.info("Assign {} to unused document", fxmlFile.getName());
                            hostWindow = unusedWindow;
                        } else {
                            logger.info("Assign {} to new document", fxmlFile.getName());
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
                //SbPlatform.runForDocument(hostWindow, () -> {
                    try {
                        hostWindow.loadFromFile(file);
                        hostWindow.openWindow();
                    } catch (IOException xx) {
                        hostWindow.closeWindow();
                        exceptions.put(file, xx);
                    }
                //});
                
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