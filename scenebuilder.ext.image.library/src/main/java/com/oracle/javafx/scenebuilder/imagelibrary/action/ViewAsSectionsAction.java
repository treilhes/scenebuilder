package com.oracle.javafx.scenebuilder.imagelibrary.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.imagelibrary.panel.ImageLibraryPanelController;
import com.oracle.javafx.scenebuilder.imagelibrary.preferences.global.ImageDisplayModePreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.view.as.sections",
		descriptionKey = "action.description.view.as.sections")
public class ViewAsSectionsAction extends AbstractAction {

	private final ImageLibraryPanelController libraryPanelController;
	private final ImageDisplayModePreference displayModePreference;

	public ViewAsSectionsAction(
	        @Autowired Api api,
			@Autowired @Lazy ImageLibraryPanelController libraryPanelController,
			@Autowired @Lazy ImageDisplayModePreference displayModePreference) {
		super(api);
		this.libraryPanelController = libraryPanelController;
		this.displayModePreference = displayModePreference;
	}

	@Override
	public boolean canPerform() {
		return libraryPanelController.getDisplayMode() != ImageLibraryPanelController.DISPLAY_MODE.SECTIONS;
	}

	@Override
	public ActionStatus perform() {
		if (libraryPanelController.getDisplayMode() != ImageLibraryPanelController.DISPLAY_MODE.SEARCH) {
            libraryPanelController.setDisplayMode(ImageLibraryPanelController.DISPLAY_MODE.SECTIONS);
        } else {
            libraryPanelController.setPreviousDisplayMode(ImageLibraryPanelController.DISPLAY_MODE.SECTIONS);
        }

		displayModePreference.setValue(libraryPanelController.getDisplayMode())
			.writeToJavaPreferences();
		return ActionStatus.DONE;
	}
}