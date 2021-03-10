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
package com.oracle.javafx.scenebuilder.drivers.treetablecolumn;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.control.DropTarget;
import com.oracle.javafx.scenebuilder.api.control.tring.AbstractGenericTring;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.drivers.treetableview.TreeTableViewDesignInfoX;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class TreeTableColumnTring extends AbstractGenericTring<Object> {

    private final TreeTableViewDesignInfoX tableViewDesignInfo
            = new TreeTableViewDesignInfoX();

    public TreeTableColumnTring(Content contentPanelController) {
        super(contentPanelController, Object.class);
    }
    
    @Override
    public void defineDropTarget(DropTarget dropTarget) {
    }
    
    @Override
    public void initialize() {
        assert getFxomInstance().getSceneGraphObject() instanceof TreeTableColumn;
    }

    public FXOMInstance getFxomInstance() {
        return (FXOMInstance) getFxomObject();
    }


    /*
     * AbstractGenericPring
     */

    @Override
    public Bounds getSceneGraphObjectBounds() {
        return tableViewDesignInfo.getColumnBounds(getTreeTableColumn());
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        return getTreeTableColumn().getTreeTableView();
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        final TreeTableView<?> tableView = getTreeTableColumn().getTreeTableView();
        startListeningToLayoutBounds(tableView);
        startListeningToLocalToSceneTransform(tableView);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        final TreeTableView<?> tableView = getTreeTableColumn().getTreeTableView();
        stopListeningToLayoutBounds(tableView);
        stopListeningToLocalToSceneTransform(tableView);
    }


    /*
     * Private
     */

    private TreeTableColumn<?,?> getTreeTableColumn() {
        assert getSceneGraphObject() instanceof TreeTableColumn;
        return (TreeTableColumn<?,?>) getSceneGraphObject();
    }
}