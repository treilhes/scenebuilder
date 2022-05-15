/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.editor.fxml.controller;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oracle.javafx.scenebuilder.api.ErrorReport;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.test.JfxInitializer;
import com.oracle.javafx.scenebuilder.ui.controller.EditorController;

/**
 * Unit test for {@link com.oracle.javafx.scenebuilder.core.fxom.util.Deprecation#setStaticLoad(javafx.fxml.FXMLLoader, boolean) }
 */
@ExtendWith(MockitoExtension.class)
public class StaticLoadTest {

	private static I18N i18nTest = new I18N(new ArrayList<>()) {
		@Override
		public String get(String key) {
			return "fake";
		}
	};

    private boolean thrown;

    @Spy
    private DocumentManager docManager = new DocumentManager.DocumentManagerImpl();
    @Spy
    private SceneBuilderManager sceneBuilderManager = new SceneBuilderManager.SceneBuilderManagerImpl();
    @Mock
    JobManager jobManager;
    @Mock
    private FileSystem fileSystem;
    @Mock
    MessageLogger messageLogger;
    @Mock
    Selection selection;
    @Mock
    ErrorReport errorReport;
    @Mock
    InlineEdit inlineEditController;

    @InjectMocks
    private EditorController editorController;

    @BeforeAll
    public static void initJFX() {
        JfxInitializer.initialize();
    }

    @Test
    public void testStaticLoadWithoutEventHandler() throws IOException {
        thrown = false;

        final URL fxmlURL = StaticLoadTest.class.getResource("testStaticLoadWithoutEventHandler.fxml");
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);
        } catch (IOException e) {
           thrown = true;
        }

        assertFalse(thrown);
    }

    @Test
    public void testStaticLoad() throws IOException {
        thrown = false;

        final URL fxmlURL = StaticLoadTest.class.getResource("testStaticLoad.fxml");
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);
        } catch (IOException e) {
           thrown = true;
        }

        assertFalse(thrown);
    }
}
