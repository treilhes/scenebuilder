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
package com.gluonhq.jfxapps.core.ui.tool;

import java.util.HashMap;
import java.util.Map;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.ui.tool.DriverExtensionRegistry;
import com.gluonhq.jfxapps.util.InheritanceMap;

@ApplicationSingleton
public class DriverExtensionRegistryImpl implements DriverExtensionRegistry {

    private final JfxAppContext context;
    private final Map<Class<?>, InheritanceMap<?>> extensions = new HashMap<>();

    public DriverExtensionRegistryImpl(JfxAppContext context) {
        super();
        this.context = context;
    }

    @Override
    public <U> void registerExtension(Class<U> extensionInterface) {
        if (!extensions.containsKey(extensionInterface)) {
            extensions.put(extensionInterface, new InheritanceMap<U>());
        }
    }

    @Override
    public <T, U extends T> void registerImplementationClass(Class<T> extensionInterface, Class<?> itemClass,
            Class<U> implementation) {
        assert extensions.containsKey(extensionInterface);

        @SuppressWarnings("unchecked")
        InheritanceMap<T> ef = (InheritanceMap<T>)extensions.get(extensionInterface);

        ef.put(itemClass, implementation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, U extends T> Class<U> getImplementationClass(Class<T> extensionInterface, Class<?> itemClass) {
        assert extensions.containsKey(extensionInterface);

        InheritanceMap<T> ef = (InheritanceMap<T>)extensions.get(extensionInterface);

        return (Class<U>)ef.getFirstInherited(itemClass);
    }

    @Override
    public <T, U extends T> U getImplementationInstance(Class<T> extensionInterface, Class<?> itemClass) {
        Class<U> uClass = getImplementationClass(extensionInterface, itemClass);

        if (uClass == null) {
            return null;
        }

        return context.getBean(uClass);
    }
}