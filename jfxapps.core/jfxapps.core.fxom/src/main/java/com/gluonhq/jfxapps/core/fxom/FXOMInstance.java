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
package com.gluonhq.jfxapps.core.fxom;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.gluonhq.jfxapps.core.fxom.glue.GlueElement;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

import javafx.fxml.FXMLLoader;

/**
 *
 *
 */
public class FXOMInstance extends FXOMElement {

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
            result.addAll(p.getChildren());
        }
        return result;
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
    public Class<?> getMetadataClass() {
        if (getDeclaredClass() != null) {
            return getDeclaredClass();
        }
        return getSceneGraphObject() == null ? null : getSceneGraphObject().getClass();
    }
}
