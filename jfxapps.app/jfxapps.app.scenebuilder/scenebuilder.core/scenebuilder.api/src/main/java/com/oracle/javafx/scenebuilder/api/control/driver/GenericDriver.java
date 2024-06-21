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
package com.oracle.javafx.scenebuilder.api.control.driver;

import org.scenebuilder.fxml.api.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.dnd.DropTarget;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.control.CurveEditor;
import com.oracle.javafx.scenebuilder.api.control.DropTargetProvider;
import com.oracle.javafx.scenebuilder.api.control.Handles;
import com.oracle.javafx.scenebuilder.api.control.InlineEditorBounds;
import com.oracle.javafx.scenebuilder.api.control.PickRefiner;
import com.oracle.javafx.scenebuilder.api.control.Pring;
import com.oracle.javafx.scenebuilder.api.control.Relocater;
import com.oracle.javafx.scenebuilder.api.control.ResizeGuide;
import com.oracle.javafx.scenebuilder.api.control.Resizer;
import com.oracle.javafx.scenebuilder.api.control.Rudder;
import com.oracle.javafx.scenebuilder.api.control.Shadow;
import com.oracle.javafx.scenebuilder.api.control.Tring;
import com.oracle.javafx.scenebuilder.api.control.curve.AbstractCurveEditor;
import com.oracle.javafx.scenebuilder.api.control.handles.AbstractHandles;
import com.oracle.javafx.scenebuilder.api.control.inlineedit.AbstractInlineEditorBounds;
import com.oracle.javafx.scenebuilder.api.control.intersect.AbstractIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.api.control.intersect.IntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.api.control.outline.AbstractOutline;
import com.oracle.javafx.scenebuilder.api.control.outline.Outline;
import com.oracle.javafx.scenebuilder.api.control.pickrefiner.AbstractPickRefiner;
import com.oracle.javafx.scenebuilder.api.control.pring.AbstractPring;
import com.oracle.javafx.scenebuilder.api.control.relocater.AbstractRelocater;
import com.oracle.javafx.scenebuilder.api.control.resizer.AbstractResizeGuide;
import com.oracle.javafx.scenebuilder.api.control.resizer.AbstractResizer;
import com.oracle.javafx.scenebuilder.api.control.resizer.AbstractShadow;
import com.oracle.javafx.scenebuilder.api.control.rudder.AbstractRudder;
import com.oracle.javafx.scenebuilder.api.control.tring.AbstractTring;

import javafx.geometry.Bounds;
import javafx.scene.Node;

@ApplicationSingleton
public class GenericDriver extends AbstractDriver {

    private static final Logger logger = LoggerFactory.getLogger(GenericDriver.class);

    private final DriverExtensionRegistry registry;

    public GenericDriver(
            DriverExtensionRegistry registry,
            @Lazy Content contentPanelController) {
        super(contentPanelController);
        this.registry = registry;
    }


    public <T> T make(Class<T> cls, FXOMObject fxomObject) {
        assert fxomObject instanceof FXOMInstance;
        assert !fxomObject.getSceneGraphObject().isEmpty();
        return registry.getImplementationInstance(cls, fxomObject.getSceneGraphObject().getObjectClass());
    }

    @Override
    public Handles<?> makeHandles(FXOMObject fxomObject) {
        AbstractHandles handles = (AbstractHandles)make(Handles.class, fxomObject);
        if (handles != null) {
            handles.setFxomObject(fxomObject);
            handles.initialize();
        }

        if (logger.isInfoEnabled()) {
            logger.info("driver makeHandles returned {} for object {}",
                    handles == null ? "null" : handles.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }
        return handles;
    }

    @Override
    public Pring<?> makePring(FXOMObject fxomObject) {
        AbstractPring pring = (AbstractPring)make(Pring.class, fxomObject);
        if (pring != null) {
            pring.setFxomObject(fxomObject);
            pring.initialize();
        }
        if (logger.isInfoEnabled()) {
            logger.info("driver makePring returned {} for object {}",
                    pring == null ? "null" : pring.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }
        return pring;
    }

    @Override
    public Relocater makeRelocater(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject().isNode();
        final Node node = fxomObject.getSceneGraphObject().getAs(Node.class);
        final FXOMObject parentObject = fxomObject.getParentObject();

        AbstractRelocater relocater = null;

        if (parentObject == null || node.getParent() == null) {// root element or detached graph
            relocater = new RootRelocater();
        } else {
            final Object sceneGraphParent = parentObject.getSceneGraphObject().get();
            relocater = (AbstractRelocater)registry.getImplementationInstance(Relocater.class, sceneGraphParent.getClass());
        }
        if (relocater != null) {
            relocater.setFxomObject(fxomObject);
            relocater.initialize();
        }
        if (logger.isInfoEnabled()) {
            logger.info("driver makeRelocater returned {} for object {}",
                    relocater == null ? "null" : relocater.getClass().getName(),
                    node == null ? "null" : node.getClass().getName());
        }

        return relocater;
    }

    @Override
    public Tring<?> makeTring(DropTarget dropTarget) {
        assert dropTarget != null;
        assert dropTarget.getTargetObject() instanceof FXOMInstance;
        AbstractTring tring = (AbstractTring)make(Tring.class, dropTarget.getTargetObject());
        if (tring != null) {
            tring.setFxomObject(dropTarget.getTargetObject());
            tring.defineDropTarget(dropTarget);
            tring.initialize();
        }
        if (logger.isInfoEnabled()) {
            logger.info("driver makeTring returned {} for object {}",
                    tring == null ? "null" : tring.getClass().getName(),
                    dropTarget.getTargetObject() == null ? "null" : dropTarget.getTargetObject().getClass().getName());
        }
        return tring;
    }

    @Override
    public Resizer<?> makeResizer(FXOMObject fxomObject) {
        AbstractResizer resizer = (AbstractResizer)make(Resizer.class, fxomObject);
        if (resizer != null) {
            resizer.setFxomObject(fxomObject);
            resizer.initialize();
        }
        if (logger.isInfoEnabled()) {
            logger.info("driver makeResizer returned {} for object {}",
                    resizer == null ? "null" : resizer.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }
        return resizer;
    }

    @Override
    public CurveEditor<?> makeCurveEditor(FXOMObject fxomObject) {
        AbstractCurveEditor curveEditor = (AbstractCurveEditor)make(CurveEditor.class, fxomObject);
        if (curveEditor != null) {
            curveEditor.setFxomObject(fxomObject);
            curveEditor.initialize();
        }
        if (logger.isInfoEnabled()) {
            logger.info("driver makeCurveEditor returned {} for object {}",
                    curveEditor == null ? "null" : curveEditor.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }
        return curveEditor;
    }

    @Override
    public FXOMObject refinePick(Node hitNode, double sceneX, double sceneY, FXOMObject fxomObject) {
        AbstractPickRefiner pickRefiner = (AbstractPickRefiner)make(PickRefiner.class, fxomObject);

        if (pickRefiner != null) {
//            pickRefiner.setSceneGraphObject(fxomObject.getSceneGraphObject().getAs(Node.class));
//            pickRefiner.initialize();
            FXOMObject refinedPick = pickRefiner.refinePick(hitNode, sceneX, sceneY, fxomObject);

            if (logger.isDebugEnabled()) {
                logger.debug("driver refinePick used {} for object {} returned {} for (x: {}, y: {})",
                        pickRefiner == null ? "null" : pickRefiner.getClass().getName(),
                        fxomObject.getSceneGraphObject().isEmpty() ? "null"
                                : fxomObject.getSceneGraphObject().getObjectClass().getName(),
                                refinedPick.getSceneGraphObject().isEmpty() ? "null"
                                : refinedPick.getSceneGraphObject().getObjectClass().getName(),
                                sceneX, sceneY);
            }

            return refinedPick;
        }

        return null;
    }

    @Override
    public DropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY) {
        DropTargetProvider factory = make(DropTargetProvider.class, fxomObject);

        if (logger.isInfoEnabled()) {
            logger.info("driver makeDropTarget used {} for object {}",
                    factory == null ? "null" : factory.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }

        if (factory != null) {
            return factory.makeDropTarget(fxomObject, sceneX, sceneY);
        }
        return null;
    }

    @Override
    public Node getInlineEditorBounds(FXOMObject fxomObject) {
        AbstractInlineEditorBounds inlineEditorBounds = (AbstractInlineEditorBounds)make(InlineEditorBounds.class, fxomObject);

        if (logger.isInfoEnabled()) {
            logger.info("driver getInlineEditorBounds used {} for object {}",
                    inlineEditorBounds == null ? "null" : inlineEditorBounds.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }

        if (inlineEditorBounds != null) {
            //inlineEditorBounds.setSceneGraphObject((Node)fxomObject.getSceneGraphObject());
            //inlineEditorBounds.initialize();
            return inlineEditorBounds.getBounds(fxomObject);
        }
        return null;
    }

    @Override
    public boolean intersectsBounds(FXOMObject fxomObject, Bounds bounds) {
        AbstractIntersectsBoundsCheck intersectsBoundsCheck = (AbstractIntersectsBoundsCheck)make(IntersectsBoundsCheck.class, fxomObject);

        if (logger.isInfoEnabled()) {
            logger.info("driver intersectsBounds used {} for object {}",
                    intersectsBoundsCheck == null ? "null" : intersectsBoundsCheck.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }

        if (intersectsBoundsCheck != null) {
            //intersectsBoundsCheck.setSceneGraphObject(fxomObject.getSceneGraphObject().getAs(Node.class));
            //intersectsBoundsCheck.initialize();
            return intersectsBoundsCheck.intersectsBounds(fxomObject, bounds);
        }
        return false;
    }


    @Override
    public Rudder makeRudder(FXOMObject fxomObject) {
        AbstractRudder<?> rudder = (AbstractRudder)make(Rudder.class, fxomObject);
        if (rudder != null) {
            rudder.setFxomObject(fxomObject);
            rudder.initialize();
        }
        if (logger.isInfoEnabled()) {
            logger.info("driver makeRudder returned {} for object {}",
                    rudder == null ? "null" : rudder.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }
        return rudder;
    }

    @Override
    public Outline makeOutline(FXOMObject fxomObject) {
        AbstractOutline<?> outline = (AbstractOutline)make(Outline.class, fxomObject);
        if (outline != null) {
            outline.setFxomObject(fxomObject);
            outline.initialize();
        }

        if (logger.isInfoEnabled()) {
            logger.info("driver makeOutline returned {} for object {}",
                    outline == null ? "null" : outline.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }
        return outline;
    }

    @Override
    public ResizeGuide<?> makeResizeGuide(FXOMObject fxomObject) {
        AbstractResizeGuide<?> resizeGuide = (AbstractResizeGuide)make(ResizeGuide.class, fxomObject);
        if (resizeGuide != null) {
            resizeGuide.setFxomObject(fxomObject);
            resizeGuide.initialize();
        }

        if (logger.isInfoEnabled()) {
            logger.info("driver makeResizeGuide returned {} for object {}",
                    resizeGuide == null ? "null" : resizeGuide.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }
        return resizeGuide;
    }

    @Override
    public Shadow<?> makeShadow(FXOMObject fxomObject) {
        AbstractShadow<?> shadow = (AbstractShadow)make(Shadow.class, fxomObject);
        if (shadow != null) {
            shadow.setFxomObject(fxomObject);
            shadow.initialize();
        }

        if (logger.isInfoEnabled()) {
            logger.info("driver makeShadow returned {} for object {}",
                    shadow == null ? "null" : shadow.getClass().getName(),
                    fxomObject.getSceneGraphObject().isEmpty() ? "null"
                            : fxomObject.getSceneGraphObject().getObjectClass().getName());
        }
        return shadow;
    }

}
