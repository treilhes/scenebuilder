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

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.CurveEditor;
import com.oracle.javafx.scenebuilder.api.DropTarget;
import com.oracle.javafx.scenebuilder.api.Handles;
import com.oracle.javafx.scenebuilder.api.Pring;
import com.oracle.javafx.scenebuilder.api.Resizer;
import com.oracle.javafx.scenebuilder.api.Tring;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.editors.drag.target.AccessoryDropTarget;
import com.oracle.javafx.scenebuilder.editors.drag.target.ContainerXYDropTarget;
import com.oracle.javafx.scenebuilder.editors.drag.target.ContainerZDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.NodeHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.pring.NodePring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.CanvasResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.ImageViewResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.RegionResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.SubSceneResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.WebViewResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.shape.ArcResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.shape.CircleResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.shape.EllipseResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.shape.RectangleResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.shape.TextResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring.NodeTring;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

/**
 *
 */
public abstract class AbstractNodeDriver extends AbstractDriver {

    private ApplicationContext context;

	public AbstractNodeDriver(
    		ApplicationContext context,
    		Content contentPanelController) {
        super(contentPanelController);
        this.context = context;
    }

    /*
     * AbstractDriver
     */

    @Override
    public Handles<?> makeHandles(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Node;
        assert fxomObject instanceof FXOMInstance;
        return new NodeHandles(context, contentPanelController, (FXOMInstance)fxomObject);
    }

    @Override
    public Pring<?> makePring(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Node;
        assert fxomObject instanceof FXOMInstance;
        return new NodePring(contentPanelController, (FXOMInstance)fxomObject);
    }

    @Override
    public Tring<?> makeTring(DropTarget dropTarget) {
        assert dropTarget != null;
        assert dropTarget.getTargetObject() instanceof FXOMInstance;
        assert dropTarget.getTargetObject().getSceneGraphObject() instanceof Node;
        return new NodeTring(contentPanelController, (FXOMInstance) dropTarget.getTargetObject());
    }

    @Override
    public Resizer<?> makeResizer(FXOMObject fxomObject) {
        final Resizer<?> result;

        /*
         * To avoid creating one driver for each resizer,
         * we make the dispatch here:
         */

        final Object sceneGraphObject = fxomObject.getSceneGraphObject();
        if (sceneGraphObject instanceof ImageView) {
            result = new ImageViewResizer((ImageView) sceneGraphObject);
        } else if (sceneGraphObject instanceof Region) {
            result = new RegionResizer((Region) sceneGraphObject);
        } else if (sceneGraphObject instanceof WebView) {
            result = new WebViewResizer((WebView) sceneGraphObject);
        } else if (sceneGraphObject instanceof Canvas) {
            result = new CanvasResizer((Canvas) sceneGraphObject);
        } else if (sceneGraphObject instanceof Arc) {
            result = new ArcResizer((Arc) sceneGraphObject);
        } else if (sceneGraphObject instanceof Circle) {
            result = new CircleResizer((Circle) sceneGraphObject);
        } else if (sceneGraphObject instanceof Ellipse) {
            result = new EllipseResizer((Ellipse) sceneGraphObject);
        } else if (sceneGraphObject instanceof Rectangle) {
            result = new RectangleResizer((Rectangle) sceneGraphObject);
        } else if (sceneGraphObject instanceof Text) {
            result = new TextResizer((Text) sceneGraphObject);
        } else if (sceneGraphObject instanceof SubScene) {
            result = new SubSceneResizer((SubScene) sceneGraphObject);
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public CurveEditor<?> makeCurveEditor(FXOMObject fxomObject) {
        return null;
    }

    @Override
    public FXOMObject refinePick(Node hitNode, double sceneX, double sceneY, FXOMObject fxomObject) {
        return fxomObject;
    }

    @Override
    public DropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY) {
        assert fxomObject instanceof FXOMInstance;
        assert fxomObject.getSceneGraphObject() != null; // Because mouse cannot be above a unresolved component

        final DropTarget result;

        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final DesignHierarchyMask mask = new DesignHierarchyMask(fxomObject);
        if (mask.isFreeChildPositioning()) {
            result = new ContainerXYDropTarget(fxomInstance, sceneX, sceneY);
        } else {
            if (mask.isAcceptingAccessory(DesignHierarchyMask.Accessory.CONTENT)) {
                result = new AccessoryDropTarget(fxomInstance, DesignHierarchyMask.Accessory.CONTENT);
            } else {
                result = new ContainerZDropTarget(fxomInstance, null);
            }
        }

        return result;
    }

    @Override
    public Node getInlineEditorBounds(FXOMObject fxomObject) {
        final Node result;

        final Object sceneGraphObject = fxomObject.getSceneGraphObject();
        if (sceneGraphObject instanceof ComboBox) {
            result = (ComboBox<?>) sceneGraphObject;
        } else if (sceneGraphObject instanceof Labeled) {
            result = (Labeled) sceneGraphObject;
        } else if (sceneGraphObject instanceof Text) {
            result = (Text) sceneGraphObject;
        } else if (sceneGraphObject instanceof TextInputControl) {
            result = (TextInputControl) sceneGraphObject;
        } else if (sceneGraphObject instanceof TitledPane) {
            result = (TitledPane) sceneGraphObject;
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public boolean intersectsBounds(FXOMObject fxomObject, Bounds bounds) {
        assert fxomObject.getSceneGraphObject() instanceof Node;

        // Note: bounds are in root scene coordinates
        final Node sceneGraphNode
                = (Node) fxomObject.getSceneGraphObject();
        final Bounds sceneGraphNodeBounds
                = sceneGraphNode.localToScene(sceneGraphNode.getLayoutBounds(), true /* rootScene */);

        return sceneGraphNodeBounds.intersects(bounds);
    }

}
