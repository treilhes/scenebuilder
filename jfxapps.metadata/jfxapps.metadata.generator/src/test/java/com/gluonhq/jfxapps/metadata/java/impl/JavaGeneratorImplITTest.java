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
package com.gluonhq.jfxapps.metadata.java.impl;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gluonhq.jfxapps.metadata.java.api.JavaGenerationContext;
import com.gluonhq.jfxapps.metadata.java.model.tbd.Descriptor;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.properties.impl.PropertyGeneratorImpl;
import com.gluonhq.jfxapps.metadata.sample.custo.ComponentCusto;
import com.gluonhq.jfxapps.metadata.sample.custo.ComponentPropertyCusto;
import com.gluonhq.jfxapps.metadata.sample.custo.ValuePropertyCusto;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

class JavaGeneratorImplITTest {

    File resourceDir = new File("./src/test/resources/sample");

    @TempDir
    File javaDir;


    @Test
    void test() throws Exception {

        var classList = Set.<Class<?>>of(
                Node.class,
                Parent.class,
                Region.class,
                Pane.class,
                BorderPane.class
                );
        var providedClassList = Set.<Descriptor>of();

        PropertyGenerationContext propertyContext = new PropertyGenerationContext();
        propertyContext.setComponentCustomizationClass(ComponentCusto.class.getName());
        propertyContext.setValuePropertyCustomizationClass(ValuePropertyCusto.class.getName());
        propertyContext.setComponentPropertyCustomizationClass(ComponentPropertyCusto.class.getName());
        propertyContext.setResourceFolder(resourceDir);

        JavaGenerationContext javaContext = new JavaGenerationContext();
        javaContext.setKeepLastPackages(2);
        javaContext.setSourceFolder(javaDir);
        javaContext.setTargetPackage("test.generation");
        javaContext.setUuid(UUID.randomUUID().toString());
        javaContext.setModuleName("some.module.name");
        javaContext.setExtensionName("TheExtension");

        javaContext.setComponentCustomizationTemplate("/template/custo/CustomizeComponent.ftl");
        javaContext.setComponentPropertyCustomizationTemplate("/template/custo/CustomizeComponentProperty.ftl");
        javaContext.setValuePropertyCustomizationTemplate("/template/custo/CustomizeValueProperty.ftl");
        javaContext.setStaticValuePropertyCustomizationTemplate("/template/custo/CustomizeStaticValueProperty.ftl");

        PropertyGeneratorImpl propGenerator = new PropertyGeneratorImpl(propertyContext);


        JavaGeneratorImpl generator = new JavaGeneratorImpl(propertyContext, javaContext);

        propGenerator.generateProperties(classList, providedClassList);
        generator.generateJavaFiles(classList, providedClassList);

        System.out.println();
    }

}
