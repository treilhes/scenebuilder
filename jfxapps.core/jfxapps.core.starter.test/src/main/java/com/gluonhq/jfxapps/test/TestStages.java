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
package com.gluonhq.jfxapps.test;

import com.gluonhq.jfxapps.core.api.javafx.FxmlController;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//@ExtendWith({ApplicationExtension.class})
public class TestStages {

//    @Start
//    private void start(Stage stage) {
//        centeredButton(stage, 800, 600);
//        stage.show();
//
//    }
//
//    @Test
//    void testUi() {
//        System.out.println("add breakpoint here and debug");
//    }


    public static Pane emptyPane(Stage stage) {
        StackPane pane = new StackPane();
        stage.setScene(new Scene(pane, 1, 1, Color.BEIGE));
        return pane;
    }

    public static Button centeredButton(Stage stage, int stageWidth, int stageHeight) {
        StackPane pane = new StackPane();
        Button btn = new Button();
        pane.getChildren().add(btn);
        StackPane.setAlignment(btn, Pos.CENTER);
        stage.setScene(new Scene(pane, stageWidth, stageHeight, Color.BEIGE));
        return btn;
    }

    public static Pane center(Stage stage, int stageWidth, int stageHeight, FxmlController controller) {
        StackPane pane = new StackPane();
        Node node = controller.getRoot();
        pane.getChildren().add(node);
        StackPane.setAlignment(node, Pos.CENTER);
        stage.setScene(new Scene(pane, stageWidth, stageHeight, Color.BEIGE));
        return pane;
    }

    public static Pane paneWithEnlargedContentToFit(Stage stage, int stageWidth, int stageHeight) {
        AnchorPane pane = new AnchorPane();
        pane.getChildren().addListener((ListChangeListener<Node>) c -> {
            while(c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(a -> {
                        AnchorPane.setTopAnchor(a, 0d);
                        AnchorPane.setRightAnchor(a, 0d);
                        AnchorPane.setBottomAnchor(a, 0d);
                        AnchorPane.setLeftAnchor(a, 0d);
                    });
                }
            }
        });
        stage.setScene(new Scene(pane, stageWidth, stageHeight, Color.BEIGE));
        return pane;
    }

    public static Pane fill(Stage stage, int stageWidth, int stageHeight, FxmlController controller) {
        AnchorPane pane = new AnchorPane();
        pane.getChildren().addListener((ListChangeListener<Node>) c -> {
            while(c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(a -> {
                        AnchorPane.setTopAnchor(a, 0d);
                        AnchorPane.setRightAnchor(a, 0d);
                        AnchorPane.setBottomAnchor(a, 0d);
                        AnchorPane.setLeftAnchor(a, 0d);
                    });
                }
            }
        });
        stage.setScene(new Scene(pane, stageWidth, stageHeight, Color.BEIGE));
        pane.getChildren().add(controller.getRoot());
        stage.show();
        return pane;
    }

    public static Pane emptyWindow(Stage stage, int stageWidth, int stageHeight) {
        AnchorPane pane = new AnchorPane();
        pane.getChildren().addListener((ListChangeListener<Node>) c -> {
            while(c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(a -> {
                        AnchorPane.setTopAnchor(a, 0d);
                        AnchorPane.setRightAnchor(a, 0d);
                        AnchorPane.setBottomAnchor(a, 0d);
                        AnchorPane.setLeftAnchor(a, 0d);
                    });
                }
            }
        });
        stage.setScene(new Scene(pane, stageWidth, stageHeight, Color.BEIGE));
        stage.setResizable(true);
        stage.initStyle(StageStyle.DECORATED);
        return pane;
    }

}
