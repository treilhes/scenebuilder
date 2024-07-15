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
package com.gluonhq.jfxapps.core.fxom;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.gluonhq.jfxapps.core.fxom.collector.FXOMCollector;
import com.gluonhq.jfxapps.core.fxom.glue.GlueElement;

/**
 *
 *
 */
public class FXOMCollection extends FXOMObject {

    private final List<FXOMObject> items = new ArrayList<>();
    private Class<?> declaredClass;

    FXOMCollection(
            FXOMDocument fxomDocument,
            GlueElement glueElement,
            Class<?> declaredClass,
            Object sceneGraphObject,
            List<FXOMObject> items) {
        super(fxomDocument, glueElement, sceneGraphObject);

        assert (declaredClass != null);
        assert (declaredClass.getSimpleName().equals(glueElement.getTagName()));
        assert sceneGraphObject instanceof Collection;
        assert items != null;

        this.declaredClass = declaredClass;
        for (FXOMObject i : items) {
            this.items.add(i);
            i.setParentCollection(this);
        }
    }


    FXOMCollection(
            FXOMDocument fxomDocument,
            Class<?> declaredClass) {
        super(fxomDocument, declaredClass.getSimpleName());
        this.declaredClass = declaredClass;
    }

    public Class<?> getDeclaredClass() {
        return declaredClass;
    }

    public void setDeclaredClass(Class<?> declaredClass) {
        this.declaredClass = declaredClass;
    }

    public List<FXOMObject> getItems() {
        return Collections.unmodifiableList(items);
    }

    /*
     * FXOMObject
     */

    @Override
    public List<FXOMObject> getChildObjects() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public <T> T collect(FXOMCollector<T> collector) {

        if (collector.accept(this)) {
            if (collector.needCollectObject()) {
                collector.collect(this);
            }

            if (collector.endCollection()) {
                return collector.getCollected();
            }

            for (FXOMObject i : items) {
                if (i.getParentProperty() == null || collector.accept(i.getParentProperty())){
                    i.collect(collector);

                    if (collector.endCollection()) {
                        break;
                    }
                }
            }
        }

        return collector.getCollected();
    }

    /*
     * FXOMNode
     */

    @Override
    protected void changeFxomDocument(FXOMDocument destination) {

        super.changeFxomDocument(destination);
        for (FXOMObject i : items) {
            i.changeFxomDocument(destination);
        }
    }

    @Override
    public void documentLocationWillChange(URL newLocation) {
        for (FXOMObject i : items) {
            i.documentLocationWillChange(newLocation);
        }
    }


    /*
     * Package
     */

    /* Reserved to FXOMObject.addToParentCollection() private use */
    void addValue(int index, FXOMObject item) {
        assert item != null;
        assert item.getParentCollection() == this;
        assert items.contains(item) == false;
        if (index == -1) {
            items.add(item);
        } else {
            items.add(index, item);
        }
    }

    /* Reserved to FXOMObject.removeFromParentCollection() private use */
    void removeValue(FXOMObject item) {
        assert item != null;
        assert item.getParentProperty() == null;
        assert items.contains(item);
        items.remove(item);
    }

    @Override
    public Class<?> getMetadataClass() {
        return getSceneGraphObject() == null ? null : getSceneGraphObject().getClass();
    }

}
