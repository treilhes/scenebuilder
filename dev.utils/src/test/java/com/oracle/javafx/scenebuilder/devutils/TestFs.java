/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.devutils;

import java.io.File;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TestFs {

    public DoubleProperty xCordinate;
    public DoubleProperty yCordinate;


    public void start(Stage primaryStage) {

        Group root = new Group();
        Image image = new Image(TestFs.class
                .getResource("someimage.jpg").toExternalForm());
        Scene scene = new Scene(root, image.getWidth(), image.getHeight(),
                Color.WHITE);
        final ImageView view = new ImageView();
        view.setImage(image);

        root.getChildren().add(view);


        Button bt = new Button("SAVE");
        bt.setOnAction((e) -> {
            final FileChooser fileChooser = new FileChooser();
            final FileChooser.ExtensionFilter f = new FileChooser.ExtensionFilter(
                    "FXML Document",
                    "*.fxml");
            final FileChooser.ExtensionFilter f2 = new FileChooser.ExtensionFilter(
                    "FXML Document2",
                    "*.fxml"); // NOI18N
            fileChooser.getExtensionFilters().add(f);
            fileChooser.getExtensionFilters().add(f2);
            File fxmlFile = fileChooser.showSaveDialog(scene.getWindow());
        });

        Button bto = new Button("OPEN");
        bto.setOnAction((e) -> {
            final FileChooser fileChooser = new FileChooser();
            final FileChooser.ExtensionFilter f = new FileChooser.ExtensionFilter(
                    "FXML Document",
                    "*.fxml");
            final FileChooser.ExtensionFilter f2 = new FileChooser.ExtensionFilter(
                    "FXML Document2",
                    "*.fxml"); // NOI18N
            fileChooser.getExtensionFilters().add(f);
            fileChooser.getExtensionFilters().add(f2);
            File fxmlFile = fileChooser.showOpenDialog(scene.getWindow());
        });
        HBox p = new HBox(bt, bto);
        root.getChildren().add(p);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
