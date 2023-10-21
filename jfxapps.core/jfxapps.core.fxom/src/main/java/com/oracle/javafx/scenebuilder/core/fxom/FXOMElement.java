/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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

import java.util.Map;

import com.oracle.javafx.scenebuilder.core.fxom.collector.OMCollector;
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

    @Override
    public <T> T collect(OMCollector<T> collector) {

        if (collector.accept(this)) {
            if (collector.needCollectObject()) {
                collector.collect(this);
            }

            if (collector.needCollectProperty()) {
                for (FXOMProperty p : getProperties().values()) {
                    if (collector.accept(p)){
                        collector.collect(p);
                    }
                }
            }

            for (FXOMObject i : getChildObjects()) {
                if (i.getParentProperty() == null || collector.accept(i.getParentProperty())){
                    i.collect(collector);
                }
            }
        }


        return collector.getCollected();
    }
}
