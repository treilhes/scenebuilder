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
package com.oracle.javafx.scenebuilder.tools.driver.window;

import java.util.List;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.content.gesture.DiscardGesture;
import com.gluonhq.jfxapps.core.api.mask.DesignHierarchyMask;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Workspace;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.ResizeGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.handles.AbstractGenericHandles;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
@Prototype
public class WindowHandles extends AbstractGenericHandles<Window> {
    private final Pane dummyPane = new Pane();
    private Node sceneGraphObject;
    private final DesignHierarchyMask.Factory maskFactory;

    public WindowHandles(
            Driver driver,
            Workspace workspace,
            FxmlDocumentManager documentManager,
            DiscardGesture.Factory discardGestureFactory,
            ResizeGesture.Factory resizeGestureFactory,
            @Autowired DesignHierarchyMask.Factory maskFactory) {

        super(driver, workspace, documentManager, discardGestureFactory, resizeGestureFactory, Window.class);
        this.maskFactory = maskFactory;
    }

    @Override
    public void initialize() {

        final HierarchyMask designHierarchyMask = maskFactory.getMask(getFxomObject());
        List<FXOMObject> sceneContent = designHierarchyMask.getAccessories(designHierarchyMask.getMainAccessory(), false);
        if (sceneContent.isEmpty()) {
            sceneGraphObject = null;
        } else {
            FXOMObject scene = sceneContent.get(0);
            HierarchyMask sceneDesignHierarchyMask = maskFactory.getMask(scene);
            List<FXOMObject> rootContent = sceneDesignHierarchyMask.getAccessories(sceneDesignHierarchyMask.getMainAccessory(), false);

            assert !rootContent.isEmpty();

            FXOMObject root = rootContent.get(0);

            assert root != null;
            assert root instanceof FXOMInstance;
            assert root.getSceneGraphObject().isNode();
            sceneGraphObject = root.getSceneGraphObject().getAs(Node.class);
        }
    }

    @Override
    public Bounds getSceneGraphObjectBounds() {
        if (sceneGraphObject != null) {
            return sceneGraphObject.getLayoutBounds();
        } else {
            return new BoundingBox(0, 0, 0, 0);
        }
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        return sceneGraphObject != null ? sceneGraphObject : dummyPane;
    }

    @Override
    public FXOMObject getFxomObjectProxy() {
        return getFxomObject();
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        if (sceneGraphObject == null) {
            return;
        }
        startListeningToLayoutBounds(sceneGraphObject);
        startListeningToLocalToSceneTransform(sceneGraphObject);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        if (sceneGraphObject == null) {
            return;
        }
        stopListeningToLayoutBounds(sceneGraphObject);
        stopListeningToLocalToSceneTransform(sceneGraphObject);
    }

}
