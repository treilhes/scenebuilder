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
package com.oracle.javafx.scenebuilder.tools.mask;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.factory.AbstractFactory;
import com.gluonhq.jfxapps.core.api.mask.Accessory;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.scene.layout.GridPane;


/**
 *
 */
@Prototype
public class BorderPaneHierarchyMask {

    private final FXOMObjectMask.Factory maskFactory;
    private FXOMObjectMask mask;

    private FXOMElement fxomElement;
    private Accessory topAccessory;
    private Accessory bottomAccessory;
    private Accessory leftAccessory;
    private Accessory rightAccessory;
    private Accessory centerAccessory;


    public BorderPaneHierarchyMask(FXOMObjectMask.Factory maskFactory) {
        this.maskFactory = maskFactory;
    }

    protected void setupMask(FXOMObject fxomObject) {

        assert fxomObject instanceof FXOMElement;
        fxomElement = (FXOMElement) mask.getFxomObject();
        assert fxomElement.getSceneGraphObject().isInstanceOf(GridPane.class);

        this.mask = maskFactory.getMask(fxomObject);

        topAccessory = mask.getAccessory(BorderPaneProperties.TOP);
        bottomAccessory = mask.getAccessory(BorderPaneProperties.BOTTOM);
        leftAccessory = mask.getAccessory(BorderPaneProperties.LEFT);
        rightAccessory = mask.getAccessory(BorderPaneProperties.RIGHT);
        centerAccessory = mask.getAccessory(BorderPaneProperties.CENTER);
    }


    public Accessory getTopAccessory() {
        return topAccessory;
    }


    public Accessory getBottomAccessory() {
        return bottomAccessory;
    }


    public Accessory getLeftAccessory() {
        return leftAccessory;
    }


    public Accessory getRightAccessory() {
        return rightAccessory;
    }


    public Accessory getCenterAccessory() {
        return centerAccessory;
    }

    protected FXOMElement getFxomElement() {
        return fxomElement;
    }

    @Singleton
    public static final class Factory extends AbstractFactory<BorderPaneHierarchyMask> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        public BorderPaneHierarchyMask getMask(FXOMObject fxomObject) {
            return create(BorderPaneHierarchyMask.class, m -> m.setupMask(fxomObject));
        }
    }
}
