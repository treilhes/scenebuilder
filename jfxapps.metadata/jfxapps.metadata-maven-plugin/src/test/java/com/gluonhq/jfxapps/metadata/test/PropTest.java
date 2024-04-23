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
package com.gluonhq.jfxapps.metadata.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.gluonhq.jfxapps.metadata.test.ComponentCusto.Qualifier;

class PropTest {

    @Test
    void test() throws IOException {
     JavaPropsMapper mapper = new JavaPropsMapper();

     TypeFactory typeFactory = TypeFactory.defaultInstance();

     // Constructing the parameterized types
     JavaType componentCustoType = typeFactory.constructType(ComponentCusto.class);
     JavaType componentPropertyCustoType = typeFactory.constructType(ComponentPropertyCusto.class);
     JavaType valuePropertyCustoType = typeFactory.constructType(ValuePropertyCusto.class);

     // Constructing the Component<ComponentCusto, ComponentPropertyCusto, ValuePropertyCusto> type
     JavaType componentType = typeFactory.constructParametricType(
             Component.class,
             componentCustoType,
             componentPropertyCustoType,
             valuePropertyCustoType
     );

     // and then read/write data as usual
     //var component = new Component<ComponentCusto, ComponentPropertyCusto, ValuePropertyCusto>();
     var component = new Component.Builder<ComponentCusto, ComponentPropertyCusto, ValuePropertyCusto>()
             .descriptor(Descriptor.<ComponentCusto>builder()
                     .name("eeeeeeeeeeeeeee")
                     .shadows(List.of("children"))
                     .customization(ComponentCusto.builder()
                             .category("Containers")
                             .resizeWhenTop(true)
                             .qualifier("default", new Qualifier.Builder()
                                     .fxml("BorderPane_default.fxml")
                                     .image("BorderPane_default.png")
                                     .imagex2("BorderPane_default@2x.png")
                                     .build())
                             .build())
                     .build())
             .componentProperty("top", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .clazz("com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata")
                     .customization(ComponentPropertyCusto.builder()
                             .order(10001)
                             .image("BorderPane-top.png")
                             .imagex2("BorderPane-top@2x.png")
                             .build())
                     .build())
             .componentProperty("left", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .clazz("com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata")
                     .customization(new ComponentPropertyCusto.Builder()
                             .order(10002)
                             .image("BorderPane-left.png")
                             .imagex2("BorderPane-left@2x.png")
                             .build())
                     .build())
             .componentProperty("center", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .clazz("com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata")
                     .customization(new ComponentPropertyCusto.Builder()
                             .order(10003)
                             .image("BorderPane-center.png")
                             .imagex2("BorderPane-center@2x.png")
                             .build())
                     .build())

             .componentProperty("right", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .clazz("com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata")
                     .customization(new ComponentPropertyCusto.Builder()
                             .order(10004)
                             .image("BorderPane-right.png")
                             .imagex2("BorderPane-right@2x.png")
                             .build())
                     .build())
             .componentProperty("bottom", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .clazz("com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata")
                     .customization(new ComponentPropertyCusto.Builder()
                             .order(10005)
                             .image("BorderPane-bottom.png")
                             .imagex2("BorderPane-bottom@2x.png")
                             .build())
                     .build())
             .staticProperty("alignment", new ValueProperty.Builder<ValuePropertyCusto>()
                     .clazz("com.gluonhq.jfxapps.core.metadata.property.value.EnumerationPropertyMetadata")
                     .customization(new ValuePropertyCusto.Builder()
                             .order(0)
                             .section("Layout")
                             .subSection("Border Pane Constraints")
                             .nullEquivalent("AUTOMATIC")
                             .build())
                     .build())
             .staticProperty("margin", new ValueProperty.Builder<ValuePropertyCusto>()
                     .clazz("com.gluonhq.jfxapps.core.metadata.property.value.InsetsPropertyMetadata")
                     .customization(new ValuePropertyCusto.Builder()
                             .order(1)
                             .section("Layout")
                             .subSection("Border Pane Constraints")
                             .build())
                     .build())
             .valueProperty("contentBias", new ValueProperty.Builder<ValuePropertyCusto>()
                     .clazz("com.gluonhq.jfxapps.core.metadata.property.value.EnumerationPropertyMetadata")
                     .customization(new ValuePropertyCusto.Builder()
                             .order(4)
                             .section("Layout")
                             .subSection("Extras")
                             .nullEquivalent("NONE")
                             .build())
                     .build())
             .build();


     String props = mapper.writeValueAsString(component);
     // or
     mapper.writeValue(new File("stuff.properties"), component);

     Component<ComponentCusto, ComponentPropertyCusto, ValuePropertyCusto> otherComp =
             mapper.readValue(props, componentType);

     System.out.println();
    }

}
