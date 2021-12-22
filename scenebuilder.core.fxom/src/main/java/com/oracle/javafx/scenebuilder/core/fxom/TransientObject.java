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

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

import javafx.beans.DefaultProperty;

/**
 *
 *
 */
class TransientObject extends TransientNode {

    private final Class<?> declaredClass;
    private final String unknownClassName;
    private final List<FXOMProperty> properties = new ArrayList<>();
    private final List<FXOMObject> collectedItems = new ArrayList<>();
    private String fxRootType;

    public TransientObject(
            TransientNode parentNode,
            Class<?> declaredClass,
            GlueElement glueElement) {
        super(parentNode, glueElement);

        assert declaredClass != null;
        assert glueElement != null;
        assert glueElement.getTagName().equals(PropertyName.makeClassFullName(declaredClass)) ||
                glueElement.getTagName().equals(declaredClass.getCanonicalName());

        this.declaredClass = declaredClass;
        this.unknownClassName = null;
    }

    public TransientObject(
            TransientNode parentNode,
            String unknownClassName,
            GlueElement glueElement) {
        super(parentNode, glueElement);

        assert unknownClassName != null;
        assert glueElement != null;
        assert glueElement.getTagName().equals(unknownClassName);

        this.declaredClass = null;
        this.unknownClassName = unknownClassName;
    }

    public TransientObject(
            TransientNode parentNode,
            GlueElement glueElement) {
        super(parentNode, glueElement);

        assert glueElement != null;
        assert glueElement.getTagName().startsWith("fx:");

        this.declaredClass = null;
        this.unknownClassName = null;
    }

    public List<FXOMProperty> getProperties() {
        return properties;
    }

    public List<FXOMObject> getCollectedItems() {
        return collectedItems;
    }

    public void setFxRootType(String fxRootType) {
        this.fxRootType = fxRootType;
    }


    public FXOMObject makeFxomObject(FXOMDocument fxomDocument) {
        final FXOMObject result;

        if (declaredClass != null) {
            assert getSceneGraphObject() != null;

            if (getSceneGraphObject() instanceof List) {
                assert properties.isEmpty();

                result = new FXOMCollection(fxomDocument, getGlueElement(),
                                          declaredClass, getSceneGraphObject(),
                                          collectedItems);
            } else {
                assert fxRootType == null;

                addDefaultProperty(fxomDocument, declaredClass);
                result = new FXOMInstance(fxomDocument, getGlueElement(),
                                          declaredClass, getSceneGraphObject(),
                                          properties);
            }
        } else if (unknownClassName != null) {
            // This is an unresolved instance
            assert getGlueElement().getTagName().equals(unknownClassName);
            assert fxRootType == null;
            result = new FXOMInstance(fxomDocument, getGlueElement(), properties);
        } else {
            // This is an fx:root'ed instance
            assert getGlueElement().getTagName().equals("fx:root");
            assert fxRootType != null;

            final Class<?> rootClass = getSceneGraphObject().getClass();
            assert fxRootType.equals(rootClass.getName())
                    || fxRootType.equals(rootClass.getSimpleName());
            addDefaultProperty(fxomDocument, rootClass);
            result = new FXOMInstance(fxomDocument, getGlueElement(),
                                      rootClass, getSceneGraphObject(),
                                      properties);
        }

        if (result instanceof FXOMInstance) {
            final Class<?> declaredClassFromResult = ((FXOMInstance) result).getDeclaredClass();
            if (fxomDocument.getInitialDeclaredClasses() != null &&
                    ! fxomDocument.getInitialDeclaredClasses().contains(declaredClassFromResult)) {
                fxomDocument.getInitialDeclaredClasses().add(declaredClassFromResult);
            }
        }
        return result;
    }


    /*
     * Private
     */

    private void addDefaultProperty(FXOMDocument fxomDocument, Class<?> klass) {
        final DefaultProperty annotation = klass.getAnnotation(DefaultProperty.class);
        if ((annotation != null) && (collectedItems.size() >= 1)) {
            assert annotation.value() != null;
            final PropertyName defaultPropertyName = new PropertyName(annotation.value());
            createDefaultProperty(defaultPropertyName, fxomDocument);
        }
    }

    private void createDefaultProperty(PropertyName defaultName, FXOMDocument fxomDocument) {
        /*
         * From :
         *
         *  <Pane>                          this.glueElement
         *      ...
         *      <Button text="B1" />        this.collectedItems.get(0).glueElement   //NOCHECK
         *      <TextField />               this.collectedItems.get(1).glueElement
         *      <Label text="Label" />      this.collectedItems.get(2).glueElement   //NOCHECK
         *      ...
         *  </Pane>
         *
         * go to:
         *
         *  <Pane>                          this.glueElement
         *      ...
         *      <children>                  syntheticElement
         *          <Button text="B1" />    this.collectedItems.get(0).glueElement   //NOCHECK
         *          <TextField />           this.collectedItems.get(1).glueElement
         *          <Label text="Label" />  this.collectedItems.get(2).glueElement   //NOCHECK
         *      </children>
         *      ...
         *  </Pane>
         *
         */

        final GlueElement propertyElement
                = new GlueElement(getGlueElement().getDocument(),
                        defaultName.toString(),  getGlueElement());
        propertyElement.setSynthetic(true);
        propertyElement.addBefore(collectedItems.get(0).getGlueElement());

        for (FXOMObject item : collectedItems) {
            item.getGlueElement().addToParent(propertyElement);
        }

        final TransientProperty transientProperty
                = new TransientProperty(this, defaultName, propertyElement);
        transientProperty.getValues().addAll(collectedItems);

        properties.add(transientProperty.makeFxomProperty(fxomDocument));
    }
}
