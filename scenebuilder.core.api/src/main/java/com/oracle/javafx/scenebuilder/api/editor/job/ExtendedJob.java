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
package com.oracle.javafx.scenebuilder.api.editor.job;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ExtendedJob<T extends Job> extends Job {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedJob.class);

    private List<JobExtension<T>> extensions;

    private boolean extended = false;
    private final Job job;

    @SuppressWarnings("unchecked")
    public ExtendedJob(T job) {
        super(job.getContext(), job.getEditorController());
        this.job = job;

        ResolvableType resolvable = ResolvableType.forClassWithGenerics(JobExtension.class, job.getClass());
        String[] beanNamesForType = getContext().getBeanNamesForType(resolvable);

        if (beanNamesForType.length > 0) {
            extensions = Arrays.asList(beanNamesForType).stream().map(b -> (JobExtension<T>) getContext().getBean(b))
                    .collect(Collectors.toList());
        }

        if (extensions != null) {
            extensions.forEach(ext -> ext.setExtendedJob(job));
            extended = true;
        }

    }

    @Override
    public boolean isExecutable() {
        return job.isExecutable();
    }

    @Override
    public void execute() {
        if (extended) {
            extensions.stream().filter(ext -> ext.isExecutable()).forEach(ext -> {
                logger.debug("preExecute extension {}", ext.getClass().getName());
                ext.preExecute();
                logger.debug("preExecute extension {} done", ext.getClass().getName());
            });
        }

        job.execute();
        logger.debug("execute job {} : {} done", job.getClass().getName(), job.getDescription());

        if (extended) {
            extensions.stream().filter(ext -> ext.isExecutable()).forEach(ext -> {
                logger.debug("postExecute extension {}", ext.getClass().getName());
                ext.postExecute();
                logger.debug("postExecute extension {} done", ext.getClass().getName());
            });
        }
    }

    @Override
    public void undo() {
        if (extended) {
            extensions.forEach(ext -> {
                logger.debug("preUndo extension {}", ext.getClass().getName());
                ext.preUndo();
                logger.debug("preUndo extension {} done", ext.getClass().getName());
            });
        }

        logger.debug("undo job {} : {}", job.getClass().getName(), job.getDescription());
        job.undo();
        logger.debug("undo job {} done", job.getClass().getName());

        if (extended) {
            extensions.forEach(ext -> {
                logger.debug("postUndo extension {}", ext.getClass().getName());
                ext.postUndo();
                logger.debug("postUndo extension {} done", ext.getClass().getName());
            });
        }
    }

    @Override
    public void redo() {
        if (extended) {
            extensions.forEach(ext -> {
                logger.debug("preRedo extension {}", ext.getClass().getName());
                ext.preRedo();
                logger.debug("preRedo extension {} done", ext.getClass().getName());
            });
        }

        logger.debug("redo job {} : {}", job.getClass().getName(), job.getDescription());
        job.redo();
        logger.debug("redo job {} done", job.getClass().getName());

        if (extended) {
            extensions.forEach(ext -> {
                logger.debug("postRedo extension {}", ext.getClass().getName());
                ext.postRedo();
                logger.debug("postRedo extension {} done", ext.getClass().getName());
            });
        }
    }

    @Override
    public String getDescription() {
        return job.getDescription();
    }

    public Job getExtendedJob() {
        return job;
    }

}
