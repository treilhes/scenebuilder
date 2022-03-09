/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.draganddrop.droptarget;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DragSource;
import com.oracle.javafx.scenebuilder.api.control.droptarget.AbstractDropTarget;
import com.oracle.javafx.scenebuilder.api.control.droptarget.DropTargetFactory;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.selection.job.SetDocumentRootJob;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class RootDropTarget extends AbstractDropTarget {

    private final SetDocumentRootJob.Factory setDocumentRootJobFactory;

    protected RootDropTarget(
            SetDocumentRootJob.Factory setDocumentRootJobFactory) {
        this.setDocumentRootJobFactory = setDocumentRootJobFactory;
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
    public AbstractJob makeDropJob(DragSource dragSource) {
        assert dragSource != null;
        assert dragSource.getDraggedObjects().size() == 1;

        final FXOMObject newRoot = dragSource.getDraggedObjects().get(0);
        return setDocumentRootJobFactory.getJob(newRoot, true /* usePredefinedSize */,
                dragSource.makeDropJobDescription());
    }

    @Override
    public boolean isSelectRequiredAfterDrop() {
        return true;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends DropTargetFactory<RootDropTarget> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public RootDropTarget getDropTarget() {
            return create(RootDropTarget.class, j -> j.setDropTargetParameters());
        }
    }
}