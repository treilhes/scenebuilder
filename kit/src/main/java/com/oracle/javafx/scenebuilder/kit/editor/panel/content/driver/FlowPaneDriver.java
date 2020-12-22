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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.DropTarget;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.editor.drag.target.AbstractDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.drag.target.ContainerZDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring.AbstractTring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring.GenericParentTring;

import javafx.scene.layout.FlowPane;

/**
 *
 */
public class FlowPaneDriver extends AbstractNodeDriver {

    public FlowPaneDriver(
    		ApplicationContext context,
    		ContentPanelController contentPanelController) {
        super(context, contentPanelController);
    }

    /*
     * AbstractDriver
     */
    @Override
    public AbstractDropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY) {

        assert fxomObject instanceof FXOMInstance;
        assert fxomObject.getSceneGraphObject() instanceof FlowPane;

        final int targetIndex = GenericParentTring.lookupCrackIndex(fxomObject, sceneX, sceneY);

        final FXOMObject beforeChild;
        if (targetIndex == -1) {
            beforeChild = null;
        } else {
            final DesignHierarchyMask m = new DesignHierarchyMask(fxomObject);
            if (targetIndex < m.getSubComponentCount()) {
                beforeChild = m.getSubComponentAtIndex(targetIndex);
            } else {
                beforeChild = null;
            }
        }

        return new ContainerZDropTarget((FXOMInstance)fxomObject, beforeChild);
    }


    @Override
    public AbstractTring<?> makeTring(DropTarget dropTarget) {
        assert dropTarget instanceof ContainerZDropTarget;
        assert dropTarget.getTargetObject() instanceof FXOMInstance;
        assert dropTarget.getTargetObject().getSceneGraphObject() instanceof FlowPane;

        final ContainerZDropTarget zDropTarget = (ContainerZDropTarget) dropTarget;
        final int targetIndex;
        if (zDropTarget.getBeforeChild() == null) {
            targetIndex = -1;
        } else {
            targetIndex = zDropTarget.getBeforeChild().getIndexInParentProperty();
        }
        return new GenericParentTring(contentPanelController,
                (FXOMInstance) dropTarget.getTargetObject(),
                targetIndex);
    }
}
