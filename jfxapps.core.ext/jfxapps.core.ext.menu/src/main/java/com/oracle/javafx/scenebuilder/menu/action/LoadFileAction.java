/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.menu.action;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;

@Prototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class LoadFileAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(LoadFileAction.class);

    private File fxmlFile;

    private final ActionFactory actionFactory;

    protected LoadFileAction(
            ActionExtensionFactory extensionFactory,
            ActionFactory actionFactory) {
        super(extensionFactory);
        this.actionFactory = actionFactory;
    }

    protected void setFxmlFile(File fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public File getFxmlFile() {
        return fxmlFile;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        try {
            final URL fxmlURL = fxmlFile.toURI().toURL();
            //loadFromURL(fxmlURL, true);

            LoadUrlAction loadUrlAction = actionFactory.create(LoadUrlAction.class);

            loadUrlAction.setKeepTrackOfLocation(true);
            loadUrlAction.setFxmlURL(fxmlURL);

            return loadUrlAction.perform();

            // TODO remove after checking the new watching system is operational in
            // EditorController or in filesystem
            // watchingController.update();

            // WarnThemeAlert.showAlertIfRequired(themePreference,
            // editorController.getFxomDocument(), documentWindow.getStage());
        } catch (MalformedURLException e) {
            logger.error("Unable to load url {}", fxmlFile, e);
            return ActionStatus.FAILED;
        }
    }

}