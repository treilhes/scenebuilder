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
import com.oracle.javafx.scenebuilder.core.fxom.util.CoordinateHelper;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class CoordinateHelperTest {
    
    private FXOMDocument newFxomDocument;
    private FXOMObject rectangle;
    private FXOMObject circle;
    private Bounds rectangleLocalBounds;
    private Bounds circleLocalBounds;
    
    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        try {
            URL url = CoordinateHelperTest.class.getResource("clipedRectangle.fxml");
            String fxmlText = new String(Files.readAllBytes(Path.of(url.toURI())));
            newFxomDocument = new FXOMDocument(fxmlText, url, CoordinateHelperTest.class.getClassLoader(), null);
            Parent root = (Parent)newFxomDocument.getSceneGraphRoot();
            stage.setScene(new Scene(root, 300, 300));
            stage.show();
            
            rectangle = newFxomDocument.searchWithFxId("rectangle");
            circle = newFxomDocument.searchWithFxId("circle");
            
            rectangleLocalBounds = ((Node)rectangle.getSceneGraphObject()).getBoundsInLocal();
            circleLocalBounds = ((Node)circle.getSceneGraphObject()).getBoundsInLocal();
            
            
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
        Node rectangleNode = (Node)rectangle.getSceneGraphObject();
        Node circleNode = (Node)circle.getSceneGraphObject();
        
        assertNotEquals(
                rectangleNode.localToScene(0, 0), 
                circleNode.localToScene(0, 0), 
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                rectangleNode.localToScene(rectangleNode.getBoundsInLocal()),
                circleNode.localToScene(circleNode.getBoundsInLocal()),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                rectangleNode.localToScene(0, 0, true),
                circleNode.localToScene(0, 0, true),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                rectangleNode.localToScene(rectangleNode.getBoundsInLocal(), true),
                circleNode.localToScene(circleNode.getBoundsInLocal(), true),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                rectangleNode.sceneToLocal(0, 0), 
                circleNode.sceneToLocal(0, 0), 
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                rectangleNode.sceneToLocal(rectangleNode.getBoundsInLocal()),
                circleNode.sceneToLocal(circleNode.getBoundsInLocal()),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                rectangleNode.sceneToLocal(0, 0, true),
                circleNode.sceneToLocal(0, 0, true),
                "Alleluia !!! javafx issue solved");
        
        assertNotEquals(
                rectangleNode.sceneToLocal(rectangleNode.getBoundsInLocal(), true),
                circleNode.sceneToLocal(circleNode.getBoundsInLocal(), true),
                "Alleluia !!! javafx issue solved");
    }
    
    @Test
    void shouldReturnTheSameScenePoint3DInSameScene() {
        Point3D sameScenePoint = new Point3D(100, 100, 0);
        Point3D rect = CoordinateHelper.localToScene(rectangle, 0.0, 0.0, 0.0);
        Point3D circ = CoordinateHelper.localToScene(circle, 0.0, 0.0, 0.0);
        
        assertEquals(sameScenePoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameScenePoint2DInSameScene() {
        Point2D sameScenePoint = new Point2D(100, 100);
        Point2D rect = CoordinateHelper.localToScene(rectangle, 0.0, 0.0);
        Point2D circ = CoordinateHelper.localToScene(circle, 0.0, 0.0);
        
        assertEquals(sameScenePoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameScenePoint3DInRootScene() {
        Point3D sameScenePoint = new Point3D(200, 200, 0);
        Point3D rect = CoordinateHelper.localToScene(rectangle, 0.0, 0.0, 0.0, true);
        Point3D circ = CoordinateHelper.localToScene(circle, 0.0, 0.0, 0.0, true);
        
        assertEquals(sameScenePoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameScenePoint2DInRootScene() {
        Point2D sameScenePoint = new Point2D(200, 200);
        Point2D rect = CoordinateHelper.localToScene(rectangle, 0.0, 0.0, true);
        Point2D circ = CoordinateHelper.localToScene(circle, 0.0, 0.0, true);
        
        assertEquals(sameScenePoint, rect);
        assertEquals(rect, circ);
    }
    
    
    @Test
    void shouldReturnTheSameLocalPoint2DFromSameScenePoint() {
        Point2D scenePoint = new Point2D(100, 100);
        Point2D expectedLocalPoint = new Point2D(0, 0);
        
        Point2D rect = CoordinateHelper.sceneToLocal(rectangle, scenePoint);
        Point2D circ = CoordinateHelper.sceneToLocal(circle, scenePoint);
        
        assertEquals(expectedLocalPoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameLocalPoint3DFromSameScenePoint() {
        Point3D scenePoint = new Point3D(100, 100, 0);
        Point3D expectedLocalPoint = new Point3D(0, 0, 0);
        
        Point3D rect = CoordinateHelper.sceneToLocal(rectangle, scenePoint);
        Point3D circ = CoordinateHelper.sceneToLocal(circle, scenePoint);
        
        assertEquals(expectedLocalPoint, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameLocalPoint2DFromRootScenePoint() {
        Point2D scenePoint = new Point2D(200, 200);
        Point2D expectedLocalPoint = new Point2D(0, 0);
        
        Point2D rect = CoordinateHelper.sceneToLocal(rectangle, scenePoint, true);
        Point2D circ = CoordinateHelper.sceneToLocal(circle, scenePoint, true);
        
        assertEquals(expectedLocalPoint, rect);
        assertEquals(rect, circ);
    }

    @Test
    void shouldReturnTheSameBoundsInSameScene() {
        Bounds expectedBounds = new BoundingBox(100, 100, 100, 100);
        Bounds rect = CoordinateHelper.localToScene(rectangle, rectangleLocalBounds);
        Bounds circ = CoordinateHelper.localToScene(circle, circleLocalBounds);
        
        assertEquals(expectedBounds, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameBoundsInRootScene() {
        Bounds expectedBounds = new BoundingBox(200, 200, 100, 100);
        Bounds rect = CoordinateHelper.localToScene(rectangle, rectangleLocalBounds, true);
        Bounds circ = CoordinateHelper.localToScene(circle, circleLocalBounds, true);
        
        assertEquals(rect, expectedBounds);
        assertEquals(rect, circ);
    }

    @Test
    void shouldReturnTheSameLocalBoundsFromSameSceneBounds() {
        Bounds sceneBounds = new BoundingBox(100, 100, 100, 100);
        Bounds expectedLocalBounds = new BoundingBox(0, 0, 100, 100);
        
        Bounds rect = CoordinateHelper.sceneToLocal(rectangle, sceneBounds);
        Bounds circ = CoordinateHelper.sceneToLocal(circle, sceneBounds);
        
        assertEquals(expectedLocalBounds, rect);
        assertEquals(rect, circ);
    }
    
    @Test
    void shouldReturnTheSameLocalBoundsFromRootSceneBounds() {
        Bounds rootSceneBounds = new BoundingBox(200, 200, 100, 100);
        Bounds expectedLocalBounds = new BoundingBox(0, 0, 100, 100);
        
        Bounds rect = CoordinateHelper.sceneToLocal(rectangle, rootSceneBounds, true);
        Bounds circ = CoordinateHelper.sceneToLocal(circle, rootSceneBounds, true);
        
        assertEquals(expectedLocalBounds, rect);
        assertEquals(rect, circ);
    }
}
