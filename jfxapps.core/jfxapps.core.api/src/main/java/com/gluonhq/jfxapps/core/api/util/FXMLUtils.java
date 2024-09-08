/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.util;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.FxmlController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FXMLUtils {

    private static final Logger log = LoggerFactory.getLogger(FXMLUtils.class);

	private FXMLUtils() {}

	public static <T> T load(Object controllerInstance, String fxml) {
        return load(null, controllerInstance, controllerInstance.getClass(), fxml);
    }

    public static <T> T load(Object controllerInstance, Class<?> resourceLoadingClass, String fxml) {
        final URL fxmlURL = resourceLoadingClass.getResource(fxml); //NOCHECK
        return load(null, controllerInstance, resourceLoadingClass, fxmlURL);
    }

    public static <T> T load(Object controllerInstance, Class<?> resourceLoadingClass, URL fxmlUrl) {
        return load(null, controllerInstance, resourceLoadingClass, fxmlUrl);
    }
	public static <T> T load(I18N i18n, Object controllerInstance, String fxml) {
		return load(i18n, controllerInstance, controllerInstance.getClass(), fxml);
	}

    public static <T> T load(I18N i18n, Object controllerInstance, Class<?> resourceLoadingClass, String fxml) {
        final URL fxmlURL = resourceLoadingClass.getResource(fxml); //NOCHECK
        return load(i18n, controllerInstance, resourceLoadingClass, fxmlURL);
    }


    public static <T extends FxmlController> Parent load(T controller, URL fxmlURL, ResourceBundle resources) {
        return load(controller, controller.getClass(), fxmlURL, resources);
    }


	public static <T> T load(I18N i18n, Object controllerInstance, Class<?> resourceLoadingClass, URL fxmlUrl) {
	    var bundle = i18n != null ? i18n.getBundle() : null;
	    return load(controllerInstance, resourceLoadingClass, fxmlUrl, bundle);
	}
	public static <T> T load(Object controllerInstance, Class<?> resourceLoadingClass, URL fxmlUrl, ResourceBundle resources) {

        ClassLoader classLoader = resourceLoadingClass != null ? resourceLoadingClass.getClassLoader()
                : controllerInstance.getClass().getClassLoader();

        FXMLLoader loader = new FXMLLoader();
        loader.setController(controllerInstance);
        loader.setLocation(fxmlUrl);

        if (resources != null) {
            loader.setResources(resources);
        }

        loader.setClassLoader(classLoader);

        try {
            return loader.load();
        } catch (RuntimeException | IOException x) {
            String clsLoader = resourceLoadingClass != null ? resourceLoadingClass.getName() : controllerInstance.getClass().getName();
            log.debug("loader.getClassLoader()=" + clsLoader); //NOCHECK
            log.debug("loader.getController()=" + loader.getController()); //NOCHECK
            log.debug("loader.getLocation()=" + loader.getLocation()); //NOCHECK
            throw new RuntimeException("Failed to load " + fxmlUrl.getFile(), x); //NOCHECK
        }
    }

}
