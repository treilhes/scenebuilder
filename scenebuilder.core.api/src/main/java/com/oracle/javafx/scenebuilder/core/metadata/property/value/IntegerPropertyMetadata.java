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
package com.oracle.javafx.scenebuilder.core.metadata.property.value;

import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 * 
 */
public class IntegerPropertyMetadata extends NumberPropertyMetadata<Integer> {

    public IntegerPropertyMetadata(PropertyName name, boolean readWrite, 
            Integer defaultValue, InspectorPath inspectorPath) {
        super(name, Integer.class, readWrite, defaultValue, inspectorPath);
        setMin(-Integer.MAX_VALUE);
        setMax(Integer.MAX_VALUE);
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
    
    public static class PositiveIntegerPropertyMetadata extends IntegerPropertyMetadata {

        public PositiveIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
                InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
            setMin(0);
        }
        
        @Override
        public boolean isValidValue(Integer value) {
            return super.isValidValue(value) && (0 <= value);
        }
    }
    
    public abstract static class GridIntegerPropertyMetadata extends PositiveIntegerPropertyMetadata {

        public GridIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
                InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
        }
        
        public static GridPane getGridPane(SelectionState selectionState) {
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
        
        public static int getRowSpanPropertyMaxIndex(SelectionState selectionState) {
            int maxIndex = 0;
            for (FXOMInstance instance : selectionState.getSelectedInstances()) {
                assert instance.getSceneGraphObject() instanceof Node;
                Integer index;
                Node node = (Node) instance.getSceneGraphObject();
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
        
        public static int getColumnSpanPropertyMaxIndex(SelectionState selectionState) {
            int maxIndex = 0;
            for (FXOMInstance instance : selectionState.getSelectedInstances()) {
                assert instance.getSceneGraphObject() instanceof Node;
                Integer index;
                Node node = (Node) instance.getSceneGraphObject();
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
    }
    
    public static class GridRowIndexIntegerPropertyMetadata extends GridIntegerPropertyMetadata {

        public GridRowIndexIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
                InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
        }
        
        @Override
        public Integer getMax(SelectionState selectionState) {
            GridPane gridPane = getGridPane(selectionState);
            if (gridPane == null) {
                // multi-selection from different GridPanes: not supported for now
                return getMin(selectionState);
            }
            int nbRow = getGridPaneRowCount(gridPane);
            // index start to 0
            return nbRow - 1;
        }
    }
    
    public static class GridRowSpanIntegerPropertyMetadata extends GridIntegerPropertyMetadata {

        public GridRowSpanIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
                InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
        }
        
        @Override
        public Integer getMax(SelectionState selectionState) {
            GridPane gridPane = getGridPane(selectionState);
            if (gridPane == null) {
                // multi-selection from different GridPanes: not supported for now
                return getMin(selectionState);
            }
            int nbRow = getGridPaneRowCount(gridPane);
            int maxIndex = getRowSpanPropertyMaxIndex(selectionState);
            return nbRow - maxIndex;
        }
    }
    
    public static class GridColumnIndexIntegerPropertyMetadata extends GridIntegerPropertyMetadata {

        public GridColumnIndexIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
                InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
        }

        @Override
        public Integer getMax(SelectionState selectionState) {
            GridPane gridPane = getGridPane(selectionState);
            if (gridPane == null) {
                // multi-selection from different GridPanes: not supported for now
                return getMin(selectionState);
            }
            int nbColumns = getGridPaneColumnCount(gridPane);
            // index start to 0
            return nbColumns - 1;
        }
    }
    
    public static class GridColumnSpanIntegerPropertyMetadata extends GridIntegerPropertyMetadata {

        public GridColumnSpanIntegerPropertyMetadata(PropertyName name, boolean readWrite, Integer defaultValue,
                InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
        }
        
        @Override
        public Integer getMax(SelectionState selectionState) {
            GridPane gridPane = getGridPane(selectionState);
            if (gridPane == null) {
                // multi-selection from different GridPanes: not supported for now
                return getMin(selectionState);
            }
            int nbColumns = getGridPaneColumnCount(gridPane);
            int maxIndex = getColumnSpanPropertyMaxIndex(selectionState);
            return nbColumns - maxIndex;
        }
    }
}
