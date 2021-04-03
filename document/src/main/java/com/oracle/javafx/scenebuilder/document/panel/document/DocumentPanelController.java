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
package com.oracle.javafx.scenebuilder.document.panel.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.DocumentPanel;
import com.oracle.javafx.scenebuilder.api.HierarchyPanel.DisplayOption;
import com.oracle.javafx.scenebuilder.api.action.Action;
import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.dock.ViewDescriptor;
import com.oracle.javafx.scenebuilder.api.dock.ViewSearch;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlViewController;
import com.oracle.javafx.scenebuilder.document.panel.hierarchy.AbstractHierarchyPanelController;
import com.oracle.javafx.scenebuilder.document.panel.hierarchy.HierarchyPanelController;
import com.oracle.javafx.scenebuilder.document.panel.info.InfoPanelController;
import com.oracle.javafx.scenebuilder.document.preferences.global.DisplayOptionPreference;
import com.oracle.javafx.scenebuilder.sb.preferences.global.AccordionAnimationPreference;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

/**
 * This class creates and controls the <b>Library Panel</b> of Scene Builder
 * Kit.
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ViewDescriptor(name = DocumentPanelController.VIEW_NAME, id=DocumentPanelController.VIEW_ID, prefDockId = Dock.LEFT_DOCK_ID, openOnStart = true, selectOnStart = false, order = 200)
public class DocumentPanelController extends AbstractFxmlViewController implements DocumentPanel {

    public final static String VIEW_ID = "d1fd6f6a-5de0-4d92-9300-4309c4332ea5";
    public final static String VIEW_NAME = "document";

	private final AbstractHierarchyPanelController hierarchyPanelController;
	private final InfoPanelController infoPanelController;
	private final DisplayOptionPreference displayOptionPreference;
	private final SceneBuilderBeanFactory sceneBuilderFactory;
	private final AccordionAnimationPreference accordionAnimationPreference;

	private final Action showInfoAction;
    private final Action showFxIdAction;
    private final Action showNodeIdAction;

	@FXML private StackPane hierarchyPanelHost;
	@FXML private StackPane infoPanelHost;
	@FXML private Accordion documentAccordion;

	private Menu hierarchyMenu;
	private RadioMenuItem showInfoMenuItem;
    private RadioMenuItem showFxIdMenuItem;
    private RadioMenuItem showNodeIdMenuItem;

    private List<MenuItem> menuItems;

    /*
     * Public
     */

    /**
     * Creates a library panel controller for the specified editor controller.
     *
     * @param editor the editor controller (never null).
     */
    //TODO after verifying setLibrary is never reused in editorcontroller, must use UserLibrary bean instead of libraryProperty
    public DocumentPanelController(
            @Autowired Api api,
    		@Autowired SceneBuilderBeanFactory sceneBuilderFactory,
    		@Autowired HierarchyPanelController hierarchyPanelController,
    		@Autowired InfoPanelController infoPanelController,
    		@Autowired DisplayOptionPreference displayOptionPreference,
    		@Autowired AccordionAnimationPreference accordionAnimationPreference,
    		@Autowired @Qualifier("documentPanelActions.ShowInfoAction") Action showInfoAction,
    		@Autowired @Qualifier("documentPanelActions.ShowFxIdAction") Action showFxIdAction,
    		@Autowired @Qualifier("documentPanelActions.ShowNodeIdAction") Action showNodeIdAction
    		) { //, UserLibrary library) {
        super(api, DocumentPanelController.class.getResource("DocumentPanel.fxml"), I18N.getBundle()); //NOI18N
        this.sceneBuilderFactory = sceneBuilderFactory;
        this.hierarchyPanelController = hierarchyPanelController;
        this.infoPanelController = infoPanelController;
        this.displayOptionPreference = displayOptionPreference;
        this.accordionAnimationPreference = accordionAnimationPreference;

        this.showInfoAction = showInfoAction;
        this.showFxIdAction = showFxIdAction;
        this.showNodeIdAction = showNodeIdAction;
        
    }

    @FXML
    public void initialize() {
    	createLibraryMenu();

    	getDocumentAccordion().getPanes().forEach(tp -> tp.setAnimated(accordionAnimationPreference.getValue()));
    	accordionAnimationPreference.getObservableValue().addListener(
    			(ob, o, n) -> getDocumentAccordion().getPanes().forEach(tp -> tp.setAnimated(n)));

    	refreshHierarchyDisplayOption(displayOptionPreference.getValue());
    	displayOptionPreference.getObservableValue().addListener(
    			(ob, o, n) -> refreshHierarchyDisplayOption(n));
    }


    /**
     * @treatAsPrivate Controller did load fxml.
     */
    @Override
    public void controllerDidLoadFxml() {
    	assert hierarchyPanelHost != null;
        assert infoPanelHost != null;
        assert documentAccordion != null;
        assert !documentAccordion.getPanes().isEmpty();

		hierarchyPanelHost.getChildren().add(hierarchyPanelController.getRoot());
        infoPanelHost.getChildren().add(infoPanelController.getRoot());

        documentAccordion.setExpandedPane(documentAccordion.getPanes().get(0));

    }

    private List<MenuItem> createLibraryMenu() {
        List<MenuItem> items = new ArrayList<>();
        
        ToggleGroup hierarchyDisplayOptionTG = new ToggleGroup();

        hierarchyMenu = sceneBuilderFactory.createViewMenu(
        		getResources().getString("hierarchy.displays"));
        showInfoMenuItem = sceneBuilderFactory.createViewRadioMenuItem(
        		getResources().getString("hierarchy.show.info"), hierarchyDisplayOptionTG);
        showFxIdMenuItem = sceneBuilderFactory.createViewRadioMenuItem(
        		getResources().getString("hierarchy.show.fxid"), hierarchyDisplayOptionTG);
        showNodeIdMenuItem = sceneBuilderFactory.createViewRadioMenuItem(
        		getResources().getString("hierarchy.show.nodeid"), hierarchyDisplayOptionTG);

        showInfoMenuItem.setOnAction((e) -> showInfoAction.checkAndPerform());
        showFxIdMenuItem.setOnAction((e) -> showFxIdAction.checkAndPerform());
        showNodeIdMenuItem.setOnAction((e) -> showNodeIdAction.checkAndPerform());

        hierarchyMenu.getItems().addAll(showInfoMenuItem, showFxIdMenuItem, showNodeIdMenuItem);
        items.add(hierarchyMenu);
        
        return items;
	}

	@Override
    public AbstractHierarchyPanelController getHierarchyPanelController() {
        return hierarchyPanelController;
    }

    public InfoPanelController getInfoPanelController() {
        return infoPanelController;
    }

    public Accordion getDocumentAccordion() {
		return documentAccordion;
	}

	public void refreshHierarchyDisplayOption(DisplayOption option) {
        switch(option) {
            case INFO:
                showInfoMenuItem.setSelected(true);
                break;
            case FXID:
                showFxIdMenuItem.setSelected(true);
                break;
            case NODEID:
                showNodeIdMenuItem.setSelected(true);
                break;
            default:
                assert false;
                break;
        }
        hierarchyPanelController.setDisplayOption(option);
    }

    @Override
    public ViewSearch getSearchController() {
        return null;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        if (menuItems == null) {
            menuItems = createLibraryMenu();
        }
        return menuItems;
    }

    @Override
    public void onShow() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onHidden() {
        // TODO Auto-generated method stub
        
    }
}
