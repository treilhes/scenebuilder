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
package com.gluonhq.jfxapps.boot.loader;

import static com.gluonhq.jfxapps.boot.loader.TestUtils.copy;
import static com.gluonhq.jfxapps.boot.loader.TestUtils.delete;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gluonh.jfxapps.boot.layer.Layer;
import com.gluonh.jfxapps.boot.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.loader.extension.Extension;


// TODO: Auto-generated Javadoc
/**
 * The Class AbstractLayerIT.
 */
class AbstractLayerIT {

    /** The Constant ROOT_ID. */
    private final static UUID ROOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    /** The root dir. */
    @TempDir
    Path rootDir;

    /**
     * After each.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @AfterEach
    public void afterEach() throws IOException {
        ModuleLayerManager.get().removeAllLayers();
    }

    /**
     * Layer must contains one module.
     *
     * @throws Exception the exception
     */
    @Test
    void layer_must_contains_one_module() throws Exception {
        copy(rootDir, Constants.IT_MODULE_JAR);
        Layer layer = ModuleLayerManager.get().create(null, ROOT_ID, rootDir);
        assertEquals(layer.modules().size(), 1);
    }

    /**
     * Layer must contains each type of module.
     *
     * @throws Exception the exception
     */
    @Test
    void layer_must_contains_each_type_of_module() throws Exception {
        copy(rootDir, Constants.IT_MODULE_WITH_DEPENDENCY_JAR);
        copy(rootDir, Constants.IT_AUTOMATIC_MODULE_JAR);
        copy(rootDir, Constants.IT_CLASSPATH_JAR);

        ModuleLayerManager manager = ModuleLayerManager.get();
        Layer layer = ModuleLayerManager.get().create(null, ROOT_ID, rootDir);

        Set<WeakReference<Module>> modules = layer.modules();
        Set<WeakReference<Module>> auto = layer.automaticModules();
        Set<WeakReference<Module>> unnamed = layer.unnamedModules();

        assertTrue(modules.size() == 1, "One module is expected");
        assertTrue(auto.size() == 1, "One automatic module is expected");
        assertTrue(unnamed.size() == 1, "One classpath module is expected");

    }

    /**
     * Layer is locked if ref exists and unlocked if none.
     *
     * @throws Exception the exception
     */
    @Test
    void layer_is_locked_if_ref_exists_and_unlocked_if_none() throws Exception {
        copy(rootDir, Constants.IT_MODULE_JAR);

        ModuleLayerManager manager = ModuleLayerManager.get();
        Layer layer = ModuleLayerManager.get().create(null, ROOT_ID, rootDir);

        Class<?> cls = layer.getClass(Constants.IT_MODULE_NAME + "/" + Constants.IT_MODULE_CLASS);
        Constructor<?> constructor = cls.getDeclaredConstructor();
        Object o = constructor.newInstance();

        assertNotNull(o);

        boolean unlocked = false;

        // refs exist so must be locked
        Future<Boolean> processing = layer.unlockLayer();
        try {
            unlocked = processing.get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            processing.cancel(true);
        }

        assertTrue(!unlocked);

        // nullifying all refs
        o = null;
        cls = null;
        constructor = null;

        // refs not exist so must be locked
        processing = layer.unlockLayer();
        try {
            unlocked = processing.get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            processing.cancel(true);
        }

        assertTrue(unlocked, "Layer must be unlocked/deletable");

        delete(rootDir, Constants.IT_MODULE_JAR);

        assertTrue(Files.notExists(rootDir.resolve(Constants.IT_MODULE_JAR.getFileName())));
    }

    /**
     * Layer must contains one extension.
     *
     * @throws Exception the exception
     */
    //@Test
    void layer_must_contains_one_extension() throws Exception {
        copy(rootDir, Constants.IT_APP_ROOT_JAR);
        Layer layer = ModuleLayerManager.get().create(null, ROOT_ID, rootDir);
        Set<Extension> exts = layer.loadService(Extension.class);

        for (Extension e:exts) {
            ModuleLayer l = e.getClass().getModule().getLayer();
            System.out.println();
        }
        assertEquals(exts.size(), 1);
    }
}
