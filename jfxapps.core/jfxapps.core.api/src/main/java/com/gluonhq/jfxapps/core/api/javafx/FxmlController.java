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
package com.gluonhq.jfxapps.core.api.javafx;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javafx.scene.Parent;

/**
 * This interface must be used in conjunction with Spring Dependency injection framework. When implementing this interface you're delegating
 * the fxml loading task to the Spring context using {@link #getFxmlURL()} and {@link #getResources()} as parameters.
 * You will then be notified of the result with calls to {@link #setRoot(Parent)} and {@link #controllerDidLoadFxml()}
 * May be used with  {@link Component} annotation or any spring annotation making the implementing class a spring bean
 * @author ptreilhes
 *
 */
public interface FxmlController extends UiController {
	/**
	 * Must return a valid URL to an Fxml file. The instance implementing this interface will be used as a javafx controller
	 * for this fxml file. Which means any fields or methods annotated with @FXML will be binded using this fxml
	 * @return the url to the fxml that must be binded to this controller
	 */
    @NonNull
	URL getFxmlURL();

	/**
	 * May return a valid ResourceBundle containing all the necessary keys to translate every i18n expression
	 * ("%xxx.xxx") contained in the fxml file provided by {@link #getFxmlURL()}
	 * @return
	 */
	ResourceBundle getResources();

	/**
     * This method is automatically called by {@link com.gluonhq.jfxapps.core.api.javafx.internal.FxmlControllerBeanPostProcessor#postProcessAfterInitialization(Object, String)}
     * after a successful call to {@link javafx.fxml.FXMLLoader#load()} using {@link #getFxmlURL()} and {@link #getResources()} as parameters and a successful call to {@link #setRoot(Parent)}
     * It notify the controller that all loading activities have ended successfully
     */
	void controllerDidLoadFxml();
}
