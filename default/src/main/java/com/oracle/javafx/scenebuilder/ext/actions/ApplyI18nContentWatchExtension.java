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
import com.oracle.javafx.scenebuilder.ext.controller.I18nResourceMenuController;
import com.oracle.javafx.scenebuilder.ext.theme.document.I18NResourcePreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class ApplyI18nContentWatchExtension extends AbstractActionExtension<ApplyI18nContentAction> implements DisposeWithDocument, WatchingCallback{

	private final I18NResourcePreference i18NResourcePreference;
	private final FileSystem fileSystem;
    private final I18nResourceMenuController i18nResourceMenuController;

	public ApplyI18nContentWatchExtension(
	        @Autowired FileSystem fileSystem,
            @Autowired I18nResourceMenuController i18nResourceMenuController,
			@Autowired @Lazy I18NResourcePreference i18NResourcePreference
			) {
		super();
		this.fileSystem = fileSystem;
        this.i18nResourceMenuController = i18nResourceMenuController;
		this.i18NResourcePreference = i18NResourcePreference;
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
        if (i18NResourcePreference.getValue() != null) {
            List<File> toWatch = i18NResourcePreference.getValue().stream()
                    .map(s -> new File(URI.create(s)))
                    .collect(Collectors.toList());
            fileSystem.watch(this, toWatch, this);
        }
	}

    @Override
    public void deleted(Path path) {
        i18nResourceMenuController.performRemoveResource(path.toFile());
    }

    @Override
    public void modified(Path path) {
        i18nResourceMenuController.performReloadResource();
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
