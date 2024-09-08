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

import org.graalvm.compiler.lir.CompositeValue.Component;

import com.gluonhq.jfxapps.core.api.action.Action;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstance;
import com.gluonhq.jfxapps.core.api.application.InstancesManager;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.template.Template;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemeDocumentPreference;


/**
 * Allow control of {@link Template} selection and loading into a document window
 * @author ptreilhes
 *
 */
@Component
public class TemplateController {

    private final InstancesManager main;
    private final SceneBuilderBeanFactory context;
    private final ActionFactory actionFactory;
    private final JobManager jobManager;
    private final UseSizeJob.Factory useSizeJobFactory;
    /**
     * Instantiates a new template controller.
     *
     * @param context the context
     * @param main the main controller instance
     */
    public TemplateController(
            SceneBuilderBeanFactory context,
            ActionFactory actionFactory,
    		InstancesManager main,
    		@Lazy JobManager jobManager,
    		UseSizeJob.Factory useSizeJobFactory) {
        this.context = context;
        this.actionFactory = actionFactory;
        this.jobManager = jobManager;
        this.useSizeJobFactory = useSizeJobFactory;
    	this.main = main;
    }

    /**
     * Open template selection window.
     *
     * @param openInNewWindow if true the template will be opened in new document window or an empty one
     */
    public void openTemplateSelectionWindow(boolean openInNewWindow) {
        TemplatesWindowController templateWindow = context.getBean(TemplatesWindowController.class);
        if (openInNewWindow) {
            templateWindow.setOnTemplateChosen(this::loadTemplateInNewWindow);
        } else {
            templateWindow.setOnTemplateChosen(this::loadTemplateInCurrentWindow);
        }
        templateWindow.openWindow();
    }

    /**
     * Load the template in a new document window.
     *
     * @param template the template
     */
    public void loadTemplateInNewWindow(Template template) {
        final ApplicationInstance newTemplateWindow = main.newInstance();
        loadTemplateInWindow(template, newTemplateWindow);
    }

    /**
     * Load template in current document window.
     *
     * @param template the template
     */
    public void loadTemplateInCurrentWindow(Template template) {
        loadTemplateInWindow(template, context.getBean(ApplicationInstance.class));
    }

    /**
     * Load template in the provided document window.
     *
     * @param template the template
     * @param document the document
     */
    private void loadTemplateInWindow(Template template, ApplicationInstance document) {

        if (template != null && template.getFxmlUrl() != null) {
            document.loadFromURL(template.getFxmlUrl(), false);
        }
        document.openWindow();

        if (template != null && template.getThemes().size() > 0) {
            ThemeDocumentPreference docThemePref = context.getBean(ThemeDocumentPreference.class);
            docThemePref.setValue(template.getThemes().get(0));
            docThemePref.writeToJavaPreferences();
            Action action = actionFactory.create(ApplyCssContentAction.class);
            action.checkAndPerform();
        }

        if (template != null && (template.getWidth() != 0 || template.getHeight() != 0)) {
            final AbstractJob job = useSizeJobFactory.getJob(template.getWidth(), template.getHeight());
            if (job.isExecutable()) {
                jobManager.push(job);
            }
        }

    }
}
