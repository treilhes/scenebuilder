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
package com.gluonhq.jfxapps.core.api.editor.selection;

import java.util.Collection;
import java.util.Map;

import com.gluonhq.jfxapps.core.api.mask.Accessory;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import jakarta.annotation.Nullable;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Node;

public interface Selection {

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
     * Check if the current selection objects are all instances of a {@link Control}
     *
     * @return true if the current selection objects are all instances of a
     *         {@link Control}, false otherwise.
     */
    boolean isSelectionControl();

    /**
     * Check if the current selection objects are all instances of a {@link Node},
     *
     * @return true if the current selection objects are all instances of a
     *         {@link Node}, false otherwise.
     */
    boolean isSelectionNode();

    /**
     * Returns the property holding the revision number of this selection.
     * Selection class adds +1 to this number each time the selection changes.
     *
     * @return the property holding the revision number of this selection.
     */
    ReadOnlyIntegerProperty revisionProperty();

    /**
     * Selection can be moved if true
     *
     * @return can be moved
     */
    boolean isMovable();

    public Map<String, FXOMObject> collectSelectedFxIds();

    public Accessory getTargetAccessory();

    public void select(Accessory targetAccessory);

    /**
     * Returns the revision number of this selection.
     *
     * @return the revision number of this selection.
     */
    int getRevision();


    Node getCheckedHitNode();


    /**
     * Replaces the selected items by the one from the specified selection group.
     *
     * @param newGroup null or the selection group defining items to be selected
     */
    void select(@Nullable SelectionGroup newGroup);

    boolean isSelected(SelectionGroup selectedGroup);

    FXOMObject getHitItem();

    void toggleSelection(SelectionGroup toggleGroup);

    /**
     * Returns null or the first selected ancestor of the specified fxom object.
     *
     * @param fxomObject an fxom object
     * @return null or the first selected ancestor of the specified fxom object.
     */
    FXOMObject lookupSelectedAncestor(FXOMObject fxomObject);

    /**
     * Empties this selection.
     * This routine adds +1 to the revision number.
     *
     */
    void clear();

    /**
     * Returns true if this selection is empty ie its selection group is null.
     * s
     * @return  true if this selection is empty.
     */
    boolean isEmpty();

    /**
     * Returns the group associated to this selection.
     * If this selection is empty, null is returned.
     *
     * @return  the group containing the selected items or null if selection is empty.
     */
    SelectionGroup getGroup();

    /**
     * Returns number of nanoseconds taken to execute selection listeners.
     *
     * @return number of nanoseconds taken to execute selection listeners.
     */
    long getLastListenerInvocationTime();

    /**
     * Begins an update sequence. Subsequent calls to select() and clear()
     * do not trigger any revision incrementation.
     */
    void beginUpdate();

    /**
     * Ends an update sequence. Revision is incremented.
     */
    void endUpdate();

    /**
     * Returns the common ancestor of the selected items or null if selection
     * is empty or root object is selected.
     *
     * @return
     */
    FXOMObject getAncestor();

    /**
     * Returns true if the selected objects are all connected to the
     * specified documents.
     *
     * @param fxomDocument an fxom document (not null)
     * @return true if the selected objects are all connected to the
     * specified documents.
     */
    boolean isValid(FXOMDocument fxomDocument);

    /**
     * Replaces the selected items by the specified fxom object and hit node.
     * This routine adds +1 to the revision number.
     *
     * @param fxomObject the object to be selected
     * @param hitNode null or the node hit by the mouse during selection
     */
    void select(FXOMObject fxomObject, Node hitNode);

    /**
     * Replaces the selected items by the specified fxom object.
     * This routine adds +1 to the revision number.
     *
     * @param fxomObject the object to be selected
     */
    void select(FXOMObject fxomObject);

    /**
     * Replaces the selected items by the specified fxom objects.
     * This routine adds +1 to the revision number.
     *
     * @param fxomObjects the objects to be selected
     */
    void select(Collection<FXOMObject> fxomObjects);

    /**
     * Replaces the selected items by the specified fxom objects.
     * This routine adds +1 to the revision number.
     *
     * @param fxomObjects the objects to be selected
     * @param hitObject the object hit by the mouse during selection
     * @param hitNode null or the node hit by the mouse during selection
     */
    void select(Collection<FXOMObject> fxomObjects, FXOMObject hitObject, Node hitNode);

    /**
     * Update the hit object and hit point of the current selection.
     *
     * @param hitObject the object hit by the mouse during selection
     * @param hitNode null or the node hit by the mouse during selection
     */
    void updateHitObject(FXOMObject hitObject, Node hitNode);

    /**
     * @param hitObject
     */
    void toggleSelection(FXOMObject hitObject);

    void selectNext();
    void selectPrevious();
    void selectAll();

    boolean isSelected(FXOMObject fxomObject);


}