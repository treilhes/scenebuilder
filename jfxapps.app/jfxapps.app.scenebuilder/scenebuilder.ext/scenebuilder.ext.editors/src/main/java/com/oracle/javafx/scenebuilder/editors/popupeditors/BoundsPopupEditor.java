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
package com.oracle.javafx.scenebuilder.editors.popupeditors;

import org.scenebuilder.fxml.api.Documentation;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.api.editors.EditorUtils;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;

/**
 * Simple string popup editor.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class BoundsPopupEditor extends PopupEditor {

    @FXML
    Label minX;
    @FXML
    Label minY;
    @FXML
    Label minZ;
    @FXML
    Label maxX;
    @FXML
    Label maxY;
    @FXML
    Label maxZ;
    @FXML
    Label width;
    @FXML
    Label height;
    @FXML
    Label depth;

    private Parent root;
    private Bounds bounds;

    public BoundsPopupEditor(
            Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem
            ) {
        super(dialog, documentation, fileSystem);
    }

    //
    // Interface from PopupEditor.
    // Methods called by PopupEditor.
    //

    @Override
    public void initializePopupContent() {
        root = FXMLUtils.load(this, "BoundsPopupEditor.fxml");
    }

    @Override
    public String getPreviewString(Object value) {
        // value should never be null
        assert value instanceof Bounds;
        Bounds boundsVal = (Bounds) value;
        String valueAsString;
        if (isIndeterminate()) {
            valueAsString = "-"; //NOCHECK
        } else {
            valueAsString = EditorUtils.valAsStr(boundsVal.getMinX()) + "," //NOCHECK
                    + EditorUtils.valAsStr(boundsVal.getMinY())
                    + "  " + EditorUtils.valAsStr(boundsVal.getWidth()) //NOCHECK
                    + "x" + EditorUtils.valAsStr(boundsVal.getHeight()); //NOCHECK
        }
        return valueAsString;
    }

    @Override
    public void setPopupContentValue(Object value) {
        if (value == null) {
            bounds = null;
            updateValues();
        } else {
            assert value instanceof Bounds;
            bounds = (Bounds) value;
            updateValues();
        }
    }

    @Override
    public Node getPopupContentNode() {
        return root;
    }

    private void updateValues() {
        minX.setText(EditorUtils.valAsStr((bounds != null) ? bounds.getMinX() : ""));//NOCHECK
        minY.setText(EditorUtils.valAsStr((bounds != null) ? bounds.getMinY() : ""));//NOCHECK
        minZ.setText(EditorUtils.valAsStr((bounds != null) ? bounds.getMinZ() : ""));//NOCHECK
        maxX.setText(EditorUtils.valAsStr((bounds != null) ? bounds.getMaxX() : ""));//NOCHECK
        maxY.setText(EditorUtils.valAsStr((bounds != null) ? bounds.getMaxY() : ""));//NOCHECK
        maxZ.setText(EditorUtils.valAsStr((bounds != null) ? bounds.getMaxZ() : ""));//NOCHECK
        width.setText(EditorUtils.valAsStr((bounds != null) ? bounds.getWidth() : ""));//NOCHECK
        height.setText(EditorUtils.valAsStr((bounds != null) ? bounds.getHeight() : ""));//NOCHECK
        depth.setText(EditorUtils.valAsStr((bounds != null) ? bounds.getDepth() : ""));//NOCHECK
    }

}
