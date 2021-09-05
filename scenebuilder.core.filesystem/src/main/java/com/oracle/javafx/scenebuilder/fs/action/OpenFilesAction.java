package com.oracle.javafx.scenebuilder.fs.action;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save",
        accelerator = "CTRL+O")
public class OpenFilesAction extends AbstractOpenFilesAction {
    
    //private static final Logger logger = LoggerFactory.getLogger(OpenFilesAction.class);

    private final FileSystem fileSystem;

    private List<File> fxmlFiles;

    // @formatter:off
    public OpenFilesAction(
            @Autowired Api api, 
            @Autowired Dialog dialog, 
            @Autowired FileSystem fileSystem, 
            @Autowired Main main, 
            @Autowired RecentItemsPreference recentItemsPreference) {
     // @formatter:on
        super(api, dialog, main, recentItemsPreference);
        this.fileSystem = fileSystem;
    }

    public List<File> getFxmlFiles() {
        return fxmlFiles;
    }

    public void setFxmlFile(List<File> fxmlFiles) {
        this.fxmlFiles = fxmlFiles;
    }

    @Override
    public boolean canPerform() {
        return fxmlFiles != null && fxmlFiles.size() > 0;
    }

    @Override
    public ActionStatus perform() {
        fileSystem.updateNextInitialDirectory(fxmlFiles.get(0));
        performOpenFiles(fxmlFiles);
        return ActionStatus.DONE;
    }
}