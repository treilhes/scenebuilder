package com.oracle.javafx.scenebuilder.ext.actions;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.FileSystem.WatchingCallback;
import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.api.lifecycle.DisposeWithDocument;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.controller.SceneStyleSheetMenuController;
import com.oracle.javafx.scenebuilder.ext.theme.document.UserStylesheetsPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class ApplyCssContentWatchExtension extends AbstractActionExtension<ApplyCssContentAction> implements DisposeWithDocument, WatchingCallback{

	private final UserStylesheetsPreference userStylesheetsPreference;
    private final FileSystem fileSystem;
    private final SceneStyleSheetMenuController sceneStyleSheetMenuController;

	public ApplyCssContentWatchExtension(
	        @Autowired FileSystem fileSystem,
	        @Autowired SceneStyleSheetMenuController sceneStyleSheetMenuController,
			@Autowired @Lazy UserStylesheetsPreference userStylesheetsPreference
			) {
		super();
		this.fileSystem = fileSystem;
		this.sceneStyleSheetMenuController = sceneStyleSheetMenuController;
		this.userStylesheetsPreference = userStylesheetsPreference;
	}

	@Override
	public boolean canPerform() {
		return true;
	}

	@Override
	public void prePerform() {
	    fileSystem.unwatch(this);
	}

    @Override
    public void postPerform() {
        if (userStylesheetsPreference.getValue() != null) {
            List<File> toWatch = userStylesheetsPreference.getValue().stream()
                    .map(s -> new File(URI.create(s)))
                    .collect(Collectors.toList());
            fileSystem.watch(this, toWatch, this);
        }
    }

    @Override
    public void deleted(Path path) {
        sceneStyleSheetMenuController.performRemoveSceneStyleSheet(path.toFile());
    }

    @Override
    public void modified(Path path) {
        sceneStyleSheetMenuController.performReloadSceneStyleSheet();
    }

    @Override
    public void created(Path path) {
        // Unused here
    }

    @Override
    public void dispose() {
        fileSystem.unwatch(this);
    }

}
