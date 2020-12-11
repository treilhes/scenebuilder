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
package com.oracle.javafx.scenebuilder.api;

import java.net.URL;
import java.util.List;

/**
 * The Interface Glossary
 */
public interface Glossary {

    /**
     * Returns the candidate fx ids tracked by this glossary.
     * If fxmlLocation, controllerClass and/or targetType are not null,
     * this glossary may use them to filter the returned list.
     * Some implementations may ignore those parameters.
     *
     * @param fxmlLocation null or the location of the fxml document being edited
     * @param controllerClass null or one the classes return by queryControllerClasses()
     * @param targetType null or the type of the component targeted by the fx id
     * @return a list of fxids (possibly empty but never null).
     */
	List<String> queryFxIds(URL fxmlLocation, String controllerClass, Class<?> targetType);

	 /**
 	 * Returns candidate controller classes tracked by this glossary.
 	 * If fxmlLocation is not null, this glossary may use it to filter the
 	 * returned list. Some implementations may ignore the fxmlLocation parameter.
 	 *
 	 * @param location the location
 	 * @return a list of class names (possibly empty but never null).
 	 */
	List<String> queryControllerClasses(URL location);

	/**
     * Returns the candidate event handlers tracked by this glossary.
     * If fxmlLocation and/or controllerClass are not null, this glossary
     * may use them to filter the returned list.
     * Some implementations may ignore those parameters.
     *
     * @param fxmlLocation null or the location of the fxml document being edited
     * @param controllerClass null or one the classes return by queryControllerClasses()
     * @return a list of event handler method name (possibly empty but never null).
     */
	List<String> queryEventHandlers(URL fxmlLocation, String controllerClass);

}
