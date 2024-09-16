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

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.factory.AbstractFactory;
import com.gluonhq.jfxapps.core.api.mask.Accessory;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.util.Deprecation;

import javafx.scene.layout.GridPane;

/**
 *
 */
@Prototype
public class GridPaneHierarchyMask {

    private final FXOMObjectMask.Factory maskFactory;
    private final GridPaneChildMask.Factory gridChildMaskFactory;
    private FXOMObjectMask mask;
    private FXOMElement fxomElement;
    private Accessory childrenAccessory;

    public GridPaneHierarchyMask(FXOMObjectMask.Factory maskFactory, GridPaneChildMask.Factory gridChildMaskFactory) {
        super();
        this.maskFactory = maskFactory;
        this.gridChildMaskFactory = gridChildMaskFactory;
    }

    protected void setupMask(FXOMObject fxomObject) {
        assert fxomObject instanceof FXOMElement;
        fxomElement = (FXOMElement) mask.getFxomObject();
        assert fxomElement.getSceneGraphObject().isInstanceOf(GridPane.class);

        this.mask = maskFactory.getMask(fxomObject);
        this.childrenAccessory = mask.getAccessory(GridPaneProperties.CHILDREN);
    }

    /**
     * Returns the number of columns constraints for this GridPane mask.
     *
     * @return the number of columns constraints
     */
    public int getColumnsConstraintsSize() {
        final var fxomProperty = getFxomElement().getProperties().get(GridPaneProperties.COLUMN_CONSTRAINTS);

        final int result;
        if (fxomProperty == null) {
            result = 0;
        } else {
            assert fxomProperty instanceof FXOMPropertyC; // ie cannot be written as an XML attribute
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
            result = fxomPropertyC.getChildren().size();
        }

        return result;
    }

    /**
     * Returns the number of rows constraints for this GridPane mask.
     *
     * @return the number of rows constraints
     */
    public int getRowsConstraintsSize() {

        final var fxomProperty = getFxomElement().getProperties().get(GridPaneProperties.ROW_CONSTRAINTS);

        final int result;
        if (fxomProperty == null) {
            result = 0;
        } else {
            assert fxomProperty instanceof FXOMPropertyC; // ie cannot be written as an XML attribute
            final var fxomPropertyC = (FXOMPropertyC) fxomProperty;
            result = fxomPropertyC.getChildren().size();
        }

        return result;
    }

    /**
     * Returns the number of columns for this GridPane mask. The number of columns
     * for a GridPane is the max of : - the number of column constraints - the max
     * column index defined in this GridPane children + 1
     *
     * @return the number of columns
     */
    public int getColumnsSize() {
//        final Object sceneGraphObject;
//        // For FXOMIntrinsic, we use the source sceneGraphObject
//        if (getFxomObject() instanceof FXOMIntrinsic) {
//            sceneGraphObject = ((FXOMIntrinsic) getFxomObject()).getSourceSceneGraphObject().get();
//        } else {
//            sceneGraphObject = getFxomObject().getSceneGraphObject().get();
//        }

        // FIXME here was a call to getSourceSceneGraphObject() if getFxomElement()
        // instanceof FXOMIntrinsic
        // For FXOMIntrinsic, we use the source sceneGraphObject
        // that method is not present anymore, need to test the code to see if it is
        // still working
        final var sceneGraphObject = getFxomElement().getSceneGraphObject().getAs(GridPane.class);
        assert sceneGraphObject != null;
        return Deprecation.getGridPaneColumnCount(sceneGraphObject);
    }

    /**
     * Returns the number of rows for this GridPane mask. The number of rows for a
     * GridPane is the max of : - the number of row constraints - the max row index
     * defined in this GridPane children + 1
     *
     * @return the number of rows
     */
    public int getRowsSize() {
//        final Object sceneGraphObject;
//        // For FXOMIntrinsic, we use the source sceneGraphObject
//        if (getFxomObject() instanceof FXOMIntrinsic) {
//            sceneGraphObject = ((FXOMIntrinsic) getFxomObject()).getSourceSceneGraphObject();
//        } else {
//            sceneGraphObject = getFxomObject().getSceneGraphObject();
//        }
//        assert sceneGraphObject instanceof GridPane;

        // FIXME here was a call to getSourceSceneGraphObject() if getFxomElement()
        // instanceof FXOMIntrinsic
        // For FXOMIntrinsic, we use the source sceneGraphObject
        // that method is not present anymore, need to test the code to see if it is
        // still working
        final var sceneGraphObject = getFxomElement().getSceneGraphObject().getAs(GridPane.class);
        assert sceneGraphObject != null;

        return Deprecation.getGridPaneRowCount(sceneGraphObject);
    }

    public List<FXOMObject> getColumnContentAtIndex(int index) {
        assert 0 <= index;

        final List<FXOMObject> result = new ArrayList<>();
        for (FXOMObject childObject : mask.getSubComponents(childrenAccessory, false)) {
            final var childMask = gridChildMaskFactory.getMask(childObject);
            if (childMask.getColumnIndex() == index) {
                result.add(childObject);
            }
        }
        return result;
    }

    public List<FXOMObject> getRowContentAtIndex(int index) {
        assert 0 <= index;

        final List<FXOMObject> result = new ArrayList<>();
        for (FXOMObject childObject : mask.getSubComponents(childrenAccessory, false)) {
            final var childMask = gridChildMaskFactory.getMask(childObject);
            if (childMask.getRowIndex() == index) {
                result.add(childObject);
            }
        }
        return result;
    }

    public FXOMObject getColumnConstraintsAtIndex(int index) {

        assert 0 <= index;

        FXOMObject result = null;

        // Retrieve the constraints property
        final var propertyName = GridPaneProperties.COLUMN_CONSTRAINTS;
        final var constraintsProperty = getFxomElement().getProperties().get(propertyName);

        if (constraintsProperty != null) {
            assert constraintsProperty instanceof FXOMPropertyC;
            final var constraintsValues = ((FXOMPropertyC) constraintsProperty).getChildren();
            if (index < constraintsValues.size()) {
                result = constraintsValues.get(index);
            }
        }

        return result;
    }

    public FXOMObject getRowConstraintsAtIndex(int index) {

        assert 0 <= index;

        FXOMObject result = null;

        // Retrieve the constraints property
        final var propertyName = GridPaneProperties.ROW_CONSTRAINTS;
        final var constraintsProperty = getFxomElement().getProperties().get(propertyName);

        if (constraintsProperty != null) {
            assert constraintsProperty instanceof FXOMPropertyC;
            final var constraintsValues = ((FXOMPropertyC) constraintsProperty).getChildren();
            if (index < constraintsValues.size()) {
                result = constraintsValues.get(index);
            }
        }

        return result;
    }

    protected FXOMElement getFxomElement() {
        return fxomElement;
    }

    @Singleton
    public static final class Factory extends AbstractFactory<GridPaneHierarchyMask> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        public GridPaneHierarchyMask getMask(FXOMObject fxomObject) {
            return create(GridPaneHierarchyMask.class, m -> m.setupMask(fxomObject));
        }
    }
}
