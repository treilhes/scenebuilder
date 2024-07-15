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
package com.gluonhq.jfxapps.core.fxom;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.gluonhq.jfxapps.core.fxom.collector.SceneGraphCollector;

import javafx.stage.Stage;

@ExtendWith({ ApplicationExtension.class, MockitoExtension.class })
class FXOMPathTest {

    private final static String FXML = """
            <?import javafx.scene.control.Button?>
            <?import javafx.scene.layout.Pane?>

            <Pane fx:id="pane1" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1">
                <children>
                  <Button fx:id="button1" />
                  <Pane fx:id="pane2">
                    <children>
                      <Button fx:id="button2" />
                    </children>
                  </Pane>
               </children>
            </Pane>
            """;

    private FXOMDocument fxomDocument;
    private FXOMObject pane1;
    private FXOMObject button1;
    private FXOMObject pane2;
    private FXOMObject button2;

    @Start
    private void start(Stage stage) {

    }

    @BeforeEach
    void setUp() throws Exception {
        fxomDocument = new FXOMDocument(FXML);
        var graphObjects = fxomDocument.collect(SceneGraphCollector.allSceneGraphObjects());
        pane1 = graphObjects.get(0);
        button1 = graphObjects.get(1);
        pane2 = graphObjects.get(2);
        button2 = graphObjects.get(3);
    }

    @Test
    void testGetSize() {
        // Pane -> Button
        assertEquals(2, FXOMPath.of(button1).getSize());
        // Pane -> Pane -> Button
        assertEquals(3, FXOMPath.of(button2).getSize());
    }

    @Test
    void testIsEmpty() {
        assertFalse(FXOMPath.of(button2).isEmpty());
    }

    @Test
    void testGetRoot() {
        assertEquals(fxomDocument.getFxomRoot(), FXOMPath.of(button2).getRoot());
    }

    @Test
    void testGetLeaf() {
        assertEquals(button2, FXOMPath.of(button2).getLeaf());
    }

    @Test
    void testGetPath() {
        assertEquals(List.of(pane1, pane2, button2), FXOMPath.of(button2).getPath());
    }

    @Test
    void testIsBefore() {
        assertTrue(FXOMPath.of(pane1).isBefore(FXOMPath.of(button1)));
        assertTrue(FXOMPath.of(button1).isBefore(FXOMPath.of(pane2)));
        assertTrue(FXOMPath.of(pane2).isBefore(FXOMPath.of(button2)));
        assertFalse(FXOMPath.of(button2).isBefore(FXOMPath.of(pane1)));
    }

    @Test
    void testIsAfter() {
        assertFalse(FXOMPath.of(pane1).isAfter(FXOMPath.of(button1)));
        assertFalse(FXOMPath.of(button1).isAfter(FXOMPath.of(pane2)));
        assertFalse(FXOMPath.of(pane2).isAfter(FXOMPath.of(button2)));
        assertTrue(FXOMPath.of(button2).isAfter(FXOMPath.of(pane1)));
    }

    @Test
    void testEquals() {
        assertEquals(FXOMPath.of(pane2), FXOMPath.of(pane2));
    }

    @Test
    void testGetCommonPathWith() {
        FXOMPath commonPath = FXOMPath.of(pane2).getCommonPathWith(FXOMPath.of(button1));
        assertEquals(FXOMPath.of(pane1), commonPath);
    }

    @Test
    void testTop() {
        FXOMPath topPath = FXOMPath.top(pane2, button1,button2);
        assertEquals(FXOMPath.of(button1), topPath);
    }

    @Test
    void testBottom() {
        FXOMPath bottomPath = FXOMPath.bottom(pane2, button2, button1);
        assertEquals(FXOMPath.of(button2), bottomPath);
    }

    @Test
    void testSort() {
        List<FXOMObject> fxomObjects = Arrays.asList(pane2, button2, button1, pane1);
        FXOMPath.sort(fxomObjects);
        assertEquals(Arrays.asList(pane1, button1, pane2, button2), fxomObjects);
    }
}
