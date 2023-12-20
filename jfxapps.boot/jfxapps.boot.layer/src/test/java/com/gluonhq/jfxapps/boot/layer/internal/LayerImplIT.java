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
package com.gluonhq.jfxapps.boot.layer.internal;

import static com.gluonhq.jfxapps.boot.layer.TestUtils.copy;
import static com.gluonhq.jfxapps.boot.layer.TestUtils.delete;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;

import com.gluonhq.jfxapps.boot.layer.Constants;
import com.gluonhq.jfxapps.boot.layer.Layer;
import com.gluonhq.jfxapps.boot.layer.ModuleLayerManager;


/**
 * The Class AbstractLayerIT.
 */
class LayerImplIT {

    /** The Constant ROOT_ID. */
    private final static UUID ROOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    /** The root dir. */
    @TempDir
    Path rootDir;

    @Spy
    ModuleLayerManager manager = new ModuleLayerManagerImpl();
    /**
     * After each.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @AfterEach
    public void afterEach() throws IOException {
        manager.removeAllLayers();
    }

    /**
     * Layer must contains one module.
     *
     * @throws Exception the exception
     */
    @Test
    void layer_must_contains_one_module_from_temp_directory() throws Exception {
        copy(rootDir, Constants.IT_MODULE_JAR);
        Layer layer = manager.create(null, ROOT_ID, null, rootDir);
        assertEquals(layer.modules().size(), 1);
    }

    @Test
    void layer_must_contains_one_module_from_module_jar_file() throws Exception {
        Layer layer = manager.create(null, ROOT_ID, List.of(Constants.IT_MODULE_JAR), rootDir);
        assertEquals(layer.modules().size(), 1);
    }

    private static Stream<Arguments> module_resource_is_accessible_parameters() {
        return Stream.of(
                Arguments.of(Set.of(Constants.IT_MODULE_JAR), Constants.IT_MODULE_NAME, Constants.IT_MODULE_RESOURCE),
                Arguments.of(Set.of(Constants.IT_AUTOMATIC_MODULE_JAR), Constants.IT_AUTOMATIC_MODULE_NAME, Constants.IT_AUTOMATIC_MODULE_RESOURCE),
                Arguments.of(Set.of(Constants.IT_CLASSPATH_JAR), Constants.IT_CLASSPATH_MODULE_NAME, Constants.IT_CLASSPATH_RESOURCE),
                Arguments.of(Set.of(Constants.IT_MODULE_WITH_DEPENDENCY_JAR, Constants.IT_MODULE_JAR, Constants.IT_AUTOMATIC_MODULE_JAR, Constants.IT_CLASSPATH_JAR), Constants.IT_MODULE_WITH_DEPENDENCY_NAME, Constants.IT_MODULE_WITH_DEPENDENCY_RESOURCE)
        );
    }
    @ParameterizedTest
    @MethodSource("module_resource_is_accessible_parameters")
    void module_resource_is_accessible(Set<Path> jarPaths, String name, String resourceName) throws Exception {
        jarPaths.forEach(jarPath -> copy(rootDir, jarPath));

        Layer layer = manager.create(null, ROOT_ID, null, rootDir);

        assertNotNull(layer.getRessource(name, resourceName));
        assertNotNull(layer.getRessource(resourceName));

        try (InputStream is = layer.getResourceAsStream(name, resourceName)){
            assertNotNull(is);
        }

        try (InputStream is = layer.getResourceAsStream(resourceName)){
            assertNotNull(is);
        }

        assertTrue(layer.getRessources(resourceName).hasMoreElements());
        assertTrue(layer.getRessources(name, resourceName).hasMoreElements());
    }

    private static Stream<Arguments> module_class_is_accessible_parameters() {
        return Stream.of(
                Arguments.of(Set.of(Constants.IT_MODULE_JAR), Constants.IT_MODULE_NAME, Constants.IT_MODULE_CLASS),
                Arguments.of(Set.of(Constants.IT_AUTOMATIC_MODULE_JAR), Constants.IT_AUTOMATIC_MODULE_NAME, Constants.IT_AUTOMATIC_MODULE_CLASS),
                Arguments.of(Set.of(Constants.IT_CLASSPATH_JAR), Constants.IT_CLASSPATH_MODULE_NAME, Constants.IT_CLASSPATH_CLASS),
                Arguments.of(Set.of(Constants.IT_MODULE_WITH_DEPENDENCY_JAR, Constants.IT_MODULE_JAR, Constants.IT_AUTOMATIC_MODULE_JAR, Constants.IT_CLASSPATH_JAR), Constants.IT_MODULE_WITH_DEPENDENCY_NAME, Constants.IT_MODULE_WITH_DEPENDENCY_CLASS)
        );
    }
    @ParameterizedTest
    @MethodSource("module_class_is_accessible_parameters")
    void module_class_is_accessible(Set<Path> jarPaths, String name, String className) throws Exception {
        jarPaths.forEach(jarPath -> copy(rootDir, jarPath));

        Layer layer = manager.create(null, ROOT_ID, null, rootDir);

        assertNotNull(layer.getClass(name, className));
        assertNotNull(layer.getClass(name + "/" + className));
    }

    /**
     * Layer must contains each type of module.
     *
     * @throws Exception the exception
     */
    @Test
    void layer_must_contains_each_type_of_module_from_temp_directory() throws Exception {
        copy(rootDir, Constants.IT_MODULE_JAR);
        copy(rootDir, Constants.IT_AUTOMATIC_MODULE_JAR);
        copy(rootDir, Constants.IT_CLASSPATH_JAR);

        Layer layer = manager.create(null, ROOT_ID, null, rootDir);

        Set<WeakReference<Module>> modules = layer.modules();
        Set<WeakReference<Module>> auto = layer.automaticModules();
        Set<WeakReference<Module>> unnamed = layer.unnamedModules();

        assertTrue(modules.size() == 1, "One module is expected");
        assertTrue(auto.size() == 1, "One automatic module is expected");
        assertTrue(unnamed.size() == 1, "One classpath module is expected");

    }

    @Test
    void layer_must_contains_a_module_from_temp_directory() throws Exception {
        copy(rootDir, Constants.IT_MODULE_JAR);

        Layer layer = manager.create(null, ROOT_ID, null, rootDir);

        Set<WeakReference<Module>> modules = layer.modules();

        assertTrue(modules.size() == 1, "One module is expected");
    }

    @Test
    void layer_must_contains_an_automatic_module_from_temp_directory() throws Exception {

        copy(rootDir, Constants.IT_AUTOMATIC_MODULE_JAR);

        Layer layer = manager.create(null, ROOT_ID, null, rootDir);

        Set<WeakReference<Module>> auto = layer.automaticModules();

        assertTrue(auto.size() == 1, "One automatic module is expected");

    }

    @Test
    void layer_must_contains_a_derived_module_from_temp_directory() throws Exception {

        copy(rootDir, Constants.IT_CLASSPATH_JAR);

        Layer layer = manager.create(null, ROOT_ID, null, rootDir);

        Set<WeakReference<Module>> unnamed = layer.unnamedModules();

        assertTrue(unnamed.size() == 1, "One classpath module is expected");

    }

    @Test
    void layer_must_contains_a_module_from_jar_file() throws Exception {
        var jars = List.of(Constants.IT_MODULE_JAR);
        Layer layer = manager.create(null, ROOT_ID, jars, rootDir);

        Set<WeakReference<Module>> modules = layer.modules();

        assertTrue(modules.size() == 1, "One module is expected");
    }

    @Test
    void layer_must_contains_an_automatic_module_from_jar_file() throws Exception {

        var jars = List.of(Constants.IT_AUTOMATIC_MODULE_JAR);
        Layer layer = manager.create(null, ROOT_ID, jars, rootDir);

        Set<WeakReference<Module>> auto = layer.automaticModules();

        assertTrue(auto.size() == 1, "One automatic module is expected");

    }

    @Test
    void layer_must_contains_a_derived_module_from_jar_file() throws Exception {

        var jars = List.of(Constants.IT_CLASSPATH_JAR);
        Layer layer = manager.create(null, ROOT_ID, jars, rootDir);

        Set<WeakReference<Module>> unnamed = layer.unnamedModules();

        assertTrue(unnamed.size() == 1, "One classpath module is expected");

    }

    @Test
    void layer_must_contains_a_module_from_classes_files() throws Exception {
        var jars = List.of(Constants.IT_MODULE_CLASSES);
        Layer layer = manager.create(null, ROOT_ID, jars, rootDir);

        Set<WeakReference<Module>> modules = layer.modules();

        assertTrue(modules.size() == 1, "One module is expected");
    }

    /**
     * Test if the java issue has been solved.
     * JDK-9076357 : ModulePath.findAll does not handle exploded archive the same as jar file
     */
    @Test
    void layer_must_contains_an_automatic_module_from_classes_files() throws Exception {

        var jars = List.of(Constants.IT_AUTOMATIC_MODULE_CLASSES);
        Layer layer = manager.create(null, ROOT_ID, jars, rootDir);

        Set<WeakReference<Module>> auto = layer.automaticModules();

        // if failing, it means the issue is solved.
        // remove the next line and uncomment the real assertion
        assertTrue(auto.size() == 0, "Java issue is propably fixed, please take a look");
        //assertTrue(auto.size() == 1, "One automatic module is expected"); // i'm the real assertion

    }

    /**
     * Test if the java issue has been solved.
     * JDK-9076357 : ModulePath.findAll does not handle exploded archive the same as jar file
     */
    @Test
    void layer_must_contains_a_derived_module_from_classes_files() throws Exception {

        var jars = List.of(Constants.IT_CLASSPATH_CLASSES);
        Layer layer = manager.create(null, ROOT_ID, jars, rootDir);

        Set<WeakReference<Module>> unnamed = layer.unnamedModules();

        // if failing, it means the issue is solved.
        // remove the next line and uncomment the real assertion
        assertTrue(unnamed.size() == 0, "Java issue is propably fixed, please take a look");
        //assertTrue(unnamed.size() == 1, "One classpath module is expected");// i'm the real assertion

    }

    /**
     * Layer is locked if ref exists and unlocked if none.
     *
     * @throws Exception the exception
     */
    @Test
    void layer_is_locked_if_ref_exists_and_unlocked_if_none() throws Exception {
        copy(rootDir, Constants.IT_MODULE_JAR);

        Layer layer = manager.create(null, ROOT_ID, null, rootDir);

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

}
