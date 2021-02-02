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
import com.oracle.javafx.scenebuilder.editors.drag.target.AbstractDropTarget;
import com.oracle.javafx.scenebuilder.editors.drag.target.AccessoryDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve.AbstractCurveEditor;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.AbstractHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.SceneHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.pring.AbstractPring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.pring.NodePring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.AbstractResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring.AbstractTring;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;

public class SceneDriver extends AbstractDriver {

	private final ApplicationContext context;

    public SceneDriver(ApplicationContext context, ContentPanelController contentPanelController) {
        super(contentPanelController);
        this.context = context;
    }

    @Override
    public AbstractHandles<?> makeHandles(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Scene;
        assert fxomObject instanceof FXOMInstance;
        return new SceneHandles(context, contentPanelController, (FXOMInstance) fxomObject);
    }

    @Override
    public AbstractTring<?> makeTring(DropTarget dropTarget) {
        return null;
    }

    @Override
    public AbstractPring<?> makePring(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Scene;
        DesignHierarchyMask designHierarchyMask = new DesignHierarchyMask(fxomObject);
        FXOMObject root = designHierarchyMask.getAccessory(designHierarchyMask.getMainAccessory());
        assert root != null;
        assert root.getSceneGraphObject() instanceof Node;
        assert root instanceof FXOMInstance;
        return new NodePring(contentPanelController, (FXOMInstance) root);
    }

    @Override
    public AbstractResizer<?> makeResizer(FXOMObject fxomObject) {
        // Resize gesture does not apply to Scenes
        return null;
    }

    @Override
    public AbstractCurveEditor<?> makeCurveEditor(FXOMObject fxomObject) {
        return null;
    }

    @Override
    public FXOMObject refinePick(Node hitNode, double sceneX, double sceneY, FXOMObject fxomObject) {
        return fxomObject;
    }

    @Override
    public AbstractDropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY) {
        assert fxomObject instanceof FXOMInstance;
        return new AccessoryDropTarget((FXOMInstance) fxomObject);
    }

    @Override
    public Node getInlineEditorBounds(FXOMObject fxomObject) {
        return null;
    }

    @Override
    public boolean intersectsBounds(FXOMObject fxomObject, Bounds bounds) {
        assert fxomObject.getSceneGraphObject() instanceof Scene;
        DesignHierarchyMask designHierarchyMask = new DesignHierarchyMask(fxomObject);
        FXOMObject root = designHierarchyMask.getAccessory(designHierarchyMask.getMainAccessory());
        assert root != null;
        assert root.getSceneGraphObject() instanceof Node;
        Node rootNode = (Node) root.getSceneGraphObject();
        final Bounds rootNodeBounds = rootNode.localToScene(rootNode.getLayoutBounds(), true /* rootScene */);
        return rootNodeBounds.intersects(bounds);
    }
}
