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
package com.gluonhq.jfxapps.metadata.properties.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.java.model.tbd.Descriptor;
import com.gluonhq.jfxapps.metadata.properties.model.Component;

public interface PropertyGenerator {

    /**
     * Generate the properties for the given classes.
     *
     * @param classes             the classes for which the properties need to be
     *                            generated
     * @param availableComponents the components that are already available in the
     *                            classpath which can be used but not generated
     */
    void generateProperties(Set<Class<?>> classes, Set<Descriptor> descriptors);

    Component<?, ?, ?> buildComponent(BeanMetaData<?> bm, Map<Class<?>, BeanMetaData<?>> beanMap,
            Set<Class<?>> availableComponents) throws StreamWriteException, DatabindException, IOException;

    public static String propertyFileName(Class<?> cls) {
        return cls.getSimpleName() + ".properties";
    }

    public static Path propertyFolder(Class<?> cls) {
        return Path.of(cls.getName().replace('$', '/').replace('.', '/').toLowerCase());
    }

    public static String resourcePath(Class<?> cls) {
        return "/" + cls.getName().replace('$', '/').replace('.', '/').replace('\\', '/').toLowerCase() + "/" + propertyFileName(cls);
    }

    public static Path propertyPath(Class<?> cls) {
        return Path.of(propertyFolder(cls).toString(), propertyFileName(cls));
    }
}
