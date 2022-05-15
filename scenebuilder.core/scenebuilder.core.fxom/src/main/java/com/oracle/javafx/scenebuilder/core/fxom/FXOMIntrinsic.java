/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.fxom;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.JavaLanguage;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

/**
 * FXOM for special elements like includes or references.
 *
 */
public abstract class FXOMIntrinsic extends FXOMElement {

    public final static PropertyName GENERIC_DEFAULT_PROPERTY = new PropertyName(GlueElement.IGNORED_PREFIX + ":GENERIC_DEFAULT_PROPERTY");

    private static final String CHARSET_PROPERTY = "charset";
    private static final String SOURCE_PROPERTY = "source";

    public enum Type {
        FX_INCLUDE,
        FX_REFERENCE,
        FX_COPY,
        UNDEFINED
    }

    //private final Map<PropertyName, FXOMProperty> properties = new LinkedHashMap<>();
    private Object sourceSceneGraphObject;


    FXOMIntrinsic(FXOMDocument document, GlueElement glueElement, Object targetSceneGraphObject,  List<FXOMProperty> properties) {
        super(document, glueElement, null);
        setSourceSceneGraphObject(targetSceneGraphObject);
        for (FXOMProperty p : properties) {
            p.setParentInstance(this);
            addProperty(p);
        }
    }

    public FXOMIntrinsic(FXOMDocument document, Type type) {
        super(document, makeTagNameFromType(type));
    }

    public FXOMIntrinsic(FXOMDocument document, Type type, String source) {
        super(document, makeTagNameFromType(type));
        getGlueElement().getAttributes().put(SOURCE_PROPERTY, source);
    }

    public void addIntrinsicProperty(FXOMDocument fxomDocument) {
        final Map<String, String> attributes = this.getGlueElement().getAttributes();
        if(attributes.containsKey(CHARSET_PROPERTY)) {
            createAndInsertProperty(attributes, fxomDocument, CHARSET_PROPERTY);
        }
        if(attributes.containsKey(SOURCE_PROPERTY)) {
            createAndInsertProperty(attributes, fxomDocument, SOURCE_PROPERTY);
        }
    }

    private void createAndInsertProperty(Map<String, String> attributes, FXOMDocument fxomDocument, String propertyKey) {
        final String valueString = attributes.get(propertyKey);
        PropertyName propertyName = new PropertyName(propertyKey);
        FXOMProperty property = new FXOMPropertyT(fxomDocument, propertyName, valueString);
        this.getProperties().put(propertyName, property);
    }

    public void removeCharsetProperty() {
        final Map<String, String> attributes = this.getGlueElement().getAttributes();
        if(attributes.containsKey(CHARSET_PROPERTY)) {
            attributes.remove(CHARSET_PROPERTY);
            PropertyName charsetPropertyName = new PropertyName(CHARSET_PROPERTY);
            this.getProperties().remove(charsetPropertyName);
        }
    }

    public Type getType() {
        final Type result;

        switch(getGlueElement().getTagName()) {
            case "fx:include":
                result = Type.FX_INCLUDE;
                break;
            case "fx:reference":
                result = Type.FX_REFERENCE;
                break;
            case "fx:copy":
                result = Type.FX_COPY;
                break;
            default:
                result = Type.UNDEFINED;
                break;
        }

        return result;
    }

    public String getSource() {
        return getGlueElement().getAttributes().get(SOURCE_PROPERTY);
    }

    public void setSource(String source) {
        if (source == null) {
            getGlueElement().getAttributes().remove(SOURCE_PROPERTY);
        } else {
            getGlueElement().getAttributes().put(SOURCE_PROPERTY, source);
        }
    }

    public Object getSourceSceneGraphObject() {
        return sourceSceneGraphObject;
    }

    public void setSourceSceneGraphObject(Object sourceSceneGraphObject) {
        this.sourceSceneGraphObject = sourceSceneGraphObject;
        setDeclaredClass(sourceSceneGraphObject == null ? null : sourceSceneGraphObject.getClass());
    }

    public FXOMInstance createFxomInstanceFromIntrinsic() {
        FXOMInstance fxomInstance = new FXOMInstance(this.getFxomDocument(), this.getGlueElement());
        fxomInstance.setSceneGraphObject(this.getSourceSceneGraphObject());
        fxomInstance.setDeclaredClass(this.getClass());
        if(!this.getProperties().isEmpty()) {
            fxomInstance.fillProperties(this.getProperties());
        }
        return fxomInstance;
    }

    /*
     * FXOMObject
     */

    @Override
    public List<FXOMObject> getChildObjects() {
        final List<FXOMObject> result = new ArrayList<>();

        for (FXOMProperty p : getProperties().values()) {
            result.addAll(p.getChildren());
        }
        return result;
    }


    @Override
    public FXOMObject searchWithSceneGraphObject(Object sceneGraphObject) {
        FXOMObject result;

        result = super.searchWithSceneGraphObject(sceneGraphObject);
        if (result == null) {
            final Iterator<FXOMProperty> it = getProperties().values().iterator();
            while ((result == null) && it.hasNext()) {
                final FXOMProperty property = it.next();
                final Iterator<FXOMObject> itValue = property.getChildren().iterator();
                while ((result == null) && itValue.hasNext()) {
                    final FXOMObject value = itValue.next();
                    result = value.searchWithSceneGraphObject(sceneGraphObject);
                }
            }
        }

        return result;
    }

    @Override
    public FXOMObject searchWithFxId(String fxId) {
        FXOMObject result;

        result = super.searchWithFxId(fxId);
        if (result == null) {
            final Iterator<FXOMProperty> it = getProperties().values().iterator();
            while ((result == null) && it.hasNext()) {
                final FXOMProperty property = it.next();
                final Iterator<FXOMObject> itValue = property.getChildren().iterator();
                while ((result == null) && itValue.hasNext()) {
                    final FXOMObject value = itValue.next();
                    result = value.searchWithFxId(fxId);
                }
            }
        }

        return result;
    }

    @Override
    protected void collectDeclaredClasses(Set<Class<?>> result) {
        assert result != null;

        for (FXOMProperty p : getProperties().values()) {
            for (FXOMObject v : p.getChildren()) {
                v.collectDeclaredClasses(result);
            }
        }

    }

    @Override
    protected void collectProperties(PropertyName propertyName, List<FXOMProperty> result) {
        assert propertyName != null;
        assert result != null;

        for (FXOMProperty p : getProperties().values()) {
            if (p.getName().equals(propertyName)) {
                result.add(p);
            }
            for (FXOMObject v : p.getChildren()) {
                v.collectProperties(propertyName, result);
            }
        }
    }

    @Override
    protected void collectNullProperties(List<FXOMPropertyT> result) {
        assert result != null;

        for (FXOMProperty p : getProperties().values()) {
            if (p instanceof FXOMPropertyT) {
                final FXOMPropertyT tp = (FXOMPropertyT) p;
                if (tp.getValue().equals("$null")) {
                    result.add(tp);
                }
            }
            for (FXOMObject v : p.getChildren()) {
                v.collectNullProperties(result);
            }
        }
    }

    @Override
    protected void collectPropertiesT(List<FXOMPropertyT> result) {
        assert result != null;

        for (FXOMProperty p : getProperties().values()) {
            if (p instanceof FXOMPropertyT) {
                final FXOMPropertyT tp = (FXOMPropertyT) p;
                result.add(tp);
            }
            for (FXOMObject v : p.getChildren()) {
                v.collectPropertiesT(result);
            }
        }
    }



    @Override
    protected void collectReferences(String source, List<FXOMIntrinsic> result) {
        assert result != null;

        if ((getType() == Type.FX_REFERENCE) && ((source == null) || source.equals(getSource()))) {
            result.add(this);
        }

        for (FXOMProperty p : getProperties().values()) {
            for (FXOMObject v : p.getChildren()) {
                v.collectReferences(source, result);
            }
        }
    }

    @Override
    protected void collectReferences(String source, FXOMObject scope, List<FXOMNode> result) {
        assert result != null;

        if ((scope == null) || (scope != this)) {
            if ((getType() == Type.FX_REFERENCE)
                    && ((source == null) || source.equals(getSource()))) {
                result.add(this);
            }

            for (FXOMProperty p : getProperties().values()) {
                for (FXOMObject v : p.getChildren()) {
                    v.collectReferences(source, scope, result);
                }
                if (p instanceof FXOMPropertyT) {
                    final FXOMPropertyT pt = (FXOMPropertyT) p;
                    final PrefixedValue pv = new PrefixedValue(pt.getValue());
                    if (pv.isExpression()) {
                        final String suffix = pv.getSuffix();
                        if (JavaLanguage.isIdentifier(suffix)) {
                            if ((source == null) || source.equals(suffix)) {
                                result.add(pt);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void collectIncludes(String source, List<FXOMIntrinsic> result) {
        assert result != null;

        if ((getType() == Type.FX_INCLUDE)
                && ((source == null) || source.equals(getSource()))) {
            result.add(this);
        }

        for (FXOMProperty p : getProperties().values()) {
            for (FXOMObject v : p.getChildren()) {
                v.collectIncludes(source, result);
            }
        }
    }

    @Override
    protected void collectFxIds(Map<String, FXOMObject> result) {
        final String fxId = getFxId();
        if (fxId != null) {
            result.put(fxId, this);
        }

        for (FXOMProperty p : getProperties().values()) {
            for (FXOMObject v : p.getChildren()) {
                v.collectFxIds(result);
            }
        }
    }

    @Override
    protected void collectObjectWithSceneGraphObjectClass(Class<?> sceneGraphObjectClass, List<FXOMObject> result) {
        if (getSceneGraphObject() != null) {
            if (getSceneGraphObject().getClass() == sceneGraphObjectClass) {
                result.add(this);
            }
            for (FXOMProperty p : getProperties().values()) {
                for (FXOMObject v : p.getChildren()) {
                    v.collectObjectWithSceneGraphObjectClass(sceneGraphObjectClass, result);
                }
            }
        }
    }

    @Override
    protected void collectEventHandlers(List<FXOMPropertyT> result) {
        //if (getSceneGraphObject() != null) {
            for (FXOMProperty p : getProperties().values()) {
                if (p instanceof FXOMPropertyT) {
                    final FXOMPropertyT pt = (FXOMPropertyT) p;
                    if (pt.getName().getName().startsWith("on") && pt.getValue().startsWith("#")) {
                        result.add(pt);
                    }
                }
                for (FXOMObject v : p.getChildren()) {
                    v.collectEventHandlers(result);
                }
            }
        //}
    }

    @Override
    protected void collectScripts(String source, List<FXOMScript> result) {
        for (FXOMProperty p : getProperties().values()) {
            for (FXOMObject v : p.getChildren()) {
                v.collectScripts(source, result);
            }
        }
    }

    @Override
    protected void collectComments(List<FXOMComment> result) {
        for (FXOMProperty p : getProperties().values()) {
            for (FXOMObject v : p.getChildren()) {
                v.collectComments(result);
            }
        }
    }

    /*
     * FXOMNode
     */

    @Override
    protected void changeFxomDocument(FXOMDocument destination) {

        super.changeFxomDocument(destination);
        for (FXOMProperty p : getProperties().values()) {
            p.changeFxomDocument(destination);
        }
    }

    @Override
    public void documentLocationWillChange(URL newLocation) {
        for (FXOMProperty p : getProperties().values()) {
            p.documentLocationWillChange(newLocation);
        }
    }



    /*
     * Private
     */

    private static String makeTagNameFromType(Type type) {
        final String result;

        switch(type) {
            case FX_COPY:
                result = "fx:copy";
                break;
            case FX_REFERENCE:
                result = "fx:reference";
                break;
            case FX_INCLUDE:
                result = "fx:include";
                break;
            default:
                assert false;
                throw new IllegalStateException("Unexpected intrinsic type " + type);
        }

        return result;
    }

    @Override
    public Class<?> getMetadataClass() {
        return this.getClass();
    }

}
