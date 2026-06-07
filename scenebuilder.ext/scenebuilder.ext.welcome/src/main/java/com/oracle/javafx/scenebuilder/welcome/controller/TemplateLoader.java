/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.welcome.controller;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationSingleton;
import com.treilhes.jfxplace.core.api.application.ApplicationActionFactory;
import com.treilhes.jfxplace.core.api.application.InstancesManager;
import com.treilhes.jfxplace.core.api.job.JobManager;
import com.treilhes.jfxplace.fxom.api.document.DocumentActionFactory;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;
import com.oracle.javafx.scenebuilder.api.template.Template;


/**
 * Allow control of {@link Template} selection and loading into a document window
 *
 */
@ApplicationSingleton
public class TemplateLoader {

    public static final Logger logger = LoggerFactory.getLogger(TemplateLoader.class);

    private final InstancesManager instancesManager;
    private final ApplicationActionFactory applicationActionFactory;
    private final DocumentActionFactory documentActionFactory;
    /**
     * Instantiates a new template loader.
     *
-     * @param instancesManager the main controller instance
     */
    public TemplateLoader(
            InstancesManager instancesManager,
            ApplicationActionFactory applicationActionFactory,
            DocumentActionFactory documentActionFactory) {
    	this.instancesManager = instancesManager;
    	this.applicationActionFactory = applicationActionFactory;
    	this.documentActionFactory = documentActionFactory;
    }

    /**
     * Load template in the provided document window.
     *
     * @param template the template
     * @param document the document
     */
    public void loadTemplate(Template template) {

        if (template == null) {
            return;
        }

        try {
            URL file = template.getFxmlUrl();
            applicationActionFactory.newInstance(instance -> {

                documentActionFactory.loadURL(file, false).perform();

                var instanceContext = instance.getContext();

                if (!template.getThemes().isEmpty()) {
                    var instanceEvents = instanceContext.getBean(FxomEvents.class);
                    var theme = template.getThemes().get(0);
                    instanceEvents.stylesheetConfig().set(theme);
                }

                if (template.getWidth() != 0 && template.getHeight() != 0) {
                    var jobManager = instanceContext.getBean(JobManager.class);
                    var sbJobsFactory = instanceContext.getBean(SbJobsFactory.class);
                    final var job = sbJobsFactory.useSize(template.getWidth(), template.getHeight());
                    if (job.isExecutable()) {
                        jobManager.push(job);
                    }
                }
            });


        } catch (Exception e) {
            logger.error("Unable to load template", e);
        }
    }
}
