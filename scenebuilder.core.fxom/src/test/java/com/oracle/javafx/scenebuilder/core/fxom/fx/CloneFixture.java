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
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMSaver;
import com.oracle.javafx.scenebuilder.core.fxom.fx.script.FxomFxScriptTagTest;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

public class CloneFixture {

    public static final String JFX_VERSION = "xxx";

    public static void testIsCloneable(Object owner, String fileName, boolean failureExpected) {
        try (var stream = owner.getClass().getResourceAsStream(fileName)) {

            String content = new String(stream.readAllBytes());

            FXOMDocument fileDocument = new FXOMDocument(content, owner.getClass().getResource(fileName),
                    FxomFxScriptTagTest.class.getClassLoader(), null);

            final FXOMDocument newDocument = FXOMNodes.newDocument(fileDocument.getFxomRoot());


            String serializedContent = new FXOMSaver().save(newDocument, JFX_VERSION);
            assertNotNull(serializedContent);
            //assertEquals(content.trim(), serializedContent.trim());

            assertNotNull(newDocument);
            assertNotEquals(fileDocument, newDocument);
            assertFxomObjectEquals(fileDocument.getFxomRoot(), newDocument.getFxomRoot());

        } catch (IOException e) {
            if (!failureExpected) {
                fail(e);
            }
        }
    }

    private static void assertFxomObjectEquals(FXOMObject original, FXOMObject newVersion) {
        assertTrue(original != newVersion);
        assertEquals(original.getClass(), newVersion.getClass());


        if (FXOMInstance.class.isInstance(original)) {
            FXOMInstance instanceOriginel = (FXOMInstance)original;
            FXOMInstance instanceNewVersion = (FXOMInstance)newVersion;

            assertEquals(instanceOriginel.getProperties().size(), instanceNewVersion.getProperties().size());

            for (PropertyName p : instanceOriginel.getProperties().keySet()) {
                FXOMProperty propOriginal = instanceOriginel.getProperties().get(p);
                FXOMProperty propNewVersion = instanceNewVersion.getProperties().get(p);

                assertFxomPropertyEquals(propOriginal, propNewVersion);
            }
        } else if (FXOMIntrinsic.class.isInstance(original) ) {
            FXOMIntrinsic instanceOriginel = (FXOMIntrinsic)original;
            FXOMIntrinsic instanceNewVersion = (FXOMIntrinsic)newVersion;

            assertEquals(instanceOriginel.getProperties().size(), instanceNewVersion.getProperties().size());

            for (PropertyName p : instanceOriginel.getProperties().keySet()) {
                FXOMProperty propOriginal = instanceOriginel.getProperties().get(p);
                FXOMProperty propNewVersion = instanceNewVersion.getProperties().get(p);

                assertFxomPropertyEquals(propOriginal, propNewVersion);
            }
        } else {
            assertEquals(original.getChildObjects().size(), newVersion.getChildObjects().size());
        }

        for (int i=0; i < original.getChildObjects().size(); i++) {
            FXOMObject childOriginal = original.getChildObjects().get(i);
            FXOMObject childNewVersion = newVersion.getChildObjects().get(i);

            assertFxomObjectEquals(childOriginal, childNewVersion);
        }
    }

    private static void assertFxomPropertyEquals(FXOMProperty original, FXOMProperty newVersion) {
        assertTrue(original != newVersion);
        assertEquals(original.getClass(), newVersion.getClass());
        assertEquals(original.getChildren().size(), newVersion.getChildren().size());

        if (FXOMPropertyT.class.isInstance(original)) {
            FXOMPropertyT instanceOriginel = (FXOMPropertyT)original;
            FXOMPropertyT instanceNewVersion = (FXOMPropertyT)newVersion;

            assertEquals(instanceOriginel.getValue(), instanceNewVersion.getValue());
        }

        for (int i=0; i < original.getChildren().size(); i++) {
            FXOMObject childOriginal = original.getChildren().get(i);
            FXOMObject childNewVersion = newVersion.getChildren().get(i);

            assertFxomObjectEquals(childOriginal, childNewVersion);
        }
    }
}
