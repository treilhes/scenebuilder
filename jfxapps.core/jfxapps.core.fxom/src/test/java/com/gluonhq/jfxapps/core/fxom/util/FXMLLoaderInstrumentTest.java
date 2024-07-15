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
package com.gluonhq.jfxapps.core.fxom.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.gluonhq.jfxapps.core.fxom.FXOMComment;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.collector.ExpressionCollector;
import com.gluonhq.jfxapps.core.fxom.testutil.FilenameProvider;
import com.gluonhq.jfxapps.core.fxom.testutil.FxmlUtil;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@SetSystemProperty(key = "javafx.allowjs", value = "true")
// TODO reactivate this test when the code is ready
class FXMLLoaderInstrumentTest {

//    FXOMDocument fxomDocument;
//
//    @Start
//    private void start(Stage stage) {
//
//    }
//
//    @BeforeEach
//    public void setup() throws Exception {
//
//    }
//
//    @Test
//    public void instrument_test() throws IOException {
//        String fxml = """
//                <?import javafx.scene.control.Button?>
//                <?import javafx.scene.layout.Pane?>
//                <?import javafx.scene.shape.Circle?>
//                <?import java.lang.String?>
//
//                <Pane xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1">
//                   <fx:define>
//                        <String fx:id="somestring" fx:value="SomeStringValue"/>
//                        <Button fx:id="somebutton" mnemonicParsing="false" text="somebutton" />
//                        <Circle fx:id="circleblue" fill="DODGERBLUE" radius="300.0" stroke="BLACK" strokeType="INSIDE" />
//                        <Circle fx:id="circleblue2" fill="DODGERBLUE" radius="300.0" stroke="BLACK" strokeType="INSIDE" />
//                   </fx:define>
//                   <fx:define>
//                        <Pane fx:id="referred1"/>
//                   </fx:define>
//                   <children>
//                      <Pane clip="$circleblue" onAction="#doSomething">
//                         <children>
//                         <Button fx:id="somebutton2" text="$x.Y" />
//                            <Button fx:id="somebutton2" text="$somestringXX" />
//                            <fx:reference fx:id="excluded" source="referred1" layoutX="100.0" layoutY="100.0">
//                                <children>
//                                  <fx:reference source="referred2" layoutX="100.0" layoutY="100.0" clip="$circleblue2"/>
//                               </children>
//                            </fx:reference>
//                            <fx:reference fx:id="refToSomebutton" source="somebutton" layoutX="200.0" layoutY="100.0"/>
//                         </children>
//                      </Pane>
//                   </children>
//
//                </Pane>
//
//                """;
//
//        FXOMDocument fxomDocument = new FXOMDocument(fxml);
//        List<FXOMNode> items = fxomDocument.collect(ExpressionCollector.allUndeclaredExpressionReferences());
//
//        assertEquals(2, items.size());
//    }
}
