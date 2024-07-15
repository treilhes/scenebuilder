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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gluonhq.jfxapps.core.fxom.glue.GlueElement;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

/**
 *
 *
 */
public abstract class FXOMProperty extends FXOMNode {

    public static final String VIRTUAL_PREFIX = "virtual:";

    private final PropertyName name;
    private FXOMElement parentElement;
    private final List<FXOMObject> children = new ArrayList<>();
    private final GlueElement propertyElement;

    FXOMProperty(
            FXOMDocument document,
            PropertyName name,
            GlueElement propertyElement) {
        super(document);

        assert name != null;

        this.name = name;
        this.propertyElement = propertyElement;
    }

    public PropertyName getName() {
        return name;
    }

    public GlueElement getPropertyElement() {
        return propertyElement;
    }

    public FXOMElement getParentInstance() {
        return parentElement;
    }

    public abstract void addToParentInstance(int index, FXOMElement newParentInstance);
    public abstract void removeFromParentInstance();
    public abstract int getIndexInParentInstance();

    /*
     * OMObject
     */

    public List<FXOMObject> getChildren() {
        return Collections.unmodifiableList(children);
    }


    protected void addChild(int index, FXOMObject value) {
        assert value != null;
        //assert value.getParentProperty() == this;
        assert children.contains(value) == false;
        if (index == -1) {
            children.add(value);
        } else {
            children.add(index, value);
        }
    }

    protected void addAllChildren(List<FXOMObject> values) {
        assert values != null;
        children.addAll(values);
    }

    protected void removeChild(FXOMObject value) {
        assert value != null;
        //assert value.getParentProperty() == null;
        assert children.contains(value);
        children.remove(value);
    }

    /*
     * FXOMNode
     */

    @Override
    protected void changeFxomDocument(FXOMDocument destination) {
        assert destination != null;
        assert destination != getFxomDocument();
        assert (parentElement == null) || (destination == parentElement.getFxomDocument());

        super.changeFxomDocument(destination);
    }



    /*
     * Package
     */

    void setParentInstance(FXOMElement parentInstance) {
        this.parentElement = parentInstance;
    }

    @Override
    public FXOMPath getPath() {
        return FXOMPath.of(this.getParentInstance());
    }
}
