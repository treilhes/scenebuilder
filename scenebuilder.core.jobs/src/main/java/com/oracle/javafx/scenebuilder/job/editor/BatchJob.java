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
package com.oracle.javafx.scenebuilder.job.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

/**
 *
 */
public class BatchJob extends Job {

    private final List<Job> subJobs = new ArrayList<>();
    private final boolean shouldRefreshSceneGraph;
    private final boolean shouldUpdateSelection;
    private final String description;
    private final FXOMDocument fxomDocument;
    private final Selection selection;


    public BatchJob(SceneBuilderBeanFactory context, Editor editor,
            boolean shouldRefreshSceneGraph,
            boolean shouldUpdateSelection,
            String description) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selection = documentManager.selectionDidChange().get().getSelection();
        this.description = description;
        this.shouldRefreshSceneGraph = shouldRefreshSceneGraph;
        this.shouldUpdateSelection = shouldUpdateSelection;
    }

    public BatchJob(SceneBuilderBeanFactory context, Editor editor,
            boolean shouldRefreshSceneGraph, String description) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selection = documentManager.selectionDidChange().get().getSelection();
        this.description = description;
        this.shouldRefreshSceneGraph = shouldRefreshSceneGraph;
        this.shouldUpdateSelection = true;
    }

     public BatchJob(SceneBuilderBeanFactory context, Editor editor, String description) {
         super(context, editor);
         DocumentManager documentManager = context.getBean(DocumentManager.class);
         this.fxomDocument = documentManager.fxomDocument().get();
         this.selection = documentManager.selectionDidChange().get().getSelection();
         this.description = description;
         this.shouldRefreshSceneGraph = true;
         this.shouldUpdateSelection = true;
    }

   public BatchJob(SceneBuilderBeanFactory context, Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selection = documentManager.selectionDidChange().get().getSelection();
        this.description = getClass().getSimpleName();
        this.shouldRefreshSceneGraph = true;
        this.shouldUpdateSelection = true;
    }

    public void addSubJob(Job subJob) {
        assert subJob != null;
        this.subJobs.add(subJob);
    }

    public void addSubJobs(List<Job> subJobs) {
        assert subJobs != null;
        this.subJobs.addAll(subJobs);
    }

    public void prependSubJob(Job subJob) {
        assert subJob != null;
        this.subJobs.add(0, subJob);
    }

    public List<Job> getSubJobs() {
        return Collections.unmodifiableList(subJobs);
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return subJobs.isEmpty() == false;
    }

    @Override
    public void execute() {
        if (shouldUpdateSelection) {
            selection.beginUpdate();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.beginUpdate();
        }
        for (Job subJob : subJobs) {
            subJob.execute();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.endUpdate();
        }
        if (shouldUpdateSelection) {
            selection.endUpdate();
        }
    }

    @Override
    public void undo() {
        if (shouldUpdateSelection) {
            selection.beginUpdate();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.beginUpdate();
        }
        for (int i = subJobs.size()-1; i >= 0; i--) {
            subJobs.get(i).undo();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.endUpdate();
        }
        if (shouldUpdateSelection) {
            selection.endUpdate();
        }
    }

    @Override
    public void redo() {
        if (shouldUpdateSelection) {
            selection.beginUpdate();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.beginUpdate();
        }
        for (Job subJob : subJobs) {
            subJob.redo();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.endUpdate();
        }
        if (shouldUpdateSelection) {
            selection.endUpdate();
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

}
