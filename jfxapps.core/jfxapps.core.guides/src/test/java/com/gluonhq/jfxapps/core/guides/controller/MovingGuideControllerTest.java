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
package com.gluonhq.jfxapps.core.guides.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.core.api.guide.MovingGuide;
import com.gluonhq.jfxapps.core.guides.preference.AlignmentGuidesColorPreference;
import com.gluonhq.jfxapps.core.guides.preference.GuidesEnabledPreference;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

@JfxAppsTest
@ContextConfiguration(classes = { MovingGuideControllerTest.Config.class, MovingGuideController.class })
class MovingGuideControllerTest {

    private final static Logger logger = LoggerFactory.getLogger(MovingGuideControllerTest.class);

    private static final double EQ_DELTA = 0.1;
    private static final double DELTA = 2.0;

    private static final double PROXYMITY_LIMIT = 6; // -fx-border-insets: -6

    @TestConfiguration
    static class Config {
        @Bean
        AlignmentGuidesColorPreference alignmentGuidesColorPreference() {
            return Mockito.mock(AlignmentGuidesColorPreference.class);
        }

        @Bean
        GuidesEnabledPreference guidesEnabledPreference() {
            GuidesEnabledPreference guidesEnabledPreference = Mockito.mock(GuidesEnabledPreference.class);

            SimpleBooleanProperty guidesEnabled = new SimpleBooleanProperty(true);
            Mockito.when(guidesEnabledPreference.getValue()).thenReturn(guidesEnabled.getValue());
            Mockito.when(guidesEnabledPreference.getObservableValue()).thenReturn(guidesEnabled);

            return guidesEnabledPreference;
        }
    }

    private MoveAndMatch moveAndMatch = (n, x, y, movingGuide) -> {
        n.setLayoutX(x - n.getWidth() / 2);
        n.setLayoutY(y - n.getHeight() / 2);
        movingGuide.match(n);
    };;

    @Autowired
    private AlignmentGuidesColorPreference alignmentGuidesColorPreference;

    @Autowired
    private GuidesEnabledPreference guidesEnabledPreference;

    @Autowired
    private MovingGuide guide;

    @BeforeEach
    public void setup() {
        Mockito.when(alignmentGuidesColorPreference.getValue()).thenReturn(Color.RED);

        guide.setMatchDistance(PROXYMITY_LIMIT);
    }

    @Test
    void must_show_the_expected_guides(StageBuilder builder, FxRobot robot) throws Exception {

        //@formatter:off
        var testStage = builder.workspace()
                .size(800, 600)
                .document("""
                        <?xml version="1.0" encoding="UTF-8"?>

                        <?import javafx.scene.control.Label?>
                        <?import javafx.scene.layout.Pane?>
                        <?import javafx.scene.shape.Rectangle?>


                        <Pane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1">
                           <children>
                              <Rectangle fx:id="square" arcHeight="5.0" arcWidth="5.0" fill="DODGERBLUE" height="100.0" layoutX="250.0" layoutY="150.0" stroke="BLACK" strokeType="INSIDE" width="100.0" />
                              <Label fx:id="dragme" alignment="CENTER" layoutX="32.0" layoutY="34.0" prefHeight="40.0" prefWidth="40.0" style="-fx-background-color: grey; -fx-border-color: blue; -fx-border-style: segments(5, 5, 5, 5)  line-cap round; -fx-border-insets: -6; -fx-text-fill: white;" text="drag&#10;me" textAlignment="CENTER" />
                           </children>
                        </Pane>
                        """)
                .setup(StageType.Fill).show();
        //@formatter:on

        var uiController = testStage.getController();
        var scene = uiController.getRoot().getScene();
        var subScene = uiController.getSubScene();


        Label dragme = robot.from(uiController.getSubSceneRoot()).lookup("#dragme").query();
        assertEquals("Must be a square", dragme.getWidth(), dragme.getHeight(), EQ_DELTA);
        double drag_side = dragme.getWidth();

        Rectangle square = robot.from(uiController.getSubSceneRoot()).lookup("#square").query();
        assertEquals("Must be a square", square.getWidth(), square.getHeight(), EQ_DELTA);

        double minx = square.getLayoutX();
        double miny = square.getLayoutY();
        double side = square.getWidth();

        Predicate<Line> isLeftGuide = (line) -> {
            var local = lineToLocal(subScene, line);
            return local.getMinX() == minx && local.getMaxX() == minx;
        };
        Predicate<Line> isRightGuide = line -> {
            var local = lineToLocal(subScene, line);
            return local.getMinX() == minx + side && local.getMaxX() == minx + side;
        };
        Predicate<Line> isTopGuide = line -> {
            var local = lineToLocal(subScene, line);
            return local.getMinY() == miny && local.getMaxY() == miny;
        };
        Predicate<Line> isBottomGuide = line -> {
            var local = lineToLocal(subScene, line);
            return local.getMinY() == miny + side && local.getMaxY() == miny + side;
        };
        Predicate<Line> isVerticalMiddleGuide = line -> {
            var local = lineToLocal(subScene, line);
            return local.getMinX() == minx + side / 2 && local.getMaxX() == minx + side / 2;
        };
        Predicate<Line> isHorizontalMiddleGuide = line -> {
            var local = lineToLocal(subScene, line);
            return local.getMinY() == miny + side / 2 && local.getMaxY() == miny + side / 2;
        };


        // if you want to play: add a breakpoint and uncomment this
        dragme.onMouseDraggedProperty().set(event -> {
            // events originate from the top scene so we need to convert them to the subscene coordinate
            var localEvt = subScene.sceneToLocal(event.getSceneX(), event.getSceneY());
            moveAndMatch.moveAndMatch(dragme, localEvt.getX(), localEvt.getY(), guide);
        });

        guide.initializeContainerBounds(scene.getRoot().getLayoutBounds());

        robot.interact(() -> uiController.getLayer().getChildren().add(guide.getGuideGroup()));

        guide.addSampleBounds(square);

        //robot.interact(() -> ScenicView.show(uiController.getRoot().getScene()));

        // dragged : outside off the reference top left
        robot.interact(() -> moveAndMatch.moveAndMatch(dragme, minx - drag_side / 2 - DELTA,
                miny - drag_side / 2 - DELTA, guide));
        var lines = lookupLines(robot);
        assertEquals("Expected two guides", 2, lines.size());
        assertTrue("Expected a left guide", lines.stream().anyMatch(isLeftGuide));
        assertTrue("Expected a top guide", lines.stream().anyMatch(isTopGuide));
        assertEquals("Expected suggestedX", DELTA, guide.getSuggestedDX(), EQ_DELTA);
        assertEquals("Expected suggestedY", DELTA, guide.getSuggestedDY(), EQ_DELTA);

        // dragged : outside off the reference top right
        robot.interact(() -> moveAndMatch.moveAndMatch(dragme, minx + side + drag_side / 2 + DELTA,
                miny - drag_side / 2 - DELTA, guide));
        lines = lookupLines(robot);
        assertEquals("Expected two guides", 2, lines.size());
        assertTrue("Expected a right guide", lines.stream().anyMatch(isRightGuide));
        assertTrue("Expected a top guide", lines.stream().anyMatch(isTopGuide));
        assertEquals("Expected suggestedX", -DELTA, guide.getSuggestedDX(), EQ_DELTA);
        assertEquals("Expected suggestedY", DELTA, guide.getSuggestedDY(), EQ_DELTA);

        // dragged : outside off the reference bottom right
        robot.interact(() -> moveAndMatch.moveAndMatch(dragme, minx + side + drag_side / 2 + DELTA,
                miny + side + drag_side / 2 + DELTA, guide));
        lines = lookupLines(robot);
        assertEquals("Expected two guides", 2, lines.size());
        assertTrue("Expected a right guide", lines.stream().anyMatch(isRightGuide));
        assertTrue("Expected a bottom guide", lines.stream().anyMatch(isBottomGuide));
        assertEquals("Expected suggestedX", -DELTA, guide.getSuggestedDX(), EQ_DELTA);
        assertEquals("Expected suggestedY", -DELTA, guide.getSuggestedDY(), EQ_DELTA);

        // dragged : outside off the reference bottom left
        robot.interact(() -> moveAndMatch.moveAndMatch(dragme, minx - drag_side / 2 - DELTA,
                miny + side + drag_side / 2 + DELTA, guide));
        lines = lookupLines(robot);
        assertEquals("Expected two guides", 2, lines.size());
        assertTrue("Expected a left guide", lines.stream().anyMatch(isLeftGuide));
        assertTrue("Expected a bottom guide", lines.stream().anyMatch(isBottomGuide));
        assertEquals("Expected suggestedX", DELTA, guide.getSuggestedDX(), EQ_DELTA);
        assertEquals("Expected suggestedY", -DELTA, guide.getSuggestedDY(), EQ_DELTA);

        // dragged : inside at center off the reference
        robot.interact(
                () -> moveAndMatch.moveAndMatch(dragme, minx + side / 2 - DELTA, miny + side / 2 - DELTA, guide));
        lines = lookupLines(robot);
        assertEquals("Expected two guides", 2, lines.size());
        assertTrue("Expected a middle vertical guide", lines.stream().anyMatch(isVerticalMiddleGuide));
        assertTrue("Expected a middle horizontal guide", lines.stream().anyMatch(isHorizontalMiddleGuide));
        assertEquals("Expected suggestedX", DELTA, guide.getSuggestedDX(), EQ_DELTA);
        assertEquals("Expected suggestedY", DELTA, guide.getSuggestedDY(), EQ_DELTA);
    }

    private List<Line> lookupLines(FxRobot robot) {
        return robot.lookup(node -> node instanceof Line && node.isVisible()).queryAll().stream()
                .map(node -> (Line) node).toList();
    }

    private Bounds lineToLocal(SubScene subScene, Line line) {
        return subScene.sceneToLocal(new BoundingBox(line.getStartX(), line.getStartY(), line.getEndX() - line.getStartX(), line.getEndY() - line.getStartY()));
    }
    private interface MoveAndMatch {
        void moveAndMatch(Region n, double x, double y, MovingGuide movingGuide);
    }
}
