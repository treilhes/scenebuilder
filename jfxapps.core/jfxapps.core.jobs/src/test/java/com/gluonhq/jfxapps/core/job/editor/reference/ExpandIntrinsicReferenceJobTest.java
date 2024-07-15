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
package com.gluonhq.jfxapps.core.job.editor.reference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMCloner;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector;
import com.gluonhq.jfxapps.core.job.editor.atomic.ReplaceObjectJob;

import javafx.stage.Stage;

@ExtendWith({ ApplicationExtension.class, MockitoExtension.class })
class ExpandIntrinsicReferenceJobTest {

    DocumentManager documentManager = new DocumentManager.DocumentManagerImpl();

    @TempDir
    File tmpDir;

    @Mock
    FxomJobsFactory fxomJobsFactory;

    @Start
    private void start(Stage stage) {

    }

    @Test
    void fx_include() throws Exception {

        String FXML_INCLUDED = """
                <?xml version="1.0" encoding="UTF-8"?>

                <?import javafx.scene.control.Button?>
                <?import javafx.scene.layout.Pane?>

                <Pane prefHeight="400.0" prefWidth="894.0" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1">
                    <children>
                      <Button mnemonicParsing="false" text="Included Button"/>
                   </children>
                </Pane>
                """;

        String FXML_FX_INCLUDE = """
                <?xml version="1.0" encoding="UTF-8"?>

                <?import javafx.scene.layout.Pane?>

                <Pane prefHeight="400.0" prefWidth="894.0" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1">
                    <children>
                      <fx:include source="included.fxml"/>
                   </children>
                </Pane>
                """;

//        when(fxomJobsFactory.replaceObject(any(), any())).thenAnswer(i -> {
//            var job = new ReplaceObjectJob(null);
//            job.setJobParameters(i.getArgument(0), i.getArgument(1));
//            return job;
//        });

        Files.writeString(new File(tmpDir,  "included.fxml").toPath(), FXML_INCLUDED, StandardCharsets.UTF_8, StandardOpenOption.CREATE);

        FXOMDocument doc = new FXOMDocument(FXML_FX_INCLUDE, new File(tmpDir,  "test.fxml").toURI().toURL(), null, null);
        documentManager.fxomDocument().set(doc);

//        final var fxRef = doc.collect(FxCollector.allFxReferences());
//        assertEquals(fxRef.size(), 1);
//
//        ExpandIntrinsicReferenceJob job = new ExpandIntrinsicReferenceJob(null, documentManager, fxomJobsFactory);
//        job.setJobParameters(fxRef.get(0), new FXOMCloner(doc));
//        job.execute();

        System.out.println(doc.getFxmlText(false));
        //fail("Not yet implemented");
    }

    @Test
    void name_and_test_to_define() throws Exception {

        String FXML_FX_REFERENCE = """
                <?xml version="1.0" encoding="UTF-8"?>

                <?import javafx.scene.control.Button?>
                <?import javafx.scene.layout.Pane?>
                <?import javafx.scene.shape.Circle?>

                <Pane prefHeight="400.0" prefWidth="894.0" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1">
                    <fx:define>
                        <Circle fx:id="myRef" fill="DODGERBLUE" radius="100.0" stroke="BLACK" strokeType="INSIDE" />
                    </fx:define>
                    <children>
                      <Button mnemonicParsing="false" text="Button">
                          <clip>
                              <fx:reference source="myRef" />
                          </clip>
                      </Button>
                   </children>
                </Pane>
                """;

        when(fxomJobsFactory.replaceObject(any(), any())).thenAnswer(i -> {
            var job = new ReplaceObjectJob(null);
            job.setJobParameters(i.getArgument(0), i.getArgument(1));
            return job;
        });

        FXOMDocument doc = new FXOMDocument(FXML_FX_REFERENCE);
        documentManager.fxomDocument().set(doc);

        final var fxRef = doc.collect(FxCollector.allFxReferences());
        assertEquals(fxRef.size(), 1);

        ExpandIntrinsicReferenceJob job = new ExpandIntrinsicReferenceJob(null, documentManager, fxomJobsFactory);
        job.setJobParameters(fxRef.get(0), new FXOMCloner(doc));
        job.execute();

        System.out.println(doc.getFxmlText(false));
        //fail("Not yet implemented");
    }

}
