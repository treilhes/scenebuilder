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
package com.oracle.javafx.scenebuilder.tools.driver.line;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.core.api.fxom.content.decoration.Decoration;
import com.gluonhq.jfxapps.core.api.fxom.gesture.DiscardGesture;
import com.gluonhq.jfxapps.core.api.fxom.ui.controller.misc.Workspace;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.oracle.javafx.scenebuilder.api.control.Resizer;
import com.oracle.javafx.scenebuilder.api.control.SbDriver;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.EditCurveGesture;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

@JfxAppsTest
@ContextConfiguration(classes = { LineHandlesTest.Config.class, LineHandles.class, DiscardGesture.Factory.class, EditCurveGesture.Factory.class })
class LineHandlesTest {

    @TestConfiguration
    public static class Config {
        @Bean
        Workspace workspace() {
            return Mockito.mock(Workspace.class);
        }

        @Bean
        Resizer<Node> resizer() {
            return Mockito.mock(Resizer.class);
        }

        @Bean
        SbDriver sbDriver() {
            return Mockito.mock(SbDriver.class);
        }

    }

    @Autowired
    Workspace workspace;

    @Autowired
    ApplicationInstanceEvents instanceEvents;

    @Autowired
    Resizer<?> resizer;

    @Autowired
    SbDriver sbDriver;

    @Autowired
    LineHandles handle;

    @Test
    void must_show_handles_and_positions_must_match(StageBuilder stageBuilder, FxRobot robot) {
        var testStage = stageBuilder.workspace()
                .document("""
                <?xml version="1.0" encoding="UTF-8"?>

                <?import javafx.scene.shape.Line?>
                <?import javafx.scene.layout.Pane?>

                <Pane prefHeight="100.0" prefWidth="300.0" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1">
                   <children>
                      <Line startX="20.0" endX="280.0" startY="20.0" endY="80.0"/>
                   </children>
                </Pane>
                """)
                .size(800, 600)
                //.setup(StageType.Fill)
                .show();

        var document = testStage.getDocument();
        var controller = testStage.getController();

        var fxomObject = document.getFxomRoot().getChildObjects().get(0);
        var subScene = controller.getSubScene();
        var layer = controller.getLayer();

        Mockito.when(workspace.getContentSubScene()).thenReturn(subScene);
        Mockito.doReturn(resizer).when(sbDriver).makeResizer(fxomObject);

        handle.setFxomObject(fxomObject);
        handle.initialize();

        robot.interact(() -> layer.getChildren().add(handle.getRootNode()));

        assertEquals("Must be the same", fxomObject, handle.getFxomObject());
        assertEquals("Must be the same", fxomObject, handle.getFxomInstance());
        assertEquals("Must be the same", fxomObject, handle.getFxomObjectProxy());

        assertEquals("Must be the same", fxomObject.getSceneGraphObject().get(), handle.getSceneGraphObject());
        assertEquals("Must be the same", fxomObject.getSceneGraphObject().get(), handle.getSceneGraphObjectProxy());
        assertEquals("Must be the same", fxomObject.getSceneGraphObject().getAs(Node.class).getLayoutBounds(), handle.getSceneGraphObjectBounds());

        assertEquals("Must be the same", Decoration.State.CLEAN, handle.getState());
        assertEquals("Must be the same", true, handle.isEnabled());

        //robot.interact(() -> ScenicView.show(controller.getRoot().getScene()));

        var toCheck = new HashMap<Circle, Point2D>();
        toCheck.put(handle.getStartHandle(), new Point2D(20, 20));
        toCheck.put(handle.getEndHandle(), new Point2D(280, 80));

        var graphToLayer = handle.computeSceneGraphToLayerTransform(fxomObject);

        for (var e : toCheck.entrySet()) {
            var node = e.getKey();
            var refPoint = e.getValue();

            var point1 = handle.sceneGraphObjectToDecoration(refPoint.getX(), refPoint.getY(), true);
            var point2 = graphToLayer.transform(refPoint);

            assertEquals("Computed points must be equals", point1, point2);

            var subSceneBounds = subScene.localToScene(subScene.getLayoutBounds(), true);
            double expectedX = subSceneBounds.getMinX() + refPoint.getX();
            double expectedY = subSceneBounds.getMinY() + refPoint.getY();

            assertEquals("Must be the same x", expectedX, node.getCenterX(), 0.1);
            assertEquals("Must be the same x", expectedY, node.getCenterY(), 0.1);
        }

        //var b = handle.findEnabledGesture(node);
        //var c = handle.findGesture(node);
    }

}