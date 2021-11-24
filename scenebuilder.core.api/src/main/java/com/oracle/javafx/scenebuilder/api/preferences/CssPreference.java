package com.oracle.javafx.scenebuilder.api.preferences;

import java.util.ArrayList;
import java.util.List;

public interface CssPreference<T> extends Preference<T> {

    List<CssClass> getClasses();

    public static class CssClass {
        private final String className;
        private final List<CssProperty> properties = new ArrayList<>();

        public CssClass(String className) {
            this.className = className;
        }

        public boolean add(CssProperty e) {
            return properties.add(e);
        }

        public String getClassName() {
            return className;
        }

        public List<CssProperty> getProperties() {
            return properties;
        }
    }

    public static class CssProperty {
        private final String propertyName;
        private final String propertyValue;

        public CssProperty(String propertyName, String propertyValue) {
            super();
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getPropertyValue() {
            return propertyValue;
        }
    }
}
