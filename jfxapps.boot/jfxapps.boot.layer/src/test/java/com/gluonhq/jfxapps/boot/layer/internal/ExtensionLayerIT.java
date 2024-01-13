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
package com.gluonhq.jfxapps.boot.layer.internal;

import static com.gluonhq.jfxapps.boot.layer.TestUtils.copy;
import static com.gluonhq.jfxapps.boot.layer.TestUtils.instanciate;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Spy;

import com.gluonhq.jfxapps.boot.layer.Constants;
import com.gluonhq.jfxapps.boot.layer.Layer;
import com.gluonhq.jfxapps.boot.layer.ModuleLayerManager;

class ExtensionLayerIT {

    private final static UUID ROOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final static UUID EXT_ID = UUID.randomUUID();

    @TempDir
    Path rootDir;

    @Spy
    ModuleLayerManager manager = new ModuleLayerManagerImpl();

    @AfterEach
    public void afterEach() throws IOException {
        manager.removeAllLayers();
    }

    @Test
    void extension_must_access_class_in_root_layer(TestInfo testInfo) throws Exception {

        Path rootLayerDir = rootDir.resolve(testInfo.getDisplayName() + "_root");
        Path extDir = rootDir.resolve(testInfo.getDisplayName() + "_ext");
        Files.createDirectory(rootLayerDir);
        Files.createDirectory(extDir);

        copy(rootLayerDir, Constants.IT_MODULE_JAR);

        Layer root = manager.create(null, ROOT_ID, null, rootLayerDir);
        Layer ext = manager.create(root, EXT_ID, null, extDir);

        Object object = instanciate(ext, Constants.IT_MODULE_NAME + "/" + Constants.IT_MODULE_CLASS);

        assertNotNull(object);

        object = null;

        //manager.removeAllLayers();
    }

    @Test
    void extension_must_access_class_in_extension_layer(TestInfo testInfo) throws Exception {

        Path rootLayerDir = rootDir.resolve(testInfo.getDisplayName() + "_root");
        Path extDir = rootDir.resolve(testInfo.getDisplayName() + "_ext");
        Files.createDirectory(rootLayerDir);
        Files.createDirectory(extDir);

        copy(extDir, Constants.IT_MODULE_JAR);

        Layer root = manager.create(null, ROOT_ID, null, rootLayerDir);
        Layer ext = manager.create(root, EXT_ID, null, extDir);

        Object object = instanciate(ext, Constants.IT_MODULE_NAME + "/" + Constants.IT_MODULE_CLASS);

        assertNotNull(object);

        object = null;

        manager.removeAllLayers();
    }

    @Test
    void root_layer_must_not_access_class_in_extension_layer(TestInfo testInfo) throws Exception {

        Path rootLayerDir = rootDir.resolve(testInfo.getDisplayName() + "_root");
        Path extDir = rootDir.resolve(testInfo.getDisplayName() + "_ext");
        Files.createDirectory(rootLayerDir);
        Files.createDirectory(extDir);

        copy(extDir, Constants.IT_MODULE_JAR);

        Layer root = manager.create(null, ROOT_ID, null, rootLayerDir);
        Layer ext = manager.create(root, EXT_ID, null, extDir);

        assertThrows(Exception.class, () -> {
            instanciate(root, Constants.IT_MODULE_NAME + "/" + Constants.IT_MODULE_CLASS);
        });

        manager.removeAllLayers();
    }
}
