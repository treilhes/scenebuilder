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
package com.oracle.javafx.scenebuilder.job.editor.gridpane;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.job.editor.DeleteObjectJob;

/**
 * Job invoked when removing column constraints.
 */
public class RemoveColumnConstraintsJob extends BatchDocumentJob {

    private final FXOMObject targetGridPane;
    private final List<Integer> targetIndexes;

    public RemoveColumnConstraintsJob(ApplicationContext context, 
            final Editor editor,
            final FXOMObject targetGridPane,
            final List<Integer> targetIndexes) {
        super(context, editor);

        assert targetGridPane != null;
        assert targetIndexes != null;
        this.targetGridPane = targetGridPane;
        this.targetIndexes = targetIndexes;
    }

    @Override
    protected List<Job> makeSubJobs() {

        final List<Job> result = new ArrayList<>();

        // Remove column constraints job
        assert targetGridPane instanceof FXOMInstance;
        assert targetIndexes.isEmpty() == false;

        final DesignHierarchyMask mask = new DesignHierarchyMask(targetGridPane);
        for (int targetIndex : targetIndexes) {
            final FXOMObject targetConstraints
                    = mask.getColumnConstraintsAtIndex(targetIndex);
            // The target index is associated to an existing constraints value :
            // => we remove the constraints value
            if (targetConstraints != null) {
                final Job removeValueJob = new DeleteObjectJob(getContext(), 
                        targetConstraints,
                        getEditorController()).extend();
                result.add(removeValueJob);
            }
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        return "Remove Column Constraints"; //NOI18N
    }
}