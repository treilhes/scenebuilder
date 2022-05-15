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
import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

/**
 *
 *
 */
public class FXOMPropertyC extends FXOMProperty {


    public FXOMPropertyC(
            FXOMDocument document,
            PropertyName name,
            List<FXOMObject> values,
            GlueElement glueElement) {
        super(document, name, glueElement);

        assert values != null;
        assert name.getName().startsWith(VIRTUAL_PREFIX) || values.isEmpty() == false;
        assert glueElement != null;
        assert name.getName().startsWith(VIRTUAL_PREFIX) || glueElement.getTagName().equals(getName().toString());

        // Adds values to this property.
        // Note we don't use addValue() because
        // here Glue is already up to date.
        for (FXOMObject v : values) {
            this.addChild(-1, v);
            v.setParentProperty(this);
        }
    }


    public FXOMPropertyC(FXOMDocument document, PropertyName name) {
        super(document, name, new GlueElement(document.getGlue(), name.toString()));
    }


    public FXOMPropertyC(FXOMDocument document, PropertyName name, FXOMObject value) {
        super(document, name, new GlueElement(document.getGlue(), name.toString()));

        assert value != null;

        value.addToParentProperty(-1, this);
    }

    public FXOMPropertyC(FXOMDocument document, PropertyName name, List<FXOMObject> values) {
        super(document, name, new GlueElement(document.getGlue(), name.toString()));

        assert values != null;
        assert values.isEmpty() == false;

        for (FXOMObject value : values) {
            value.addToParentProperty(-1, this);
        }
    }



    public GlueElement getGlueElement() {
        return getPropertyElement();
    }



    /*
     * FXOMProperty
     */

    @Override
    public void addToParentInstance(int index, FXOMElement newParentInstance) {

        if (getParentInstance() != null) {
            removeFromParentInstance();
        }

        setParentInstance(newParentInstance);
        newParentInstance.addProperty(this);

        final GlueElement newParentElement = newParentInstance.getGlueElement();
        getPropertyElement().addToParent(index, newParentElement);
    }

    @Override
    public void removeFromParentInstance() {

        assert getParentInstance() != null;

        final FXOMElement currentParentInstance = getParentInstance();

        assert getPropertyElement().getParent() == currentParentInstance.getGlueElement();
        getPropertyElement().removeFromParent();

        setParentInstance(null);
        currentParentInstance.removeProperty(this);
    }

    @Override
    public int getIndexInParentInstance() {
        final int result;

        if (getParentInstance() == null) {
            result = -1;
        } else {
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
        assert getPropertyElement().getParent() == null;

        getPropertyElement().moveToDocument(destination.getGlue());
        changeFxomDocument(destination);
    }


    @Override
    protected void changeFxomDocument(FXOMDocument destination) {
        assert destination != null;
        assert destination != getFxomDocument();
        assert destination.getGlue() == getPropertyElement().getDocument();

        super.changeFxomDocument(destination);
        for (FXOMObject v : getChildren()) {
            v.changeFxomDocument(destination);
        }
    }

    @Override
    public void documentLocationWillChange(URL newLocation) {
        for (FXOMObject v : getChildren()) {
            v.documentLocationWillChange(newLocation);
        }
    }


    /*
     * Package
     */

    /* Reserved to FXOMObject.addToParentProperty() private use */
    //@Override
    void addValue(int index, FXOMObject value) {
        assert value != null;
        assert value.getParentProperty() == this;
        super.addChild(index, value);
    }

    /* Reserved to FXOMObject.removeFromParentProperty() private use */
    //@Override
    void removeValue(FXOMObject value) {
        assert value != null;
        assert value.getParentProperty() == null;
        super.removeChild(value);
    }
}
