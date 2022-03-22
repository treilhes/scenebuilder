/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.ui.dialog;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;

import javafx.stage.Window;

@Component
public class DialogController implements Dialog {

    private final SceneBuilderBeanFactory context;
    private final SceneBuilderManager sceneBuilderManager;
    private final IconSetting iconSetting;

    public DialogController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            SceneBuilderBeanFactory context) {
	    this.sceneBuilderManager = sceneBuilderManager;
	    this.iconSetting = iconSetting;
	    this.context = context;
	}


    @Override
    public void showErrorAndWait(Window owner, String title, String message, String detail, Throwable cause) {
        final ErrorDialog errorDialog = (ErrorDialog)context.getBean("errorDialog", sceneBuilderManager, iconSetting, context, owner);

        errorDialog.setTitle(title);
        errorDialog.setMessage(message);
        errorDialog.setDetails(detail);
        errorDialog.setDebugInfoWithThrowable(cause);
        errorDialog.showAndWait();
    }
    @Override
    public void showErrorAndWait(Window owner, String title, String message, String detail) {
        showErrorAndWait(owner, title, message, detail, null);
    }
    @Override
	public void showErrorAndWait(String title, String message, String detail, Throwable cause) {
		showErrorAndWait(null, title, message, detail, cause);
	}
	@Override
    public void showErrorAndWait(String title, String message, String detail) {
        showErrorAndWait(null, title, message, detail, null);
    }

	@Override
    public Alert customAlert() {
        return customAlert(null);
    }
    @Override
    public Alert customAlert(Window owner) {
        return (Alert)context.getBean("alertDialog", sceneBuilderManager, iconSetting, owner);
    }

	@Override
    public void showAlertAndWait(Window owner, String title, String message, String detail) {
        Alert alert = customAlert();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setDetails(detail);
        alert.setActionButtonDisable(true);
        alert.setActionButtonVisible(false);
        alert.setOKButtonDisable(true);
        alert.setOKButtonVisible(false);
        alert.setCancelButtonTitle(I18N.getString("label.close"));
        alert.showAndWait();
    }

	@Override
    public void showAlertAndWait(String title, String message, String detail) {
	    showAlertAndWait(null, title, message, detail);
	}
}
