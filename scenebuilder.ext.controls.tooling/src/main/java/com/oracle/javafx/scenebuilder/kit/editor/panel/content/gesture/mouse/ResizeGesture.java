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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.CardinalPoint;
import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.HudWindow;
import com.oracle.javafx.scenebuilder.api.content.ModeManager;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractMouseGesture;
import com.oracle.javafx.scenebuilder.api.content.mode.Layer;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.control.Handles;
import com.oracle.javafx.scenebuilder.api.control.Relocater;
import com.oracle.javafx.scenebuilder.api.control.ResizeGuide;
import com.oracle.javafx.scenebuilder.api.control.Resizer;
import com.oracle.javafx.scenebuilder.api.control.Resizer.Feature;
import com.oracle.javafx.scenebuilder.api.control.Rudder;
import com.oracle.javafx.scenebuilder.api.control.Shadow;
import com.oracle.javafx.scenebuilder.api.control.driver.GenericDriver;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides.ResizingGuideController;

import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 *
 */
public class ResizeGesture extends AbstractMouseGesture {

    private final FXOMInstance fxomInstance;
    private final CardinalPoint tunable;
    //private final ResizeRudder rudder;

    private Resizer<?> resizer;
    private Relocater<?> relocater;
    private ResizingGuideController resizingGuideController;
    //private RegionRectangle shadow;
    private boolean snapEnabled;
    private boolean guidesDisabled;
	private final ApplicationContext context;
	private final GenericDriver driver;
    private final ModeManager modeManager;
    private Layer<Rudder> rudderLayer;
    private Layer<Handles> handleLayer;
    private Layer<ResizeGuide> resizeGuideLayer;
    private Layer<Shadow> shadowLayer;
    
    private boolean matchWidth;
    private boolean matchHeight;

    public ResizeGesture(ApplicationContext context, Content contentPanelController, FXOMInstance fxomInstance, CardinalPoint tunable) {
        super(contentPanelController);
        this.context = context;
        this.driver = context.getBean(GenericDriver.class);
        this.modeManager = context.getBean(ModeManager.class);
        
        assert fxomInstance.getSceneGraphObject() instanceof Node;
        this.fxomInstance = fxomInstance;
        this.tunable = tunable;
        
        if (modeManager.hasModeEnabled()) {
            rudderLayer = modeManager.getEnabledMode().getLayer(Rudder.class);
            handleLayer = modeManager.getEnabledMode().getLayer(Handles.class);
            resizeGuideLayer = modeManager.getEnabledMode().getLayer(ResizeGuide.class);
            shadowLayer = modeManager.getEnabledMode().getLayer(Shadow.class);
        }
        
        assert rudderLayer != null;
        assert handleLayer != null;
        assert resizeGuideLayer != null;
        assert shadowLayer != null;
    }

    
    
    /*
     * AbstractMouseGesture
     */

    @Override
    public void start(InputEvent e, Observer observer) {
        AbstractGesture.attachGesture((Node)fxomInstance.getSceneGraphObject(), this);
        super.start(e, observer);
    }



    @Override
    protected void performTermination() {
        AbstractGesture.detachGesture((Node)fxomInstance.getSceneGraphObject(), this);
        super.performTermination();
    }



    @Override
    protected void mousePressed() {
        // Everthing is done in mouseDragStarted
    }

    @Override
    protected void mouseDragStarted() {
        resizer = context.getBean(Driver.class).makeResizer(fxomInstance);
        assert resizer != null;
        assert resizer.getSceneGraphObject() == fxomInstance.getSceneGraphObject();

        relocater = driver.makeRelocater(resizer.getFxomObject());

        if (relocater != null && contentPanelController.isGuidesVisible()) {
            setupResizingGuideController();
            assert resizingGuideController != null;
        }

        snapEnabled = getMousePressedEvent().isShiftDown();

        setupAndOpenHudWindow();
        //showShadow();

        handleLayer.disable();
        resizeGuideLayer.enable();
        shadowLayer.enable();
        //contentPanelController.getHandleLayer().setVisible(false);

        // Now same as mouseDragged
        mouseDragged();
    }

    @Override
    protected void mouseDragged() {
        resizeGuideLayer.update();
        shadowLayer.update();
        
        setRudderVisible(isSnapRequired());
        updateSceneGraphObjectSize();
        contentPanelController.getHudWindowController().updatePopupLocation();
        //updateShadow();
    }

    @Override
    protected void mouseDragEnded() {
        updateSceneGraphObjectSize();

        /*
         * Three steps
         *
         * 1) Collects sizing properties that have changed
         * 2) Reverts to initial sizing
         *    => this step is equivalent to userDidCancel()
         * 3) Push a BatchModifyObjectJob to officially resize the object
         */

        // Step #1
        final Map<PropertyName, Object> changeMap = new HashMap<>();
        changeMap.putAll(resizer.getChangeMap());
        if (relocater != null) {
            changeMap.putAll(relocater.getChangeMap());
        }

        // Step #2
        userDidCancel();

        // Step #3
        final Metadata metadata = Metadata.getMetadata();
        final Map<ValuePropertyMetadata, Object> metaValueMap = new HashMap<>();
        for (Map.Entry<PropertyName,Object> e : changeMap.entrySet()) {
            final ValuePropertyMetadata vpm = metadata.queryValueProperty(fxomInstance, e.getKey());
            assert vpm != null;
            metaValueMap.put(vpm, e.getValue());
        }
        if (changeMap.isEmpty() == false) {
            final Editor editorController
                    = contentPanelController.getEditorController();
            for (Map.Entry<ValuePropertyMetadata, Object> e : metaValueMap.entrySet()) {
                final Job job = new ModifyObjectJob(
                		context,
                        fxomInstance,
                        e.getKey(),
                        e.getValue(),
                        editorController,
                        "Resize").extend();
                if (job.isExecutable()) {
                    editorController.getJobManager().push(job);
                }
            }
        }

    }

    @Override
    protected void mouseReleased() {
        // Everything is done in mouseDragEnded
    }

    @Override
    protected void keyEvent(KeyEvent ke) {
        if (ke.getCode() == KeyCode.SHIFT) {
            final EventType<KeyEvent> eventType = ke.getEventType();
            if (eventType == KeyEvent.KEY_PRESSED) {
                snapEnabled = true;
            } else if (eventType == KeyEvent.KEY_RELEASED) {
                snapEnabled = false;
            }
            if (isMouseDidDrag()) {
                mouseDragged();
            }
        } else if (ke.getCode() == KeyCode.ALT) {
            final EventType<KeyEvent> eventType = ke.getEventType();
            if (eventType == KeyEvent.KEY_PRESSED) {
                guidesDisabled = true;
            } else if (eventType == KeyEvent.KEY_RELEASED) {
                guidesDisabled = false;
            }
            if (isMouseDidDrag()) {
                mouseDragged();
            }
        }
    }

    @Override
    protected void userDidCancel() {
        resizer.revertToOriginalSize();
        if (relocater != null) {
            relocater.revertToOriginalLocation();
        }
        if (resizingGuideController != null) {
            dismantleResizingGuideController();
            assert resizingGuideController == null;
        }
        setRudderVisible(false);
        //hideShadow();
        contentPanelController.getHudWindowController().closeWindow();
        
        handleLayer.enable();
        resizeGuideLayer.disable();
        shadowLayer.disable();
        //contentPanelController.getHandleLayer().setVisible(true);
        
        //resizer.getSceneGraphObject().getParent().layout();
        
        //support for detached graph (clip, shape,...)
        ((Node)resizer.getFxomObject().getClosestMainGraphNode().getSceneGraphObject()).getParent().layout();
    }


    /*
     * Private
     */

    private void updateSceneGraphObjectSize() {
        assert resizer != null;

        // Put the scene graph object back in its size/location at mouse pressed time
        resizer.revertToOriginalSize();
        if (relocater != null) {
            relocater.revertToOriginalLocation();
        }
        
        final Node sceneGraphObject = resizer.getSceneGraphObject();
        Parent parentToLayout = (Parent)resizer.getFxomObject().getClosestMainGraphNode().getClosestParent().getSceneGraphObject();
        
        parentToLayout.layout();

        // Compute mouse displacement in local coordinates of scene graph object
        final double startSceneX = getMousePressedEvent().getSceneX();
        final double startSceneY = getMousePressedEvent().getSceneY();
        final double currentSceneX = getLastMouseEvent().getSceneX();
        final double currentSceneY = getLastMouseEvent().getSceneY();
        final Point2D start = CoordinateHelper.sceneToLocal(resizer.getFxomObject(), startSceneX, startSceneY, true /* rootScene */);
        final Point2D current = CoordinateHelper.sceneToLocal(resizer.getFxomObject(), currentSceneX, currentSceneY, true /* rootScene */);
        final double rawDeltaX, rawDeltaY;
        if ((start != null) && (current != null)) {
            rawDeltaX = current.getX() - start.getX();
            rawDeltaY = current.getY() - start.getY();
        } else {
            // sceneGraphObject is bizarrely configured (eg it has scaleX=0)
            // We use the scene coordinates
            rawDeltaX = currentSceneX - startSceneX;
            rawDeltaY = currentSceneY - startSceneY;
        }

        // Clamps deltaX/deltaY relatively to tunable.
        // Example: tunable == E => clampDeltaX = rawDeltaX, clampDeltaY = 0.0
        final Point2D clampDelta = tunable.clampVector(rawDeltaX, rawDeltaY);
        final double clampDeltaX = clampDelta.getX();
        final double clampDeltaY = clampDelta.getY();

        // Compute candidateBounds
        final Bounds layoutBounds = sceneGraphObject.getLayoutBounds();
        final Bounds resizedBounds = tunable.getResizedBounds(layoutBounds, clampDeltaX, clampDeltaY);
        final Bounds candidateBounds;
        if (isSnapRequired()) {
            final double ratio = layoutBounds.getHeight() / layoutBounds.getWidth();
            candidateBounds = tunable.snapBounds(resizedBounds, ratio);
        } else {
            candidateBounds = resizedBounds;
        }

        // Computes new layout bounds from the candidate bounds
        final double candidateWidth = candidateBounds.getWidth();
        final double candidateHeight = candidateBounds.getHeight();
        final Bounds newLayoutBounds = resizer.computeBounds(candidateWidth, candidateHeight);

        final Bounds guidedLayoutBounds;
        if (resizingGuideController == null) {
            guidedLayoutBounds = newLayoutBounds;
        } else if (guidesDisabled) {
            resizingGuideController.clear();
            guidedLayoutBounds = newLayoutBounds;
        } else {
            resizingGuideController.match(newLayoutBounds);
            final double suggestedWidth  = resizingGuideController.getSuggestedWidth();
            final double suggestedHeight = resizingGuideController.getSuggestedHeight();
            guidedLayoutBounds = resizer.computeBounds(suggestedWidth, suggestedHeight);
        }

        // Now computes the new location (in parent's local coordinate space)
        final CardinalPoint fix = tunable.getOpposite();
        final Point2D currentFixPos = fix.getPosition(layoutBounds);
        final Point2D newFixPos = fix.getPosition(guidedLayoutBounds);
        final Point2D currentParent = sceneGraphObject.localToParent(currentFixPos);
        final Point2D newParent = sceneGraphObject.localToParent(newFixPos);
        final double layoutDX = currentParent.getX() - newParent.getX();
        final double layoutDY = currentParent.getY() - newParent.getY();
        final double newLayoutX = sceneGraphObject.getLayoutX() + layoutDX;
        final double newLayoutY = sceneGraphObject.getLayoutY() + layoutDY;

        // Apply the new size and new location
        resizer.changeWidth(guidedLayoutBounds.getWidth());
        resizer.changeHeight(guidedLayoutBounds.getHeight());
        if (relocater != null) {
            ((Parent)fxomInstance.getClosestParent().getSceneGraphObject()).layout();
            relocater.moveToLayoutX(newLayoutX, guidedLayoutBounds);
            relocater.moveToLayoutY(newLayoutY, guidedLayoutBounds);
        }
        
        parentToLayout.layout();

        updateHudWindow();
    }


    public boolean isSnapRequired() {
        return snapEnabled || (resizer.getFeature() == Feature.SCALING);
    }

    private void setRudderVisible(boolean visible) {
//        if (visible) {
//            rudderLayer.enable();
//        } else {
//            rudderLayer.disable();
//        }
 
//        final boolean alreadyVisible = rudder.getRootNode().getParent() != null;
//
//        if (alreadyVisible != visible) {
//            final Group rudderLayer = contentPanelController.getRudderLayer();
//            if (visible) {
//                assert rudder.getRootNode().getParent() == null;
//                rudderLayer.getChildren().add(rudder.getRootNode());
//            } else {
//                assert rudder.getRootNode().getParent() == rudderLayer;
//                rudderLayer.getChildren().remove(rudder.getRootNode());
//            }
//        }
    }


    private void setupAndOpenHudWindow() {
        final HudWindow hudWindowController
                = contentPanelController.getHudWindowController();


        final int sizeRowCount = resizer.getPropertyNames().size();
        final int locationRowCount;
        if (relocater != null) {
            locationRowCount = relocater.getPropertyNames().size();
        } else {
            locationRowCount = 0;
        }
        hudWindowController.setRowCount(sizeRowCount + locationRowCount);


        final List<PropertyName> sizePropertyNames = resizer.getPropertyNames();
        for (int i = 0; i < sizeRowCount; i++) {
            final PropertyName pn = sizePropertyNames.get(i);
            hudWindowController.setNameAtRowIndex(makeNameString(pn), i);
        }

        if (relocater != null) {
            final List<PropertyName> locationPropertyNames = relocater.getPropertyNames();
            for (int i = 0; i < locationRowCount; i++) {
                final PropertyName pn = locationPropertyNames.get(i);
                hudWindowController.setNameAtRowIndex(makeNameString(pn), sizeRowCount+i);
            }
        }

        updateHudWindow();

        hudWindowController.setRelativePosition(tunable);
        hudWindowController.openWindow((Node)resizer.getFxomObject().getClosestMainGraphNode().getSceneGraphObject());
    }

    private String makeNameString(PropertyName pn) {
        return pn.getName() + ":";
    }


    private void updateHudWindow() {
        final HudWindow hudWindowController
                = contentPanelController.getHudWindowController();
        final List<PropertyName> sizePropertyNames = resizer.getPropertyNames();
        final int sizeRowCount = sizePropertyNames.size();

        for (int i = 0; i < sizeRowCount; i++) {
            final PropertyName pn = sizePropertyNames.get(i);
            final String value = String.valueOf(resizer.getValue(pn));
            hudWindowController.setValueAtRowIndex(value, i);
        }

        if (relocater != null) {
            final List<PropertyName> locationPropertyNames = relocater.getPropertyNames();
            final int locationRowCount = locationPropertyNames.size();
            for (int i = 0; i < locationRowCount; i++) {
                final PropertyName pn = locationPropertyNames.get(i);
                final String value = String.valueOf(relocater.getValue(pn));
                hudWindowController.setValueAtRowIndex(value, sizeRowCount+i);
            }
        }
    }


//    private void showShadow() {
//        assert shadow == null;
//
//        shadow = new RegionRectangle();
//        shadow.getRegion().getStyleClass().add("resize-shadow");
//        shadow.setMouseTransparent(true);
//        rudderLayer.getLayerUI().getChildren().add(shadow);
//
//        updateShadow();
//    }
//
//    private void updateShadow() {
//        assert shadow != null;
//
//        final Node sceneGraphObject
//                = resizer.getSceneGraphObject();
//        final Transform sceneGraphObjectTransform
//                = rudderLayer.computeSceneGraphToLayerTransform(sceneGraphObject);
//        shadow.getTransforms().clear();
//        shadow.getTransforms().add(sceneGraphObjectTransform);
//        shadow.setLayoutBounds(sceneGraphObject.getLayoutBounds());
//    }
//
//    private void hideShadow() {
//        assert shadow != null;
//        rudderLayer.getLayerUI().getChildren().remove(shadow);
//        shadow = null;
//    }


    private void setupResizingGuideController() {

        switch(tunable) {
            case N:
            case S:
                matchWidth = false;
                matchHeight = true;
                break;
            case E:
            case W:
                matchWidth = true;
                matchHeight = false;
                break;
            default:
            case SE:
            case SW:
            case NE:
            case NW:
                matchWidth = true;
                matchHeight = true;
                break;
        }
        resizingGuideController = new ResizingGuideController(
                matchWidth, matchHeight, contentPanelController.getGuidesColor());

        addToResizingGuideController(fxomInstance.getFxomDocument().getFxomRoot());

        final Group guideGroup = resizingGuideController.getGuideGroup();
        assert guideGroup.isMouseTransparent();
        rudderLayer.getLayerUI().getChildren().add(guideGroup);
    }


    private void addToResizingGuideController(FXOMObject fxomObject) {
        assert fxomObject != null;

        if (fxomObject != fxomInstance) {
            if (fxomObject.getSceneGraphObject() instanceof Node) {
                final Node sceneGraphNode = (Node) fxomObject.getSceneGraphObject();
                resizingGuideController.addSampleBounds(sceneGraphNode);
            }

            final DesignHierarchyMask m = new DesignHierarchyMask(fxomObject);
            if (m.isAcceptingSubComponent()) {
                for (int i = 0, count = m.getSubComponentCount(); i < count; i++) {
                    addToResizingGuideController(m.getSubComponentAtIndex(i));
                }
            }
        }
    }


    private void dismantleResizingGuideController() {
        assert resizingGuideController != null;
        final Group guideGroup = resizingGuideController.getGuideGroup();
        assert rudderLayer.getLayerUI().getChildren().contains(guideGroup);
        rudderLayer.getLayerUI().getChildren().remove(guideGroup);
        resizingGuideController = null;
    }



    public Bounds getResizedBounds(Bounds currentBounds, double dx, double dy) {
        return tunable.getResizedBounds(currentBounds, dx, dy);
    }



    public Point2D clampVector(double dx, double dy) {
        return tunable.clampVector(dx, dy);
    }



    public Bounds snapBounds(Bounds bounds, double ratio) {
        return tunable.snapBounds(bounds, ratio);
    }

    public boolean isMatchWidth() {
        return matchWidth;
    }

    public boolean isMatchHeight() {
        return matchHeight;
    }
    
    
}