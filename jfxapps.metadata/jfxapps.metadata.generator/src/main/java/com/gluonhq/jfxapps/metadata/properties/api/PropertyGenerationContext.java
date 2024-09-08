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

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PropertyGenerationContext {
    private final Map<Constructor<?>, Class<?>[]> altConstructors = new HashMap<>();
    private File outputResourceFolder;

    private Optional<Class<?>> componentCustomizationClass = Optional.empty();
    private Optional<Class<?>> componentPropertyCustomizationClass = Optional.empty();
    private Optional<Class<?>> valuePropertyCustomizationClass = Optional.empty();

    private boolean failOnError = true;

    private final ClassLoader loader;

    public PropertyGenerationContext(ClassLoader loader) {
        super();
        this.loader = loader;
    }

    public Map<Constructor<?>, Class<?>[]> getAltConstructors() {
        return altConstructors;
    }

    public void addAltConstructor(Constructor<?> constructor, Class<?>[] parameters) {
        altConstructors.put(constructor, parameters);
    }

    public File getOutputResourceFolder() {
        return outputResourceFolder;
    }

    public void setOutputResourceFolder(File outputResourceFolder) {
        this.outputResourceFolder = outputResourceFolder;
    }

    public Optional<Class<?>> getComponentCustomizationClass() {
        return componentCustomizationClass;
    }

    public void setComponentCustomizationClass(String componentCustomizationClass) throws ClassNotFoundException {
        this.componentCustomizationClass = componentCustomizationClass == null ? Optional.empty()
                : Optional.of(loader.loadClass(componentCustomizationClass));
    }

    public Optional<Class<?>> getComponentPropertyCustomizationClass() {
        return componentPropertyCustomizationClass;
    }

    public void setComponentPropertyCustomizationClass(String componentPropertyCustomizationClass)
            throws ClassNotFoundException {
        this.componentPropertyCustomizationClass = componentPropertyCustomizationClass == null ? Optional.empty()
                : Optional.of(loader.loadClass(componentPropertyCustomizationClass));
    }

    public Optional<Class<?>> getValuePropertyCustomizationClass() {
        return valuePropertyCustomizationClass;
    }

    public void setValuePropertyCustomizationClass(String valuePropertyCustomizationClass)
            throws ClassNotFoundException {
        this.valuePropertyCustomizationClass = valuePropertyCustomizationClass == null ? Optional.empty()
                : Optional.of(loader.loadClass(valuePropertyCustomizationClass));
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }
}
