package com.oracle.javafx.scenebuilder.imagelibrary.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.imagelibrary.panel.ImageLibraryPanelController;
import com.oracle.javafx.scenebuilder.imagelibrary.preferences.global.ImageDisplayModePreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.view.as.list",
		descriptionKey = "action.description.view.as.list")
public class ViewAsListAction extends AbstractAction {

	private final ImageLibraryPanelController libraryPanelController;
	private final ImageDisplayModePreference displayModePreference;

	public ViewAsListAction(
	        @Autowired Api api,
			@Autowired @Lazy ImageLibraryPanelController libraryPanelController,
			@Autowired @Lazy ImageDisplayModePreference displayModePreference) {
		super(api);
		this.libraryPanelController = libraryPanelController;
		this.displayModePreference = displayModePreference;
	}

	@Override
	public boolean canPerform() {
		return libraryPanelController.getDisplayMode() != ImageLibraryPanelController.DISPLAY_MODE.LIST;
	}

	@Override
	public ActionStatus perform() {
		if (libraryPanelController.getDisplayMode() != ImageLibraryPanelController.DISPLAY_MODE.SEARCH) {
            libraryPanelController.setDisplayMode(ImageLibraryPanelController.DISPLAY_MODE.LIST);
        } else {
            libraryPanelController.setPreviousDisplayMode(ImageLibraryPanelController.DISPLAY_MODE.LIST);
        }

		displayModePreference.setValue(libraryPanelController.getDisplayMode())
			.writeToJavaPreferences();
		return ActionStatus.DONE;
	}
}