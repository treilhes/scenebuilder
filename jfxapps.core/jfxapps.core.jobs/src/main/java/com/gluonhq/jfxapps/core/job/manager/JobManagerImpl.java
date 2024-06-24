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
package com.gluonhq.jfxapps.core.job.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.context.annotation.NonNull;
import com.gluonhq.jfxapps.core.api.factory.AbstractFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.job.JobPipeline;
import com.gluonhq.jfxapps.core.api.job.base.BatchJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.collector.CompositeCollector;
import com.gluonhq.jfxapps.core.fxom.collector.ExpressionCollector;
import com.gluonhq.jfxapps.core.fxom.collector.ExpressionCollector.ExpressionReference;
import com.gluonhq.jfxapps.core.fxom.collector.FXOMCollector;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector.FxIdMap;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector.FxReferenceBySource;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * @treatAsPrivate
 */
/**
 *
 */
/**
 *
 */
@ApplicationInstanceSingleton
public class JobManagerImpl implements JobManager {

    private static final int UNDO_STACK_MAX_SIZE = 50;

    private final DocumentManager documentManager;
    private final JobPipelineFactory jobPipelineFactory;
    private final BatchJob.Factory batchJobFactory;

    private final List<Job> undoStack = new ArrayList<>();
    private final List<Job> redoStack = new ArrayList<>();
    private final SimpleIntegerProperty revision = new SimpleIntegerProperty();
    private boolean lock;





    public JobManagerImpl(
            DocumentManager documentManager,
            JobPipelineFactory jobPipelineFactory,
            BatchJob.Factory batchJobFactory) {
        this.documentManager = documentManager;
        this.jobPipelineFactory = jobPipelineFactory;
        this.batchJobFactory = batchJobFactory;

        revision.addListener((ob, o, n) -> documentManager.dirty().set(true));

        documentManager.fxomDocument().subscribe(om -> clear());
    }

    @Override
    public List<Job> getUndoStack() {
        return Collections.unmodifiableList(undoStack);
    }

    @Override
    public List<Job> getRedoStack() {
        return Collections.unmodifiableList(redoStack);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Job push(Job job) {
        assert job != null;
        assert job.isExecutable();

        if (lock) {
            // Method is called from a revision property listener
            throw new IllegalStateException("Pushing jobs from another job or a job manager listener is forbidden"); // NOCHECK
        }

        final var defaultJobPipeline = new DefaultJobPipeline();
        final var jobPipeline = jobPipelineFactory.get();
        final var composite = new CompositeJobPipeline(defaultJobPipeline, jobPipeline);
        final var executedJob = executePipeline(composite, job);

        undoStack.add(0, executedJob);
        if (undoStack.size() > UNDO_STACK_MAX_SIZE) {
            undoStack.remove(undoStack.size() - 1);
        }
        redoStack.clear();
        incrementRevision();

        return executedJob;
    }

    @Override
    public void clear() {
        if (lock) {
            // Method is called from a revision property listener
            throw new IllegalStateException(
                    "Clearing job stack from another job or a job manager listener is forbidden"); // NOCHECK
        }

        undoStack.clear();
        redoStack.clear();
        // We don't change the revision.
    }

    @Override
    public boolean canUndo() {
        return undoStack.isEmpty() == false;
    }

    @Override
    public String getUndoDescription() {
        final String result;
        if (canUndo()) {
            result = undoStack.get(0).getDescription();
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public void undo() {
        assert canUndo();

        if (lock) {
            // Method is called from a revision property listener
            throw new IllegalStateException("Undoing jobs from another job or a job manager listener is forbidden"); // NOCHECK
        }

        final Job job = undoStack.get(0);
        undoJob(job);
        undoStack.remove(0);
        redoStack.add(0, job);
        incrementRevision();
    }

    @Override
    public boolean canRedo() {
        return redoStack.isEmpty() == false;
    }

    @Override
    public String getRedoDescription() {
        final String result;
        if (canRedo()) {
            result = redoStack.get(0).getDescription();
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public void redo() {
        assert canRedo();

        if (lock) {
            // Method is called from a revision property listener
            throw new IllegalStateException("Redoing jobs from another job or a job manager listener is forbidden"); // NOCHECK
        }

        final Job job = redoStack.get(0);
        redoJob(job);
        redoStack.remove(0);
        undoStack.add(0, job);
        incrementRevision();
    }

    /**
     * Returns the property holding the revision number of this job manager. Job
     * manager adds +1 to this number each time a job is done or undone.
     *
     * @return the property holding the revision number of this job manager.
     */
    @Override
    public ReadOnlyIntegerProperty revisionProperty() {
        return revision;
    }

    /**
     * Returns the job which has just been processed and which can be undone.
     *
     * @return the current job, which is the one at index 0 in the undo stack. It
     *         can be null.
     */
    @Override
    public Job getCurrentJob() {
        if (undoStack.size() > 0) {
            return undoStack.get(0);
        } else {
            return null;
        }
    }

    /*
     * Private
     */

    private Job executePipeline(JobPipeline pipeline, Job job) {
        final var batch = batchJobFactory.getJob();

        lock = true;
        try {

            final var preExecutionCollectors = pipeline.preExecutionCollectors();
            final var document = documentManager.fxomDocument().get();

            if (document != null) {
                final var composite = CompositeCollector.of(preExecutionCollectors.values());
                if (!composite.isEmpty()) {
                    document.collect(composite);
                }
            }

            final var preExecutionJob = pipeline.preExecutionJob(null);
            if (preExecutionJob != null) {
                batch.addSubJob(preExecutionJob);
            }

            job.execute();
            batch.addSubJob(job);
            batch.setDescription(job.getDescription());

            final var postExecutionCollectors = pipeline.postExecutionCollectors();
            if (document != null) {
                final var composite = CompositeCollector.of(postExecutionCollectors.values());
                if (!composite.isEmpty()) {
                    document.collect(composite);
                }
            }

            final var postExecutionJob = pipeline.postExecutionJob(null);
            if (postExecutionJob != null) {
                batch.addSubJob(postExecutionJob);
            }

            return batch;
        } finally {
            lock = false;
        }
    }

    private void undoJob(Job job) {
        lock = true;
        try {
            job.undo();
        } finally {
            lock = false;
        }
    }

    private void redoJob(Job job) {
        lock = true;
        try {
            job.redo();
        } finally {
            lock = false;
        }
    }

    private void incrementRevision() {
        lock = true;
        try {
            revision.set(revision.get() + 1);
        } finally {
            lock = false;
        }
    }

    protected class DefaultJobPipeline implements JobPipeline{


        protected DefaultJobPipeline() {
            final ExpressionReference fxValueReference = ExpressionCollector.allExpressionReferences();
            final FxReferenceBySource fxIntrinsicReference = FxCollector.allFxReferences();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, FXOMCollector<?>> preExecutionCollectors() {
            return Map.of();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, FXOMCollector<?>> postExecutionCollectors() {
            return Map.of();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Job preExecutionJob(Map<String, FXOMObject> preIdMap) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Job postExecutionJob(Map<String, FXOMObject> preIdMap) {
            return null;
        }
    }

    private class CompositeJobPipeline implements JobPipeline{

        final DefaultJobPipeline defaultPipeline;
        final JobPipeline customPipeline;
        final FxIdMap preIdCollector = FxCollector.fxIdsMap();
        final FxIdMap postIdCollector = FxCollector.fxIdsMap();

        protected CompositeJobPipeline(@NonNull DefaultJobPipeline defaultPipeline, JobPipeline customPipeline) {
            this.defaultPipeline = defaultPipeline;
            this.customPipeline = customPipeline;

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, FXOMCollector<?>> preExecutionCollectors() {
            Map<String, FXOMCollector<?>> result = new HashMap<>();

            result.put(preIdCollector.toString(), preIdCollector);

            if (defaultPipeline != null && defaultPipeline.preExecutionCollectors() != null) {
                result.putAll(defaultPipeline.preExecutionCollectors());
            }
            if (customPipeline != null && customPipeline.preExecutionCollectors() != null) {
                result.putAll(customPipeline.preExecutionCollectors());
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, FXOMCollector<?>> postExecutionCollectors() {
            Map<String, FXOMCollector<?>> result = new HashMap<>();

            result.put(postIdCollector.toString(), preIdCollector);

            if (defaultPipeline != null && defaultPipeline.postExecutionCollectors() != null) {
                result.putAll(defaultPipeline.postExecutionCollectors());
            }
            if (customPipeline != null && customPipeline.postExecutionCollectors() != null) {
                result.putAll(customPipeline.postExecutionCollectors());
            }

            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Job preExecutionJob(Map<String, FXOMObject> preIdMap) {
            final var batch = batchJobFactory.getJob();

            preIdMap = preIdCollector.getCollected();

            final var defaultJob = defaultPipeline != null ? defaultPipeline.preExecutionJob(preIdMap) : null;
            if (defaultJob != null) {
                batch.addSubJob(defaultJob);
            }

            final var customJob = customPipeline != null ? customPipeline.preExecutionJob(preIdMap) : null;
            if (customJob != null) {
                batch.addSubJob(customJob);
            }
            return batch;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Job postExecutionJob(Map<String, FXOMObject> postIdMap) {
            final var batch = batchJobFactory.getJob();

            postIdMap = postIdCollector.getCollected();

            final var defaultJob = defaultPipeline != null ? defaultPipeline.postExecutionJob(postIdMap) : null;
            if (defaultJob != null) {
                batch.addSubJob(defaultJob);
            }

            final var customJob = customPipeline != null ? customPipeline.postExecutionJob(postIdMap) : null;
            if (customJob != null) {
                batch.addSubJob(customJob);
            }
            return batch;
        }
    }

    @ApplicationInstanceSingleton
    protected static class JobPipelineFactory extends AbstractFactory<JobPipeline>{

        public JobPipelineFactory(JfxAppContext sbContext) {
            super(sbContext);
        }

        public JobPipeline get() {
            return create(JobPipeline.class, null);
        }
    }

}
