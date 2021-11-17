package com.oracle.javafx.scenebuilder.metadata.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.oracle.javafx.scenebuilder.metadata.bean.PropertyMetaData;

public class Property implements Comparable<Property>{
    public enum Type {
        VALUE,
        COMPONENT
    }

    //common
    private Type type;
    private final PropertyMetaData raw;
    private final Map<String, Object> custom = new HashMap<>();

    public Property(PropertyMetaData raw, Type type) {
        super();
        this.raw = raw;
        this.type = type;
    }

    public PropertyMetaData getRaw() {
        return raw;
    }

    public Map<String, Object> getCustom() {
        return custom;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int compareTo(Property o) {
        Comparator<Property> comparator = Comparator.comparing((Property p) -> p.getRaw().getName())
                .thenComparing((Property p) -> p.getRaw().getGetterMethod().getDeclaringClass() == null ? "" : p.getRaw().getGetterMethod().getDeclaringClass().getName())
                .thenComparing(p -> p.getRaw().isStatic());
        return comparator.compare(this, o);
    }

}