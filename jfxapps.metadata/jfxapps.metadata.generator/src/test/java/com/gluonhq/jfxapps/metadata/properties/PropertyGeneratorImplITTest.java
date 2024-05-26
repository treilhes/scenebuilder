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
package com.gluonhq.jfxapps.metadata.properties;

import java.io.File;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerator;
import com.gluonhq.jfxapps.metadata.properties.impl.PropertyGeneratorImpl;
import com.gluonhq.jfxapps.metadata.sample.custo.ComponentCusto;
import com.gluonhq.jfxapps.metadata.sample.custo.ComponentPropertyCusto;
import com.gluonhq.jfxapps.metadata.sample.custo.ValuePropertyCusto;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

@ExtendWith(MockitoExtension.class)
class PropertyGeneratorImplITTest {

    @TempDir
    File tmpDir;

    @TempDir
    File sampleResources;

    @Mock
    BeanMetaData<?> beanMetaData;

    @Mock
    BeanMetaData<?> beanMetaData2;

    @Mock
    PropertyMetaData propertyMetaData;

    @Test
    void should_generate_successfully() throws Exception {

        PropertyGenerationContext context = new PropertyGenerationContext();
        context.setComponentCustomizationClass(ComponentCusto.class.getName());
        context.setValuePropertyCustomizationClass(ValuePropertyCusto.class.getName());
        context.setComponentPropertyCustomizationClass(ComponentPropertyCusto.class.getName());
        context.setResourceFolder(tmpDir);

        PropertyGenerator generator = new PropertyGeneratorImpl(context);

        generator.generateProperties(Set.of(
                Node.class,
                Parent.class,
                Region.class,
                Pane.class,
                BorderPane.class
                ), Set.of());

        System.out.println();
    }

}
