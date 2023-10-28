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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

import com.gluonh.jfxapps.boot.layer.Layer;

// TODO: Auto-generated Javadoc
/**
 * The Class TestUtils.
 */
public final class TestUtils {

    /**
     * Instantiates a new test utils.
     */
    private TestUtils() {
        super();
    }

    /**
     * Copy.
     *
     * @param target the target
     * @param jarPath the jar path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void copy(Path target, Path jarPath) throws IOException {
        Files.copy(jarPath, target.resolve(jarPath.getFileName()));
    }

    /**
     * Delete.
     *
     * @param target the target
     * @param jarPath the jar path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void delete(Path target, Path jarPath) throws IOException {
        Files.deleteIfExists(target.resolve(jarPath.getFileName()));
    }

    /**
     * Instanciate.
     *
     * @param layer the layer
     * @param moduleSlashClass the module slash class
     * @return the object
     * @throws Exception the exception
     */
    public static Object instanciate(Layer layer, String moduleSlashClass) throws Exception {
        Class<?> cls = layer.getClass(moduleSlashClass);
        Constructor<?> constructor = cls.getDeclaredConstructor();
        return constructor.newInstance();
    }
}
