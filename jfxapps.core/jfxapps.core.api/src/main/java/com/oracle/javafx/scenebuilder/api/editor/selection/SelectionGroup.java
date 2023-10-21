/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.editor.selection;

import java.util.List;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.scene.Node;

/**
 * This interface must be implemented by classes expecting to represent a selection content
 */
public interface SelectionGroup extends Cloneable {
    /**
     * Get the latest clicked/hit item in the selected {@link OMObject} collection
     * @return the latest clicked/hit item
     */
    public FXOMObject getHitItem();
    /**
     * Get the common ancestor {@link OMObject} to all selected {@link OMObject}
     * or null if root is selected
     * @return the common ancestor {@link OMObject}
     */
    public FXOMObject getAncestor();
    /**
     * A selection group is valid only if all the selected {@link OMObject} are part of the provided {@link FXOMDocument}
     * @param fxomDocument the owner document
     * @return true if all the selected {@link OMObject} are part of the provided {@link FXOMDocument}
     */
    public boolean isValid(FXOMDocument fxomDocument);
    /**
     * Get the collection of all the selected objects in the group
     * @return all the selected objects
     */
    public Set<? extends FXOMObject> getItems();
    /**
     * If all the selected objects in the group are siblings it will return the selected
     * objects and all the remaining unselected siblings<br/>
     * <br/>
     * What is a sibling object:<br/> - it is up to the implementor to decide but selectNext and selectPrevious will use it to navigate by default
     * @return the complete list of siblings objects or an empty list if selected objects are not siblings
     */
    List<? extends FXOMObject> getSiblings();

    SelectionGroup selectAll();

    SelectionGroup selectNext();

    SelectionGroup selectPrevious();

    SelectionGroup clone() throws CloneNotSupportedException;

    Node getCheckedHitNode();
//    Job makeDeleteJob();
    SelectionGroup toggle(SelectionGroup toggleGroup);
    boolean isSelected(SelectionGroup group);
}
