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
package com.gluonhq.jfxapps.metadata.properties.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;


@JsonPropertyOrder(value = { "componentProperties", "valueProperties", "staticValueProperties" })
public class ComponentBase<CC, CPC, CPB extends ComponentPropertyBase<CPC>, VPC, VPB extends ValuePropertyBase<VPC>> {

    @JsonIgnore
    private BeanMetaData<?> metadata;

    @JsonInclude(Include.NON_NULL)
    private String name;
    @JsonInclude(Include.NON_NULL)
    private String version;
    @JsonInclude(Include.NON_EMPTY)
    private List<String> shadows;
    @JsonProperty("custo")
    @JsonInclude(Include.NON_NULL)
    private CC customization;

    @JsonProperty("component")
    @JsonInclude(Include.NON_EMPTY)
    private Map<String, CPB> componentProperties;
    @JsonProperty("property")
    @JsonInclude(Include.NON_EMPTY)
    private Map<String, VPB> valueProperties;
    @JsonProperty("static")
    @JsonInclude(Include.NON_EMPTY)
    private Map<String, VPB> staticValueProperties;

    public ComponentBase() {
        shadows = new ArrayList<>();
        valueProperties = new HashMap<>();
        componentProperties = new HashMap<>();
        staticValueProperties = new HashMap<>();
    }

    public BeanMetaData<?> getMetadata() {
        return metadata;
    }

    public void setMetadata(BeanMetaData<?> metadata) {
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public CC getCustomization() {
        return customization;
    }

    public void setCustomization(CC customization) {
        this.customization = customization;
    }

    public List<String> getShadows() {
        return shadows;
    }

    public void setShadows(List<String> shadows) {
        if (shadows == null) {
            shadows = new ArrayList<>();
        }
        this.shadows = shadows;
    }

    public Map<String, VPB> getValueProperties() {
        return valueProperties;
    }

    public void setValueProperties(Map<String, VPB> valueProperties) {
        if (valueProperties == null) {
            valueProperties = new HashMap<>();
        }
        this.valueProperties = valueProperties;
    }

    public Map<String, CPB> getComponentProperties() {
        return componentProperties;
    }

    public void setComponentProperties(Map<String, CPB> componentProperties) {
        if (componentProperties == null) {
            componentProperties = new HashMap<>();
        }
        this.componentProperties = componentProperties;
    }

    public Map<String, VPB> getStaticValueProperties() {
        return staticValueProperties;
    }

    public void setStaticValueProperties(Map<String, VPB> staticValueProperties) {
        if (staticValueProperties == null) {
            staticValueProperties = new HashMap<>();
        }
        this.staticValueProperties = staticValueProperties;
    }

    public static class BuilderBase<CC, CPC, CPB extends ComponentPropertyBase<CPC>, VPC, VPB extends ValuePropertyBase<VPC>, CTB extends ComponentBase<CC, CPC, CPB, VPC, VPB>> {
        private Class<CTB> classToBuild;
        private String name;
        private List<String> shadows;
        private CC customization;
        private Map<String, CPB> componentProperties;
        private Map<String, VPB> valueProperties;
        private Map<String, VPB> staticValueProperties;

        public BuilderBase(Class<CTB> classToBuild) {
            super();
            this.classToBuild = classToBuild;
        }

        public BuilderBase<CC, CPC, CPB, VPC, VPB, CTB> name(String name) {
            this.name = name;
            return this;
        }

        public BuilderBase<CC, CPC, CPB, VPC, VPB, CTB> shadows(List<String> shadows) {
            this.shadows = shadows;
            return this;
        }

        public BuilderBase<CC, CPC, CPB, VPC, VPB, CTB> customization(CC customization) {
            this.customization = customization;
            return this;
        }

        public BuilderBase<CC, CPC, CPB, VPC, VPB, CTB> componentProperties(Map<String, CPB> componentProperties) {
            this.componentProperties = componentProperties;
            return this;
        }

        public BuilderBase<CC, CPC, CPB, VPC, VPB, CTB> valueProperties(Map<String, VPB> valueProperties) {
            this.valueProperties = valueProperties;
            return this;
        }

        public BuilderBase<CC, CPC, CPB, VPC, VPB, CTB> staticValueProperties(Map<String, VPB> staticValueProperties) {
            this.staticValueProperties = staticValueProperties;
            return this;
        }

        public BuilderBase<CC, CPC, CPB, VPC, VPB, CTB> componentProperty(String key, CPB property) {
            if (componentProperties == null) {
                componentProperties = new HashMap<>();
            }
            this.componentProperties.put(key, property);
            return this;
        }

        public BuilderBase<CC, CPC, CPB, VPC, VPB, CTB> valueProperty(String key, VPB property) {
            if (valueProperties == null) {
                valueProperties = new HashMap<>();
            }
            this.valueProperties.put(key, property);
            return this;
        }

        public BuilderBase<CC, CPC, CPB, VPC, VPB, CTB> staticProperty(String key, VPB property) {
            if (staticValueProperties == null) {
                staticValueProperties = new HashMap<>();
            }
            this.staticValueProperties.put(key, property);
            return this;
        }

        public CTB build() {
            CTB component;
            try {
                component = classToBuild.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            component.setName(name);
            component.setShadows(shadows);
            component.setCustomization(customization);
            component.setComponentProperties(componentProperties);
            component.setValueProperties(valueProperties);
            component.setStaticValueProperties(staticValueProperties);
            return component;
        }
    }


    public static abstract class AbstractDeserializer<CMP extends ComponentBase, CP extends ComponentPropertyBase, VP extends ValuePropertyBase>
            extends StdDeserializer<CMP> implements ContextualDeserializer {

        private static final Logger logger = LoggerFactory.getLogger(AbstractDeserializer.class);

        private final ObjectMapper mapper;
        private final Class<CP> componentPropertyClass;
        private final Class<VP> valuePropertyClass;
        private JavaType type;

        public AbstractDeserializer(Class<CP> componentPropertyClass, Class<VP> valuePropertyClass) {
            super((Class<?>)null);
            this.mapper = new ObjectMapper();
            this.componentPropertyClass = componentPropertyClass;
            this.valuePropertyClass = valuePropertyClass;
        }

//        public AbstractDeserializer(JavaType valueType) {
//            super(valueType);
//            this.mapper = new ObjectMapper();
//        }

        @Override
        public CMP deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            JsonNode componentNode = jp.getCodec().readTree(jp);
            CMP component = null;
            try {
                component = (CMP)type.getRawClass().getConstructor().newInstance();
            } catch (Exception e) {
                throw ValueInstantiationException.from(jp, "Unable to instanciate class", type, e);
            }

            TypeFactory typeFactory = TypeFactory.defaultInstance();

            JavaType stringType = typeFactory.constructType(String.class);

            JavaType cc = type.containedType(0);
            JavaType cpc = type.containedType(1);
            JavaType vpc = type.containedType(2);

            JavaType componentProperty = typeFactory.constructParametricType(componentPropertyClass, cpc);
            JavaType valueProperty = typeFactory.constructParametricType(valuePropertyClass, vpc);

            JavaType shadowType = typeFactory.constructParametricType(List.class, String.class);

            JavaType componentPropertyMap = typeFactory.constructParametricType(Map.class, stringType, componentProperty);
            JavaType valuePropertyMap = typeFactory.constructParametricType(Map.class, stringType, valueProperty);

            var descriptor = componentNode.get("class");

            try {
                if (descriptor != null) {
                    component.setName(mapper.treeToValue(descriptor.get("name"), stringType));
                    component.setShadows(mapper.treeToValue(descriptor.get("shadows"), shadowType));
                    component.setCustomization(mapper.treeToValue(descriptor.get("custo"), cc));
                }

                Map componentProperties = (Map)mapper.treeToValue(componentNode.get("component"), componentPropertyMap);
                if (componentProperties != null) {
                    componentProperties.keySet()
                        .forEach(k -> logger.debug("componentProperties: {} -> {}", k, componentProperties.get(k)));
                    component.setComponentProperties(componentProperties);
                }

                var valueProperties = (Map)mapper.treeToValue(componentNode.get("property"), valuePropertyMap);
                if (valueProperties != null) {
                    valueProperties.keySet()
                        .forEach(k -> logger.debug("valueProperties: {} -> {}", k, valueProperties.get(k)));
                    component.setValueProperties(valueProperties);
                }

                var staticProperties = (Map)mapper.treeToValue(componentNode.get("static"), valuePropertyMap);
                if (staticProperties != null) {
                    staticProperties.keySet()
                         .forEach(k -> logger.debug("staticProperties: {} -> {}", k, staticProperties.get(k)));
                    component.setStaticValueProperties(staticProperties);
                }
            } catch (Exception e) {
                throw e;
            }

            return component;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JsonMappingException {
            AbstractDeserializer<?,?,?> deserializer = newInstance();
            deserializer.type = ctxt.getContextualType();
            return deserializer;
        }

        public abstract AbstractDeserializer<?,?,?> newInstance();
    }


    public static class Serializer extends StdSerializer<ComponentBase> {

        public Serializer() {
            this(null);
        }

        public Serializer(Class<ComponentBase> t) {
            super(t);
        }

        @Override
        public void serialize(ComponentBase value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();

            gen.writeObjectFieldStart("class");
            gen.writeStringField("name", value.getName());
            provider.defaultSerializeField("shadows", value.getShadows(), gen);
            provider.defaultSerializeField("custo", value.getCustomization(), gen);
            gen.writeEndObject();

            provider.defaultSerializeField("component", value.getComponentProperties(), gen);
            provider.defaultSerializeField("property", value.getValueProperties(), gen);
            provider.defaultSerializeField("static", value.getStaticValueProperties(), gen);

            gen.writeEndObject();

        }

    }
}
