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
package com.oracle.javafx.scenebuilder.tools.driver.gridpane;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.core.api.fxom.content.decoration.Decoration;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.fxom.gesture.CardinalPoint;
import com.gluonhq.jfxapps.core.api.fxom.gesture.DiscardGesture;
import com.gluonhq.jfxapps.core.api.fxom.ui.controller.misc.Workspace;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.oracle.javafx.scenebuilder.api.control.Resizer;
import com.oracle.javafx.scenebuilder.api.control.SbDriver;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.ResizeGesture;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.gesture.SelectAndMoveInGridGesture;

import javafx.geometry.Point2D;
import javafx.scene.Node;

@JfxAppsTest
@ContextConfiguration(classes = { GridPaneHandlesTest.Config.class, GridPaneHandles.class, DiscardGesture.Factory.class,
        ResizeGesture.Factory.class, SelectAndMoveInGridGesture.Factory.class, ResizeColumnGesture.Factory.class,
        ResizeRowGesture.Factory.class, ObjectSelectionGroup.Factory.class })
class GridPaneHandlesTest {

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

        @Bean
        Selection selection() {
            return Mockito.mock(Selection.class);
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
    Selection selection;

    @Autowired
    GridPaneHandles handle;

    @Test
    void test(StageBuilder stageBuilder, FxRobot robot) throws Exception {
        var testStage = stageBuilder.workspace()
                .document(
                        """
                                <?import javafx.scene.control.Button?>
                                <?import javafx.scene.control.Label?>
                                <?import javafx.scene.layout.ColumnConstraints?>
                                <?import javafx.scene.layout.GridPane?>
                                <?import javafx.scene.layout.Pane?>
                                <?import javafx.scene.layout.RowConstraints?>


                                <Pane prefHeight="427.0" prefWidth="640.0" xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1">
                                   <children>
                                      <GridPane layoutX="35.0" layoutY="32.0" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="364.0" prefWidth="555.0">
                                        <columnConstraints>
                                          <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
                                          <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
                                        </columnConstraints>
                                        <rowConstraints>
                                          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                                          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                                          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                                        </rowConstraints>
                                         <children>
                                            <Button mnemonicParsing="false" text="Button" />
                                            <Button mnemonicParsing="false" text="Button" GridPane.rowIndex="2" />
                                            <Button mnemonicParsing="false" text="Button" GridPane.columnIndex="1" GridPane.rowIndex="1" />
                                            <Label text="Label" GridPane.rowIndex="1" />
                                            <Label text="Label" GridPane.columnIndex="1" />
                                            <Label text="Label" GridPane.columnIndex="1" GridPane.rowIndex="2" />
                                         </children>
                                      </GridPane>
                                   </children>
                                </Pane>
                                """)
                .css("""
                        /*****************************************************************/


                        .tray {
                            -fx-border-color: rgba(255, 255, 255, 0.4), rgba(0, 0, 0, 0.5), rgba(255, 255, 255, 0.5);
                            -fx-border-insets: 0, 1, 2;
                            -fx-border-radius: 5 5 0 0, 4 4 0 0, 3 3 0 0;
                            -fx-background-color: #bbddff; /* Can be overwritten by user preferences */
                            -fx-background-radius: 2 2 0 0;
                            -fx-background-insets: 3;
                            -fx-alignment: center;
                        }

                        .tray.south {
                            -fx-border-radius: 0 0 5 5, 0 0 4 4, 0 0 3 3;
                            -fx-background-radius: 0 0 2 2;
                        }

                        .tray.text {
                            -fx-text-fill: black;
                            -fx-text-overrun: leading-word-ellipsis;
                            -fx-font-size: 0.769em; /* 10px (base pixel size of 13px) */
                        }

                        .tray.west .text {
                           -fx-rotate: +90;
                        }

                        .tray.east .text {
                           -fx-rotate: -90;
                        }
                        .gap {
                            -fx-stroke: transparent;
                            -fx-fill: rgba(0,0,0,0.2);
                        }

                        .gap.selected {
                            -fx-fill: rgba(0,0,0,0.4);
                        }

                        .gap.hilit {
                            -fx-stroke: rgba(0,0,0,0.4);
                            -fx-stroke-line-cap: butt;
                            /* -fx-stroke-width is setup programmatically */
                        }

                        .gap.empty {
                            -fx-stroke: rgba(0,0,0,0.4);
                            -fx-stroke-line-cap: butt;
                            -fx-stroke-dash-array: 3 3;
                        }

                        .tray.selected {
                            -fx-border-color: rgba(255, 255, 255, 0.4), rgba(0, 0, 0, 1), rgba(255, 255, 255, 0.5);
                            -fx-background-color: yellow;
                        }

                        .tray.selected .text {
                            -fx-text-fill: rgba(0,0,0,0.8);
                        }

                        .selection-rect {
                            -fx-stroke: null;
                            -fx-fill: rgba(0, 0, 0, 0.05);
                        }
                        """).size(800, 600)
                // .setup(StageType.Fill)
                .show();

        GridSelectionGroup selectionGroup = Mockito.mock(GridSelectionGroup.class);
        Mockito.when(selection.getGroup()).thenReturn(selectionGroup);
        Mockito.when(selectionGroup.getType()).thenReturn(GridSelectionGroup.Type.COLUMN);
        Mockito.when(selectionGroup.getIndexes()).thenReturn(Set.of(0));

        var document = testStage.getDocument();
        var controller = testStage.getController();

        var fxomObject = document.getFxomRoot().getChildObjects().get(0);
        var subScene = controller.getSubScene();
        var layer = controller.getLayer();

        Mockito.when(workspace.getContentSubScene()).thenReturn(subScene);
        Mockito.doReturn(resizer).when(sbDriver).makeResizer(fxomObject);

        handle.setFxomObject(fxomObject);
        handle.initialize();
        handle.update();

        robot.interact(() -> layer.getChildren().add(handle.getRootNode()));

        assertEquals("Must be the same", fxomObject, handle.getFxomObject());
        assertEquals("Must be the same", fxomObject, handle.getFxomInstance());
        assertEquals("Must be the same", fxomObject, handle.getFxomObjectProxy());

        assertEquals("Must be the same", fxomObject.getSceneGraphObject().get(), handle.getSceneGraphObject());
        assertEquals("Must be the same", fxomObject.getSceneGraphObject().get(), handle.getSceneGraphObjectProxy());
        assertEquals("Must be the same", fxomObject.getSceneGraphObject().getAs(Node.class).getLayoutBounds(),
                handle.getSceneGraphObjectBounds());

        assertEquals("Must be the same", Decoration.State.CLEAN, handle.getState());
        assertEquals("Must be the same", true, handle.isEnabled());

        //robot.interact(() -> ScenicView.show(controller.getRoot().getScene()));

        Mockito.when(selectionGroup.getType()).thenReturn(GridSelectionGroup.Type.COLUMN);
        Mockito.when(selectionGroup.getIndexes()).thenReturn(Set.of(1));
        handle.update();

        Mockito.when(selectionGroup.getType()).thenReturn(GridSelectionGroup.Type.COLUMN);
        Mockito.when(selectionGroup.getIndexes()).thenReturn(Set.of(0,1));
        handle.update();

        Mockito.when(selectionGroup.getType()).thenReturn(GridSelectionGroup.Type.ROW);
        Mockito.when(selectionGroup.getIndexes()).thenReturn(Set.of(0));
        handle.update();

        Mockito.when(selectionGroup.getType()).thenReturn(GridSelectionGroup.Type.ROW);
        Mockito.when(selectionGroup.getIndexes()).thenReturn(Set.of(1));
        handle.update();

        Mockito.when(selectionGroup.getType()).thenReturn(GridSelectionGroup.Type.ROW);
        Mockito.when(selectionGroup.getIndexes()).thenReturn(Set.of(2));
        handle.update();

        Mockito.when(selectionGroup.getType()).thenReturn(GridSelectionGroup.Type.ROW);
        Mockito.when(selectionGroup.getIndexes()).thenReturn(Set.of(0,2));
        handle.update();

        var toCheck = new HashMap<CardinalPoint, Point2D>();
        toCheck.put(CardinalPoint.N, new Point2D(150, 100));
        toCheck.put(CardinalPoint.NE, new Point2D(200, 100));
        toCheck.put(CardinalPoint.E, new Point2D(200, 150));
        toCheck.put(CardinalPoint.SE, new Point2D(200, 200));
        toCheck.put(CardinalPoint.S, new Point2D(150, 200));
        toCheck.put(CardinalPoint.SW, new Point2D(100, 200));
        toCheck.put(CardinalPoint.W, new Point2D(100, 150));
        toCheck.put(CardinalPoint.NW, new Point2D(100, 100));

        var graphToLayer = handle.computeSceneGraphToLayerTransform(fxomObject);

        for (var cp : CardinalPoint.values()) {
            var refPoint = toCheck.get(cp);
            var node = handle.getHandleNode(cp);
            var point1 = handle.sceneGraphObjectToDecoration(refPoint.getX(), refPoint.getY(), true);
            var point2 = graphToLayer.transform(refPoint);

            assertEquals("Computed points must be equals", point1, point2);

            var subSceneBounds = subScene.localToScene(subScene.getLayoutBounds(), true);
            double expectedX = subSceneBounds.getMinX() + refPoint.getX();
            double expectedY = subSceneBounds.getMinY() + refPoint.getY();

            assertEquals("Must be the same x", expectedX, node.getLayoutX(), 0.1);
            assertEquals("Must be the same x", expectedY, node.getLayoutY(), 0.1);
        }

        // var b = handle.findEnabledGesture(node);
        // var c = handle.findGesture(node);

        // var b = handle.findEnabledGesture(node);
        // var c = handle.findGesture(node);
    }

}
