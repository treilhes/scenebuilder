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
package com.oracle.javafx.scenebuilder.tools.driver.hbox;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.HierarchyMask;
import com.gluonhq.jfxapps.core.api.dnd.DropTarget;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.util.CoordinateHelper;
import com.gluonhq.jfxapps.core.core.dnd.droptarget.AccessoryDropTarget;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.control.droptarget.AbstractDropTargetProvider;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
public final class HBoxDropTargetProvider extends AbstractDropTargetProvider {

    private final FXOMObjectMask.Factory maskFactory;
    private final AccessoryDropTarget.Factory accessoryDropTargetFactory;

    public HBoxDropTargetProvider(
            FXOMObjectMask.Factory maskFactory,
            AccessoryDropTarget.Factory accessoryDropTargetFactory) {
        super();
        this.maskFactory = maskFactory;
        this.accessoryDropTargetFactory = accessoryDropTargetFactory;
    }

    @Override
    public DropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY) {

        assert fxomObject instanceof FXOMInstance;
        assert fxomObject.getSceneGraphObject().isInstanceOf(HBox.class);

        final HBox hbox = fxomObject.getSceneGraphObject().getAs(HBox.class);
        assert hbox.getScene() != null;

        final double localX = CoordinateHelper.sceneToLocal(fxomObject, sceneX, sceneY, true /* rootScene */).getX();
        final int childCount = hbox.getChildrenUnmodifiable().size();

        final int targetIndex;
        if (childCount == 0) {
            // No children : we append
            targetIndex = -1;

        } else {
            assert childCount >= 1;

            int childIndex = 0;
            Node child = hbox.getChildrenUnmodifiable().get(childIndex++);
            Bounds childBounds = child.getBoundsInParent();
            double midX = (childBounds.getMinX() + childBounds.getMaxX()) / 2.0;
            while ((localX > midX) && (childIndex < childCount)) {
                child = hbox.getChildrenUnmodifiable().get(childIndex++);
                childBounds = child.getBoundsInParent();
                midX = (childBounds.getMinX() + childBounds.getMaxX()) / 2.0;
            }
            if (localX <= midX) {
                assert childIndex-1 < childCount;
                targetIndex = childIndex-1;
            } else {
                targetIndex = -1;
            }
        }

        final FXOMObject beforeChild;
        if (targetIndex == -1) {
            beforeChild = null;
        } else {
            final HierarchyMask m = maskFactory.getMask(fxomObject);
            if (targetIndex < m.getSubComponentCount(m.getMainAccessory(), false)) {
                beforeChild = m.getSubComponentAtIndex(m.getMainAccessory(), targetIndex, false);
            } else {
                beforeChild = null;
            }
        }

        return accessoryDropTargetFactory.getDropTarget((FXOMInstance)fxomObject, beforeChild);
    }

}
