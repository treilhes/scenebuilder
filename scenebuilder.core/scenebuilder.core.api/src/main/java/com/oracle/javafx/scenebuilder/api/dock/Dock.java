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
package com.oracle.javafx.scenebuilder.api.dock;

import java.util.Collection;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.layout.Region;

public interface Dock {

    public static final String LEFT_DOCK_ID = "42094e12-3aa8-44e4-bc4c-7633d6bc5b05";
    public static final String RIGHT_DOCK_ID = "18cce02f-3a67-4b96-b5b4-53d7e0145a64";
    public static final String BOTTOM_DOCK_ID = "e8a0168d-f074-47e7-b107-aa7302a27cf8";
    public static final UUID LEFT_DOCK_UUID = UUID.fromString(LEFT_DOCK_ID);
    public static final UUID RIGHT_DOCK_UUID = UUID.fromString(RIGHT_DOCK_ID);
    public static final UUID BOTTOM_DOCK_UUID = UUID.fromString(BOTTOM_DOCK_ID);

	UUID getId();

	boolean isWindow();
	SceneBuilderWindow getParentWindow();

    Collection<View> getViews();

    //temp
    Region getContent();
    /**
     * @param dockType
     * @param view
     */
    //void changedDockType(DockType<?> dockType, View view);

    ObjectProperty<DockType> dockTypeProperty();
    default void setDockType(DockType dockType) {
        dockTypeProperty().set(dockType);
    }
    default DockType<?> getDockType() {
        return dockTypeProperty().get();
    }

    ObjectProperty<View> focusedProperty();
    default void setFocused(View focused) {
        focusedProperty().set(focused);
    }
    default View getFocused() {
        return focusedProperty().get();
    }

    BooleanProperty minimizedProperty();
    default void setMinimized(boolean visible) {
        minimizedProperty().set(visible);
    }
    default boolean isMinimized() {
        return minimizedProperty().get();
    }

    ReadOnlyBooleanProperty visibleProperty();
    default boolean isVisible() {
        return visibleProperty().get();
    }
}
