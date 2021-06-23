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
package com.oracle.javafx.scenebuilder.devutils.tbview;

import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TestContent4 {


    private int size;
    private TextField enterText;
    private Slider sizeSlider;
    private Button button;

    private Text selected;


    public void start(Stage primaryStage) {
        Pane root = new Pane();
        // Button for creating new text Object
        button = new Button("text");
        button.setLayoutX(200);

        // Slider for Size
        sizeSlider = new Slider(0, 255, 0);
        sizeSlider.setLayoutX(250);
        sizeSlider.setLayoutY(0);

        // TextField
        enterText = new TextField();

        // Button functionality
        button.setOnAction(e -> {
            Text text = new Text(150, 300, "Text");
            // Moving created text
            text.setOnMouseDragged(f -> {
                text.setX(f.getX());
                text.setY(f.getY());
            });
            text.setLayoutX(300);
            text.setLayoutY(300);
            text.setFont(Font.font("Phosphate"));

            text.textProperty().bind(enterText.textProperty());

            text.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {

                if (selected != null) {
                    selected.setFill(Color.BLACK);
                }

                selected = (Text) mouseEvent.getTarget();
                selected.setFill(Color.RED);

            });

            root.getChildren().addAll(text);
        });

        sizeSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number curVal, Number newVal) -> {

                if (selected != null) {
                size = (int) sizeSlider.getValue();
                Font fontSize = Font.font(size);
                selected.setFont(fontSize);
            }
        });

        root.getChildren().addAll(button, sizeSlider, enterText);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}