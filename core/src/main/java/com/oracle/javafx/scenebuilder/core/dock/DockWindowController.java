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
package com.oracle.javafx.scenebuilder.core.dock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlWindowController;

import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class DockWindowController extends AbstractFxmlWindowController {

    @FXML
    private VBox mainHost;

    private final DocumentManager documentManager;
    private final Dialog dialog;
    private final ApplicationContext context;

    private final ViewManager viewManager;
    private final DockManager dockManager;
    private final DockPanelController dockPanelController;
    private final DockNameHelper dockNameHelper;
    // @formatter:off
    public DockWindowController(
            @Autowired Api api, 
            @Autowired DocumentWindow documentWindow,
            @Autowired DockPanelController dockPanelController, 
            @Autowired ViewManager viewManager,
            @Autowired DockManager dockManager,
            @Autowired DockNameHelper dockNameHelper) {
        super(api, DockWindowController.class.getResource("DockWindow.fxml"), I18N.getBundle(), documentWindow);
     // @formatter:on
        
        this.context = api.getContext();
        this.dialog = api.getApiDoc().getDialog();
        this.documentManager = api.getApiDoc().getDocumentManager();
        this.viewManager = viewManager;
        this.dockManager = dockManager;
        this.dockPanelController = dockPanelController;
        this.dockNameHelper = dockNameHelper;

        dockPanelController.setParentWindow(this);
        dockPanelController.notifyDockCreated();
    }

    @FXML
    public void initialize() {
    }

    /*
     * AbstractFxmlWindowController
     */

    protected Dock getDock() {
        return dockPanelController;
    }

    @Override
    public void controllerDidLoadFxml() {
        assert mainHost != null;

        // Add a border to the Windows app, because of the specific window decoration on
        // Windows.
        if (EditorPlatform.IS_WINDOWS) {
            getRoot().getStyleClass().add("windows-document-decoration");// NOI18N
        }

        setupDockContainer(dockPanelController, mainHost);

    }

    private void setupDockContainer(DockPanelController dock, Pane host) {
        // attach the dock container to the host
        host.getChildren().add(dock.getContent());
        // and set it for auto grow
        VBox.setVgrow(dock.getContent(), Priority.ALWAYS);

        // if dock container does not have content remove window
        dock.getContent().getChildren().addListener((Change<? extends Node> c) -> {
            if (dock.getViews().size() == 0) {
                closeWindow();
            }
        });
    }

    @Override
    protected void controllerDidCreateStage() {
        final Stage stage = getStage();
        assert stage != null;
        stage.setTitle(dockNameHelper.getName(this.getDock().getId()));
        getStage().setOnCloseRequest(we -> {
            closeWindow();
        });
    }

    @Override
    public void openWindow() {
        if (!super.isOpen()) {
            super.openWindow();
        }
    }

    @Override
    public void closeWindow() {
        dockManager.dockHide().onNext(this.getDock());
        super.closeWindow();
    }

    @Override
    public void onCloseRequest() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFocus() {
        // TODO Auto-generated method stub

    }

}