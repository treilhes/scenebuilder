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
package com.oracle.javafx.scenebuilder.kit.editor.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.ViewContent;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlController;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlPanelController;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.StackPane;

// TODO: Auto-generated Javadoc
/**
 * AbstractViewFxmlPanelController is the abstract base class for all the
 * view controller which build their UI components from an FXML file.
 *
 * Subclasses should provide a {@link AbstractFxmlPanelController#controllerDidLoadFxml() }
 * method in charge of finishing the initialization of the UI components
 * loaded from the FXML file.
 *
 * It provides input controls for filtering, a placeholder menu and basic docking functionalities
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ViewController extends AbstractFxmlController implements ViewContent {

	/** The views. */
	@Autowired
	private DockManager views;

	/** The view panel host. */
	@FXML private StackPane viewPanelHost;

    /** The view search panel host. */
    @FXML private StackPane viewSearchPanelHost;

    /** The view label. */
    @FXML private Label viewLabel;

    /** The view menu button. */
    @FXML private MenuButton viewMenuButton;

    /*
     * Public
     */
	/**
     * Base constructor for invocation by the subclasses.
     *
     * @param sceneBuilderManager the scene builder manager
     * @param editor the editor
     */
    public ViewController(
            @Autowired SceneBuilderManager sceneBuilderManager,
            @Autowired Editor editor) {
        super(sceneBuilderManager, ViewController.class.getResource("View.fxml"), editor);
    }

    /**
     * Text property.
     *
     * @return the string property
     */
    @Override
    public StringProperty textProperty() {
        return viewLabel.textProperty();
    }

    /**
     * Sets the content.
     *
     * @param content the new content
     */
    @Override
    public void setContent(Parent content) {
        viewPanelHost.getChildren().add(content);
    }

    /**
     * Sets the search control.
     *
     * @param searchControl the new search control
     */
    @Override
    public void setSearchControl(Parent searchControl) {
        viewSearchPanelHost.getChildren().add(searchControl);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void controllerDidLoadFxml() {
    	assert viewPanelHost != null;
        assert viewSearchPanelHost != null;
        //views.showDockTarget().resubscribe(d -> )
    }

	/**
	 * Gets the view menu button.
	 *
	 * @return the view menu button
	 */
	@Override
    public MenuButton getViewMenuButton() {
		return viewMenuButton;
	}


}