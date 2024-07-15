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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@SetSystemProperty(key = "javafx.allowjs", value = "true")
class FxIncludeCollectorTest {

    private static final String INCLUDE = """
            <?import javafx.scene.layout.Pane?>
            <Pane xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1" />
            """;

    private static final String FXML = """
            <?import javafx.scene.control.Button?>
            <?import javafx.scene.layout.Pane?>
            <?import javafx.scene.shape.Circle?>

            <Pane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1">
               <fx:define>
                    <fx:include fx:id="referred1" source="included.fxml" />
                    <fx:include fx:id="referred2" source="included.fxml" />
               </fx:define>
               <children>
                  <Pane layoutX="200.0" layoutY="100.0" prefHeight="200.0" prefWidth="200.0" clip="$circleblue">
                     <children>
                        <fx:include fx:id="referred3" source="included.fxml" />
                        <fx:reference fx:id="excluded" source="referred1" layoutX="100.0" layoutY="100.0">
                            <children>
                              <fx:include fx:id="referred4" source="included2.fxml" />
                           </children>
                        </fx:reference>
                     </children>
                  </Pane>
               </children>
            </Pane>
            """;

    @TempDir
    static Path tempDir;

    @Start
    private void start(Stage stage) {

    }

    @BeforeAll
    public static void init() throws Exception {
        Files.writeString(tempDir.resolve("included.fxml"), INCLUDE, StandardOpenOption.CREATE);
        Files.writeString(tempDir.resolve("included2.fxml"), INCLUDE, StandardOpenOption.CREATE);
    }

    @Test
    public void should_return_the_right_number_of_fxincludes() throws Exception {
        FXOMDocument fxomDocument = new FXOMDocument(FXML, tempDir.toAbsolutePath().toUri().toURL(), null, null);

        List<FXOMIntrinsic> items = fxomDocument.getFxomRoot().collect(FxCollector.allFxIncludes());

        assertEquals(4, items.size());
    }

    @Test
    public void should_return_the_right_number_of_fxincludes_by_source() throws Exception {
        FXOMDocument fxomDocument = new FXOMDocument(FXML, tempDir.toAbsolutePath().toUri().toURL(), null, null);

        String source = "included2.fxml";
        List<FXOMIntrinsic> items = fxomDocument.getFxomRoot().collect(FxCollector.fxIncludeBySource(source));

        assertEquals(1, items.size());
    }

}
