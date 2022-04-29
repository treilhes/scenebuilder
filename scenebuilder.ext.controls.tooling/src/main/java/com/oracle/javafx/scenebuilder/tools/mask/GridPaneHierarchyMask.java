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

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.mask.AbstractHierarchyMask;
import com.oracle.javafx.scenebuilder.api.mask.MaskFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.util.Deprecation;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

import javafx.scene.layout.GridPane;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class GridPaneHierarchyMask extends AbstractHierarchyMask {

    private static final PropertyName ROW_CONSTRAINTS = new PropertyName("rowConstraints"); // NOCHECK
    private static final PropertyName COLUMN_CONSTRAINTS = new PropertyName("columnConstraints"); // NOCHECK
    private static final PropertyName CHILDREN = new PropertyName("children"); // NOCHECK

    private Accessory childrenAccessory;

    public GridPaneHierarchyMask(Metadata metadata) {
        super(metadata);
    }

    @Override
    protected void setupMask(FXOMObject fxomObject) {
        super.setupMask(fxomObject);
        this.childrenAccessory = getAccessory(CHILDREN);
    }

    /**
     * Returns the number of columns constraints for this GridPane mask.
     *
     * @return the number of columns constraints
     */
    public int getColumnsConstraintsSize() {
        assert getFxomObject() instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) getFxomObject();
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(COLUMN_CONSTRAINTS);

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
        assert getFxomObject() instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) getFxomObject();
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(ROW_CONSTRAINTS);

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
     * Returns the number of columns for this GridPane mask. The number of columns
     * for a GridPane is the max of : - the number of column constraints - the max
     * column index defined in this GridPane children + 1
     *
     * @return the number of columns
     */
    public int getColumnsSize() {
        final Object sceneGraphObject;
        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (getFxomObject() instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) getFxomObject()).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = getFxomObject().getSceneGraphObject();
        }
        assert sceneGraphObject instanceof GridPane;
        return Deprecation.getGridPaneColumnCount((GridPane) sceneGraphObject);
    }

    /**
     * Returns the number of rows for this GridPane mask. The number of rows for a
     * GridPane is the max of : - the number of row constraints - the max row index
     * defined in this GridPane children + 1
     *
     * @return the number of rows
     */
    public int getRowsSize() {
        final Object sceneGraphObject;
        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (getFxomObject() instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) getFxomObject()).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = getFxomObject().getSceneGraphObject();
        }
        assert sceneGraphObject instanceof GridPane;
        return Deprecation.getGridPaneRowCount((GridPane) sceneGraphObject);
    }

    public List<FXOMObject> getColumnContentAtIndex(int index) {
        assert 0 <= index;
        assert getFxomObject() instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) getFxomObject();
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final List<FXOMObject> result = new ArrayList<>();
        for (FXOMObject childObject:getSubComponents(childrenAccessory, false)) {
            final GridPaneHierarchyMask childMask = new GridPaneHierarchyMask(getMetadata());
            childMask.setupMask(childObject);
            if (childMask.getColumnIndex() == index) {
                result.add(childObject);
            }
        }
        return result;
    }

    public List<FXOMObject> getRowContentAtIndex(int index) {
        assert 0 <= index;
        assert getFxomObject() instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) getFxomObject();
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final List<FXOMObject> result = new ArrayList<>();
        for (FXOMObject childObject:getSubComponents(childrenAccessory, false)) {
            final GridPaneHierarchyMask childMask = new GridPaneHierarchyMask(getMetadata());
            childMask.setupMask(childObject);
            if (childMask.getRowIndex() == index) {
                result.add(childObject);
            }
        }
        return result;
    }

    public FXOMObject getColumnConstraintsAtIndex(int index) {

        assert 0 <= index;
        assert getFxomObject() instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) getFxomObject();
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        FXOMObject result = null;

        // Retrieve the constraints property
        final PropertyName propertyName = new PropertyName("columnConstraints"); // NOCHECK
        final FXOMProperty constraintsProperty = fxomInstance.getProperties().get(propertyName);

        if (constraintsProperty != null) {
            assert constraintsProperty instanceof FXOMPropertyC;
            final List<FXOMObject> constraintsValues = ((FXOMPropertyC) constraintsProperty).getChildren();
            if (index < constraintsValues.size()) {
                result = constraintsValues.get(index);
            }
        }

        return result;
    }

    public FXOMObject getRowConstraintsAtIndex(int index) {

        assert 0 <= index;
        assert getFxomObject() instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) getFxomObject();
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        FXOMObject result = null;

        // Retrieve the constraints property
        final PropertyName propertyName = new PropertyName("rowConstraints"); // NOCHECK
        final FXOMProperty constraintsProperty = fxomInstance.getProperties().get(propertyName);

        if (constraintsProperty != null) {
            assert constraintsProperty instanceof FXOMPropertyC;
            final List<FXOMObject> constraintsValues = ((FXOMPropertyC) constraintsProperty).getChildren();
            if (index < constraintsValues.size()) {
                result = constraintsValues.get(index);
            }
        }

        return result;
    }

    /**
     * Returns the column index for this GridPane child mask.
     *
     * @return the column index
     */
    public int getColumnIndex() {
        int result = 0;
        if (getFxomObject() instanceof FXOMInstance) {
            assert getFxomObject().getSceneGraphObject() != null;
            final FXOMInstance fxomInstance = (FXOMInstance) getFxomObject();
            result = getIndexFromGrid(fxomInstance, "columnIndex");
        } else if (getFxomObject() instanceof FXOMIntrinsic) {
            FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) getFxomObject();
            FXOMInstance fxomInstance = fxomIntrinsic.createFxomInstanceFromIntrinsic();
            result = getIndexFromGrid(fxomInstance, "columnIndex");
        }
        return result;
    }

    /**
     * Returns the row index for this GridPane child mask.
     *
     * @return the row index
     */
    public int getRowIndex() {
        int result = 0;
        if (getFxomObject() instanceof FXOMInstance) {
            assert getFxomObject().getSceneGraphObject() != null;
            final FXOMInstance fxomInstance = (FXOMInstance) getFxomObject();
            result = getIndexFromGrid(fxomInstance, "rowIndex");
        } else if (getFxomObject() instanceof FXOMIntrinsic) {
            FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) getFxomObject();
            FXOMInstance fxomInstance = fxomIntrinsic.createFxomInstanceFromIntrinsic();
            result = getIndexFromGrid(fxomInstance, "rowIndex");
        }
        return result;
    }

    private int getIndexFromGrid(final FXOMInstance fxomInstance, final String columnOrRow) {
        int result;
        final FXOMObject parentFxomObject = fxomInstance.getParentObject();
        assert parentFxomObject.getSceneGraphObject() instanceof GridPane;

        final PropertyName propertyName = new PropertyName(columnOrRow, GridPane.class); // NOCHECK
        final ValuePropertyMetadata vpm = getMetadata().queryValueProperty(fxomInstance, propertyName);
        final Object value = vpm.getValueObject(fxomInstance);
        // TODO : when DTL-5920 will be fixed, the null check will become unecessary
        if (value == null) {
            result = 0;
        } else {
            assert value instanceof Integer;
            result = ((Integer) value);
        }
        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static final class Factory extends MaskFactory<GridPaneHierarchyMask> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public GridPaneHierarchyMask getMask(FXOMObject fxomObject) {
            return create(GridPaneHierarchyMask.class, m -> m.setupMask(fxomObject));
        }
    }
}
