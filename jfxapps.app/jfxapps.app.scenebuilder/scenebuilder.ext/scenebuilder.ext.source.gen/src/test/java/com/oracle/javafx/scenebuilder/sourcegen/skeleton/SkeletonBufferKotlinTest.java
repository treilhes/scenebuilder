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
package com.oracle.javafx.scenebuilder.sourcegen.skeleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

public class SkeletonBufferKotlinTest {

    private I18N i18n = new I18N(List.of(), true);

    @BeforeClass
    public static void initialize() {
        JfxInitializer.initialize();
    }

    @Test
    public void skeletonToString_nestedTestFxml() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("TestNested.fxml");

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        assertEqualsFileContent("skeleton_kotlin_nested.txt", skeleton);
    }

    @Test
    public void skeletonToString_testFxml_full_withComments() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("Test.fxml");
        skeletonBuffer.setFormat(SkeletonSettings.FORMAT_TYPE.FULL);
        skeletonBuffer.setTextType(SkeletonSettings.TEXT_TYPE.WITH_COMMENTS);

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        assertEqualsFileContent("skeleton_kotlin_full_comments.txt", skeleton);
    }

    @Test
    public void skeletonToString_testFxml_withComments() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("Test.fxml");
        skeletonBuffer.setTextType(SkeletonSettings.TEXT_TYPE.WITH_COMMENTS);

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        assertEqualsFileContent("skeleton_kotlin_comments.txt", skeleton);
    }

    @Test
    public void skeletonToString_testFxml_fullFormat() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("Test.fxml");
        skeletonBuffer.setFormat(SkeletonSettings.FORMAT_TYPE.FULL);

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        assertEqualsFileContent("skeleton_kotlin_full.txt", skeleton);
    }

    private void assertEqualsFileContent(String fileName, String actual) {
        URL url = this.getClass().getResource(fileName);
        File file = new File(url.getFile());

        try {
            String expectedFileContent = Files.readString(file.toPath());
            assertEquals(expectedFileContent.replace("\r", ""), actual.replace("\r", ""));
        } catch (IOException e) {
            fail("Unable to open file: " + fileName);
        }
    }

    private SkeletonBuffer load(String fxmlFile) throws IOException {
        final URL fxmlURL = SkeletonBufferKotlinTest.class.getResource(fxmlFile);
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        FXOMDocument fxomDocument = new FXOMDocument(fxmlText, fxmlURL, SkeletonBufferKotlinTest.class.getClassLoader(), null);
        SkeletonBuffer skeletonBuffer = new SkeletonBuffer(fxomDocument, "test");
        skeletonBuffer.setLanguage(SkeletonSettings.LANGUAGE.KOTLIN);
        return skeletonBuffer;
    }
}
