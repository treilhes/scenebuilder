/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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

import java.io.File;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.application.InstancesManager;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;
import com.oracle.javafx.scenebuilder.api.template.Template;


/**
 * Allow control of {@link Template} selection and loading into a document window
 * @author ptreilhes
 *
 */
@ApplicationInstanceSingleton
public class TemplateLoader {

    public final static Logger logger = LoggerFactory.getLogger(TemplateLoader.class);

    private final InstancesManager instancesManager;
    /**
     * Instantiates a new template loader.
     *
-     * @param instancesManager the main controller instance
     */
    public TemplateLoader(InstancesManager instancesManager) {
    	this.instancesManager = instancesManager;
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
            File file = new File(template.getFxmlUrl().toURI());
            var instance = instancesManager.open(file, false);
            var instanceContext = instance.getContext();

            if (!template.getThemes().isEmpty()) {
                var instanceEvents = instanceContext.getBean(ApplicationInstanceEvents.class);
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
        } catch (URISyntaxException e) {
            logger.error("Unable to load template", e);
        }
    }
}
