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
package com.oracle.javafx.scenebuilder.fxml.api.selection;

import java.util.Map;

import org.scenebuilder.fxml.api.HierarchyMask.Accessory;

import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.scene.Node;
import javafx.scene.control.Control;

public interface FxmlSelection extends Selection<FXOMDocument, FXOMObject> {

    /**
     * Check if the current selection objects are all instances of the provided
     * type,
     *
     * @param the required type of selected objects
     * @return true if the current selection objects are all instances of the
     *         provided type, false otherwise.
     */
    boolean isSelectionOfType(Class<?> type);

    /**
     * Check if the current selection objects are all instances of a {@link Node},
     *
     * @return true if the current selection objects are all instances of a
     *         {@link Node}, false otherwise.
     */
    boolean isSelectionNode();

    /**
     * Check if the current selection objects are all instances of a {@link Control}
     *
     * @return true if the current selection objects are all instances of a
     *         {@link Control}, false otherwise.
     */
    boolean isSelectionControl();

    /**
     * Selection can be moved if true
     *
     * @return can be moved
     */
    boolean isMovable();

    public Map<String, FXOMObject> collectSelectedFxIds();

    public Accessory getTargetAccessory();

    public void select(Accessory targetAccessory);
}