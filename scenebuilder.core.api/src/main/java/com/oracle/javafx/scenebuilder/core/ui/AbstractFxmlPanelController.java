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
package com.oracle.javafx.scenebuilder.core.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.core.di.FxmlController;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactoryPostProcessor;

/**
 * AbstractFxmlPanelController is the abstract base class for all the
 * panel controller which build their UI components from an FXML file.
 *
 * Subclasses should provide a {@link AbstractFxmlPanelController#controllerDidLoadFxml() }
 * method in charge of finishing the initialization of the UI components
 * loaded from the FXML file.
 *
 *
 */
public abstract class AbstractFxmlPanelController extends AbstractPanelController implements FxmlController {

    private final URL fxmlURL;
    private final ResourceBundle resources;

    /**
     * Base constructor for invocation by the subclasses.
     *
     * @param api api aggregator
     * @param fxmlURL the URL of the FXML file to be loaded (cannot be null)
     * @param resources I18n resource bundle
     */
    protected AbstractFxmlPanelController(Api api, URL fxmlURL, ResourceBundle resources) {
        super(api);
        this.fxmlURL = fxmlURL;
        this.resources = resources;
        assert fxmlURL != null : "Check the name of the FXML file used by "
                + getClass().getSimpleName();
    }

    /*
     * AbstractPanelController
     */

    @Override
    public URL getFxmlURL() {
		return fxmlURL;
	}

    @Override
	public ResourceBundle getResources() {
		return resources;
	}
    
    

    /*
     * Protected
     */

    /**
     * Called by {@link SceneBuilderBeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory) } after
     * the FXML file has been successfully loaded.
     * Warning : this routine may be invoked outside of the event thread.
     */
    @Override
	public abstract void controllerDidLoadFxml();

}
