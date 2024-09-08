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

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

/**
 * Unit test for {@link SkeletonBuffer#toString()}.
 */
public class SkeletonBufferTest {

    private I18N i18n = new I18N(List.of(), true);

    @Test
    public void testControllerWithoutPackageName() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("ControllerWithoutPackage.fxml");

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        String firstLine = skeleton.substring(0, skeleton.indexOf("\n"));
        assertEquals("", firstLine.trim());
    }

    @Test
    public void testControllerWithSimplePackageName() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("ControllerWithSimplePackage.fxml");

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        String firstLine = skeleton.substring(0, skeleton.indexOf("\n"));
        assertEquals("package com;", firstLine.trim());
    }

    @Test
    public void testControllerWithAdvancedPackageName() throws IOException {
        // given
        SkeletonBuffer skeletonBuffer = load("ControllerWithAdvancedPackage.fxml");

        // when
        String skeleton = skeletonBuffer.toString();

        // then
        String firstLine = skeleton.substring(0, skeleton.indexOf("\n"));
        assertEquals("package com.example.app.view;", firstLine.trim());
    }

    private SkeletonBuffer load(String fxmlFile) throws IOException {
        final URL fxmlURL = SkeletonBufferTest.class.getResource(fxmlFile);
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        FXOMDocument fxomDocument = new FXOMDocument(fxmlText, fxmlURL, SkeletonBufferTest.class.getClassLoader(), null);
        return new SkeletonBuffer(fxomDocument, "test");
    }
}
