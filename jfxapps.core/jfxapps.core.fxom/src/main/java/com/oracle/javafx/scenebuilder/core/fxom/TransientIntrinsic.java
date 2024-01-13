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

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

/**
 *
 *
 */
abstract class TransientIntrinsic extends TransientNode {

    private final FXOMIntrinsic.Type type;
    private final List<FXOMProperty> properties = new ArrayList<>();
    private final List<FXOMObject> collectedItems = new ArrayList<>();

    public TransientIntrinsic(
            TransientNode parentNode,
            FXOMIntrinsic.Type type,
            GlueElement glueElement) {
        super(parentNode, glueElement);
        this.type = type;
    }

    public abstract FXOMIntrinsic makeFxomIntrinsicInstance(FXOMDocument fxomDocument);

    public FXOMIntrinsic makeFxomIntrinsic(FXOMDocument fxomDocument) {
        if (getCollectedItems() != null && !getCollectedItems().isEmpty()) {
            createDefaultProperty(FXOMIntrinsic.GENERIC_DEFAULT_PROPERTY, fxomDocument);
        }

        final FXOMIntrinsic result = makeFxomIntrinsicInstance(fxomDocument);
        assert result.getType() == type;
        // need to deal with a charset property here
        result.addIntrinsicProperty(fxomDocument);
        return result;
    }

    public List<FXOMProperty> getProperties() {
        return properties;
    }

    public List<FXOMObject> getCollectedItems() {
        return collectedItems;
    }

    protected void createDefaultProperty(PropertyName defaultName, FXOMDocument fxomDocument) {
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