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
package com.oracle.javafx.scenebuilder.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.ExtendedAction;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.template.Template;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;
import com.oracle.javafx.scenebuilder.job.editor.UseSizeJob;

/**
 *
 */
@Component
public class TemplateController {

    private final Main main;
    private final ApplicationContext context;
    
    public TemplateController(
            @Autowired ApplicationContext context,
    		@Autowired Main main) {
        this.context = context;
    	this.main = main;
    }

    public void openTemplateSelectionWindow(boolean openInNewWindow) {
        TemplatesWindowController templateWindow = context.getBean(TemplatesWindowController.class);
        if (openInNewWindow) {
            templateWindow.setOnTemplateChosen(this::loadTemplateInNewWindow);
        } else {
            templateWindow.setOnTemplateChosen(this::loadTemplateInCurrentWindow);
        }
        templateWindow.openWindow();
    }

    public void loadTemplateInNewWindow(Template template) {
        final DocumentWindow newTemplateWindow = main.makeNewWindow();
        loadTemplateInWindow(template, newTemplateWindow);
    }
    
    public void loadTemplateInCurrentWindow(Template template) {
        loadTemplateInWindow(template, context.getBean(DocumentWindow.class));
    }

    private void loadTemplateInWindow(Template template, DocumentWindow document) {

        if (template != null && template.getFxmlUrl() != null) {
            document.loadFromURL(template.getFxmlUrl());
        }
        document.openWindow();

        if (template != null && template.getThemes().size() > 0) {
            ThemePreference docThemePref = context.getBean(ThemePreference.class);
            docThemePref.setValue(template.getThemes().get(0));
            docThemePref.writeToJavaPreferences();
            ExtendedAction<?> extendedJob = context.getBean(ApplyCssContentAction.class).extend();
            extendedJob.checkAndPerform();
        }
        
        if (template != null && (template.getWidth() != 0 || template.getHeight() != 0)) {
            final Job job = new UseSizeJob(context, context.getBean(Editor.class), template.getWidth(), template.getHeight()).extend();
            if (job.isExecutable()) {
                context.getBean(JobManager.class).push(job);
            }
        }
        
    }
}
