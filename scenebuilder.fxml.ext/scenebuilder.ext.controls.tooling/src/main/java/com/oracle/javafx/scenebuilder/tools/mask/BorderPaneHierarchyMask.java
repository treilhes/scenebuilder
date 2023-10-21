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
package com.oracle.javafx.scenebuilder.tools.mask;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.mask.AbstractHierarchyMask;
import com.oracle.javafx.scenebuilder.api.mask.MaskFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.IMetadata;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class BorderPaneHierarchyMask extends AbstractHierarchyMask {

    private static final PropertyName TOP = new PropertyName("top"); //NOCHECK
    private static final PropertyName BOTTOM = new PropertyName("bottom"); //NOCHECK
    private static final PropertyName CENTER = new PropertyName("center"); //NOCHECK
    private static final PropertyName LEFT = new PropertyName("left"); //NOCHECK
    private static final PropertyName RIGHT = new PropertyName("right"); //NOCHECK

    private Accessory topAccessory;
    private Accessory bottomAccessory;
    private Accessory leftAccessory;
    private Accessory rightAccessory;
    private Accessory centerAccessory;

    public BorderPaneHierarchyMask(IMetadata metadata) {
        super(metadata);
    }

    @Override
    protected void setupMask(FXOMObject fxomObject) {
        super.setupMask(fxomObject);
        topAccessory = getAccessory(TOP);
        bottomAccessory = getAccessory(BOTTOM);
        leftAccessory = getAccessory(LEFT);
        rightAccessory = getAccessory(RIGHT);
        centerAccessory = getAccessory(CENTER);
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static final class Factory extends MaskFactory<BorderPaneHierarchyMask> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public BorderPaneHierarchyMask getMask(FXOMObject fxomObject) {
            return create(BorderPaneHierarchyMask.class, m -> m.setupMask(fxomObject));
        }
    }
}
