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

package com.oracle.javafx.scenebuilder.template.controller;

import java.util.List;

import org.graalvm.compiler.lir.CompositeValue.Component;

import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.template.Template;
import com.gluonhq.jfxapps.core.api.template.TemplateGroup;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;

import javafx.stage.Modality;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class TemplatesWindowController extends TemplatesBaseWindowController {
    public TemplatesWindowController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            @Autowired ApplicationInstanceWindow document,
            @Autowired List<TemplateGroup> templateGroups,
            @Autowired List<Template> templates) {
        super(sceneBuilderManager, iconSetting, TemplatesWindowController.class.getResource("TemplatesWindow.fxml"),
                I18N.getBundle(), document, templateGroups, templates);
    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();

        setupTemplateButtonHandlers();
    }

    @Override
    public void onCloseRequest() {
        getStage().hide();
    }

    @Override
    public void onFocus() {
    }

    @Override
    protected void controllerDidCreateStage() {
        super.controllerDidCreateStage();
        getStage().setTitle(I18N.getString("template.dialog.title"));
        getStage().initModality(Modality.WINDOW_MODAL);
    }
}
