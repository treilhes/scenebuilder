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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class CoordinateHelperNestedClipTest {
    
    private FXOMDocument newFxomDocument;
    private FXOMObject rectangle;
    private FXOMObject circleClip1;
    private FXOMObject rectangleClip2;
    private FXOMObject rectangleClip3;
    private FXOMObject expected;
    
    private Bounds rectangleLocalBounds;
    private Bounds circleClip1LocalBounds;
    private Bounds rectangleClip2LocalBounds;
    private Bounds rectangleClip3LocalBounds;
    private Bounds expectedLocalBounds;
    
    private Transform rectangleLocalTransform;
    private Transform circleClip1LocalTransform;
    private Transform rectangleClip2LocalTransform;
    private Transform rectangleClip3LocalTransform;
    private Transform expectedClip3LocalTransform;
    
    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        try {
            URL url = CoordinateHelperNestedClipTest.class.getResource("3levelClipedRectangle.fxml");
            String fxmlText = new String(Files.readAllBytes(Path.of(url.toURI())));
            newFxomDocument = new FXOMDocument(fxmlText, url, CoordinateHelperNestedClipTest.class.getClassLoader(), null);
            Parent root = (Parent)newFxomDocument.getSceneGraphRoot();
            stage.setScene(new Scene(root, 300, 300));
            stage.show();
            
            rectangle = newFxomDocument.searchWithFxId("rectangle");
            circleClip1 = newFxomDocument.searchWithFxId("circleClip1");
            rectangleClip2 = newFxomDocument.searchWithFxId("rectangleClip2");
            rectangleClip3 = newFxomDocument.searchWithFxId("rectangleClip3");
            expected = newFxomDocument.searchWithFxId("expected");
            
            
            rectangleLocalBounds = ((Node)rectangle.getSceneGraphObject()).getBoundsInLocal();
            circleClip1LocalBounds = ((Node)circleClip1.getSceneGraphObject()).getBoundsInLocal();
            rectangleClip2LocalBounds = ((Node)rectangleClip2.getSceneGraphObject()).getBoundsInLocal();
            rectangleClip3LocalBounds = ((Node)rectangleClip3.getSceneGraphObject()).getBoundsInLocal();
            expectedLocalBounds = ((Node)expected.getSceneGraphObject()).getBoundsInLocal();
            
//            rectangleLocalTransform = ((Node)rectangle.getSceneGraphObject()).getBoundsInLocal();
//            circleClip1LocalBounds = ((Node)circleClip1.getSceneGraphObject()).getBoundsInLocal();
//            rectangleClip2LocalBounds = ((Node)rectangleClip2.getSceneGraphObject()).getBoundsInLocal();
//            rectangleClip3LocalBounds = ((Node)rectangleClip3.getSceneGraphObject()).getBoundsInLocal();
//            expectedLocalBounds = ((Node)expected.getSceneGraphObject()).getBoundsInLocal();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test if the java FX issue has been solved.
     * JDK-8262116 : Nodes used as clip or shape return invalid result for sceneToLocal/localToScene
     */
    @Test
    void testIfJavaFXIssueHasBeenSolved() {
        Node expectedNode = (Node)expected.getSceneGraphObject();
        Node rectangleClip3Node = (Node)rectangleClip3.getSceneGraphObject();
        
        assertNotEquals(
                expectedNode.localToScene(0, 0), 
                rectangleClip3Node.localToScene(0, 0), 
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                expectedNode.localToScene(expectedNode.getBoundsInLocal()),
                rectangleClip3Node.localToScene(rectangleClip3Node.getBoundsInLocal()),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                expectedNode.localToScene(0, 0, true),
                rectangleClip3Node.localToScene(0, 0, true),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                expectedNode.localToScene(expectedNode.getBoundsInLocal(), true),
                rectangleClip3Node.localToScene(rectangleClip3Node.getBoundsInLocal(), true),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                expectedNode.sceneToLocal(0, 0), 
                rectangleClip3Node.sceneToLocal(0, 0), 
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                expectedNode.sceneToLocal(expectedNode.getBoundsInLocal()),
                rectangleClip3Node.sceneToLocal(rectangleClip3Node.getBoundsInLocal()),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                expectedNode.sceneToLocal(0, 0, true),
                rectangleClip3Node.sceneToLocal(0, 0, true),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                expectedNode.sceneToLocal(expectedNode.getBoundsInLocal(), true),
                rectangleClip3Node.sceneToLocal(rectangleClip3Node.getBoundsInLocal(), true),
                "Alleluia !!! javafx issue solved");
    }
    
    @Test
    void shouldReturnTheSameScenePoint3DInSameScene() {
        Point3D sameScenePoint = new Point3D(200, 200, 0);
        Point3D rect = CoordinateHelper.localToScene(expected, 0.0, 0.0, 0.0);
        Point3D circ = CoordinateHelper.localToScene(rectangleClip3, 0.0, 0.0, 0.0);
        
        assertEquals(sameScenePoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameScenePoint2DInSameScene() {
        Point2D sameScenePoint = new Point2D(200, 200);
        Point2D rect = CoordinateHelper.localToScene(expected, 0.0, 0.0);
        Point2D circ = CoordinateHelper.localToScene(rectangleClip3, 0.0, 0.0);
        
        assertEquals(sameScenePoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameScenePoint3DInRootScene() {
        Point3D sameScenePoint = new Point3D(200, 200, 0);
        Point3D rect = CoordinateHelper.localToScene(expected, 0.0, 0.0, 0.0, true);
        Point3D circ = CoordinateHelper.localToScene(rectangleClip3, 0.0, 0.0, 0.0, true);
        
        assertEquals(sameScenePoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameScenePoint2DInRootScene() {
        Point2D sameScenePoint = new Point2D(200, 200);
        Point2D rect = CoordinateHelper.localToScene(expected, 0.0, 0.0, true);
        Point2D circ = CoordinateHelper.localToScene(rectangleClip3, 0.0, 0.0, true);
        
        assertEquals(sameScenePoint, rect);
        assertEquals(rect, circ);
    }
    
    
    @Test
    void shouldReturnTheSameLocalPoint2DFromSameScenePoint() {
        Point2D scenePoint = new Point2D(200, 200);
        Point2D expectedLocalPoint = new Point2D(0, 0);
        
        Point2D rect = CoordinateHelper.sceneToLocal(expected, scenePoint);
        Point2D circ = CoordinateHelper.sceneToLocal(rectangleClip3, scenePoint);
        
        assertEquals(expectedLocalPoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameLocalPoint3DFromSameScenePoint() {
        Point3D scenePoint = new Point3D(200, 200, 0);
        Point3D expectedLocalPoint = new Point3D(0, 0, 0);
        
        Point3D rect = CoordinateHelper.sceneToLocal(expected, scenePoint);
        Point3D circ = CoordinateHelper.sceneToLocal(rectangleClip3, scenePoint);
        
        assertEquals(expectedLocalPoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameLocalPoint2DFromRootScenePoint() {
        Point2D scenePoint = new Point2D(200, 200);
        Point2D expectedLocalPoint = new Point2D(0, 0);
        
        Point2D rect = CoordinateHelper.sceneToLocal(expected, scenePoint, true);
        Point2D circ = CoordinateHelper.sceneToLocal(rectangleClip3, scenePoint, true);
        
        assertEquals(expectedLocalPoint, rect);
        assertEquals(rect, circ);
    }

    @Test
    void shouldReturnTheSameBoundsInSameScene() {
        Bounds expectedBounds = new BoundingBox(200, 200, 75, 75);
        Bounds rect = CoordinateHelper.localToScene(expected, expectedLocalBounds);
        Bounds circ = CoordinateHelper.localToScene(rectangleClip3, rectangleClip3LocalBounds);
        
        assertEquals(expectedBounds, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameBoundsInRootScene() {
        Bounds expectedBounds = new BoundingBox(200, 200, 75, 75);
        Bounds rect = CoordinateHelper.localToScene(expected, expectedLocalBounds, true);
        Bounds circ = CoordinateHelper.localToScene(rectangleClip3, rectangleClip3LocalBounds, true);
        
        assertEquals(rect, expectedBounds);
        assertEquals(rect, circ);
    }

    @Test
    void shouldReturnTheSameLocalBoundsFromSameSceneBounds() {
        Bounds sceneBounds = new BoundingBox(200, 200, 75, 75);
        Bounds expectedLocalBounds = new BoundingBox(0, 0, 75, 75);
        
        Bounds rect = CoordinateHelper.sceneToLocal(expected, sceneBounds);
        Bounds circ = CoordinateHelper.sceneToLocal(rectangleClip3, sceneBounds);
        
        assertEquals(expectedLocalBounds, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameLocalBoundsFromRootSceneBounds() {
        Bounds rootSceneBounds = new BoundingBox(200, 200, 75, 75);
        Bounds expectedLocalBounds = new BoundingBox(0, 0, 75, 75);
        
        Bounds rect = CoordinateHelper.sceneToLocal(expected, rootSceneBounds, true);
        Bounds circ = CoordinateHelper.sceneToLocal(rectangleClip3, rootSceneBounds, true);
        
        assertEquals(expectedLocalBounds, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnSameTransform() throws Exception {
        Point3D scenePoint = new Point3D(200, 200, 0);
        Point3D otherScenePoint = new Point3D(100, 100, 100);
        
        final Transform expectedTransform = ((Node)expected.getSceneGraphObject()).getLocalToSceneTransform();
        final Transform rectangleClip3Transform = CoordinateHelper.localToSceneTransform(rectangleClip3);
        
        assertEquals(expectedTransform.transform(scenePoint), rectangleClip3Transform.transform(scenePoint));
        assertEquals(expectedTransform.transform(otherScenePoint), rectangleClip3Transform.transform(otherScenePoint));
    }
}
