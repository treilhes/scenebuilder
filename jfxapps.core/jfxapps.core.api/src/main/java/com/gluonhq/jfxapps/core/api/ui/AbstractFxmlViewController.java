/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.ui;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.dock.Dock;
import com.gluonhq.jfxapps.core.api.ui.dock.View;
import com.gluonhq.jfxapps.core.api.ui.dock.ViewContent;
import com.gluonhq.jfxapps.core.api.ui.dock.ViewSearch;
import com.gluonhq.jfxapps.util.NodeUtils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuButton;

/**
 * AbstractViewFxmlPanelController is the abstract base class for all the view
 * controller which build their UI components from an FXML file.
 *
 * Subclasses should provide a
 * {@link AbstractFxmlPanelController#controllerDidLoadFxml() } method in charge
 * of finishing the initialization of the UI components loaded from the FXML
 * file.
 *
 * It provides input controls for filtering, a placeholder menu and basic
 * docking functionalities
 */
public abstract class AbstractFxmlViewController extends AbstractFxmlPanelController implements View, ViewContent { // ,
                                                                                                                    // ViewMenuProvider
                                                                                                                    // {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AbstractFxmlViewController.class);

    private final ViewMenuController viewMenuController;
//	@Autowired
//	private @Getter ViewManager viewManager;

    private StringProperty nameProperty;

    private final DocumentManager documentManager;

    private ObjectProperty<Dock> parentDock = new SimpleObjectProperty<>();
    private BooleanProperty minimizedProperty = new SimpleBooleanProperty();

    /*
     * Public
     */
    /**
     * Base constructor for invocation by the subclasses.
     *
     * @param api       api agregator
     * @param fxmlURL   the URL of the FXML file to be loaded (cannot be null)
     * @param resources I18n resource bundle
     */
    public AbstractFxmlViewController(SceneBuilderManager scenebuilderManager, DocumentManager documentManager,
            ViewMenuController viewMenuController, URL fxmlURL, ResourceBundle resources) {
        super(scenebuilderManager, documentManager, fxmlURL, resources); // NOCHECK
        this.viewMenuController = viewMenuController;
        this.documentManager = documentManager;

        String viewName = getViewName();
        nameProperty = new SimpleStringProperty(I18N.getStringOrDefault(viewName, viewName));

    }

    @Override
    public void controllerDidLoadFxml() {
        documentManager.focused().subscribe((f) -> {
            if (NodeUtils.isDescendantOf(getRoot(), f.getRoot())) {
                notifyFocused();
            }
        });
    }

    @Override
    public void notifyFocused() {
        if (documentManager.focusedView().get() != this) {
            logger.info("Active view : {}", this.getClass().getName());
            documentManager.focusedView().set(this);
        }
    }

    @Override
    public ViewContent getViewController() {
        return this;
    }

    private BooleanProperty visibleProperty;

    @Override
    public BooleanProperty visibleProperty() {
        if (visibleProperty == null) {
            visibleProperty = new SimpleBooleanProperty(false);
            visibleProperty.addListener((ob, o, n) -> {
                if (n) {
                    onShow();
                } else {
                    onHidden();
                }
            });
        }
        return visibleProperty;
    }

    public abstract void onShow();

    public abstract void onHidden();

    @Override
    public void populateMenu(MenuButton menuButton) {
        viewMenuController.buildMenu(this, menuButton);
    }

    @Override
    public void clearMenu(MenuButton menuButton) {
        viewMenuController.clearMenu(this, menuButton);
    }

    @Override
    public final ObjectProperty<Dock> parentDockProperty() {
        return this.parentDock;
    }

    @Override
    public ViewSearch getSearchController() {
        return null;
    }

    @Override
    public final StringProperty nameProperty() {
        return this.nameProperty;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String nameProperty) {
        this.nameProperty().set(nameProperty);
    }

}
