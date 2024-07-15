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
package com.gluonhq.jfxapps.core.fxom.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector.FxIdUniqueMap;

import javafx.scene.control.Button;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@SetSystemProperty(key = "javafx.allowjs", value = "true")
class FxIdCollectorTest {

    private static final String INCLUDE = """
            <?import javafx.scene.layout.Pane?>
            <Pane xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1" />
            """;

    @TempDir
    static Path tempDir;

    @Start
    private void start(Stage stage) {

    }

    @BeforeAll
    public static void init() throws Exception {
        Files.writeString(tempDir.resolve("included.fxml"), INCLUDE, StandardOpenOption.CREATE);
    }

    @Test
    public void should_return_the_right_number_of_fxid() throws Exception {
        String fxml = """
                <?import javafx.scene.control.Button?>
                <?import javafx.scene.layout.Pane?>

                <Pane xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1">
                   <fx:define>
                        <fx:include fx:id="id1" source="included.fxml" />
                        <fx:include fx:id="id2" source="included.fxml" />
                        <Button fx:id="id3" text="somebutton" />
                   </fx:define>
                   <children>
                      <Pane fx:id="id4">
                         <children>
                            <Button fx:id="id5"/>
                            <fx:reference fx:id="id6" source="id1">
                                <children>
                                  <fx:reference fx:id="id7" source="id2" />
                               </children>
                            </fx:reference>
                            <fx:reference fx:id="id8" source="id3"/>
                         </children>
                      </Pane>
                   </children>
                </Pane>
                """;

        FXOMDocument fxomDocument = new FXOMDocument(fxml, tempDir.toAbsolutePath().toUri().toURL(), null, null);

        Map<String, FXOMObject> items = fxomDocument.getFxomRoot().collect(FxCollector.fxIdsUniqueMap());

        assertEquals(8, items.size());
    }

    @Test
    public void should_throw_a_non_unique_exception_on_duplicate_id() throws Exception {
        String fxml = """
                <?import javafx.scene.control.Button?>
                <?import javafx.scene.layout.Pane?>

                <Pane xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1">
                   <children>
                      <Button fx:id="id1"/>
                      <Button fx:id="id1"/>
                   </children>
                </Pane>
                """;

        FXOMDocument fxomDocument = new FXOMDocument(fxml);
        boolean ENSURE_UNICITY = true;

        assertThrows(FxIdUniqueMap.DuplicateIdException.class,
                () -> fxomDocument.getFxomRoot().collect(FxCollector.fxIdsUniqueMap(ENSURE_UNICITY)));
    }

    @Test
    public void should_return_the_first_occurence_for_each_fxid() throws Exception {
        String fxml = """
                <?import javafx.scene.control.Button?>
                <?import javafx.scene.layout.Pane?>
                <?import javafx.scene.shape.Circle?>

                <Pane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1">
                   <fx:define>
                        <fx:include fx:id="referred1" source="included.fxml" />
                        <fx:include fx:id="referred2" source="included.fxml" />
                        <Button fx:id="somebutton" mnemonicParsing="false" text="somebutton" />
                        <Button fx:id="btnClip" mnemonicParsing="false" text="first" />
                        <Button fx:id="btnClip" mnemonicParsing="false" text="second" />
                        <Button fx:id="btnDefine" mnemonicParsing="false" text="first" />
                        <Button fx:id="btnDefine" mnemonicParsing="false" text="second" />
                   </fx:define>
                   <children>
                      <Pane layoutX="200.0" layoutY="100.0" prefHeight="200.0" prefWidth="200.0" clip="$btnClip">
                         <children>
                            <Button fx:id="btn" mnemonicParsing="false" text="first" />
                            <Button fx:id="btn" mnemonicParsing="false" text="second" />
                            <fx:reference fx:id="excluded" source="referred1" layoutX="100.0" layoutY="100.0">
                                <children>
                                  <fx:reference source="referred2" layoutX="100.0" layoutY="100.0"/>
                               </children>
                            </fx:reference>
                            <fx:reference source="somebutton" layoutX="200.0" layoutY="100.0"/>
                         </children>
                      </Pane>
                   </children>
                </Pane>
                """;
        FXOMDocument fxomDocument = new FXOMDocument(fxml, tempDir.toAbsolutePath().toUri().toURL(), null, null);

        final String value = "first";
        String id = null;
        Optional<FXOMObject> item = null;

        id = "btnClip";
        item = fxomDocument.getFxomRoot().collect(FxCollector.fxIdFindFirst(id));

        assertTrue(!item.isEmpty());
        assertTrue(item.get().getSceneGraphObject().isInstanceOf(Button.class));
        assertTrue(item.get().getSceneGraphObject().getAs(Button.class).getText().equals(value));

        id = "btnDefine";
        item = fxomDocument.getFxomRoot().collect(FxCollector.fxIdFindFirst(id));

        assertTrue(!item.isEmpty());
        assertTrue(item.get().getSceneGraphObject().isInstanceOf(Button.class));
        assertTrue(item.get().getSceneGraphObject().getAs(Button.class).getText().equals(value));

        id = "btn";
        item = fxomDocument.getFxomRoot().collect(FxCollector.fxIdFindFirst(id));

        assertTrue(!item.isEmpty());
        assertTrue(item.get().getSceneGraphObject().isInstanceOf(Button.class));
        assertTrue(item.get().getSceneGraphObject().getAs(Button.class).getText().equals(value));
    }

}
