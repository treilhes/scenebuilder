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
package com.gluonhq.jfxapps.core.fxom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.core.fxom.glue.GlueCharacters;
import com.gluonhq.jfxapps.core.fxom.glue.GlueElement;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

/**
 * Collects property data until the end of the property xml declaration then
 * will create an FXOMProperty instance according collected data.
 * <br/><br/>
 * Since the introduction of virtual elements {@link FXOMComment},{@link FXOMScript} and {@link FXOMDefine} we can distinguish<br/>
 * Four cases:<br/><br/>
 * values is an aggregate of 'reals' elements {@link FXOMInstance} and 'virtuals' elements {@link FXOMComment},{@link FXOMScript} and {@link FXOMDefine}<br/>
 * <br/><br/>
 * 1) (values.size() == 0)
 * <br/><br/>
 * => it's a textual property expressed as plain text<br/>
 * => we create an {@link FXOMPropertyT} instance<br/>
 * => for example with:<br/>
 * Button.text: &lt;Button>&lt;text>OK&lt;/text>&lt;/Button>
 * <br/><br/>
 *
 * 2) (reals.size() == 1) && (reals.get(0) instanceof FXOMInstance) && (reals.get(0).getFxValue() != null)
 * <br/><br/>
 * => it's a textual property expressed with fx:value <br/>
 * => it may contains virtual elements<br/>
 * => we create an {@link FXOMPropertyT} instance <br/>
 * => for example with Button.text:<br/>
 *
 * &lt;Button>&lt;text>&lt;String fx:value="OK"/>&lt;/text>&lt;/Button>
 *<br/><br/>
 * 2) (reals.size() == 0) && (virtuals.size() > 0)
 * <br/><br/>
 * => it's a textual property expressed as plain text and mixed with virtual elements<br/>
 * => we must find the text value in children fronts/tails and convert it as an fx:value to keep them ordered<br/>
 * => we create an {@link FXOMPropertyT} instance <br/>
 * => for example with Button.text:<br/>
 *
 * &lt;Button>&lt;text>&lt;-- xx -->OK&lt;fx:script />&lt;/text>&lt;/Button>
 *<br/><br/>
 *
 * 3) else
 *<br/><br/>
 * => it's a complex property <br/>
 * => we create an {@link FXOMPropertyC} instance<br/>
 * <br/><br/>
 *
 */
class TransientProperty extends TransientNode {

    private final PropertyName name;
    private final List<FXOMObject> values = new ArrayList<>();
    private final List<FXOMProperty> collectedProperties = new ArrayList<>();

    public TransientProperty(TransientNode parentNode, PropertyName name, GlueElement propertyElement) {
        super(parentNode, propertyElement);

        assert name != null;
        assert propertyElement != null;
        assert propertyElement.getTagName().equals(name.toString());

        this.name = name;

    }

    public List<FXOMObject> getValues() {
        return values;
    }

    public List<FXOMProperty> getCollectedProperties() {
        return collectedProperties;
    }

    public FXOMProperty makeFxomProperty(FXOMDocument fxomDocument) {
        final FXOMProperty result;
        final GlueElement propertyElement = getGlueElement();

        if (collectedProperties.isEmpty()) {

            Map<Boolean, List<FXOMObject>> partition = values.stream().collect(Collectors.partitioningBy(FXOMVirtual.class::isInstance));

            List<FXOMObject> virtuals = partition.get(true);
            List<FXOMObject> reals = partition.get(false);

            if (values.isEmpty()) {
                // Case #1
                assert propertyElement.getChildren().size() == virtuals.size();
                //assert propertyElement.getContent().isEmpty() == false;
                result = new FXOMPropertyT(fxomDocument, name, propertyElement, null, propertyElement.getContentText());

            } else if ((reals.size() == 1) && (reals.get(0) instanceof FXOMInstance)) {
                final FXOMInstance value = (FXOMInstance) reals.get(0);
                final String fxValue = value.getFxValue();
                if (fxValue != null) { // TODO what about fx:constant ?
                    // Case #2
                    result = new FXOMPropertyT(fxomDocument, name, propertyElement, values, fxValue);
                } else {
                    // Case #4
                    result = new FXOMPropertyC(fxomDocument, name, values, propertyElement);
                }
            } else if (reals.isEmpty() && !virtuals.isEmpty()) {
                // find the textual value : find non blank value in tails and fronts from bottom to top
                String value = createFxValue(fxomDocument, propertyElement, values, getSceneGraphObject());

                if (value != null) {
                    // Case #3
                    result = new FXOMPropertyT(fxomDocument, name, propertyElement, values, value);
                } else {
                    // Case #4
                    result = new FXOMPropertyC(fxomDocument, name, values, propertyElement);
                }
            } else {
                // Case #4
                result = new FXOMPropertyC(fxomDocument, name, values, propertyElement);
            }
        } else {
            // It is a property of type Map ; currently we don't support
            // map property editing ; so we create a fake value.
            assert getSceneGraphObject().isInstanceOf(Map.class);
            result = new FXOMPropertyT(fxomDocument, name, "fake-value");
        }

        return result;
    }

    private static String createFxValue(FXOMDocument fxomDocument, GlueElement propertyElement, List<FXOMObject> fxoms, Object sceneGraphObject) {
        int index = -1;
        String sceneValue = sceneGraphObject != null ? sceneGraphObject.toString() : "";
        String v = "";

        if (v.isBlank()) {
            v = propertyElement.getTail().stream()
                    .filter(GlueCharacters.class::isInstance)
                    .map(t -> ((GlueCharacters)t).getData())
                    .collect(Collectors.joining());//.trim();

            if (!v.isBlank()) {

                String[] parts = v.split(sceneValue);
                String newFront = parts[0];
                String newTail = parts.length > 1 ? parts[1] : "\n";
                v = v.trim();

                FXOMInstance fxomValue = new FXOMInstance(fxomDocument, Object.class);
                fxomValue.setFxValue(v);
                fxoms.add(fxomValue);
                fxomValue.getGlueElement().addToParent(propertyElement);

                fxomValue.getGlueElement().getFront().clear();
                fxomValue.getGlueElement().getFront().add(new GlueCharacters(propertyElement.getDocument(), newFront));

                propertyElement.getTail().clear();
                propertyElement.getTail().add(new GlueCharacters(propertyElement.getDocument(), newTail));
            }
        }

        List<FXOMObject> fxomsCopy = new ArrayList<>(fxoms);
        for (int i = fxomsCopy.size() - 1; i >= 0; i--) {
            FXOMObject fxom = fxomsCopy.get(i);

            if (v.isBlank()) {
                v = fxom.getGlueElement().getFront().stream()
                        .filter(GlueCharacters.class::isInstance)
                        .map(t -> ((GlueCharacters)t).getData())
                        .collect(Collectors.joining());//.trim();

                if (!v.isBlank()) {
                    index = i;

                    String[] parts = v.split(sceneValue);
                    String newFront = parts.length > 0 ? parts[0] : "";
                    String newTail = parts.length > 1 ? parts[1] : "";
                    v = v.trim();

                    FXOMInstance fxomValue = new FXOMInstance(fxomDocument, Object.class);
                    fxomValue.setFxValue(v);
                    fxoms.add(index, fxomValue);
                    fxomValue.getGlueElement().addBefore(fxom.getGlueElement());

                    fxomValue.getGlueElement().getFront().clear();
                    fxomValue.getGlueElement().getFront().add(new GlueCharacters(propertyElement.getDocument(), newFront));

                    fxom.getGlueElement().getFront().clear();
                    fxom.getGlueElement().getFront().add(new GlueCharacters(propertyElement.getDocument(), newTail));

                    //fxom.getGlueElement().getFront().clear();
                }
            }
        }

        if (index == -1) {
            return null;
        } else {
            return v;
        }
    }
}