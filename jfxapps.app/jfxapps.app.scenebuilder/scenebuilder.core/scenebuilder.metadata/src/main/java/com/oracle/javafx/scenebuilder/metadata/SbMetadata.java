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
package com.oracle.javafx.scenebuilder.metadata;

import static com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization.InspectorPath.CUSTOM_SECTION;
import static com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization.InspectorPath.CUSTOM_SUB_SECTION;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.AbstractMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.BooleanPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DurationPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.EnumerationPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.EventHandlerPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.FontPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.ImagePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.IntegerPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.list.StringListPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.paint.ColorPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.paint.PaintPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.util.SBDuration;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentClassMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentPropertyMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization.InspectorPath;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization.InspectorPathComparator;

import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class SbMetadata extends AbstractMetadata<
    ComponentClassMetadataCustomization,
    ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, SbComponentClassMetadata<?>>,
    ValuePropertyMetadata<ValuePropertyMetadataCustomization>,
    SbComponentClassMetadata<?>> {

    private final List<String> sectionNames = new ArrayList<>();
    private final Map<String, List<String>> subSectionMap = new HashMap<>();

    public final InspectorPathComparator INSPECTOR_PATH_COMPARATOR
            = new InspectorPathComparator(sectionNames, subSectionMap);


    /**
     * parent related properties can be understood as transient properties that have a meaning only in the current parent
     * Changing the parent means those properties can be deleted because the meaning is lost
     * Ex: positioning/scaling/rotation
     */
    private final Set<PropertyName> parentRelatedProperties = new HashSet<>();

    protected SbMetadata(
            List<SbComponentClassMetadata<?>> componentClassMetadatas,
            SbMetadataIntrospector metadataIntrospector) {
        super(componentClassMetadatas);
        setMetadataIntrospector(new SbMetadataIntrospector());
        // Populates parentRelatedProperties
//        parentRelatedProperties.add(PropertyNames.layoutXName);
//        parentRelatedProperties.add(PropertyNames.layoutYName);
//        parentRelatedProperties.add(PropertyNames.translateXName);
//        parentRelatedProperties.add(PropertyNames.translateYName);
//        parentRelatedProperties.add(PropertyNames.translateZName);
//        parentRelatedProperties.add(PropertyNames.scaleXName);
//        parentRelatedProperties.add(PropertyNames.scaleYName);
//        parentRelatedProperties.add(PropertyNames.scaleZName);
//        parentRelatedProperties.add(PropertyNames.rotationAxisName);
//        parentRelatedProperties.add(PropertyNames.rotateName);
        parentRelatedProperties.add(new PropertyName("layoutX"));
        parentRelatedProperties.add(new PropertyName("layoutY"));
        parentRelatedProperties.add(new PropertyName("translateX"));
        parentRelatedProperties.add(new PropertyName("translateY"));
        parentRelatedProperties.add(new PropertyName("translateZ"));
        parentRelatedProperties.add(new PropertyName("scaleX"));
        parentRelatedProperties.add(new PropertyName("scaleY"));
        parentRelatedProperties.add(new PropertyName("scaleZ"));
        parentRelatedProperties.add(new PropertyName("rotationAxis"));
        parentRelatedProperties.add(new PropertyName("rotate"));

        // Populates sectionNames
        sectionNames.add("Properties"); //NOCHECK
        sectionNames.add("Layout"); //NOCHECK
        sectionNames.add("Code"); //NOCHECK

        // Populates subSectionMap
        final List<String> ss0 = new ArrayList<>();
        ss0.add("Custom"); //NOCHECK
        ss0.add("Text"); //NOCHECK
        ss0.add("Specific"); //NOCHECK
        ss0.add("Graphic"); //NOCHECK
        ss0.add("3D"); //NOCHECK
        ss0.add("Pagination"); //NOCHECK
        ss0.add("Stroke"); //NOCHECK
        ss0.add("Node"); //NOCHECK
        ss0.add("JavaFX CSS"); //NOCHECK
        ss0.add("Extras"); //NOCHECK
        ss0.add("Accessibility"); //NOCHECK
        subSectionMap.put("Properties", ss0); //NOCHECK
        final List<String> ss1 = new ArrayList<>();
        ss1.add("Anchor Pane Constraints"); //NOCHECK
        ss1.add("Border Pane Constraints"); //NOCHECK
        ss1.add("Flow Pane Constraints"); //NOCHECK
        ss1.add("Grid Pane Constraints"); //NOCHECK
        ss1.add("HBox Constraints"); //NOCHECK
        ss1.add("Split Pane Constraints"); //NOCHECK
        ss1.add("Stack Pane Constraints"); //NOCHECK
        ss1.add("Tile Pane Constraints"); //NOCHECK
        ss1.add("VBox Constraints"); //NOCHECK
        ss1.add("Internal"); //NOCHECK
        ss1.add("Specific"); //NOCHECK
        ss1.add("Size"); //NOCHECK
        ss1.add("Position"); //NOCHECK
        ss1.add("Transforms"); //NOCHECK
        ss1.add("Bounds"); //NOCHECK
        ss1.add("Extras"); //NOCHECK
        ss1.add("Specific"); //NOCHECK
        subSectionMap.put("Layout", ss1); //NOCHECK
        final List<String> ss2 = new ArrayList<>();
        ss2.add("Main"); //NOCHECK
        ss2.add("Edit"); //NOCHECK
        ss2.add("DragDrop"); //NOCHECK
        ss2.add("Closing"); //NOCHECK
        ss2.add("HideShow"); //NOCHECK
        ss2.add("Keyboard"); //NOCHECK
        ss2.add("Mouse"); //NOCHECK
        ss2.add("Rotation"); //NOCHECK
        ss2.add("Swipe"); //NOCHECK
        ss2.add("Touch"); //NOCHECK
        ss2.add("Zoom"); //NOCHECK
        subSectionMap.put("Code", ss2); //NOCHECK
    }


    /**
     * During prune properties job a property is trimmed
     * if the property is static
     * if the property is transient (has a meaning in the current parent only)
     * @param name
     * @return
     */

    public boolean isPropertyTrimmingNeeded(PropertyName name) {
        final boolean result;

        if (name.getResidenceClass() != null) {
            // It's a static property eg GridPane.rowIndex
            // All static property are "parent related" and needs trimming
            result = true;
        } else {
            result = parentRelatedProperties.contains(name);
        }

        return result;
    }

    public class SbMetadataIntrospector implements AbstractMetadata.MetadataIntrospector<SbComponentClassMetadata<?>>  {

        /**
         * During data introspection of an unknown custom component, if the name match,
         * the property will be ignored.
         */
        private final Set<PropertyName> hiddenProperties = new HashSet<>();

        public SbMetadataIntrospector() {
         // Populates hiddenProperties
            addHiddenProperty(new PropertyName("activated")); // NOCHECK
            addHiddenProperty(new PropertyName("alignWithContentOrigin")); // NOCHECK
            addHiddenProperty(new PropertyName("armed")); // NOCHECK
            addHiddenProperty(new PropertyName("anchor")); // NOCHECK
            addHiddenProperty(new PropertyName("antiAliasing")); // NOCHECK
            addHiddenProperty(new PropertyName("border")); // NOCHECK
            addHiddenProperty(new PropertyName("background")); // NOCHECK
            addHiddenProperty(new PropertyName("caretPosition")); // NOCHECK
            addHiddenProperty(new PropertyName("camera")); // NOCHECK
            addHiddenProperty(new PropertyName("cellFactory")); // NOCHECK
            addHiddenProperty(new PropertyName("cellValueFactory")); // NOCHECK
            addHiddenProperty(new PropertyName("characters")); // NOCHECK
            addHiddenProperty(new PropertyName("childrenUnmodifiable")); // NOCHECK
            addHiddenProperty(new PropertyName("chronology")); // NOCHECK
            addHiddenProperty(new PropertyName("class")); // NOCHECK
            addHiddenProperty(new PropertyName("comparator")); // NOCHECK
            addHiddenProperty(new PropertyName("converter")); // NOCHECK
            addHiddenProperty(new PropertyName("controlCssMetaData")); // NOCHECK
            addHiddenProperty(new PropertyName("cssMetaData")); // NOCHECK
            addHiddenProperty(new PropertyName("customColors")); // NOCHECK
            addHiddenProperty(new PropertyName("data")); // NOCHECK
            addHiddenProperty(new PropertyName("dayCellFactory")); // NOCHECK
            addHiddenProperty(new PropertyName("depthBuffer")); // NOCHECK
            addHiddenProperty(new PropertyName("disabled")); // NOCHECK
            addHiddenProperty(new PropertyName("dividers")); // NOCHECK
            addHiddenProperty(new PropertyName("editingCell")); // NOCHECK
            addHiddenProperty(new PropertyName("editingIndex")); // NOCHECK
            addHiddenProperty(new PropertyName("editingItem")); // NOCHECK
            addHiddenProperty(new PropertyName("editor")); // NOCHECK
            addHiddenProperty(new PropertyName("engine")); // NOCHECK
            addHiddenProperty(new PropertyName("eventDispatcher")); // NOCHECK
            addHiddenProperty(new PropertyName("expandedPane")); // NOCHECK
            addHiddenProperty(new PropertyName("filter")); // NOCHECK
            addHiddenProperty(new PropertyName("focused")); // NOCHECK
            addHiddenProperty(new PropertyName("focusModel")); // NOCHECK
            addHiddenProperty(new PropertyName("graphicsContext2D")); // NOCHECK
            addHiddenProperty(new PropertyName("hover")); // NOCHECK
            addHiddenProperty(new PropertyName("inputMethodRequests")); // NOCHECK
            addHiddenProperty(new PropertyName("localToParentTransform")); // NOCHECK
            addHiddenProperty(new PropertyName("localToSceneTransform")); // NOCHECK
            addHiddenProperty(new PropertyName("managed")); // NOCHECK
            addHiddenProperty(new PropertyName("mediaPlayer")); // NOCHECK
            addHiddenProperty(new PropertyName("needsLayout")); // NOCHECK
            addHiddenProperty(new PropertyName("nodeColumnEnd", javafx.scene.layout.GridPane.class)); // NOCHECK
            addHiddenProperty(new PropertyName("nodeColumnIndex", javafx.scene.layout.GridPane.class)); // NOCHECK
            addHiddenProperty(new PropertyName("nodeColumnSpan", javafx.scene.layout.GridPane.class)); // NOCHECK
            addHiddenProperty(new PropertyName("nodeHgrow", javafx.scene.layout.GridPane.class)); // NOCHECK
            addHiddenProperty(new PropertyName("nodeMargin", javafx.scene.layout.BorderPane.class)); // NOCHECK
            addHiddenProperty(new PropertyName("nodeRowEnd", javafx.scene.layout.GridPane.class)); // NOCHECK
            addHiddenProperty(new PropertyName("nodeRowIndex", javafx.scene.layout.GridPane.class)); // NOCHECK
            addHiddenProperty(new PropertyName("nodeRowSpan", javafx.scene.layout.GridPane.class)); // NOCHECK
            addHiddenProperty(new PropertyName("nodeVgrow", javafx.scene.layout.GridPane.class)); // NOCHECK
            addHiddenProperty(new PropertyName("ownerWindow")); // NOCHECK
            addHiddenProperty(new PropertyName("ownerNode")); // NOCHECK
            addHiddenProperty(new PropertyName("pageFactory")); // NOCHECK
            addHiddenProperty(new PropertyName("paragraphs")); // NOCHECK
            addHiddenProperty(new PropertyName("parent")); // NOCHECK
            addHiddenProperty(new PropertyName("parentColumn")); // NOCHECK
            addHiddenProperty(new PropertyName("parentMenu")); // NOCHECK
            addHiddenProperty(new PropertyName("parentPopup")); // NOCHECK
            addHiddenProperty(new PropertyName("pressed")); // NOCHECK
            addHiddenProperty(new PropertyName("properties")); // NOCHECK
            addHiddenProperty(new PropertyName("pseudoClassStates")); // NOCHECK
            addHiddenProperty(new PropertyName("redoable")); // NOCHECK
            addHiddenProperty(new PropertyName("root")); // NOCHECK
            addHiddenProperty(new PropertyName("rowFactory")); // NOCHECK
            addHiddenProperty(new PropertyName("scene")); // NOCHECK
            addHiddenProperty(new PropertyName("selection")); // NOCHECK
            addHiddenProperty(new PropertyName("selectionModel")); // NOCHECK
            addHiddenProperty(new PropertyName("selectedText")); // NOCHECK
            addHiddenProperty(new PropertyName("showing")); // NOCHECK
            addHiddenProperty(new PropertyName("sortPolicy")); // NOCHECK
            addHiddenProperty(new PropertyName("skin")); // NOCHECK
            addHiddenProperty(new PropertyName("strokeDashArray")); // NOCHECK
            addHiddenProperty(new PropertyName("styleableParent")); // NOCHECK
            addHiddenProperty(new PropertyName("tableView")); // NOCHECK
            addHiddenProperty(new PropertyName("tabPane")); // NOCHECK
            addHiddenProperty(new PropertyName("transforms")); // NOCHECK
            addHiddenProperty(new PropertyName("treeTableView")); // NOCHECK
            addHiddenProperty(new PropertyName("typeInternal")); // NOCHECK
            addHiddenProperty(new PropertyName("typeSelector")); // NOCHECK
            addHiddenProperty(new PropertyName("undoable")); // NOCHECK
            addHiddenProperty(new PropertyName("userData")); // NOCHECK
            addHiddenProperty(new PropertyName("useSystemMenuBar")); // NOCHECK
            addHiddenProperty(new PropertyName("valueChanging")); // NOCHECK
            addHiddenProperty(new PropertyName("valueConverter")); // NOCHECK
            addHiddenProperty(new PropertyName("valueFactory")); // NOCHECK
            addHiddenProperty(new PropertyName("visibleLeafColumns")); // NOCHECK
        }

        protected void addHiddenProperty(PropertyName propertyName) {
            hiddenProperties.add(propertyName);
        }

        public Set<PropertyName> getHiddenProperties() {
            return hiddenProperties;
        }


        @Override
        public SbComponentClassMetadata<?> introspect(Class<?> componentClass) {

            // Let's find the first certified ancestor
            Class<?> ancestorClass = componentClass.getSuperclass();
            SbComponentClassMetadata<?> ancestorMetadata = null;
            while ((ancestorClass != null) && (ancestorMetadata == null)) {
                ancestorMetadata = queryComponentMetadata(ancestorClass);
                ancestorClass = ancestorClass.getSuperclass();
            }

            final Set<PropertyMetadata<?>> properties = new HashSet<>();
            final Set<PropertyName> hiddenProperties = getHiddenProperties();
            Exception exception;
            int counter = 0;

            try {

                final Object sample = instantiate(componentClass);
                final BeanInfo beanInfo = Introspector.getBeanInfo(componentClass);

                for (PropertyDescriptor d : beanInfo.getPropertyDescriptors()) {

                    final PropertyName name = new PropertyName(d.getName());

                    if (!hiddenProperties.contains(name)) {

                        PropertyMetadata<?> propertyMetadata = lookupPropertyMetadata(ancestorMetadata, name);

                        if (propertyMetadata == null) {
                            propertyMetadata = makePropertyMetadata(name, d, sample, counter);
                        }

                        if (propertyMetadata != null) {
                            properties.add(propertyMetadata);
                        }
                    }

                }
                exception = null;
            } catch (IOException | IntrospectionException x) {
                exception = x;
            }

            final ComponentClassMetadataCustomization componentCustomization = ComponentClassMetadataCustomization.builder()
                    .qualifier(ComponentClassMetadataCustomization.Qualifier.DEFAULT, ComponentClassMetadataCustomization.Qualifier.UNKNOWN)
                    .build();

            final CustomComponentClassMetadata<?> result = new CustomComponentClassMetadata<>(componentClass,
                    ancestorMetadata, componentCustomization, exception);

            result.getProperties().addAll(properties);

            return result;
        }

        /*
         * Private
         */

        private Object instantiate(Class<?> componentClass) throws IOException {
            final StringBuilder sb = new StringBuilder();
            Object result;

            /*
             * <?xml version="1.0" encoding="UTF-8"?> // NOCHECK
             *
             * <?import a.b.C?>
             *
             * <C/>
             */

            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); // NOI18N

            sb.append("<?import "); // NOI18N
            sb.append(componentClass.getCanonicalName());
            sb.append("?>"); // NOI18N
            sb.append("<"); // NOI18N
            sb.append(componentClass.getSimpleName());
            sb.append("/>\n"); // NOI18N

            final FXMLLoader fxmlLoader = new FXMLLoader();
            final String fxmlText = sb.toString();
            final byte[] fxmlBytes = fxmlText.getBytes(Charset.forName("UTF-8")); // NOI18N

            try {
                fxmlLoader.setClassLoader(componentClass.getClassLoader());
                result = fxmlLoader.load(new ByteArrayInputStream(fxmlBytes));
            } catch (RuntimeException x) {
                throw new IOException(x);
            }

            return result;
        }

        private PropertyMetadata<?> lookupPropertyMetadata(SbComponentClassMetadata<?> ccm, PropertyName propertyName) {
            PropertyMetadata<?> result = null;

            while ((ccm != null) && (result == null)) {
                result = ccm.lookupProperty(propertyName);
                ccm = ccm.getParentMetadata();
            }

            return result;
        }

        private PropertyMetadata<?> makePropertyMetadata(PropertyName name, PropertyDescriptor propertyDescriptor,
                Object sample, int counter) {
            PropertyMetadata<?> result;

            if (propertyDescriptor.getPropertyType() == null) {
                result = null;
            } else if (propertyDescriptor.getReadMethod() == null) {
                result = null;
            } else {
                final Class<?> propertyType = canonizeClass(propertyDescriptor.getPropertyType());
                final boolean readWrite = propertyDescriptor.getWriteMethod() != null;

                final InspectorPath inspectorPath = new InspectorPath(CUSTOM_SECTION, CUSTOM_SUB_SECTION, counter++);
                var propertyCustomization = new ValuePropertyMetadataCustomization.Builder().inspectorPath(inspectorPath).build();

                if (propertyType.isArray()) {
                    result = null;
                } else if (propertyType.isEnum()) {
                    final Object fallback = propertyType.getEnumConstants()[0];

                    // @formatter:off
                    result = new EnumerationPropertyMetadata.Builder<Enum<?>, ValuePropertyMetadataCustomization>((Class<Enum<?>>) propertyType)
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue((Enum<?>) getDefaultValue(sample, propertyDescriptor.getReadMethod(), fallback))
                            .customization(propertyCustomization)
                            .build();
                    // @formatter:on

                } else if (propertyType == Boolean.class) {
                    // @formatter:off
                    result = new BooleanPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue((Boolean) getDefaultValue(sample, propertyDescriptor.getReadMethod(), false))
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on

                } else if (propertyType == Integer.class) {

                 // @formatter:off
                    result = new IntegerPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue((Integer) getDefaultValue(sample, propertyDescriptor.getReadMethod(), 0))
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on

                } else if (propertyType == Double.class) {

                 // @formatter:off
                    result = new CoordinateDoublePropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue((Double) getDefaultValue(sample, propertyDescriptor.getReadMethod(), 0.0))
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on

                } else if (propertyType == String.class) {
                 // @formatter:off
                    result = new I18nStringPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue((String) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null))
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on
                } else if (propertyType == javafx.scene.paint.Color.class) {
                 // @formatter:off
                    result = new ColorPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue((Color) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null))
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on
                } else if (propertyType == javafx.scene.paint.Paint.class) {
                 // @formatter:off
                    result = new PaintPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue((Paint) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null))
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on
                } else if (propertyType == javafx.scene.text.Font.class) {
                 // @formatter:off
                    result = new FontPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue((Font) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null))
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on
                } else if (propertyType == javafx.scene.image.Image.class) {
                 // @formatter:off
                    result = new ImagePropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue(null)
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on
                } else if (propertyType == javafx.util.Duration.class) {
                 // @formatter:off
                    Duration defaultValue = (Duration) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null);
                    result = new DurationPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue(defaultValue == null ? null : new SBDuration(defaultValue))
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on
                } else if (propertyType == javafx.event.EventHandler.class) {
                 // @formatter:off
                    result = new EventHandlerPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                            .name(name)
                            .readWrite(readWrite)
                            .defaultValue(null)
                            .customization(propertyCustomization)
                            .build();
                 // @formatter:on

//                The following doesn't work because FXMLLoader is only prepared to load 'function' types
//                of type EventHandler
    //
//                } else if (propertyType == java.util.function.Function.class) {
//                    result = new FunctionalInterfacePropertyMetadata(
//                            name,
//                            readWrite,
//                            null,
//                            inspectorPath, FUNCTION);
                } else if (propertyType == javafx.collections.ObservableList.class) {
                    String propertyName = name.getName();
                    String methodName = "get" + propertyName.substring(0, 1).toUpperCase(Locale.ROOT)
                            + propertyName.substring(1); // NOI18N
                    result = null;
                    try {
                        Method method = sample.getClass().getMethod(methodName);
                        Type type = method.getGenericReturnType();
                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            Type genericType = parameterizedType.getActualTypeArguments()[0];
                            if (genericType instanceof Class) {
                                Class<?> genericClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                                if (genericClass.equals(java.lang.String.class)) {
                                 // @formatter:off
                                    result = new StringListPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
                                            .name(name)
                                            .readWrite(readWrite)
                                            .defaultValue(Collections.emptyList())
                                            .customization(propertyCustomization)
                                            .build();
                                 // @formatter:on
                                }
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else {
                    result = null;
                }
            }

            return result;
        }

        private Class<?> canonizeClass(Class<?> c) {
            final Class<?> result;

            if (c.equals(boolean.class)) {
                result = Boolean.class;
            } else if (c.equals(double.class)) {
                result = Double.class;
            } else if (c.equals(int.class)) {
                result = Integer.class;
            } else {
                result = c;
            }

            return result;
        }

        private Object getDefaultValue(Object sample, Method readMethod, Object fallback) {
            Object result;

            try {
                result = readMethod.invoke(sample);
            } catch (InvocationTargetException | IllegalAccessException x) {
                result = fallback;
            }

            return result;
        }

    }
}
