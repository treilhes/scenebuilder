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
package com.gluonhq.jfxapps.core.api.ui.controller.dock;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.subjects.DockManager;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

// TODO: Auto-generated Javadoc
/**
 * AbstractViewFxmlPanelController is the abstract base class for all the
 * view controller which build their UI components from an FXML file.
 *
 * Subclasses should provide a {@link AbstractFxmlController#controllerDidLoadFxml() }
 * method in charge of finishing the initialization of the UI components
 * loaded from the FXML file.
 *
 * It provides input controls for filtering, a placeholder menu and basic docking functionalities
 */
@Prototype
public class ViewController {

	/** The views. */
	private final DockManager dockManager;

	/** The view label. */
	@FXML
    private Label viewLabel;

	/** The view search panel host. */
	@FXML
    private StackPane viewSearchHost;

	/** The view content panel host. */
	@FXML
    private Pane viewContentHost;

	/** The view menu button. */
	@FXML
    private MenuButton viewMenuButton;

    /*
     * Public
     */
	/**
     * Base constructor for invocation by the subclasses.
     *
     * @param dockManager the docking manager
     */
    public ViewController(DockManager dockManager) {
        super();
        this.dockManager = dockManager;
    }

    public Label getViewLabel() {
        return viewLabel;
    }

    public StackPane getViewSearchHost() {
        return viewSearchHost;
    }

    public Pane getViewContentHost() {
        return viewContentHost;
    }

    public MenuButton getViewMenuButton() {
        return viewMenuButton;
    }
}
