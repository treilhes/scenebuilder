/*
 * Copyright (c) 2016, 2017 Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.kit.editor.panel.util;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.oracle.javafx.scenebuilder.api.View;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.search.SearchController;
import com.oracle.javafx.scenebuilder.kit.editor.view.ViewController;

import lombok.Getter;

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
public abstract class AbstractViewFxmlPanelController extends AbstractFxmlPanelController implements View {

	@Autowired
	private @Getter ViewManager viewManager;
	
	@Autowired
	private @Getter SearchController searchController;
	
	@Autowired
	private @Getter ViewController viewController;
	
	/*
     * Public
     */
	/**
     * Base constructor for invocation by the subclasses.
     * 
     * @param editorController  the editor controller (cannot be null)
     */
    public AbstractViewFxmlPanelController(URL fxmlURL, ResourceBundle resources, EditorController c) {
        super(fxmlURL, resources, c); //NOI18NgetSearchController
//        searchController = new SearchController(c);
//        viewController = new ViewController(c);
    }

	@Override
	protected void makePanel() {
		assert getSearchController() != null;
		assert getViewController() != null;
		super.makePanel();
		getViewController().makePanel();
		getViewController().setSearchControl(getSearchController().getPanelRoot());
		getViewController().setContent(super.getPanelRoot());
	}

    
}
