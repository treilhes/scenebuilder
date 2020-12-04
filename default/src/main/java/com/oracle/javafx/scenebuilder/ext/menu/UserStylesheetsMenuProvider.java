package com.oracle.javafx.scenebuilder.ext.menu;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.ext.controller.SceneStyleSheetMenuController;
import com.oracle.javafx.scenebuilder.ext.theme.document.UserStylesheetsPreference;

import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class UserStylesheetsMenuProvider implements MenuItemProvider {

	private final static String THEME_ID = "themeMenu";

	private final UserStylesheetsPreference userStylesheetsPreference;
	private final SceneStyleSheetMenuController sceneStyleSheetMenuController;

	public UserStylesheetsMenuProvider(
			@Autowired @Lazy SceneStyleSheetMenuController sceneStyleSheetMenuController,
			@Autowired @Lazy UserStylesheetsPreference userStylesheetsPreference
			) {
		this.sceneStyleSheetMenuController = sceneStyleSheetMenuController;
		this.userStylesheetsPreference = userStylesheetsPreference;
	}

	@Override
	public List<MenuAttachment> menuItems() {
		return Arrays.asList(new UserStylesheetsMenuAttachment());
	}

	public class UserStylesheetsMenuAttachment implements MenuAttachment {

	    private Menu stylesheetMenu = null;

	    @FXML
	    private MenuItem addSceneStyleSheetMenuItem;
	    @FXML
	    private Menu removeSceneStyleSheetMenu;
	    @FXML
	    private Menu openSceneStyleSheetMenu;

		public UserStylesheetsMenuAttachment() {}

		@Override
		public String getTargetId() {
			return THEME_ID;
		}

		@Override
		public PositionRequest getPositionRequest() {
			return PositionRequest.AsNextSibling;
		}

		@Override
		public MenuItem getMenuItem() {

			if (stylesheetMenu != null) {
				return stylesheetMenu;
			}

			stylesheetMenu = FXMLUtils.load(this, "userStylesheetsMenu.fxml");

			assert addSceneStyleSheetMenuItem != null;
	        assert removeSceneStyleSheetMenu != null;
	        assert openSceneStyleSheetMenu != null;

	        stylesheetMenu.setId("stylesheetMenu");

	        addSceneStyleSheetMenuItem.setOnAction((e) -> sceneStyleSheetMenuController.performAddSceneStyleSheet());

	        updateOpenAndRemoveSceneStyleSheetMenus();

	        userStylesheetsPreference.getValue().addListener((Change<? extends String> c) -> {
				updateOpenAndRemoveSceneStyleSheetMenus();
			});

			return stylesheetMenu;
		}

		private void updateOpenAndRemoveSceneStyleSheetMenus() {
	        assert removeSceneStyleSheetMenu != null;

	        List<String> sceneStyleSheets = userStylesheetsPreference.getValue();

	        removeSceneStyleSheetMenu.getItems().clear();
            openSceneStyleSheetMenu.getItems().clear();

            if (sceneStyleSheets.size() == 0) {
            	MenuItem miRemove = new MenuItem(I18N.getString("scenestylesheet.none"));
				miRemove.setDisable(true);
				removeSceneStyleSheetMenu.getItems().add(miRemove);

				MenuItem miOpen = new MenuItem(I18N.getString("scenestylesheet.none"));
				miOpen.setDisable(true);
				openSceneStyleSheetMenu.getItems().add(miOpen);
            } else {
                for (String f : sceneStyleSheets) {
                	File file = new File(URI.create(f));
                	MenuItem miRemove = new MenuItem(file.getName());
					miRemove.setOnAction((e) -> sceneStyleSheetMenuController.performRemoveSceneStyleSheet(file));
					removeSceneStyleSheetMenu.getItems().add(miRemove);

					MenuItem miOpen = new MenuItem(file.getName());
					miOpen.setOnAction((e) -> sceneStyleSheetMenuController.performOpenSceneStyleSheet(file));
					openSceneStyleSheetMenu.getItems().add(miOpen);
                }
            }
	    }
	}
}
