/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.JavaLanguage;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

import javafx.fxml.FXMLLoader;

/**
 *
 *
 */
public class FXOMInstance extends FXOMElement {


    //FIXME why not using this.getFxomDocument()
    private FXOMDocument fxomDocument;

    FXOMInstance(
            FXOMDocument fxomDocument,
            GlueElement glueElement,
            Class<?> declaredClass,
            Object sceneGraphObject,
            List<FXOMProperty> properties) {
        super(fxomDocument, glueElement, sceneGraphObject);

        assert declaredClass != null;
        assert glueElement.getTagName().equals("fx:root")
                || glueElement.getTagName().equals(PropertyName.makeClassFullName(declaredClass))
                || glueElement.getTagName().equals(declaredClass.getCanonicalName());
        assert sceneGraphObject != null;
        assert properties != null;

        this.fxomDocument = fxomDocument;
        setDeclaredClass(declaredClass);
        for (FXOMProperty p : properties) {
            p.setParentInstance(this);
            addProperty(p);
        }
    }

    FXOMInstance(
            FXOMDocument fxomDocument,
            GlueElement glueElement,
            List<FXOMProperty> properties) {
        super(fxomDocument, glueElement, null);

        assert properties != null;

        for (FXOMProperty p : properties) {
            p.setParentInstance(this);
            addProperty(p);
        }
    }

    public FXOMInstance(FXOMDocument fxomDocument, GlueElement glueElement) {
        this(fxomDocument, glueElement, Collections.emptyList());
    }

    public FXOMInstance(FXOMDocument fxomDocument, Class<?> declaredClass) {
        super(fxomDocument, PropertyName.makeClassFullName(declaredClass));
        setDeclaredClass(declaredClass);
    }

    public FXOMInstance(FXOMDocument fxomDocument, String tagName) {
        super(fxomDocument, tagName);// This is an unresolved instance
    }

    public boolean isFxRoot() {
        return getGlueElement().getTagName().equals("fx:root");
    }

    public void toggleFxRoot() {

        if (isFxRoot()) {
            assert getType() != null;
            getGlueElement().setTagName(getType());
            getGlueElement().getAttributes().remove(FXMLLoader.ROOT_TYPE_ATTRIBUTE);
        } else {
            assert getType() == null;
            getGlueElement().getAttributes().put(FXMLLoader.ROOT_TYPE_ATTRIBUTE, getGlueElement().getTagName());
            getGlueElement().setTagName("fx:root");
        }
    }

    public String getType() {
        return getGlueElement().getAttributes().get(FXMLLoader.ROOT_TYPE_ATTRIBUTE);
    }

    /*
     * FXOMObject
     */

    @Override
    public void addToParentCollection(int index, FXOMCollection newParentCollection) {
        super.addToParentCollection(index, newParentCollection);

        // May be this object was root : fx:root, type properties must be reset.
        resetRootProperties();
    }

    @Override
    public void addToParentProperty(int index, FXOMProperty newParentProperty) {
        super.addToParentProperty(index, newParentProperty); //To change body of generated methods, choose Tools | Templates.

        // May be this object was root : fx:root, type properties must be reset.
        resetRootProperties();
    }


    @Override
    public List<FXOMObject> getChildObjects() {
        final List<FXOMObject> result = new ArrayList<>();

        for (FXOMProperty p : getProperties().values()) {
//            if (p instanceof FXOMPropertyC) {
//                final FXOMPropertyC pc = (FXOMPropertyC) p;
//                result.addAll(pc.getChildren());
//            }
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
//                if (property instanceof FXOMPropertyC) {
//                    FXOMPropertyC propertyC = (FXOMPropertyC) property;
//                    final Iterator<FXOMObject> itValue = propertyC.getChildren().iterator();
//                    while ((result == null) && itValue.hasNext()) {
//                        final FXOMObject value = itValue.next();
//                        result = value.searchWithSceneGraphObject(sceneGraphObject);
//                    }
//                }

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
//                if (property instanceof FXOMPropertyC) {
//                    FXOMPropertyC propertyC = (FXOMPropertyC) property;
//                    final Iterator<FXOMObject> itValue = propertyC.getChildren().iterator();
//                    while ((result == null) && itValue.hasNext()) {
//                        final FXOMObject value = itValue.next();
//                        result = value.searchWithFxId(fxId);
//                    }
//                }
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

        if (getDeclaredClass() != null && getDeclaredClass() != Object.class) {
            result.add(getDeclaredClass());
        }

        for (FXOMProperty p : getProperties().values()) {
//            if (p instanceof FXOMPropertyC) {
//                for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                    v.collectDeclaredClasses(result);
//                }
//            } else if (p instanceof FXOMPropertyT) {
//                collectGlueElementPropertiesT(((FXOMPropertyT)p).getValueElement(), result);
//            }
            for (FXOMObject v : p.getChildren()) {
                v.collectDeclaredClasses(result);
            }
        }

    }

    private void collectGlueElementPropertiesT(GlueElement element, Set<Class<?>> result) {
        if (element == null) {
            return;
        }
        if (! element.getChildren().isEmpty()) {
            for (GlueElement e : element.getChildren()) {
                collectGlueElementPropertiesT(e, result);
            }
        } else {
            String clazz = element.getTagName();
            if (clazz != null) {
                for (Class<?> c : fxomDocument.getInitialDeclaredClasses()) {
                    if (c.getCanonicalName().equals(clazz) || c.getSimpleName().equals(clazz)) {
                        if (! result.contains(c)) {
                            result.add(c);
                        }
                        break;
                    }
                }
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
//            if (p instanceof FXOMPropertyC) {
//                for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                    v.collectProperties(propertyName, result);
//                }
//            }
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
//            else {
//                assert p instanceof FXOMPropertyC;
//                for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                    v.collectNullProperties(result);
//                }
//            }
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
//            else {
//                assert p instanceof FXOMPropertyC;
//                for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                    v.collectPropertiesT(result);
//                }
//            }
            for (FXOMObject v : p.getChildren()) {
                v.collectPropertiesT(result);
            }
        }
    }

    @Override
    protected void collectReferences(String source, List<FXOMIntrinsic> result) {
        for (FXOMProperty p : getProperties().values()) {
//            if (p instanceof FXOMPropertyC) {
//                for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                    v.collectReferences(source, result);
//                }
//            }
            for (FXOMObject v : p.getChildren()) {
                v.collectReferences(source, result);
            }
        }
    }

    @Override
    protected void collectReferences(String source, FXOMObject scope, List<FXOMNode> result) {
        if ((scope == null) || (scope != this)) {
            for (FXOMProperty p : getProperties().values()) {
//                if (p instanceof FXOMPropertyC) {
//                    for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                        v.collectReferences(source, scope, result);
//                    }
//                } else if (p instanceof FXOMPropertyT) {
//                    final FXOMPropertyT pt = (FXOMPropertyT) p;
//                    final PrefixedValue pv = new PrefixedValue(pt.getValue());
//                    if (pv.isExpression()) {
//                        final String suffix = pv.getSuffix();
//                        if (JavaLanguage.isIdentifier(suffix)) {
//                            if ((source == null) || source.equals(suffix)) {
//                                result.add(pt);
//                            }
//                        }
//                    }
//                }
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
        for (FXOMProperty p : getProperties().values()) {
//            if (p instanceof FXOMPropertyC) {
//                for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                    v.collectIncludes(source, result);
//                }
//            }
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
//            if (p instanceof FXOMPropertyC) {
//                for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                    v.collectFxIds(result);
//                }
//            }
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
//                if (p instanceof FXOMPropertyC) {
//                    for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                        v.collectObjectWithSceneGraphObjectClass(sceneGraphObjectClass, result);
//                    }
//                }
                for (FXOMObject v : p.getChildren()) {
                    v.collectObjectWithSceneGraphObjectClass(sceneGraphObjectClass, result);
                }
            }
        }
    }

    @Override
    protected void collectEventHandlers(List<FXOMPropertyT> result) {
        if (getSceneGraphObject() != null) {
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
//            for (FXOMProperty p : properties.values()) {
//                if (p instanceof FXOMPropertyC) {
//                    for (FXOMObject v : ((FXOMPropertyC)p).getChildren()) {
//                        v.collectEventHandlers(result);
//                    }
//                }
//            }
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
     * Package
     */



    /*
     * Private
     */

    private void resetRootProperties() {
        if (isFxRoot()) {
            toggleFxRoot();
        }
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
}
