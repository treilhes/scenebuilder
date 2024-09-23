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
package com.oracle.javafx.scenebuilder.tools.driver.tablecolumn;

import org.scenebuilder.fxml.api.subjects.ApplicationInstanceEvents;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.dnd.DropTarget;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Content;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.control.tring.AbstractGenericTring;
import com.oracle.javafx.scenebuilder.tools.driver.tableview.TableViewDesignInfoX;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class TableColumnTring extends AbstractGenericTring<Object> {

    private final TableViewDesignInfoX tableViewDesignInfo
            = new TableViewDesignInfoX();

    public TableColumnTring(
            Content contentPanelController,
            ApplicationInstanceEvents documentManager) {
        super(contentPanelController, documentManager, Object.class);

    }

    @Override
    public void defineDropTarget(DropTarget dropTarget) {
    }

    @Override
    public void initialize() {
        assert getFxomInstance().getSceneGraphObject().isInstanceOf(TableColumn.class);
    }

    public FXOMInstance getFxomInstance() {
        return (FXOMInstance) getFxomObject();
    }


    /*
     * AbstractGenericPring
     */

    @Override
    public Bounds getSceneGraphObjectBounds() {
        return tableViewDesignInfo.getColumnBounds(getTableColumn());
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        return getTableColumn().getTableView();
    }

    @Override
    public FXOMObject getFxomObjectProxy() {
        return getFxomObject().getParentObject();
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        final TableView<?> tableView = getTableColumn().getTableView();
        startListeningToLayoutBounds(tableView);
        startListeningToLocalToSceneTransform(tableView);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        final TableView<?> tableView = getTableColumn().getTableView();
        stopListeningToLayoutBounds(tableView);
        stopListeningToLocalToSceneTransform(tableView);
    }


    /*
     * Private
     */

    private TableColumn<?,?> getTableColumn() {
        assert getSceneGraphObject().isInstanceOf(TableColumn.class);
        return (TableColumn<?,?>) getSceneGraphObject();
    }
}
