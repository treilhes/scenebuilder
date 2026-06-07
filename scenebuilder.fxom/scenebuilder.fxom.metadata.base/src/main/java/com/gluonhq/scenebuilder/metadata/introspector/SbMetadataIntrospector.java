package com.gluonhq.scenebuilder.metadata.introspector;

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
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.oracle.javafx.scenebuilder.metadata.custom.ComponentClassMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.CustomComponentClassMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.SbComponentClassMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization.InspectorPath;
import com.treilhes.emc4j.boot.api.context.annotation.Lazy;
import com.treilhes.jfxplace.core.metadata.AbstractMetadata;
import com.treilhes.jfxplace.core.metadata.property.PropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.BooleanPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.DurationPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.EnumerationPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.EventHandlerPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.FontPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.ImagePropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.IntegerPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.list.StringListPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.paint.ColorPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.paint.PaintPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.util.SBDuration;
import com.treilhes.jfxplace.fxom.model.util.PropertyName;

import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class SbMetadataIntrospector implements AbstractMetadata.MetadataIntrospector<SbComponentClassMetadata<?>>  {

    /**
     * During data introspection of an unknown custom component, if the name match,
     * the property will be ignored.
     */
    private final Set<PropertyName> hiddenProperties = new HashSet<>();
    private final SbMetadata metadata;

    public SbMetadataIntrospector(@Lazy SbMetadata metadata) {
        this.metadata = metadata;
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
            ancestorMetadata = metadata.queryComponentMetadata(ancestorClass);
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
        final byte[] fxmlBytes = fxmlText.getBytes(StandardCharsets.UTF_8);

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

//            The following doesn't work because FXMLLoader is only prepared to load 'function' types
//            of type EventHandler
//
//            } else if (propertyType == java.util.function.Function.class) {
//                result = new FunctionalInterfacePropertyMetadata(
//                        name,
//                        readWrite,
//                        null,
//                        inspectorPath, FUNCTION);
            } else if (propertyType == javafx.collections.ObservableList.class) {
                String propertyName = name.getName();
                String methodName = "get" + propertyName.substring(0, 1).toUpperCase(Locale.ROOT)
                        + propertyName.substring(1); // NOI18N
                result = null;
                try {
                    Method method = sample.getClass().getMethod(methodName);
                    Type type = method.getGenericReturnType();
                    if (type instanceof ParameterizedType parameterizedType) {
                        Type genericType = parameterizedType.getActualTypeArguments()[0];
                        if (genericType instanceof Class genericClass && genericClass.equals(java.lang.String.class)) {
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
