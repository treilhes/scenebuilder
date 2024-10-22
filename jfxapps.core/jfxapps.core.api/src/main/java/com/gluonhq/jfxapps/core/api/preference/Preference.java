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
package com.gluonhq.jfxapps.core.api.preference;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public interface Preference<T> extends ManagedPreference {

    static ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new SimpleModule()
                    .addKeySerializer(Object.class, new GeneralKeySerializer())
                    .addDeserializer(ObservableList.class, new ObservableListDeserializer())
                    .addDeserializer(ObservableMap.class, new ObservableMapDeserializer()))
            .build();

    JfxAppContext getContext();

    UUID getId();

    String getName();

    Preference<T> setValue(T value);

    Preference<T> reset();

    T getValue();

    ObservableValue<T> getObservableValue();

    T getDefault();

    Class<T> getDataClass();

    boolean isValid();

    static class ObservableListDeserializer extends JsonDeserializer<ObservableList<?>> implements ContextualDeserializer {

        private JavaType valueType;

        public ObservableListDeserializer() {
            // Default constructor with no type
        }

        // Constructor that accepts the generic type for T
        public ObservableListDeserializer(JavaType valueType) {
            this.valueType = valueType;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JsonMappingException {
            return new ObservableListDeserializer(ctxt.getContextualType());
        }

        @Override
        public ObservableList<?> deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JacksonException {
            JsonNode array = ctxt.readTree(p);
            ObservableList list = FXCollections.observableArrayList();

            // Get the content type (e.g., T in List<T>)
            JavaType elementType = valueType.containedTypeOrUnknown(0);

            // Iterate over each node in the array
            for (JsonNode node : array) {
                // Deserialize each node into the appropriate type and add it to the list
                Object element = objectMapper.treeToValue(node, elementType);
                list.add(element);
            }

            return list;
        }



    }

    static class ObservableMapDeserializer extends JsonDeserializer<ObservableMap<?, ?>> implements ContextualDeserializer {

        private JavaType valueType;

        public ObservableMapDeserializer() {
            // Default constructor with no type
        }

        // Constructor that accepts the generic type for T
        public ObservableMapDeserializer(JavaType valueType) {
            this.valueType = valueType;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JsonMappingException {
            return new ObservableMapDeserializer(ctxt.getContextualType());
        }
        @Override
        public ObservableMap<?, ?> deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JacksonException {
            // Get the ObjectMapper from the JsonParser
            ObjectMapper mapper = (ObjectMapper) p.getCodec();

            // Get the key and value types
            JavaType mapKeyType = valueType.containedTypeOrUnknown(0); // First generic type (K)
            JavaType mapValueType = valueType.containedTypeOrUnknown(1); // Second generic type (V)

            // Read the JSON as an ObjectNode (representing the map)
            ObjectNode mapNode = mapper.readTree(p);
            ObservableMap<Object, Object> resultMap = FXCollections.observableHashMap();

            // Iterate over the entries in the JSON object
            Iterator<Entry<String, JsonNode>> fields = mapNode.fields();
            while (fields.hasNext()) {
                Entry<String, JsonNode> field = fields.next();

                // Deserialize the key (if it's a string, convert it directly)
                Object key = mapper.readValue(field.getKey().getBytes(), mapKeyType);

                // Deserialize the value
                Object value = mapper.convertValue(field.getValue(), mapValueType);

                // Add the deserialized entry to the ObservableMap
                resultMap.put(key, value);
            }

            return resultMap;
        }

    }

    public class GeneralKeySerializer extends JsonSerializer<Object> {

        @Override
        public void serialize(Object key, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (key instanceof String) {
                // If the key is a String, just write it as-is
                gen.writeFieldName((String) key);
            } else {
                // For all other types, convert the key to a JSON string representation
                gen.writeFieldName(((ObjectMapper)gen.getCodec()).writeValueAsString(key));
            }
        }
    }

}
