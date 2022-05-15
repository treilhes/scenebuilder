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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.JavaLanguage;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

public class FXOMDefine extends FXOMVirtual {

    public final static PropertyName defineContentName = new PropertyName(GlueElement.IGNORED_PREFIX + ":content");

    private final List<FXOMObject> items = new ArrayList<>();

    FXOMDefine(FXOMDocument fxomDocument, GlueElement glueElement, List<FXOMObject> children) {
        super(fxomDocument, glueElement, null);

        if (children != null && !children.isEmpty()) {
            FXOMPropertyC contentProperty = new FXOMPropertyC(fxomDocument, defineContentName, children);
            contentProperty.setParentInstance(this);
            addProperty(contentProperty);
            contentProperty.getPropertyElement().addToParent(glueElement);
        }

    }

    public FXOMDefine(FXOMDocument document) {
        super(document, "fx:define");
    }

    public List<FXOMObject> getItems() {
        return Collections.unmodifiableList(items);
    }

//    /* Reserved to FXOMObject.addToParentCollection() private use */
//    void addValue(int index, FXOMObject item) {
//        assert item != null;
//        assert item.getParentDefine() == this;
//        assert items.contains(item) == false;
//        if (index == -1) {
//            items.add(item);
//        } else {
//            items.add(index, item);
//        }
//    }
//
//    /* Reserved to FXOMObject.removeFromParentCollection() private use */
//    void removeValue(FXOMObject item) {
//        assert item != null;
//        assert item.getParentProperty() == null;
//        assert items.contains(item);
//        items.remove(item);
//    }

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

        if (getDeclaredClass() != null && getDeclaredClass() != Object.class) {
            result.add(getDeclaredClass());
        }

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
        for (FXOMProperty p : getProperties().values()) {
            for (FXOMObject v : p.getChildren()) {
                v.collectReferences(source, result);
            }
        }
    }

    @Override
    protected void collectReferences(String source, FXOMObject scope, List<FXOMNode> result) {
        if ((scope == null) || (scope != this)) {
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
