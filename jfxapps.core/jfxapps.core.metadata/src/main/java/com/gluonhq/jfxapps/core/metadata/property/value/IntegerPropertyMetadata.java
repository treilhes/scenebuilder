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
package com.gluonhq.jfxapps.core.metadata.property.value;

import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.metadata.BasicSelection;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 *
 */
public class IntegerPropertyMetadata<VC> extends NumberPropertyMetadata<Integer, VC> {

//    protected IntegerPropertyMetadata(PropertyName name, boolean readWrite,
//            Integer defaultValue, InspectorPath inspectorPath) {
//        super(name, Integer.class, readWrite, defaultValue, inspectorPath);
//        setMin(-Integer.MAX_VALUE);
//        setMax(Integer.MAX_VALUE);
//    }

    protected IntegerPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    public boolean isValidValue(Integer value) {
        return (value != null);
    }

    /*
     * TextEncodablePropertyMetadata
     */
    @Override
    public Integer makeValueFromString(String string) {
        return Integer.valueOf(string);
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends NumberPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Integer, VC> {
        public AbstractBuilder() {
            super();
            valueClass(Integer.class);
            min(-Integer.MAX_VALUE);
            max(Integer.MAX_VALUE);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, IntegerPropertyMetadata<VC>, VC> {
        @Override
        public IntegerPropertyMetadata<VC> build() {
            return new IntegerPropertyMetadata<VC>(this);
        }
    }

    public static class PositiveIntegerPropertyMetadata<VC> extends IntegerPropertyMetadata<VC> {

//        protected PositiveIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
//                InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            setMin(0);
//        }

        protected PositiveIntegerPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Integer value) {
            return super.isValidValue(value) && (0 <= value);
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends IntegerPropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {
            public AbstractBuilder() {
                super();
                min(0);
            }
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, PositiveIntegerPropertyMetadata<VC>, VC> {
            @Override
            public PositiveIntegerPropertyMetadata<VC> build() {
                return new PositiveIntegerPropertyMetadata<VC>(this);
            }
        }
    }

    public abstract static class GridIntegerPropertyMetadata<VC> extends PositiveIntegerPropertyMetadata<VC> {

//        protected GridIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
//                InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected GridIntegerPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        public static GridPane getGridPane(BasicSelection selectionState) {
                FXOMObject commonParent = selectionState.getCommonParentObject();
                if (commonParent == null) {
                    return null;
                }
                Object parentObj = commonParent.getSceneGraphObject();
                assert parentObj instanceof GridPane;
                return (GridPane) parentObj;
        }

        public static int getGridPaneColumnCount(GridPane gridPane) {
            return gridPane.getColumnCount();
        }

        public static int getGridPaneRowCount(GridPane gridPane) {
            return gridPane.getRowCount();
        }

        public static int getRowSpanPropertyMaxIndex(BasicSelection selectionState) {
            int maxIndex = 0;
            for (FXOMElement instance : selectionState.getSelectedInstances()) {
                assert instance.getSceneGraphObject().isNode();
                Integer index;
                Node node = instance.getSceneGraphObject().getAs(Node.class);
                index = GridPane.getRowIndex(node);
                if (index == null) {
                    index = 0;
                }
                if (index > maxIndex) {
                    maxIndex = index;
                }
            }
            return maxIndex;
        }

        public static int getColumnSpanPropertyMaxIndex(BasicSelection selectionState) {
            int maxIndex = 0;
            for (FXOMElement instance : selectionState.getSelectedInstances()) {
                assert instance.getSceneGraphObject().isNode();
                Integer index;
                Node node = instance.getSceneGraphObject().getAs(Node.class);
                index = GridPane.getColumnIndex(node);
                if (index == null) {
                    index = 0;
                }
                if (index > maxIndex) {
                    maxIndex = index;
                }
            }
            return maxIndex;
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends PositiveIntegerPropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {}

    }

    public static class GridRowIndexIntegerPropertyMetadata<VC> extends GridIntegerPropertyMetadata<VC> {

//        protected GridRowIndexIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
//                InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected GridRowIndexIntegerPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public Integer getMax(BasicSelection selectionState) {
            GridPane gridPane = getGridPane(selectionState);
            if (gridPane == null) {
                // multi-selection from different GridPanes: not supported for now
                return getMin(selectionState);
            }
            int nbRow = getGridPaneRowCount(gridPane);
            // index start to 0
            return nbRow - 1;
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends GridIntegerPropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {}

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, GridIntegerPropertyMetadata<VC>, VC> {
            @Override
            public GridRowIndexIntegerPropertyMetadata<VC> build() {
                return new GridRowIndexIntegerPropertyMetadata<VC>(this);
            }
        }
    }

    public static class GridRowSpanIntegerPropertyMetadata<VC> extends GridIntegerPropertyMetadata<VC> {

//        protected GridRowSpanIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
//                InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected GridRowSpanIntegerPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public Integer getMax(BasicSelection selectionState) {
            GridPane gridPane = getGridPane(selectionState);
            if (gridPane == null) {
                // multi-selection from different GridPanes: not supported for now
                return getMin(selectionState);
            }
            int nbRow = getGridPaneRowCount(gridPane);
            int maxIndex = getRowSpanPropertyMaxIndex(selectionState);
            return nbRow - maxIndex;
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends GridIntegerPropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {

            public AbstractBuilder() {
                super();
                constant("REMAINING", GridPane.REMAINING);
                min(1);
            }

        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, GridRowSpanIntegerPropertyMetadata<VC>, VC> {
            @Override
            public GridRowSpanIntegerPropertyMetadata<VC> build() {
                return new GridRowSpanIntegerPropertyMetadata<VC>(this);
            }
        }
    }

    public static class GridColumnIndexIntegerPropertyMetadata<VC> extends GridIntegerPropertyMetadata<VC> {

//        protected GridColumnIndexIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
//                InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected GridColumnIndexIntegerPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public Integer getMax(BasicSelection selectionState) {
            GridPane gridPane = getGridPane(selectionState);
            if (gridPane == null) {
                // multi-selection from different GridPanes: not supported for now
                return getMin(selectionState);
            }
            int nbColumns = getGridPaneColumnCount(gridPane);
            // index start to 0
            return nbColumns - 1;
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends GridIntegerPropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {}

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, GridColumnIndexIntegerPropertyMetadata<VC>, VC> {
            @Override
            public GridColumnIndexIntegerPropertyMetadata<VC> build() {
                return new GridColumnIndexIntegerPropertyMetadata<VC>(this);
            }
        }
    }

    public static class GridColumnSpanIntegerPropertyMetadata<VC> extends GridIntegerPropertyMetadata<VC> {

//        protected GridColumnSpanIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
//                InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected GridColumnSpanIntegerPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public Integer getMax(BasicSelection selectionState) {
            GridPane gridPane = getGridPane(selectionState);
            if (gridPane == null) {
                // multi-selection from different GridPanes: not supported for now
                return getMin(selectionState);
            }
            int nbColumns = getGridPaneColumnCount(gridPane);
            int maxIndex = getColumnSpanPropertyMaxIndex(selectionState);
            return nbColumns - maxIndex;
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends GridIntegerPropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {

            public AbstractBuilder() {
                super();
                constant("REMAINING", GridPane.REMAINING);
                min(1);
            }

        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, GridColumnSpanIntegerPropertyMetadata<VC>, VC> {
            @Override
            public GridColumnSpanIntegerPropertyMetadata<VC> build() {
                return new GridColumnSpanIntegerPropertyMetadata<VC>(this);
            }
        }
    }
}
