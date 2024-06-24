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
package com.gluonhq.jfxapps.core.api.job.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtension;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;

/**
 * An implementation of the {@link Job} interface to serve as a basis for implementing various kinds of jobs.
 * This class implements an extension mechanism for the job. It allows multiple extensions to be loaded using the provided {@link JobExtensionFactory} during instantiation
 * The class implements functions like execute/undo/redo and the subclasses are intended to implements function like doExecute/doundo/doRedo.<br/>.
 * {@link Job#execute()}/{@link Job#undo()}/{@link Job#redo()} functions wrap the call to {@link AbstractJob#doExecute()}/{@link AbstractJob#doUndo()}/{@link AbstractJob#doRedo()}
 * between entension functions like {@link JobExtension#preExecute()}/{@link JobExtension#postExecute()}, {@link JobExtension#preUndo()}/{@link JobExtension#postUndo()},
 * {@link JobExtension#preRedo()}/{@link JobExtension#postRedo()}.
 * <br/>
 * Currently there is no guarantee on the order of the extensions function execution.
 * <br/>
 *
 */
public abstract class AbstractJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(AbstractJob.class);

    private final List<JobExtension<?>> extensions = new ArrayList<>();

    private String description;

    private final List<Job> subJobs = new ArrayList<>();

    public AbstractJob(JobExtensionFactory extensionFactory) {

        if (extensionFactory != null) {
            extensions.addAll(extensionFactory.getExtensions(this));
        }

        // Should normally not reach the user
        setDescription(getClass().getSimpleName());
    }

    @Override
    public final void execute() {
        if (!extensions.isEmpty()) {
            extensions.stream().filter(ext -> ext.isExecutable()).forEach(ext -> {
                logger.debug("preExecute extension {}", ext.getClass().getName());
                ext.preExecute();
                logger.debug("preExecute extension {} done", ext.getClass().getName());
            });
        }

        doExecute();
        logger.debug("execute job {} : {} done", getClass().getName(), getDescription());

        if (!extensions.isEmpty()) {
            extensions.stream().filter(ext -> ext.isExecutable()).forEach(ext -> {
                logger.debug("postExecute extension {}", ext.getClass().getName());
                ext.postExecute();
                logger.debug("postExecute extension {} done", ext.getClass().getName());
            });
        }
    }

    @Override
    public final void undo() {
        if (!extensions.isEmpty()) {
            extensions.forEach(ext -> {
                logger.debug("preUndo extension {}", ext.getClass().getName());
                ext.preUndo();
                logger.debug("preUndo extension {} done", ext.getClass().getName());
            });
        }

        logger.debug("undo job {} : {}", getClass().getName(), getDescription());
        doUndo();
        logger.debug("undo job {} done", getClass().getName());

        if (!extensions.isEmpty()) {
            extensions.forEach(ext -> {
                logger.debug("postUndo extension {}", ext.getClass().getName());
                ext.postUndo();
                logger.debug("postUndo extension {} done", ext.getClass().getName());
            });
        }
    }

    @Override
    public final void redo() {
        if (!extensions.isEmpty()) {
            extensions.forEach(ext -> {
                logger.debug("preRedo extension {}", ext.getClass().getName());
                ext.preRedo();
                logger.debug("preRedo extension {} done", ext.getClass().getName());
            });
        }

        logger.debug("redo job {} : {}", getClass().getName(), getDescription());
        doRedo();
        logger.debug("redo job {} done", getClass().getName());

        if (!extensions.isEmpty()) {
            extensions.forEach(ext -> {
                logger.debug("postRedo extension {}", ext.getClass().getName());
                ext.postRedo();
                logger.debug("postRedo extension {} done", ext.getClass().getName());
            });
        }
    }


    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public abstract boolean isExecutable();
    public abstract void doExecute();
    public abstract void doUndo();
    public abstract void doRedo();

    public void addSubJob(Job subJob) {
        assert subJob != null;
        this.subJobs.add(subJob);
    }

    public void addSubJobs(List<Job> subJobs) {
        assert subJobs != null;
        this.subJobs.addAll(subJobs);
    }

    @Override
    public List<Job> getSubJobs() {
        return Collections.unmodifiableList(subJobs);
    }

    public void prependSubJob(Job subJob) {
        assert subJob != null;
        this.subJobs.add(0, subJob);
    }


}
