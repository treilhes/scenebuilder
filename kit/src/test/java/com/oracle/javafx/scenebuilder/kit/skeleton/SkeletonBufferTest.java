/*
 * Copyright (c) 2016, 2017 Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.library.BuiltinLibrary;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link SkeletonBuffer#toString()}.
 */
@ExtendWith(MockitoExtension.class)
public class SkeletonBufferTest {

	private static I18N i18nTest = new I18N(new ArrayList<>()) {
		@Override
		public String get(String key) {
			return "fake";
		}
	};
	
	@Mock
    private BuiltinLibrary library;
	
    @Test
    public void testControllerWithoutPackageName() throws IOException {
        EditorController editorController = new EditorController(library, null, null, null, null);
        final URL fxmlURL = SkeletonBufferTest.class.getResource("ControllerWithoutPackage.fxml");
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);

        SkeletonBuffer skeletonBuffer = new SkeletonBuffer(editorController.getFxomDocument(), "test");
        String skeleton = skeletonBuffer.toString();

        String firstLine = skeleton.substring(0, skeleton.indexOf("\n"));
        assertEquals("", firstLine);
    }

    @Test
    public void testControllerWithSimplePackageName() throws IOException {
        EditorController editorController = new EditorController(library, null, null, null, null);
        final URL fxmlURL = SkeletonBufferTest.class.getResource("ControllerWithSimplePackage.fxml");
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);

        SkeletonBuffer skeletonBuffer = new SkeletonBuffer(editorController.getFxomDocument(), "test");
        String skeleton = skeletonBuffer.toString();

        String firstLine = skeleton.substring(0, skeleton.indexOf("\n"));
        assertEquals("package com;", firstLine);
    }

    @Test
    public void testControllerWithAdvancedPackageName() throws IOException {
        EditorController editorController = new EditorController(library, null, null, null, null);
        final URL fxmlURL = SkeletonBufferTest.class.getResource("ControllerWithAdvancedPackage.fxml");
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);

        SkeletonBuffer skeletonBuffer = new SkeletonBuffer(editorController.getFxomDocument(), "test");
        String skeleton = skeletonBuffer.toString();

        String firstLine = skeleton.substring(0, skeleton.indexOf("\n"));
        assertEquals("package com.example.app.view;", firstLine);
    }
}
