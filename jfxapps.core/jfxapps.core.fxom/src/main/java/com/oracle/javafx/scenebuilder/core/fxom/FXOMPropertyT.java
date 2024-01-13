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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.FXMLFormatUtils;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

/**
 *
 * A FXOMPropertyT represents a property with a single value that can be
 * represented as text : String, Boolean, Integer, Double, Enum...
 * <br/><br/>
 * There are three ways to express such a property in FXML. Let's take
 * Button.text as an example:
 * <br/><br/>
 * 1) &lt;Button text="OK" /><br/>
 *  value = "OK"<br/>
 *  propertyElement == null<br/>
 *  valueElement == null
 *
 * <br/><br/>
 * 2) &lt;Button>&lt;text>OK&lt;/text>&lt;/Button><br/>
 * value = "OK"<br/>
 * propertyElement != null &&
 * propertyElement.getTagName() == "text" &&
 * propertyElement.getChildren().size() == 0 &&
 * propertyElement.getContentText().equals("OK")
 *
 * <br/><br/>
 * 3) &lt;Button>&lt;text>&lt;String fx:value="OK"/>&lt;/text>&lt;/Button><br/>
 * value = "OK"<br/>
 * propertyElement != null && propertyElement.getTagName() == "text" &&
 * propertyElement.getChildren().size() == 1 &&
 * propertyElement.getChildren().get(0) == valueElement valueElement != null &&
 * valueElement.getAttributes().get("fx:value").equals("OK")
 */
public class FXOMPropertyT extends FXOMProperty {

    /*
     *
     */

    private String value;
    private final GlueElement valueElement;

    public FXOMPropertyT(FXOMDocument document, PropertyName name, GlueElement propertyElement,
            List<FXOMObject> children, String value) {
        super(document, name, propertyElement);
        assert children == null || children.stream().filter(FXOMInstance.class::isInstance).count() == 1;

        if (children != null) {
            this.valueElement = children.stream().filter(FXOMInstance.class::isInstance).findFirst().get()
                    .getGlueElement();
            addAllChildren(children);
        } else {
            this.valueElement = null;
        }

        this.value = value;
    }

    public FXOMPropertyT(FXOMDocument document, PropertyName name, String value) {
        super(document, name, null);
        assert value != null;
        this.valueElement = null;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String newValue) {
        assert newValue != null;

        if (getPropertyElement() != null) {
            if (valueElement != null) { // Case #3
                final Map<String, String> attributes = valueElement.getAttributes();
                assert attributes.get("fx:value") != null;
                assert attributes.get("fx:value").equals(value);
                attributes.put("fx:value", newValue);

            } else { // Case #2
                assert getPropertyElement().getContentText() != null;
                assert getPropertyElement().getContentText().equals(value);
                getPropertyElement().setContentText(newValue);
            }
        } else { // Case #1
            final FXOMElement parentInstance = getParentInstance();
            if (parentInstance != null) {
                final GlueElement parentElement = parentInstance.getGlueElement();
                final Map<String, String> parentAttributes = parentElement.getAttributes();
                assert parentAttributes.get(getName().toString()).equals(value);
                parentAttributes.put(getName().toString(), newValue);
            }
        }

        value = newValue;
    }

    public GlueElement getValueElement() {
        return valueElement;
    }

    /*
     * FXOMProperty
     */

    @Override
    public void addToParentInstance(int index, FXOMElement newParentInstance) {

        assert newParentInstance != null;

        if (getParentInstance() != null) {
            removeFromParentInstance();
        }

        setParentInstance(newParentInstance);
        newParentInstance.addProperty(this);

        final GlueElement newParentElement = newParentInstance.getGlueElement();

        if (getPropertyElement() == null) { // Case #1
            // index is ignored
            final Map<String, String> attributes = newParentElement.getAttributes();
            assert attributes.get(getName().toString()) == null;
            attributes.put(getName().toString(), value);
        } else { // Case #2 or #3
            assert -1 <= index;
            assert index <= newParentElement.getChildren().size();
            getPropertyElement().addToParent(index, newParentElement);
        }
    }

    @Override
    public void removeFromParentInstance() {

        assert getParentInstance() != null;

        final FXOMElement currentParentInstance = getParentInstance();
        final GlueElement currentParentElement = currentParentInstance.getGlueElement();

        if (getPropertyElement() == null) { // Case #1
            final Map<String, String> attributes = currentParentElement.getAttributes();
            assert attributes.get(getName().toString()) != null;
            attributes.remove(getName().toString());
        } else { // Case #2 or #3
            getPropertyElement().removeFromParent();
        }

        setParentInstance(null);
        currentParentInstance.removeProperty(this);
    }

    @Override
    public int getIndexInParentInstance() {
        final int result;

        if (getParentInstance() == null) {
            result = -1;
        } else if (getPropertyElement() == null) { // Case #1
            result = -1;
        } else { // Case #2 or #3
            final GlueElement parentElement = getParentInstance().getGlueElement();
            result = parentElement.getChildren().indexOf(getPropertyElement());
            assert result != -1;
        }

        return result;
    }

    /*
     * FXOMNode
     */

    @Override
    public void moveToFxomDocument(FXOMDocument destination) {
        assert destination != null;
        assert destination != getFxomDocument();

        documentLocationWillChange(destination.getLocation());

        if (getParentInstance() != null) {
            assert getParentInstance().getFxomDocument() == getFxomDocument();
            removeFromParentInstance();
        }

        assert getParentInstance() == null;
        assert (getPropertyElement() == null) || (getPropertyElement().getParent() == null);

        if (getPropertyElement() != null) {
            getPropertyElement().moveToDocument(destination.getGlue());
            assert (valueElement == null) || (valueElement.getDocument() == destination.getGlue());
        }
        changeFxomDocument(destination);
    }

    @Override
    protected void changeFxomDocument(FXOMDocument destination) {
        assert destination != null;
        assert destination != getFxomDocument();
        assert (getPropertyElement() == null) || (destination.getGlue() == getPropertyElement().getDocument());

        super.changeFxomDocument(destination);
    }

    @Override
    public void documentLocationWillChange(URL newLocation) {
        final URL currentLocation = getFxomDocument().getLocation();

        final List<String> currentItems = FXMLFormatUtils.splitArrayValue(getValue());
        final List<String> newItems = new ArrayList<>();
        int changeCount = 0;
        for (String currentItem : currentItems) {
            final PrefixedValue pv = new PrefixedValue(currentItem);
            if (pv.isDocumentRelativePath()) {
                assert currentLocation != null;

                /*
                 * currentItem is a path relative to currentLocation. We compute the absolute
                 * path and, if new location is non null, we relativize the absolute path
                 * against newLocation.
                 */
                final URL assetURL = pv.resolveDocumentRelativePath(currentLocation);
                final String newValue;
                if (newLocation == null) {
                    newValue = assetURL.toString();
                } else {
                    final PrefixedValue pv2 = PrefixedValue.makePrefixedValue(assetURL, newLocation);
                    newValue = pv2.toString();
                }
                newItems.add(newValue);
                changeCount++;
            } else if (pv.isPlainString() && (currentLocation == null)) {

                /*
                 * currentItem is a plain string. We check if it is an URL.
                 *
                 * Since currentLocation is null and newLocation non null, then all URLs should
                 * be converted to relative path.
                 */
                assert newLocation != null;
                try {
                    final URL assetURL = new URL(pv.getSuffix());
                    final PrefixedValue pv2 = PrefixedValue.makePrefixedValue(assetURL, newLocation);
                    newItems.add(pv2.toString());
                    changeCount++;
                } catch (MalformedURLException x) {
                    // p.getValue() is not an URL
                    // We keep currentItem unchanged.
                    newItems.add(currentItem);
                }
            } else {
                newItems.add(currentItem);
            }
        }
        assert currentItems.size() == newItems.size();

        if (changeCount >= 1) {
            setValue(FXMLFormatUtils.assembleArrayValue(newItems));
        }

        getChildren().forEach(c -> c.documentLocationWillChange(newLocation));
    }

}