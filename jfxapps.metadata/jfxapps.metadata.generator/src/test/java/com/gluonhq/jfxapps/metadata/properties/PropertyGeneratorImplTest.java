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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.EnumerationPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.InsetsPropertyMetadata;
import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerator;
import com.gluonhq.jfxapps.metadata.properties.impl.PropertyGeneratorImpl;
import com.gluonhq.jfxapps.metadata.properties.model.Component;
import com.gluonhq.jfxapps.metadata.properties.model.ComponentProperty;
import com.gluonhq.jfxapps.metadata.properties.model.ValueProperty;
import com.gluonhq.jfxapps.metadata.sample.custo.ComponentCusto;
import com.gluonhq.jfxapps.metadata.sample.custo.ComponentCusto.Qualifier;
import com.gluonhq.jfxapps.metadata.sample.custo.ComponentPropertyCusto;
import com.gluonhq.jfxapps.metadata.sample.custo.ValuePropertyCusto;

import sample.Root;

@ExtendWith(MockitoExtension.class)
class PropertyGeneratorImplTest {

    private static final String PROP_NAME = "propName";

    @TempDir
    File tmpDir;

    File sampleResources = new File("src/test/resources/sample");

    @Mock
    BeanMetaData<?> beanMetaData;

    @Mock
    BeanMetaData<?> beanMetaData2;

    @Mock
    PropertyMetaData propertyMetaData;

    PropertyGenerationContext context;

    @BeforeEach
    public void setup() {
        try {
            context = new PropertyGenerationContext();
            context.setOutputResourceFolder(tmpDir);
            context.setComponentCustomizationClass(ComponentCusto.class.getName());
            context.setComponentPropertyCustomizationClass(ComponentPropertyCusto.class.getName());
            context.setValuePropertyCustomizationClass(ValuePropertyCusto.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    //@Test
    @Deprecated
    void property_hidden_must_be_discarded() throws Exception {

        when(beanMetaData.getProperties()).thenReturn(List.of(propertyMetaData));
        //when(propertyMetaData.isHidden()).thenReturn(true);

        PropertyGeneratorImpl generator = new PropertyGeneratorImpl(context);
        var component = generator.buildComponent(context.getOutputResourceFolder(), beanMetaData, Map.of(), Set.of());

        assertThat(component.getComponentProperties()).isEmpty();
        assertThat(component.getStaticValueProperties()).isEmpty();
        assertThat(component.getValueProperties()).isEmpty();
    }

    @Test
    void property_non_local_must_be_discarded() throws Exception {

        Mockito.<Class<?>>when(beanMetaData.getType()).thenReturn(Root.class);
        when(beanMetaData.getProperties()).thenReturn(List.of(propertyMetaData));
        //when(propertyMetaData.isHidden()).thenReturn(false);
        when(propertyMetaData.isLocal()).thenReturn(false);

        PropertyGeneratorImpl generator = new PropertyGeneratorImpl(context);
        var component = generator.buildComponent(context.getOutputResourceFolder(), beanMetaData, Map.of(), Set.of());

        assertThat(component.getComponentProperties()).isEmpty();
        assertThat(component.getStaticValueProperties()).isEmpty();
        assertThat(component.getValueProperties()).isEmpty();
    }

    @Test
    void property_not_in_beanmap_and_available_components_must_be_a_value_property() throws Exception {

        when(beanMetaData.getProperties()).thenReturn(List.of(propertyMetaData));
        Mockito.<Class<?>>when(beanMetaData.getType()).thenReturn(Root.class);
        Mockito.<Class<?>>when(propertyMetaData.getContentType()).thenReturn(Root.class);
        when(propertyMetaData.getName()).thenReturn(PROP_NAME);
        when(propertyMetaData.isLocal()).thenReturn(true);

        PropertyGeneratorImpl generator = new PropertyGeneratorImpl(context);
        var component = generator.buildComponent(context.getOutputResourceFolder(), beanMetaData, Map.of(), Set.of());

        assertThat(component.getComponentProperties()).isEmpty();
        assertThat(component.getStaticValueProperties()).isEmpty();
        assertThat(component.getValueProperties()).containsKey(PROP_NAME);
    }

    @Test
    void property_static_must_be_a_static_value_property() throws Exception {

        when(beanMetaData.getProperties()).thenReturn(List.of(propertyMetaData));
        Mockito.<Class<?>>when(beanMetaData.getType()).thenReturn(Root.class);
        Mockito.<Class<?>>when(propertyMetaData.getContentType()).thenReturn(Root.class);
        when(propertyMetaData.getName()).thenReturn(PROP_NAME);
        when(propertyMetaData.isLocal()).thenReturn(true);
        when(propertyMetaData.isStatic()).thenReturn(true);

        PropertyGeneratorImpl generator = new PropertyGeneratorImpl(context);
        var component = generator.buildComponent(context.getOutputResourceFolder(), beanMetaData, Map.of(), Set.of());

        assertThat(component.getComponentProperties()).isEmpty();
        assertThat(component.getStaticValueProperties()).containsKey(PROP_NAME);
        assertThat(component.getValueProperties()).isEmpty();
    }

    @Test
    void property_in_beanmap_must_be_a_component_property() throws Exception {

        when(beanMetaData.getProperties()).thenReturn(List.of(propertyMetaData));
        Mockito.<Class<?>>when(beanMetaData.getType()).thenReturn(Root.class);
        Mockito.<Class<?>>when(propertyMetaData.getContentType()).thenReturn(Root.class);
        when(propertyMetaData.getName()).thenReturn(PROP_NAME);
        when(propertyMetaData.isLocal()).thenReturn(true);

        PropertyGeneratorImpl generator = new PropertyGeneratorImpl(context);
        var component = generator.buildComponent(context.getOutputResourceFolder(), beanMetaData, Map.of(Root.class, beanMetaData2), Set.of());

        assertThat(component.getComponentProperties()).containsKey(PROP_NAME);
        assertThat(component.getStaticValueProperties()).isEmpty();
        assertThat(component.getValueProperties()).isEmpty();
    }

    @Test
    void property_in_available_components_must_be_a_component_property() throws Exception {

        when(beanMetaData.getProperties()).thenReturn(List.of(propertyMetaData));
        Mockito.<Class<?>>when(beanMetaData.getType()).thenReturn(Root.class);
        Mockito.<Class<?>>when(propertyMetaData.getContentType()).thenReturn(Root.class);
        when(propertyMetaData.getName()).thenReturn(PROP_NAME);
        when(propertyMetaData.isLocal()).thenReturn(true);

        PropertyGeneratorImpl generator = new PropertyGeneratorImpl(context);
        var component = generator.buildComponent(context.getOutputResourceFolder(), beanMetaData, Map.of(), Set.of(Root.class));

        assertThat(component.getComponentProperties()).containsKey(PROP_NAME);
        assertThat(component.getStaticValueProperties()).isEmpty();
        assertThat(component.getValueProperties()).isEmpty();
    }

    @Test
    void property_in_beanmap_must_be_a_component_propertyxxx() throws Exception {

        PropertyGeneratorImpl generator = new PropertyGeneratorImpl(context);
        generator.generateProperties(Set.of(Root.class), null);

        Path target = context.getOutputResourceFolder().toPath().resolve(PropertyGenerator.propertyPath(Root.class));

        assertTrue("File must be generated", Files.exists(target));
    }
    @Test
    void should_generate_from_class_default_properties_and_load_successfully() throws IOException {


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
             .name("eeeeeeeeeeeeeee")
             .shadows(List.of("children"))
             .customization(ComponentCusto.builder()
                     .category("Containers")
                     .resizeNeededWhenTop(true)
                     .qualifier("default", new Qualifier.Builder()
                             .fxml("BorderPane_default.fxml")
                             .image("BorderPane_default.png")
                             .imagex2("BorderPane_default@2x.png")
                             .build())
                     .build())
             .componentProperty("top", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .metadataClass(ComponentPropertyMetadata.class)
                     .customization(ComponentPropertyCusto.builder()
                             .order(10001)
                             .image("BorderPane-top.png")
                             .imagex2("BorderPane-top@2x.png")
                             .build())
                     .build())
             .componentProperty("left", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .metadataClass(ComponentPropertyMetadata.class)
                     .customization(new ComponentPropertyCusto.Builder()
                             .order(10002)
                             .image("BorderPane-left.png")
                             .imagex2("BorderPane-left@2x.png")
                             .build())
                     .build())
             .componentProperty("center", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .metadataClass(ComponentPropertyMetadata.class)
                     .customization(new ComponentPropertyCusto.Builder()
                             .order(10003)
                             .image("BorderPane-center.png")
                             .imagex2("BorderPane-center@2x.png")
                             .build())
                     .build())

             .componentProperty("right", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .metadataClass(ComponentPropertyMetadata.class)
                     .customization(new ComponentPropertyCusto.Builder()
                             .order(10004)
                             .image("BorderPane-right.png")
                             .imagex2("BorderPane-right@2x.png")
                             .build())
                     .build())
             .componentProperty("bottom", new ComponentProperty.Builder<ComponentPropertyCusto>()
                     .metadataClass(ComponentPropertyMetadata.class)
                     .customization(new ComponentPropertyCusto.Builder()
                             .order(10005)
                             .image("BorderPane-bottom.png")
                             .imagex2("BorderPane-bottom@2x.png")
                             .build())
                     .build())
             .staticProperty("alignment", new ValueProperty.Builder<ValuePropertyCusto>()
                     .metadataClass(EnumerationPropertyMetadata.class)
                     .customization(new ValuePropertyCusto.Builder()
                             .order(0)
                             .section("Layout")
                             .subSection("Border Pane Constraints")
                             .nullEquivalent("AUTOMATIC")
                             .build())
                     .build())
             .staticProperty("margin", new ValueProperty.Builder<ValuePropertyCusto>()
                     .metadataClass(InsetsPropertyMetadata.class)
                     .customization(new ValuePropertyCusto.Builder()
                             .order(1)
                             .section("Layout")
                             .subSection("Border Pane Constraints")
                             .build())
                     .build())
             .valueProperty("contentBias", new ValueProperty.Builder<ValuePropertyCusto>()
                     .metadataClass(EnumerationPropertyMetadata.class)
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
     mapper.writeValue(new File(tmpDir, "stuff.properties"), component);

     Component<ComponentCusto, ComponentPropertyCusto, ValuePropertyCusto> otherComp =
             mapper.readValue(props, componentType);

     System.out.println();
    }

}
