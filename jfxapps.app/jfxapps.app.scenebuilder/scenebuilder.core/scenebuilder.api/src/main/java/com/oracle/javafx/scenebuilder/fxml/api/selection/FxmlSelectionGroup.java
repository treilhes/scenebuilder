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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.om.OMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;

/**
 * This interface must be implemented by classes expecting to represent a selection content
 */
public interface FxmlSelectionGroup extends SelectionGroup, Cloneable {

    public boolean isMovable();

    // TODO : START: overrides only to keep the doc, move doc to impl
    /**
     * Get the latest clicked/hit item in the selected {@link FXOMObject} collection
     * @return the latest clicked/hit item
     */
    @Override
    public FXOMObject getHitItem();
    /**
     * Get the common ancestor {@link FXOMObject} to all selected {@link FXOMObject}
     * or null if root is selected
     * @return the common ancestor {@link FXOMObject}
     */
    @Override
    public FXOMObject getAncestor();
    /**
     * A selection group is valid only if all the selected {@link FXOMObject} are part of the provided {@link FXOMDocument}
     * @param fxomDocument the owner document
     * @return true if all the selected {@link FXOMObject} are part of the provided {@link FXOMDocument}
     */
    public boolean isValid(FXOMDocument fxomDocument);
    /**
     * Get the collection of all the selected objects in the group
     * @return all the selected objects
     */
    @Override
    public Set<FXOMObject> getItems();
    /**
     * If all the selected objects in the group are siblings it will return the selected
     * objects and all the remaining unselected siblings<br/>
     * <br/>
     * What is a sibling object:<br/>
     * - if part of a collection ( {@link FXOMPropertyC} ) : all other objects in the collection<br/>
     * - if part of a single valued ( {@link FXOMPropertyT} ) : all other objects in parent {@link FXOMPropertyT} properties<br/>
     * @return the complete list of siblings objects or an empty list if selected objects are not siblings
     */
    @Override
    List<FXOMObject> getSiblings();
 // TODO : END : overrides only to keep the doc, move doc to impl

    Set<? extends Object> getInnerItems();

    /**
     * Collect all the fx ids in the current selection.
     *
     * @return the map fx id : fxom object
     */
    Map<String, FXOMObject> collectSelectedFxIds();

    @Override
    FxmlSelectionGroup clone() throws CloneNotSupportedException;

    /**
     * Make delete job.
     *
     * @return the job
     */
    Job makeDeleteJob();
}