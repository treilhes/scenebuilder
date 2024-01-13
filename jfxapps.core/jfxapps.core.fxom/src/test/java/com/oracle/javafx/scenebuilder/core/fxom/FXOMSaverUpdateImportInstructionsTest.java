/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.fxom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.oracle.javafx.scenebuilder.core.fxom.collector.DeclaredClassCollector;
import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueComment;

import javafx.stage.Stage;

/**
 * Unit test for {@link FXOMSaver#updateImportInstructions(FXOMDocument)}.
 */
@ExtendWith(ApplicationExtension.class)
@SetSystemProperty(key = "javafx.allowjs", value = "true")
public class FXOMSaverUpdateImportInstructionsTest {

    @TempDir
    public File temporaryFolder;

    private static FXOMDocument fxomDocument;
    private static FXOMSaver serviceUnderTest;

    @Start
    private void start(Stage stage) {

    }

    @Test
    public void testEmptyFXML() throws IOException {
        setupTestCase(FxmlTestInfo.EMPTY);

        assertTrue("fxml is empty", fxomDocument.getFxmlText(false).isEmpty());
    }

    @Test
    public void testNoWildcard() {
        setupTestCase(FxmlTestInfo.NO_WILDCARD);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all()).forEach(dc -> {
            imports.add(dc.getName());
        });

        Set<String> givenImports = new TreeSet<>();
        givenImports.add("javafx.scene.control.Button");
        givenImports.add("javafx.scene.control.ComboBox");
        givenImports.add("javafx.scene.control.TextField");
        givenImports.add("javafx.scene.layout.AnchorPane");
        assertTrue(imports.containsAll(givenImports));
    }

    @Test
    public void testUnusedImports() {
        setupTestCase(FxmlTestInfo.UNUSED_IMPORTS);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all()).forEach(dc -> {
            imports.add(dc.getName());
        });

        Set<String> unusedImports = new TreeSet<>();
        unusedImports.add("java.util.Date");
        unusedImports.add("java.math.*");
        unusedImports.add("java.util.Set");
        unusedImports.add("org.junit.Test");
        assertFalse("unused imports are not present", imports.containsAll(unusedImports));
    }

    @Test
    public void testWithWildcard() {
        setupTestCase(FxmlTestInfo.WITH_WILDCARD);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all()).forEach(dc -> {
            imports.add(dc.getName());
        });

        assertFalse("fxml import does not contain javafx.scene.*", imports.contains("javafx.scene.*"));

    }

    @Test
    public void testWithMoreWildcards() {
        setupTestCase(FxmlTestInfo.WITH_MORE_WILDCARDS);

        Set<String> imports = new TreeSet<>();
        // java.lang.* is not a declared class, therefore not in the imports Set
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all()).forEach(dc -> {
            imports.add(dc.getName());
        });

        assertFalse("fxml import does not contain javafx.scene.*", imports.contains("javafx.scene.*"));
        assertFalse("fxml import does not contain javafx.scene.control.*", imports.contains("javafx.scene.control.*"));
    }

    @Test
    public void testWithGlueElements() {
        setupTestCase(FxmlTestInfo.WITH_GLUE_ELEMENTS);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all()).forEach(dc -> {
            imports.add(dc.getName());
        });

        assertTrue("fxml import contains java.lang.String", imports.contains("java.lang.String"));
        assertTrue("fxml import contains javafx.scene.paint.Color", imports.contains("javafx.scene.paint.Color"));
    }

    @Test
    public void testDuplicates() {
        setupTestCase(FxmlTestInfo.DUPLICATES);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all()).forEach(dc -> {
            imports.add(dc.getName());
        });

        // java.lang.* is not a declared class, therefore not in the imports Set
        assertTrue("fxml contain only 4 imports", (imports.size() == 4));
    }

    @Test
    public void testWildcardsAndDuplicates() {
        setupTestCase(FxmlTestInfo.WILDCARDS_AND_DUPLICATES);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all()).forEach(dc -> {
            imports.add(dc.getName());
        });

        // java.lang.* is not a declared class, therefore not in the imports Set
        imports.forEach(i -> {
            assertFalse("fxml does not contain .*", i.contains(".*"));
        });
        assertTrue("fxml contains only 4 imports", (imports.size() == 4));

    }

    @Test
    public void testCustomButton() {
        setupTestCase(FxmlTestInfo.CUSTOM_BUTTON);

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all()).forEach(dc -> {
            imports.add(dc.getName());
        });

        assertTrue("fxml has import com.oracle.javafx.scenebuilder.core.fxom.TestCustomButton",
                imports.contains("com.oracle.javafx.scenebuilder.core.fxom.TestCustomButton"));
        assertFalse("fxml does not contain com.oracle.javafx.scenebuilder.*",
                imports.contains("com.oracle.javafx.scenebuilder.*"));

        // java.lang.* is not a declared class, therefore not in the imports Set
        imports.forEach(i -> {
            assertFalse("fxml does not contain .*", i.contains(".*"));
        });

    }

    @Test
    public void testImportsWithComments() {

        System.setProperty("javafx.allowjs", "true");

        System.out.println("javafx.allowjs : " + System.getProperty("javafx.allowjs"));
        setupTestCase(FxmlTestInfo.HEADER_WITH_NOT_ONLY_IMPORTS);
        System.out.println("javafx.allowjs : " + System.getProperty("javafx.allowjs"));
        final int NUMBER_OF_FIRST_LEVEL_ELEMENTS = 6;

        Set<String> imports = new TreeSet<>();
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all()).forEach(dc -> {
            imports.add(dc.getName());
        });
        System.out.println("javafx.allowjs : " + System.getProperty("javafx.allowjs"));
        assertEquals("comment line should not be removed", NUMBER_OF_FIRST_LEVEL_ELEMENTS,
                fxomDocument.getGlue().getContent().size());

        assertTrue("second glue node should be a comment",
                fxomDocument.getGlue().getContent().get(1) instanceof GlueComment);
        assertTrue("fifth glue node should be a comment",
                fxomDocument.getGlue().getContent().get(4) instanceof GlueComment);

        assertTrue("fxml does not contain javafx.scene.control.ComboBox",
                imports.contains("javafx.scene.control.ComboBox"));
        assertTrue("fxml does not contain javafx.scene.layout.AnchorPane",
                imports.contains("javafx.scene.layout.AnchorPane"));

    }

    @Test
    public void testWildcardsAndStaticProperties() {
        setupTestCase(FxmlTestInfo.WILDCARDS_AND_STATIC_PROPERTIES);

        ArrayList<String> imports = new ArrayList<>();
        fxomDocument.getGlue().collectInstructions("import").forEach(i -> imports.add(i.getData()));

        assertEquals("imports length should be 5", 5, imports.size());
        assertTrue("HBox import was not found", imports.contains("javafx.scene.layout.HBox"));
        assertTrue("VBox import was not found", imports.contains("javafx.scene.layout.VBox"));

        assertFalse("Wildcard imports are present", imports.contains("java.scene.layout.*") || imports.contains("java.scene.control.*"));
    }

    @Test
    public void testPublicStaticImport() {
        setupTestCase(FxmlTestInfo.PUBLIC_STATIC_IMPORT);

        ArrayList<String> imports = new ArrayList<>();
        fxomDocument.getGlue().collectInstructions("import").forEach(i -> imports.add(i.getData()));

        assertEquals("imports length should be 4", 4, imports.size());
        assertTrue("Lighting import was not found.", imports.contains("javafx.scene.effect.Lighting"));
        assertTrue("Light.Distant import was not found.", imports.contains("javafx.scene.effect.Light.Distant"));

        assertFalse("Wildcard imports are present", imports.contains("java.scene.layout.*"));
    }

    private String callService() {
        return serviceUnderTest.save(fxomDocument);
    }

    private void setupTestCase(FxmlTestInfo n) {
        Path pathToFXML = Paths.get("src/test/resources/com/oracle/javafx/scenebuilder/core/fxom/" + n.getFilename() + ".fxml");
        try {
            Path pathToTestFXML = new File(temporaryFolder, "testerFXML.fxml").toPath();

            // Setup for the fxomDocument from the FXML file that will be tested
            setupFXOMDocument(pathToFXML);

            String savedFXML = callService();

            // Creates new FXML file with the new output
            Files.write(pathToTestFXML, savedFXML.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // setup for the FXOMDocument that will be tested
    private void setupFXOMDocument(Path fxmlTesterFile) {
        serviceUnderTest = new FXOMSaver();
        try {
            URL location = fxmlTesterFile.toFile().toURI().toURL();
            String fxmlString = getFxmlAsString(fxmlTesterFile);

            fxomDocument = new FXOMDocument(fxmlString, location, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // reads FXML file and gives it back as a String
    private static String getFxmlAsString(Path path) throws IOException {
        List<String> fxml = Files.readAllLines(path, StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder();
        fxml.forEach(line -> {
            sb.append(line).append('\n');
        });

        return sb.toString();
    }

    private enum FxmlTestInfo {
        CUSTOM_BUTTON("CustomButton"),
        DUPLICATES("Duplicates"),
        EMPTY("Empty"),
        HEADER_WITH_NOT_ONLY_IMPORTS("HeaderWithNotOnlyImports"),
        NO_WILDCARD("NoWildcard"),
        UNUSED_IMPORTS("UnusedImports"),
        WILDCARDS_AND_DUPLICATES("WildcardsAndDuplicates"),
        WILDCARDS_AND_STATIC_PROPERTIES("WildcardsAndStaticProperties"),
        WITH_MORE_WILDCARDS("WithMoreWildcards"),
        WITH_WILDCARD("WithWildcard"),
        WITH_GLUE_ELEMENTS("WithGlueElements"),
        PUBLIC_STATIC_IMPORT("PublicStaticImport");

        private String filename;
        FxmlTestInfo(String filename) {
            this.filename = filename;
        }

        String getFilename() {
            return filename;
        }
    }
}