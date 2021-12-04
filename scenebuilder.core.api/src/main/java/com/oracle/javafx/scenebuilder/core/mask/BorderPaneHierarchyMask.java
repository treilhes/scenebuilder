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
package com.oracle.javafx.scenebuilder.core.mask;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

/**
 *
 */
public class BorderPaneHierarchyMask extends DesignHierarchyMask {

    private static final PropertyName TOP = new PropertyName("top"); //NOCHECK
    private static final PropertyName BOTTOM = new PropertyName("bottom"); //NOCHECK
    private static final PropertyName CENTER = new PropertyName("center"); //NOCHECK
    private static final PropertyName LEFT = new PropertyName("left"); //NOCHECK
    private static final PropertyName RIGHT = new PropertyName("right"); //NOCHECK

    private final Accessory topAccessory;
    private final Accessory bottomAccessory;
    private final Accessory leftAccessory;
    private final Accessory rightAccessory;
    private final Accessory centerAccessory;

    public BorderPaneHierarchyMask(FXOMObject fxomObject) {
        super(fxomObject);

        topAccessory = getAccessoryForPropertyName(TOP);
        bottomAccessory = getAccessoryForPropertyName(BOTTOM);
        leftAccessory = getAccessoryForPropertyName(LEFT);
        rightAccessory = getAccessoryForPropertyName(RIGHT);
        centerAccessory = getAccessoryForPropertyName(CENTER);
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


}