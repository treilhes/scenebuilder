/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.ext.menu;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.util.FXMLUtils;
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
	public List<MenuItemAttachment> menuItems() {
		return Arrays.asList(new UserStylesheetsMenuAttachment());
	}

	public class UserStylesheetsMenuAttachment implements MenuItemAttachment {

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
