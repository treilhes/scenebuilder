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
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.util.FxmlController;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.SceneBuilderBeanFactoryPostProcessor;

/**
 * AbstractFxmlController is the abstract base class for all the
 * controller which build their UI components from an FXML file.
 *
 * Subclasses should provide a {@link AbstractFxmlPanelController#controllerDidLoadFxml() }
 * method in charge of finishing the initialization of the UI components
 * loaded from the FXML file.
 *
 *
 */
public abstract class AbstractFxmlController extends AbstractPanelController implements FxmlController{

    private final URL fxmlURL;

    /**
     * Base constructor for invocation by the subclasses.
     * @param api api agregator
     * @param fxmlURL the URL of the FXML file to be loaded (cannot be null)
     */
    protected AbstractFxmlController(Api api, URL fxmlURL) {
        super(api);
        this.fxmlURL = fxmlURL;
        assert fxmlURL != null : "Check the name of the FXML file used by "
                + getClass().getSimpleName();
    }

    @Override
	public URL getFxmlURL() {
		return this.fxmlURL;
	}

	@Override
	public ResourceBundle getResources() {
		return I18N.getBundle();
	}

    /*
     * AbstractPanelController
     */

//	/**
//     * This implementation loads the FXML file using the URL passed to
//     * {@link AbstractFxmlPanelController}.
//     * Subclass implementation should make sure that this method can be invoked
//     * outside of the JavaFX thread
//     */
//    @Override
//    public void makePanel() {
//        final FXMLLoader loader = new FXMLLoader();
//
//        loader.setController(this);
//        loader.setLocation(fxmlURL);
//        loader.setResources(I18N.getBundle());
//        try {
//            setRoot((Parent)loader.load());
//            controllerDidLoadFxml();
//        } catch (RuntimeException | IOException x) {
//            System.out.println("loader.getController()=" + loader.getController());
//            System.out.println("loader.getLocation()=" + loader.getLocation());
//            throw new RuntimeException("Failed to load " + fxmlURL.getFile(), x); //NOI18N
//        }
//    }

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

        // Note : remember that here:
        // 1) getHost() might be null
        // 2) getRoot().getScene() might be null
        // 3) getRoot().getScene().getWindow() might be null

}
