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
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

import javafx.stage.FileChooser;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save",
        accelerator = "CTRL+O")
public class SelectAndOpenFilesAction extends AbstractOpenFilesAction {
    
    //private static final Logger logger = LoggerFactory.getLogger(SelectAndOpenFilesAction.class);

    private final FileSystem fileSystem;

    // @formatter:off
    public SelectAndOpenFilesAction(
            @Autowired Api api, 
            @Autowired Dialog dialog, 
            @Autowired FileSystem fileSystem, 
            @Autowired Main main, 
            @Autowired RecentItemsPreference recentItemsPreference) {
     // @formatter:on
        super(api, dialog, main, recentItemsPreference);
        this.fileSystem = fileSystem;
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
            performOpenFiles(fxmlFiles);
        }
        return ActionStatus.DONE;
    }
    
}