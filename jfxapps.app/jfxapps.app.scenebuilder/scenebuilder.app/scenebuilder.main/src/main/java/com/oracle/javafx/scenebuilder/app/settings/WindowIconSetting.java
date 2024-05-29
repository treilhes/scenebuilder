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
package com.oracle.javafx.scenebuilder.app.settings;

import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.settings.AbstractSetting;
import com.gluonhq.jfxapps.core.api.ui.misc.IconSetting;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@Component
public class WindowIconSetting extends AbstractSetting implements IconSetting {
	
	public static final String APP_ICON_16 = WindowIconSetting.class.getResource("SceneBuilderLogo_16.png").toString();
    public static final String APP_ICON_32 = WindowIconSetting.class.getResource("SceneBuilderLogo_32.png").toString();

	public WindowIconSetting() {}
	
	@Override
    public void setWindowIcon(Alert alert) {
        setWindowIcon((Stage)alert.getDialogPane().getScene().getWindow());
    }
    @Override
    public void setWindowIcon(Stage stage) {
        Image icon16 = new Image(WindowIconSetting.APP_ICON_16);
        Image icon32 = new Image(WindowIconSetting.APP_ICON_32);
        stage.getIcons().addAll(icon16, icon32);
    }

}
