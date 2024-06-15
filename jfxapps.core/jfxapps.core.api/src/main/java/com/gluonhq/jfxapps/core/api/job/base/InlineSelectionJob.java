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

import java.util.List;

import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;

/**
 * This Job updates the FXOM document AND the selection at execution time.
 *
 * The sub jobs are created and executed just after.
 */
public abstract class InlineSelectionJob extends InlineDocumentJob {

    private SelectionGroup oldSelectionGroup;
    private SelectionGroup newSelectionGroup;
    private final Selection selection;

    // @formatter:off
    protected InlineSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection) {
     // @formatter:on
        super(extensionFactory, documentManager);
        this.selection = selection;
    }

    protected Selection getSelection() {
        return selection;
    }

    protected final SelectionGroup getOldSelectionGroup() {
        return oldSelectionGroup;
    }

    protected abstract SelectionGroup getNewSelectionGroup();

    @Override
    public final void doExecute() {

        try {
            selection.beginUpdate();
            oldSelectionGroup = selection.getGroup() == null ? null : selection.getGroup().clone();
            super.doExecute();
            newSelectionGroup = getNewSelectionGroup();
            selection.select(newSelectionGroup);
            selection.endUpdate();

        } catch (CloneNotSupportedException x) {
            // Emergency code
            throw new RuntimeException(x);
        }
    }

    @Override
    public final void doUndo() {
        selection.beginUpdate();
        super.doUndo();
        selection.select(oldSelectionGroup);
        selection.endUpdate();
    }

    @Override
    public final void doRedo() {
        selection.beginUpdate();
        super.doRedo();
        selection.select(newSelectionGroup);
        selection.endUpdate();
    }

    @Override
    protected abstract List<Job> makeAndExecuteSubJobs();
}
