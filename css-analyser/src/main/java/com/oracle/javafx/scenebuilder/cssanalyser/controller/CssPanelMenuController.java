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
package com.oracle.javafx.scenebuilder.cssanalyser.controller;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import javafx.scene.control.MenuItem;


/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class CssPanelMenuController {
     private boolean showStyledOnly = false;
    private boolean splitDefaults = false;
    private final CssPanelController cssPanelController;

    public CssPanelMenuController(CssPanelController cssPanelController) {
        this.cssPanelController = cssPanelController;
    }

    public void viewRules() {
        cssPanelController.changeView(CssPanelController.View.RULES);
    }

    public void viewTable() {
        cssPanelController.changeView(CssPanelController.View.TABLE);
    }

    public void viewText() {
        cssPanelController.changeView(CssPanelController.View.TEXT);
    }

    public void copyStyleablePath() {
        cssPanelController.copyStyleablePath();
    }

    public void splitDefaultsAction(MenuItem cssPanelSplitDefaultsMi) {
        cssPanelController.splitDefaultsAction();
        splitDefaults = !splitDefaults;
        if (splitDefaults) {
            cssPanelSplitDefaultsMi.setText(I18N.getString("csspanel.defaults.join"));
        } else {
            cssPanelSplitDefaultsMi.setText(I18N.getString("csspanel.defaults.split"));
        }
    }

    public void showStyledOnly(MenuItem cssPanelShowStyledOnlyMi) {
        cssPanelController.showStyledOnly();
        showStyledOnly = !showStyledOnly;
        if (showStyledOnly) {
            cssPanelShowStyledOnlyMi.setText(I18N.getString("csspanel.show.default.values"));
        } else {
            cssPanelShowStyledOnlyMi.setText(I18N.getString("csspanel.hide.default.values"));
        }
    }

}