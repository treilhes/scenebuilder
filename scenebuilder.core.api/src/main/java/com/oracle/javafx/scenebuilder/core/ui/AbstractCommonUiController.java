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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider;
import com.oracle.javafx.scenebuilder.core.di.FxmlController;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactoryPostProcessor;
import com.oracle.javafx.scenebuilder.util.NodeUtils;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;

public abstract class AbstractCommonUiController  {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AbstractCommonUiController.class);

    /** The scene builder api. */
    private final Api api;

    /** The panel root. */
    private Parent panelRoot;

    /** The tool stylesheet config. */
    private StylesheetProvider toolStylesheetConfig;

    /** The scene builder manager. */
    private final SceneBuilderManager sceneBuilderManager;

    private ChangeListener<? super Node> focusListener = (obj, old, node) -> {
        if (NodeUtils.isDescendantOf(panelRoot, node)) {
            logger.info("Active component : {}", this.getClass().getName());
            getApi().getApiDoc().getDocumentManager().focused().set(this);
        }
    };

    /**
     * Base constructor for invocation by the subclasses.
     * Subclass implementations should make sure that this constructor can be
     * invoked outside of the JavaFX thread.
     *
     * @param api the api object
     */
    protected AbstractCommonUiController(Api api) {
        this.api = api;
        this.sceneBuilderManager = api.getSceneBuilderManager();
       //this.documentManager = api.getApiDoc().getDocumentManager();
    }
    
    /**
     * Returns the root FX object of this panel.
     *
     * @return the root object of the panel (never null)
     */
    public Parent getRoot() {
        assert panelRoot != null;
        //startListeners();
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

        panelRoot.sceneProperty().addListener((obj, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.focusOwnerProperty().removeListener(focusListener);
            }
            if (newScene != null) {
                newScene.focusOwnerProperty().addListener(focusListener);
            }
        });
    }
    
    
    
    public Api getApi() {
        return api;
    }
    
    /**
     * Replaces old Stylesheet config by the tool style sheet assigned to this
     * controller. This methods is event binded to {@link DocumentManager#stylesheetConfig()} using an RxJava2 subscription.
     *
     * @param newToolStylesheetConfig null or the new style sheet configuration to apply
     */
    protected void toolStylesheetDidChange(StylesheetProvider newToolStylesheetConfig) {

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