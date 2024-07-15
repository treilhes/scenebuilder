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

package com.gluonhq.jfxapps.core.dnd.target;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.dnd.AbstractDropTarget;
import com.gluonhq.jfxapps.core.api.dnd.DragSource;
import com.gluonhq.jfxapps.core.api.dnd.DropTargetFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

/**
 *
 */
@ApplicationInstancePrototype
public final class RootDropTarget extends AbstractDropTarget {

    private final SelectionJobsFactory selectionJobsFactory;

    protected RootDropTarget(SelectionJobsFactory selectionJobsFactory) {
        this.selectionJobsFactory = selectionJobsFactory;
    }

    protected void setDropTargetParameters() {
    }
    /*
     * AbstractDropTarget
     */

    @Override
    public FXOMObject getTargetObject() {
        return null;
    }

    @Override
    public boolean acceptDragSource(DragSource dragSource) {
        assert dragSource != null;
        return dragSource.getDraggedObjects().size() == 1;
    }

    @Override
    public Job makeDropJob(DragSource dragSource) {
        assert dragSource != null;
        assert dragSource.getDraggedObjects().size() == 1;

        final FXOMObject newRoot = dragSource.getDraggedObjects().get(0);
        final var job = selectionJobsFactory.setDocumentRoot(newRoot);
        job.setDescription(dragSource.makeDropJobDescription());

        return job;
    }

    @Override
    public boolean isSelectRequiredAfterDrop() {
        return true;
    }

    @ApplicationInstanceSingleton
    public static class Factory extends DropTargetFactory<RootDropTarget> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        public RootDropTarget getDropTarget() {
            return create(RootDropTarget.class, j -> j.setDropTargetParameters());
        }
    }
}
