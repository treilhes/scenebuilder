/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.context.boot;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.lang.NonNull;

import com.gluonhq.jfxapps.boot.api.loader.BootContextConfigClasses;
import com.gluonhq.jfxapps.boot.context.impl.JfxAppContextImpl;

/**
 * This initializer is used to register classes provided with {@link BootContextConfigClasses} spi in the application context.
 * JfxAppContextImpl is used to register the classes instead of the Spring context.
 * This mainly use the register method of the JfxAppContextImpl class in order to keep track of the classes registered.
 */
public class BootContextInitializer implements ApplicationContextInitializer<JfxAppContextImpl> {

    private List<Class<?>> includedClasses;
    private List<Class<?>> filteredClasses;

    public BootContextInitializer() {
        this(List.of(), List.of());
    }

    public BootContextInitializer(List<Class<?>> includedClasses, List<Class<?>> filteredClasses) {
        this.includedClasses = new ArrayList<>(includedClasses);
        this.filteredClasses = new ArrayList<>(filteredClasses);
    }

    @Override
    public void initialize(@NonNull JfxAppContextImpl applicationContext) {

        var bootClasses = ServiceLoader.load(BootContextConfigClasses.class).stream()
        		.map(p -> p.get())
                .map(bc -> bc.classes())
                .flatMap(l -> l.stream())
                .distinct()
                .filter(classInheritingFilterClass())
                .filter(classAnnotatedByFilterClass())
                .collect(Collectors.toCollection(ArrayList::new));

        bootClasses.addAll(includedClasses);

        applicationContext.register(bootClasses.toArray(Class<?>[]::new));
    }

	@SuppressWarnings("unchecked")
	private Predicate<? super Class<?>> classAnnotatedByFilterClass() {
		return c ->  filteredClasses.stream().allMatch(f -> !(Annotation.class.isAssignableFrom(f) && c.isAnnotationPresent((Class<Annotation>)f)));
	}

	private Predicate<? super Class<?>> classInheritingFilterClass() {
		return c ->  filteredClasses.stream().allMatch(f -> !f.isAssignableFrom(c));
	}

}
