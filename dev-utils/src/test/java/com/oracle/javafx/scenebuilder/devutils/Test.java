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
package com.oracle.javafx.scenebuilder.devutils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class Test extends AppTester {

    public DoubleProperty xCordinate;
    public DoubleProperty yCordinate;

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Image image = new Image(Test.class
                .getResource("someimage.jpg").toExternalForm());
        Scene scene = new Scene(root, image.getWidth(), image.getHeight(),
                Color.WHITE);
        final ImageView view = new ImageView();
        view.setImage(image);

        xCordinate = new SimpleDoubleProperty(100.0f);
        yCordinate = new SimpleDoubleProperty(100.0f);


        final Circle c1 = new Circle();
        c1.centerXProperty().bind(xCordinate);
        c1.centerYProperty().bind(yCordinate);
        c1.setRadius(50.0f);

        final Circle c2 = new Circle();
        c2.centerXProperty().bind(xCordinate);
        c2.centerYProperty().bind(yCordinate);
        c2.setRadius(35.0f);

        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xCordinate.set(event.getX());
                yCordinate.set(event.getY());
                //System.out.println("xCordinate " + xCordinate + " yCordinate " + yCordinate);
                // update mask clip
                Shape mask = Path.subtract(c1, c2);
                view.setClip(mask);
            }
        });

        Shape mask = Path.subtract(c1, c2);
        view.setClip(mask);

        root.getChildren().add(view);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnZoom((e) -> System.out.println("setOnZoom"));
        scene.setOnZoomStarted((e) -> System.out.println("setOnZoomStarted"));
        scene.setOnZoomFinished((e) -> System.out.println("setOnZoomFinished"));
        
        scene.addEventFilter(Event.ANY, (e) -> {
            
            if (e instanceof KeyEvent) {
                KeyEvent k = (KeyEvent)e;
                System.out.println(String.format("%s %s %s %s %s", 
                        k.getCharacter(),
                        k.getCode(),
                        k.getText(),
                        k.isAltDown(),
                        k.isControlDown()));
                
            } else if (e instanceof MouseEvent) {
                MouseEvent k = (MouseEvent)e;
                System.out.println(String.format("%s %s %s %s %s", 
                        k.isControlDown(),
                        k.isMetaDown(),
                        k.isMiddleButtonDown(),
                        k.isAltDown(),
                        k.isControlDown()));
                
            } else if (e instanceof ScrollEvent) {
                ScrollEvent k = (ScrollEvent)e;
                System.out.println(String.format("%s %s %s %s", 
                        k.isControlDown(),
                        k.isMetaDown(),
                        k.getDeltaX(),
                        k.getDeltaY()));
                
            }else {
                System.out.println(e.getClass().getName());                
            }
        });
    }

    public static void main(String args[]) {
        launch(args);
    }
}
