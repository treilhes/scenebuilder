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

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public enum StageType implements StageSetup {

    Center((s,w,h,c) -> {
        StackPane pane = new StackPane();
        pane.getChildren().add(c);
        StackPane.setAlignment(c, Pos.CENTER);
        s.setScene(new Scene(pane, w, h, Color.BEIGE));

        s.show();

        return pane;
    }),
    Fill((s,w,h,c) -> {
        AnchorPane pane = new AnchorPane();
        pane.getChildren().addListener((ListChangeListener<Node>) ch -> {
            while(ch.next()) {
                if (ch.wasAdded()) {
                    ch.getAddedSubList().forEach(a -> {
                        AnchorPane.setTopAnchor(a, 0d);
                        AnchorPane.setRightAnchor(a, 0d);
                        AnchorPane.setBottomAnchor(a, 0d);
                        AnchorPane.setLeftAnchor(a, 0d);
                    });
                }
            }
        });
        s.setScene(new Scene(pane, w, h, Color.BEIGE));
        pane.getChildren().add(c);

        s.show();
        return pane;
    }),

    None((s,w,h,c) -> {
        return null;
    });

    StageSetup setup;

    StageType(StageSetup setup) {
        this.setup = setup;
    }

    @Override
    public Node setup(Stage stage, int width, int height, Parent content) {
        return setup.setup(stage, width, height, content);
    }


}
