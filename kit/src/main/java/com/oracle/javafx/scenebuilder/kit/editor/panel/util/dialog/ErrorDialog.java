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
package com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import javafx.stage.Window;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ErrorDialog extends AlertDialog {

    private final ApplicationContext context;
    //private final SceneBuilderManager sceneBuilderManager;

    private String debugInfo;

    protected ErrorDialog(Api api, Window owner) {
        super(api, owner);
        this.context = api.getContext();
    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        setOKButtonVisible(false);
        setShowDefaultButton(true);
        setDefaultButtonID(AlertDialog.ButtonID.CANCEL);
        setCancelButtonTitle(I18N.getString("label.close"));
        setActionButtonTitle(I18N.getString("error.dialog.label.details"));
        setActionButtonVisible(true);
        setActionRunnable(() -> showDetailsDialog());
        updateActionButtonVisibility(); // not visible by default
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
        updateActionButtonVisibility();
    }

    public void setDebugInfoWithThrowable(Throwable t) {
        final String info;

        if (t == null) {
            info = null;
        } else {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            t./**/printStackTrace(pw);
            info = sw.toString();
        }

        setDebugInfo(info);
    }

    /*
     * Private
     */

    private void updateActionButtonVisibility() {
        setActionButtonVisible(debugInfo != null);
    }

    private void showDetailsDialog() {
        final TextViewDialog detailDialog = context.getBean(TextViewDialog.class);
        detailDialog.setText(debugInfo);
        detailDialog.showAndWait();
    }
}
