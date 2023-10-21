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
package com.oracle.javafx.scenebuilder.tools.job.gridpane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.DefaultSelectionGroupFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridSelectionGroup.Type;
import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;

import javafx.scene.layout.GridPane;

/**
 * Utilities to build GridPane jobs.
 */
public class GridPaneJobUtils {

    public enum Position {

        ABOVE, BELOW, BEFORE, AFTER
    }

    /**
     * Returns the list of target GridPane objects contained in the current Selection.
     * @param selection the selection to check
     * @return the list of target GridPane objects
     */
    static List<FXOMObject> getTargetGridPanes(final Selection selection) {

        final AbstractSelectionGroup asg = selection.getGroup();
        assert asg instanceof DefaultSelectionGroupFactory
                || asg instanceof GridSelectionGroup;

        final List<FXOMObject> result = new ArrayList<>();

        // Selection == GridPanes
        if (asg instanceof DefaultSelectionGroupFactory) {
            final DefaultSelectionGroupFactory osg = (DefaultSelectionGroupFactory) asg;
            result.addAll(osg.getItems());
        } //
        // Selection == GridPane rows or columns
        else if (asg instanceof GridSelectionGroup) {
            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
            result.add(gsg.getHitItem());
        }

        return result;
    }

    /**
     * Returns the list of integers
     * - greater (or ==) than fromIndex
     * - smaller (or ==) than toIndex
     *
     * @param fromIndex
     * @param toIndex
     * @return
     */
    static List<Integer> getIndexes(int fromIndex, int toIndex) {
        assert fromIndex <= toIndex;
        final List<Integer> result = new ArrayList<>();
        int index = fromIndex;
        while (index <= toIndex) {
            result.add(index++);
        }
        return result;
    }

    /**
     * Returns the list of indexes to be added :
     * When adding several rows/columns to a single GridPane (targetIndexes >= 1),
     * each added row/column must be shifted as many times as rows/columns added before.
     *
     * @return the list of target indexes
     */
    static Set<Integer> getAddedIndexes(
            final Set<Integer> targetIndexes,
            final Position position) {
        final Set<Integer> result = new HashSet<>();
        int shiftIndex = 0;
        for (int targetIndex : targetIndexes) {
            int addedIndex = targetIndex + shiftIndex++;
            if (position == Position.BELOW || position == Position.AFTER) {
                addedIndex++;
            }
            result.add(addedIndex);
        }
        return result;
    }

    /**
     * Returns true if the selection is :
     * - either 1 or more GridPanes
     * - or 1 or more rows/columns within a single GridPane
     *
     * @param editor
     * @return
     */
    static boolean canPerformAdd(final Selection selection) {

        boolean result;
        final AbstractSelectionGroup asg = selection.getGroup();

        if (asg instanceof DefaultSelectionGroupFactory) {
            final DefaultSelectionGroupFactory osg = (DefaultSelectionGroupFactory) asg;
            result = true;
            for (FXOMObject obj : osg.getItems()) {
                if ((obj.getSceneGraphObject() instanceof GridPane) == false) {
                    result = false;
                    break;
                }
            }
        } else {
            result = asg instanceof GridSelectionGroup;
        }
        return result;
    }

    /**
     * Returns true if the selection is 1 or more rows/columns
     * within a single GridPane
     *
     * @param editor
     * @return
     */
    static boolean canPerformRemove(final Selection selection) {

        final AbstractSelectionGroup asg = selection.getGroup();

        return asg instanceof GridSelectionGroup;
    }

    /**
     * Returns true if the selection is 1 or more rows/columns
     * within a single GridPane
     *
     * @param editor
     * @return
     */
    static boolean canPerformMove(
            final Selection selection,
            final Position position,
            final GridPaneHierarchyMask.Factory maskFactory) {

        boolean result;
        final AbstractSelectionGroup asg = selection.getGroup();

        if (asg instanceof GridSelectionGroup) {
            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
            final FXOMObject gridPane = gsg.getHitItem();
            final Type type = gsg.getType();
            final GridPaneHierarchyMask mask = maskFactory.getMask(gridPane);

            switch (type) {
                case COLUMN:
                    if (position == Position.BEFORE) {
                        result = true;
                        for (int index : gsg.getIndexes()) {
                            // First index column cannot be moved before
                            if (index == 0) {
                                result = false;
                                break;
                            }
                        }
                    } else if (position == Position.AFTER) {
                        result = true;
                        for (int index : gsg.getIndexes()) {
                            // Last index column cannot be moved after
                            if (index == (mask.getColumnsSize() - 1)) {
                                result = false;
                                break;
                            }
                        }
                    } else {
                        result = false;
                    }
                    break;
                case ROW:
                    if (position == Position.ABOVE) {
                        result = true;
                        for (int index : gsg.getIndexes()) {
                            // First index row cannot be moved above
                            if (index == 0) {
                                result = false;
                                break;
                            }
                        }
                    } else if (position == Position.BELOW) {
                        result = true;
                        for (int index : gsg.getIndexes()) {
                            // Last index row cannot be moved below
                            if (index == (mask.getRowsSize() - 1)) {
                                result = false;
                                break;
                            }
                        }
                    } else {
                        result = false;
                    }
                    break;
                default:
                    result = false;
                    assert false;
            }
        } else {
            result = false;
        }
        return result;
    }
}
