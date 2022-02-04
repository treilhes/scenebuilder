package com.oracle.javafx.scenebuilder.core.fxom;

import java.util.Map;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.IndexedHashMap;
import com.oracle.javafx.scenebuilder.core.fxom.util.IndexedMap;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

public abstract class FXOMElement extends FXOMObject {

    private final IndexedMap<PropertyName, FXOMProperty> properties = new IndexedHashMap<>();
    private Class<?> declaredClass;

    public FXOMElement(FXOMDocument fxomDocument, GlueElement glueElement, Object sceneGraphObject) {
        super(fxomDocument, glueElement, sceneGraphObject);
    }

    public FXOMElement(FXOMDocument fxomDocument, String tagName) {
        super(fxomDocument, tagName);
    }

    public IndexedMap<PropertyName, FXOMProperty> getProperties() {
        //return Collections.unmodifiableMap(properties);
        return properties;
    }

    /* For FXOMProperty.addToParentInstance() private use only */
    void addProperty(FXOMProperty property) {
        assert property.getParentInstance() == this;
//        assert property instanceof FXOMPropertyC ||
//            (property instanceof FXOMPropertyT && properties.get(property.getName()) == null);
        assert properties.get(property.getName()) == null;
        properties.put(property.getName(), property);
    }

    /* For FXOMProperty.removeFromParentInstance() private use only */
    void removeProperty(FXOMProperty property) {
        assert property.getParentInstance() == null;
        assert properties.get(property.getName()) == property;
        properties.remove(property.getName());

    }

    public void fillProperties(Map<PropertyName, FXOMProperty> properties ) {
        for (FXOMProperty p : properties.values()) {
            this.properties.put(p.getName(), p);
            p.setParentInstance(this);
        }
    }


    public Class<?> getDeclaredClass() {
        return declaredClass;
    }

    public void setDeclaredClass(Class<?> declaredClass) {
        this.declaredClass = declaredClass;
    }

}
