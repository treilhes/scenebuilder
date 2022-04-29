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
package com.oracle.javafx.scenebuilder.core.fxom.fx;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMSaver;
import com.oracle.javafx.scenebuilder.core.fxom.fx.script.FxomFxScriptTagTest;

import javafx.fxml.FXMLLoader;

public class IOFixture {

    public static final String JFX_VERSION = "xxx";

    public static void testIsLoadableByJavafx(Object owner, String fileName, boolean failureExpected) {
        try (var stream = owner.getClass().getResourceAsStream(fileName)) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(owner.getClass().getResource(fileName));
            loader.load(stream);
        } catch (IOException e) {
            if (!failureExpected) {
                fail(e);
            }
        }
    }

    public static void testIsFxomLoadable(Object owner, String fileName, boolean failureExpected) {
        try (var stream = owner.getClass().getResourceAsStream(fileName)) {
            new FXOMDocument(new String(stream.readAllBytes()), owner.getClass().getResource(fileName),
                    IOFixture.class.getClassLoader(), null);
        } catch (IOException e) {
            if (!failureExpected) {
                fail(e);
            }
        }
    }

    public static void testIsFxomSerializable(Object owner, String fileName, boolean failureExpected) {
        try (var stream = owner.getClass().getResourceAsStream(fileName)) {
            FXOMDocument fxomDocument = new FXOMDocument(new String(stream.readAllBytes()),
                    owner.getClass().getResource(fileName), FxomFxScriptTagTest.class.getClassLoader(), null);
            new FXOMSaver().save(fxomDocument);
        } catch (IOException e) {
            if (!failureExpected) {
                fail(e);
            }
        }
    }

    public static void testSerializedIsEqualToSource(Object owner, String fileName, boolean failureExpected) {
        try (var stream = owner.getClass().getResourceAsStream(fileName)) {
            String content = new String(stream.readAllBytes());
            FXOMDocument fxomDocument = new FXOMDocument(content, owner.getClass().getResource(fileName),
                    IOFixture.class.getClassLoader(), null);
            String serializedContent = new FXOMSaver().save(fxomDocument, JFX_VERSION);
            assertNotNull(serializedContent);
            assertEquals(content.trim(), serializedContent.trim());
        } catch (IOException e) {
            if (!failureExpected) {
                fail(e);
            }
        }
    }
}
