/*
 * Copyright (c) 2014, Oracle and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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

package com.oracle.javafx.scenebuilder.kit.editor.job;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.RemoveFxControllerJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.RemovePropertyJob;

/**
 *
 */
public class PrunePropertiesJob extends BatchDocumentJob {

    private final FXOMObject fxomObject;
    private final FXOMObject targetParent;

    public PrunePropertiesJob(ApplicationContext context, FXOMObject fxomObject, FXOMObject targetParent, Editor editor) {
        super(context, editor);

        assert fxomObject != null;

        this.fxomObject = fxomObject;
        this.targetParent = targetParent;
    }


    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();
        final Metadata metadata = Metadata.getMetadata();

        if (fxomObject instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;

            for (FXOMProperty p : fxomInstance.getProperties().values()) {
                if (metadata.isPropertyTrimmingNeeded(p.getName())) {
                    final Class<?> residentClass = p.getName().getResidenceClass();
                    final boolean prune;
                    if (residentClass == null) {
                        prune = true;
                    } else if (targetParent instanceof FXOMInstance) {
                        final FXOMInstance parentInstance = (FXOMInstance) targetParent;
                        prune = residentClass != parentInstance.getDeclaredClass();
                    } else {
                        assert (targetParent == null) || (targetParent instanceof FXOMCollection);
                        prune = true;
                    }
                    if (prune) {
                        result.add(new RemovePropertyJob(getContext(), p, getEditorController()).extend());
                    }
                }
            }
        }

        if ((fxomObject.getFxController() != null) && (targetParent != null)) {
            result.add(new RemoveFxControllerJob(getContext(), fxomObject, getEditorController()).extend());
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName(); // Should not reach user
    }

}
