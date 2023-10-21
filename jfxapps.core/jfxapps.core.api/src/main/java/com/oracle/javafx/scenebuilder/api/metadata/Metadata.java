package com.oracle.javafx.scenebuilder.api.metadata;

import java.util.Collection;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

public interface Metadata {

    ComponentClassMetadata<?> queryComponentMetadata(Class<?> componentClass);

    Set<PropertyMetadata> queryProperties(Class<?> componentClass);

    Set<PropertyMetadata> queryProperties(Collection<Class<?>> componentClasses);

    Set<ComponentPropertyMetadata> queryComponentProperties(Class<?> componentClass);

    ComponentPropertyMetadata queryComponentProperty(Class<?> componentClass, PropertyName name);

    Set<ValuePropertyMetadata> queryValueProperties(Set<Class<?>> componentClasses);

    PropertyMetadata queryProperty(Class<?> componentClass, PropertyName targetName);

    ValuePropertyMetadata queryValueProperty(FXOMElement fxomInstance, PropertyName targetName);

    Collection<ComponentClassMetadata<?>> getComponentClasses();

    Set<PropertyName> getHiddenProperties();

    /**
     * During prune properties job a property is trimmed
     * if the property is static
     * if the property is transient (has a meaning in the current parent only)
     * @param name
     * @return
     */
    boolean isPropertyTrimmingNeeded(PropertyName name);

    ComponentClassMetadata<?> queryComponentMetadata(Class<?> clazz, PropertyName propName);

}