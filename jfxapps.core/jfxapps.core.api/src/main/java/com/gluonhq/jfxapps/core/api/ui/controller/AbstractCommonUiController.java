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
package com.gluonhq.jfxapps.core.api.ui.controller;

import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.api.di.FxmlController;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolStylesheetProvider;
import com.gluonhq.jfxapps.util.NodeUtils;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;

public abstract class AbstractCommonUiController  {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AbstractCommonUiController.class);

    /** The scene builder manager. */
    private final SceneBuilderManager sceneBuilderManager;

    /** The document manager. */
    private final DocumentManager documentManager;

    private final ChangeListener<? super Node> focusListener;

    /** The panel root. */
    private Parent panelRoot;

    /** The tool stylesheet config. */
    private ToolStylesheetProvider toolStylesheetConfig;

    /**
     * Base constructor for invocation by the subclasses.
     * Subclass implementations should make sure that this constructor can be
     * invoked outside of the JavaFX thread.
     *
     * @param api the api object
     */
    protected AbstractCommonUiController(
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager) {
        this.sceneBuilderManager = scenebuilderManager;
        this.documentManager = documentManager;

        this.focusListener = (obj, old, node) -> {
            if (NodeUtils.isDescendantOf(panelRoot, node)) {
                notifyFocused();
            }
        };
    }

    private void notifyFocused() {
        if (this.documentManager.focused().get() != this) {
            logger.info("Active component : {}", this.getClass().getName());
            this.documentManager.focused().set(this);
        }
    }

    /**
     * Returns the root FX object of this panel.
     *
     * @return the root object of the panel (never null)
     */
    public Parent getRoot() {
        assert panelRoot != null;
        return panelRoot;
    }

    /**
     * Set the root of this panel controller.<br>
     * This routine is invoked by {@link SceneBuilderBeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)}
     * using {@link FxmlController#setRoot(Parent)}
     *
     * @param panelRoot the root panel (non null).
     */
    public void setRoot(Parent panelRoot) {
        assert panelRoot != null;
        this.panelRoot = panelRoot;

        if (sceneBuilderManager != null) {
            sceneBuilderManager.stylesheetConfig()
                .subscribeOn(JavaFxScheduler.platform()).subscribe(s -> {
                toolStylesheetDidChange(s);
            });
        }
        panelRoot.setOnMouseEntered((e) -> notifyFocused());

        panelRoot.sceneProperty().addListener((obj, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.focusOwnerProperty().removeListener(focusListener);
            }
            if (newScene != null) {
                newScene.focusOwnerProperty().addListener(focusListener);
            }
        });

    }

    /**
     * Replaces old Stylesheet config by the tool style sheet assigned to this
     * controller. This methods is event binded to {@link DocumentManager#stylesheetConfig()} using an RxJava2 subscription.
     *
     * @param newToolStylesheetConfig null or the new style sheet configuration to apply
     */
    protected void toolStylesheetDidChange(ToolStylesheetProvider newToolStylesheetConfig) {

        if (panelRoot == null) { // nothing to style so return
            return;
        }

        if (toolStylesheetConfig != null) { // if old conf then removeit
            panelRoot.getStylesheets().remove(toolStylesheetConfig.getUserAgentStylesheet());
            panelRoot.getStylesheets().removeAll(toolStylesheetConfig.getStylesheets());
        }

        if (newToolStylesheetConfig != null) { // replace the active conf only if the new one is valid
            toolStylesheetConfig = newToolStylesheetConfig;
        }

        //apply the conf if the current one is valid
        if (toolStylesheetConfig != null) {
            if (toolStylesheetConfig.getUserAgentStylesheet() != null) {
                panelRoot.getStylesheets().add(toolStylesheetConfig.getUserAgentStylesheet());
            }
            if (toolStylesheetConfig.getStylesheets() != null) {
                logger.info("Applying new tool theme using {} on {}",
                        toolStylesheetConfig.getStylesheets(), this.getClass().getName());
                panelRoot.getStylesheets().addAll(toolStylesheetConfig.getStylesheets());
            }
        }
    }
}
