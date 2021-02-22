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
package com.oracle.javafx.scenebuilder.core.util;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.sun.javafx.geometry.BoundsUtils;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneUtils;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.SubScene;

/**
 * This class has been created has a workaround for the issue :
 * JDK-8262116 : Nodes used as clip or shape return invalid result for sceneToLocal/localToScene
 * When calculating position for nodes used as clip or as shape, the lack of parent
 * (getParent() return null) cause a calculation error. This class aims to solve this problem
 * until a valid fix is available 
 * @author ptreilhes
 */
public class CoordinateHelper {
    
    public static Point2D localToScene(FXOMObject fxomObject, double localX, double localY) {
        Point3D tmp = localToScene(fxomObject, localX, localY, 0);
        return new Point2D(tmp.getX(), tmp.getY());
    }
    
    public static Point2D localToScene(FXOMObject fxomObject, Point2D localPoint) {
        return localToScene(fxomObject, localPoint.getX(), localPoint.getY());
    }
    
    public static Point3D localToScene(FXOMObject fxomObject, double localX, double localY, double localZ) {
        return localToScene(fxomObject, new Point3D(localX, localY, localZ));
    }
    
    public static Point3D localToScene(FXOMObject fxomObject, Point3D localPoint) {
        Node thisNode = (Node)fxomObject.getSceneGraphObject();
        Point3D sceneXY = thisNode.localToParent(localPoint);
        if (fxomObject.getParentObject() != null  && !(fxomObject.getParentObject().getSceneGraphObject() instanceof SubScene)) {
            sceneXY = localToScene(fxomObject.getParentObject(), sceneXY);
        }
        return sceneXY;
    }
    
    public static Point2D localToScene(FXOMObject fxomObject, double localX, double localY, boolean rootScene) {
        Point3D tmp = localToScene(fxomObject, localX, localY, 0, rootScene);
        return new Point2D(tmp.getX(), tmp.getY());
    }
    
    public static Point2D localToScene(FXOMObject fxomObject, Point2D localPoint, boolean rootScene) {
        return localToScene(fxomObject, localPoint.getX(), localPoint.getY(), rootScene);
    }
    
    public static Point3D localToScene(FXOMObject fxomObject, double localX, double localY, double localZ, boolean rootScene) {
        return localToScene(fxomObject, new Point3D(localX, localY, localZ), rootScene);
    }
    
    public static Point3D localToScene(FXOMObject fxomObject, Point3D localPoint, boolean rootScene) {
        Point3D sceneXY = localToScene(fxomObject, localPoint);
        if (rootScene) {
            final SubScene subScene = NodeHelper.getSubScene((Node)fxomObject.getClosestMainGraphNode().getSceneGraphObject());
            //final SubScene subScene = NodeHelper.getSubScene((Node)fxomObject.getSceneGraphObject());
            if (subScene != null) {
                sceneXY = SceneUtils.subSceneToScene(subScene, sceneXY);
            }
        }
        return sceneXY;
    }
    
    public static Bounds localToScene(FXOMObject fxomObject, Bounds bounds) {
        Node thisNode = (Node)fxomObject.getSceneGraphObject();
        Bounds newBounds = thisNode.localToParent(bounds);
        if (fxomObject.getParentObject() != null && !(fxomObject.getParentObject().getSceneGraphObject() instanceof SubScene)) {
            newBounds = localToScene(fxomObject.getParentObject(), newBounds);
        }
        return newBounds;
    }
    
    public static Bounds localToScene(FXOMObject fxomObject, Bounds localBounds, boolean rootScene) {
        if (!rootScene) {
            return localToScene(fxomObject, localBounds);
        }
        Point3D p1 = localToScene(fxomObject, localBounds.getMinX(), localBounds.getMinY(), localBounds.getMinZ(), true);
        Point3D p2 = localToScene(fxomObject, localBounds.getMinX(), localBounds.getMinY(), localBounds.getMaxZ(), true);
        Point3D p3 = localToScene(fxomObject, localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMinZ(), true);
        Point3D p4 = localToScene(fxomObject, localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMaxZ(), true);
        Point3D p5 = localToScene(fxomObject, localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMinZ(), true);
        Point3D p6 = localToScene(fxomObject, localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMaxZ(), true);
        Point3D p7 = localToScene(fxomObject, localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMinZ(), true);
        Point3D p8 = localToScene(fxomObject, localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMaxZ(), true);
        return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);
    }

    public static Point2D sceneToLocal(FXOMObject fxomObject, double sceneX, double sceneY) {
        Point3D tmp = sceneToLocal(fxomObject, sceneX, sceneY, 0);
        return new Point2D(tmp.getX(), tmp.getY());
    }
    
    public static Point2D sceneToLocal(FXOMObject fxomObject, Point2D scenePoint) {
        return sceneToLocal(fxomObject, scenePoint.getX(), scenePoint.getY());
    }
    
    public static Point2D sceneToLocal(FXOMObject fxomObject, double sceneX, double sceneY, boolean rootScene) {
        if (!rootScene) {
            return sceneToLocal(fxomObject, sceneX, sceneY);
        }
        
        Point2D tempPt = new Point2D(sceneX, sceneY);

        //final SubScene subScene = NodeHelper.getSubScene((Node)fxomObject.getSceneGraphObject());
        final SubScene subScene = NodeHelper.getSubScene((Node)fxomObject.getClosestMainGraphNode().getSceneGraphObject());
        if (subScene != null) {
            final Point2D ssCoord = SceneUtils.sceneToSubScenePlane(subScene, tempPt);
            if (ssCoord == null) {
                return null;
            }
            tempPt = ssCoord;
        }

        tempPt = sceneToLocal(fxomObject, tempPt);
        return tempPt;
    }
    
    public static Point2D sceneToLocal(FXOMObject fxomObject, Point2D scenePoint, boolean rootScene) {
        return sceneToLocal(fxomObject, scenePoint.getX(), scenePoint.getY(), rootScene);
    }
    
    public static Point3D sceneToLocal(FXOMObject fxomObject, double sceneX, double sceneY, double sceneZ) {
        return sceneToLocal(fxomObject, new Point3D(sceneX, sceneY, sceneZ));
    }
    
    public static Point3D sceneToLocal(FXOMObject fxomObject, Point3D scenePoint) {
        Node thisNode = (Node)fxomObject.getSceneGraphObject();
        if (fxomObject.getParentObject() != null 
                && !(fxomObject.getParentObject().getSceneGraphObject() instanceof SubScene)) {
            scenePoint = sceneToLocal(fxomObject.getParentObject(), scenePoint);
        }
        return thisNode.parentToLocal(scenePoint);
    }
    
    public static Bounds sceneToLocal(FXOMObject fxomObject, Bounds sceneBounds) {
        Point3D p1 = sceneToLocal(fxomObject, sceneBounds.getMinX(), sceneBounds.getMinY(), sceneBounds.getMinZ());
        Point3D p2 = sceneToLocal(fxomObject, sceneBounds.getMinX(), sceneBounds.getMinY(), sceneBounds.getMaxZ());
        Point3D p3 = sceneToLocal(fxomObject, sceneBounds.getMinX(), sceneBounds.getMaxY(), sceneBounds.getMinZ());
        Point3D p4 = sceneToLocal(fxomObject, sceneBounds.getMinX(), sceneBounds.getMaxY(), sceneBounds.getMaxZ());
        Point3D p5 = sceneToLocal(fxomObject, sceneBounds.getMaxX(), sceneBounds.getMaxY(), sceneBounds.getMinZ());
        Point3D p6 = sceneToLocal(fxomObject, sceneBounds.getMaxX(), sceneBounds.getMaxY(), sceneBounds.getMaxZ());
        Point3D p7 = sceneToLocal(fxomObject, sceneBounds.getMaxX(), sceneBounds.getMinY(), sceneBounds.getMinZ());
        Point3D p8 = sceneToLocal(fxomObject, sceneBounds.getMaxX(), sceneBounds.getMinY(), sceneBounds.getMaxZ());
        return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);
    }
    
    public static Bounds sceneToLocal(FXOMObject fxomObject, Bounds bounds, boolean rootScene) {
        if (!rootScene) {
            return sceneToLocal(fxomObject, bounds);
        }
        if (bounds.getMinZ() != 0 || bounds.getMaxZ() != 0) {
            return null;
        }
        final Point2D p1 = sceneToLocal(fxomObject, bounds.getMinX(), bounds.getMinY(), true);
        final Point2D p2 = sceneToLocal(fxomObject, bounds.getMinX(), bounds.getMaxY(), true);
        final Point2D p3 = sceneToLocal(fxomObject, bounds.getMaxX(), bounds.getMinY(), true);
        final Point2D p4 = sceneToLocal(fxomObject, bounds.getMaxX(), bounds.getMaxY(), true);

        return BoundsUtils.createBoundingBox(p1, p2, p3, p4);
    }
    
    public static boolean isHit(FXOMObject fxomObject, double sceneX, double sceneY) {
        if (fxomObject.isNode()) {
            Node node = (Node)fxomObject.getSceneGraphObject();
            return localToScene(fxomObject, node.getBoundsInLocal(), true).contains(sceneX, sceneY);
        }
        return false;
    }
    

    
}
