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
import com.gluonhq.jfxapps.core.api.javafx.UiController;
import com.gluonhq.jfxapps.core.guides.preference.AlignmentGuidesColorPreference;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

@JfxAppsTest
@ContextConfiguration(classes = { MovingGuideControllerTest.Config.class, MovingGuideController.class })
class MovingGuideControllerTest {

    private final static Logger logger = LoggerFactory.getLogger(MovingGuideControllerTest.class);

    private static final double EQUALS_DELTA = 0.1;
    private static final double EXPECTED_DELTA = 2.0;

    private static final int CENTER_MINY = 250;
    private static final int CENTER_MINX = 350;

    private static final int BOTTOMRIGHT_MINY = 375;
    private static final int BOTTOMRIGHT_MINX = 575;
    private static final int BOTTOMLEFT_MINY = 350;
    private static final int BOTTOMLEFT_MINX = 125;
    private static final int TOPRIGHT_MINY = 125;
    private static final int TOPRIGHT_MINX = 550;
    private static final int TOPLEFT_MINY = 100;
    private static final int TOPLEFT_MINX = 100;

    private static final int SIDE = 100;
    private static final int DRAGGED_SIDE = 40;

    private static List<Node> CENTERED_SQUARE = List.of(new Rectangle(CENTER_MINX, CENTER_MINY, SIDE, SIDE));
    private static List<Node> FOUR_SQUARES = List.of(new Rectangle(TOPLEFT_MINX, TOPLEFT_MINY, SIDE, SIDE),
            new Rectangle(TOPRIGHT_MINX, TOPRIGHT_MINY, SIDE, SIDE),
            new Rectangle(BOTTOMRIGHT_MINX, BOTTOMRIGHT_MINY, SIDE, SIDE),
            new Rectangle(BOTTOMLEFT_MINX, BOTTOMLEFT_MINY, SIDE, SIDE));

    final double proxymityLimit = 6;

    @TestConfiguration
    static class Config {
        @Bean
        AlignmentGuidesColorPreference alignmentGuidesColorPreference() {
            return Mockito.mock(AlignmentGuidesColorPreference.class);
        }
    }

    private Label draggedObject;
    private Rectangle proxymityLimitBounds;
    private MoveAndMatch moveAndMatch;

    @Autowired
    private AlignmentGuidesColorPreference alignmentGuidesColorPreference;

    @Autowired
    private MovingGuide movingGuideController;

    @BeforeEach
    public void setup() {
        Mockito.when(alignmentGuidesColorPreference.getValue()).thenReturn(Color.RED);

        movingGuideController.setMatchDistance(proxymityLimit);
    }

    @Test
    void must_show_the_expected_guides(StageBuilder builder, FxRobot robot) throws Exception {

        Predicate<Line> isLeftGuide = line -> line.getStartX() == CENTER_MINX && line.getEndX() == CENTER_MINX;
        Predicate<Line> isRightGuide = line -> line.getStartX() == CENTER_MINX + SIDE
                && line.getEndX() == CENTER_MINX + SIDE;
        Predicate<Line> isTopGuide = line -> line.getStartY() == CENTER_MINY && line.getEndY() == CENTER_MINY;
        Predicate<Line> isBottomGuide = line -> line.getStartY() == CENTER_MINY + SIDE
                && line.getEndY() == CENTER_MINY + SIDE;
        Predicate<Line> isVerticalMiddleGuide = line -> line.getStartX() == CENTER_MINX + SIDE / 2
                && line.getEndX() == CENTER_MINX + SIDE / 2;
        Predicate<Line> isHorizontalMiddleGuide = line -> line.getStartY() == CENTER_MINY + SIDE / 2
                && line.getEndY() == CENTER_MINY + SIDE / 2;

        var uiController = builder.size(800, 600).setup(StageType.Fill).show();

        movingGuideController.initializeContainerBounds(uiController.getRoot().getLayoutBounds());

        setupTestUi(robot, uiController, movingGuideController, CENTERED_SQUARE);
        // setupTestUi(robot, uiController, movingGuideController, FOUR_SQUARES);

        CENTERED_SQUARE.forEach(node -> movingGuideController.addSampleBounds(node));
        // FOUR_SQUARES.forEach(node -> movingGuideController.addSampleBounds(node));

        // robot.interact(() -> ScenicView.show(uiController.getRoot()));

        // dragged : outside off the reference top left
        robot.interact(() -> moveAndMatch.moveAndMatch(CENTER_MINX - DRAGGED_SIDE / 2 - EXPECTED_DELTA,
                CENTER_MINY - DRAGGED_SIDE / 2 - EXPECTED_DELTA, movingGuideController));
        var lines = lookupLines(robot);
        assertEquals("Expected two guides", lines.size(), 2);
        assertTrue("Expected a left guide", lines.stream().anyMatch(isLeftGuide));
        assertTrue("Expected a top guide", lines.stream().anyMatch(isTopGuide));
        assertEquals("Expected suggestedX", movingGuideController.getSuggestedDX(), EXPECTED_DELTA, EQUALS_DELTA);
        assertEquals("Expected suggestedY", movingGuideController.getSuggestedDY(), EXPECTED_DELTA, EQUALS_DELTA);

        // dragged : outside off the reference top right
        robot.interact(() -> moveAndMatch.moveAndMatch(CENTER_MINX + SIDE + DRAGGED_SIDE / 2 + EXPECTED_DELTA,
                CENTER_MINY - DRAGGED_SIDE / 2 - EXPECTED_DELTA, movingGuideController));
        lines = lookupLines(robot);
        assertEquals("Expected two guides", lines.size(), 2);
        assertTrue("Expected a right guide", lines.stream().anyMatch(isRightGuide));
        assertTrue("Expected a top guide", lines.stream().anyMatch(isTopGuide));
        assertEquals("Expected suggestedX", movingGuideController.getSuggestedDX(), -EXPECTED_DELTA, EQUALS_DELTA);
        assertEquals("Expected suggestedY", movingGuideController.getSuggestedDY(), EXPECTED_DELTA, EQUALS_DELTA);

        // dragged : outside off the reference bottom right
        robot.interact(() -> moveAndMatch.moveAndMatch(CENTER_MINX + SIDE + DRAGGED_SIDE / 2 + EXPECTED_DELTA,
                CENTER_MINY + SIDE + DRAGGED_SIDE / 2 + EXPECTED_DELTA, movingGuideController));
        lines = lookupLines(robot);
        assertEquals("Expected two guides", lines.size(), 2);
        assertTrue("Expected a right guide", lines.stream().anyMatch(isRightGuide));
        assertTrue("Expected a bottom guide", lines.stream().anyMatch(isBottomGuide));
        assertEquals("Expected suggestedX", movingGuideController.getSuggestedDX(), -EXPECTED_DELTA, EQUALS_DELTA);
        assertEquals("Expected suggestedY", movingGuideController.getSuggestedDY(), -EXPECTED_DELTA, EQUALS_DELTA);

        // dragged : outside off the reference bottom left
        robot.interact(() -> moveAndMatch.moveAndMatch(CENTER_MINX - DRAGGED_SIDE / 2 - EXPECTED_DELTA,
                CENTER_MINY + SIDE + DRAGGED_SIDE / 2 + EXPECTED_DELTA, movingGuideController));
        lines = lookupLines(robot);
        assertEquals("Expected two guides", lines.size(), 2);
        assertTrue("Expected a left guide", lines.stream().anyMatch(isLeftGuide));
        assertTrue("Expected a bottom guide", lines.stream().anyMatch(isBottomGuide));
        assertEquals("Expected suggestedX", movingGuideController.getSuggestedDX(), EXPECTED_DELTA, EQUALS_DELTA);
        assertEquals("Expected suggestedY", movingGuideController.getSuggestedDY(), -EXPECTED_DELTA, EQUALS_DELTA);

        // dragged : inside at center off the reference
        robot.interact(() -> moveAndMatch.moveAndMatch(
                CENTER_MINX + SIDE/2 - EXPECTED_DELTA,
                CENTER_MINY + SIDE/2 - EXPECTED_DELTA,
                movingGuideController));
        lines = lookupLines(robot);
        assertEquals("Expected two guides", lines.size(), 2);
        assertTrue("Expected a middle vertical guide", lines.stream().anyMatch(isVerticalMiddleGuide));
        assertTrue("Expected a middle horizontal guide", lines.stream().anyMatch(isHorizontalMiddleGuide));
        assertEquals("Expected suggestedX", movingGuideController.getSuggestedDX(), EXPECTED_DELTA, EQUALS_DELTA);
        assertEquals("Expected suggestedY", movingGuideController.getSuggestedDY(), EXPECTED_DELTA, EQUALS_DELTA);
    }

    private void setupTestUi(FxRobot robot, UiController uiController, MovingGuide movingGuideController,
            List<Node> contents) {
        setupProximity();

        var objectsInScene = new Pane();

        setupDraggedObject();

        // stack.setAlignment(Pos.TOP_LEFT);
        draggedObject.onMouseDraggedProperty().set(event -> {
            moveAndMatch.moveAndMatch(event.getSceneX(), event.getSceneY(), movingGuideController);
        });

        // without controller root is a StackPane
        StackPane stack = (StackPane) uiController.getRoot();

        robot.interact(() -> {
            stack.getChildren().add(objectsInScene);
            contents.forEach(node -> objectsInScene.getChildren().add(node));
            objectsInScene.getChildren().add(proxymityLimitBounds);
            objectsInScene.getChildren().add(draggedObject);
            objectsInScene.getChildren().add(movingGuideController.getGuideGroup());
        });

    }

    private void setupDraggedObject() {
        draggedObject = new Label("drag\nme");
        draggedObject.setMinWidth(DRAGGED_SIDE);
        draggedObject.setMinHeight(DRAGGED_SIDE);
        draggedObject.setMaxWidth(DRAGGED_SIDE);
        draggedObject.setMaxHeight(DRAGGED_SIDE);
        draggedObject.setAlignment(Pos.CENTER);
        // draggedObject.setTextAlignment(TextAlignment.CENTER);
        draggedObject.setStyle("-fx-background-color: beige;");

        moveAndMatch = (x, y, movingGuide) -> {
            draggedObject.setLayoutX(x - DRAGGED_SIDE / 2);
            draggedObject.setLayoutY(y - DRAGGED_SIDE / 2);
            reconciliateLimits(proxymityLimitBounds, draggedObject);
            movingGuide.match(draggedObject);
        };
    }

    private void setupProximity() {
        proxymityLimitBounds = new Rectangle(0, 0, proxymityLimit, proxymityLimit);
        proxymityLimitBounds.setFill(Color.TRANSPARENT);
        proxymityLimitBounds.setStroke(Color.BLUE);
        proxymityLimitBounds.getStrokeDashArray().addAll(4.0, 4.0);
    }

    private void reconciliateLimits(Rectangle proxymityLimitBounds, Label draggedObject) {
        proxymityLimitBounds.setLayoutX(draggedObject.getLayoutX() - proxymityLimit);
        proxymityLimitBounds.setLayoutY(draggedObject.getLayoutY() - proxymityLimit);
        proxymityLimitBounds.setWidth(2 * proxymityLimit + draggedObject.getWidth());
        proxymityLimitBounds.setHeight(2 * proxymityLimit + draggedObject.getHeight());
    }

    private List<Line> lookupLines(FxRobot robot) {
        return robot.lookup(node -> node instanceof Line && node.isVisible()).queryAll().stream()
                .map(node -> (Line) node).toList();
    }

    private interface MoveAndMatch {
        void moveAndMatch(double x, double y, MovingGuide movingGuide);
    }
}
