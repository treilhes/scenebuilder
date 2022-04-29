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
package com.oracle.javafx.scenebuilder.document.api;

import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public interface HierarchyPanel {

    /**
     * Gets the tree view.
     *
     * @return the tree view
     */
    TreeView<HierarchyItem> getTreeView();

    /**
     * Gets the treeview root item.
     *
     * @return the root item
     */
    TreeItem<HierarchyItem> getRootItem();

    /**
     * Lookup tree item associated with the provided {@link FXOMObject}.
     *
     * @param fxomObject the fxom object
     * @return the tree item
     */
    TreeItem<HierarchyItem> lookupTreeItem(FXOMObject fxomObject);

    /**
     * Returns the last visible TreeItem
     * @return the last visible TreeItem
     */
    TreeItem<HierarchyItem> getLastVisibleTreeItem();

    /**
     * Returns the last visible TreeItem descendant of the specified parent
     * TreeItem.
     *
     * @param parentTreeItem the parent TreeItem
     * @return the last visible TreeItem
     */
    TreeItem<HierarchyItem> getLastVisibleTreeItem(TreeItem<HierarchyItem> parentTreeItem);

    /**
     * Gets the common parent TreeItem of the specified TreeItems.
     *
     * @param treeItems the tree items
     * @return the common parent tree item
     */
    TreeItem<HierarchyItem> getCommonParentTreeItem(List<TreeItem<HierarchyItem>> treeItems);

    void scrollTo(TreeItem<HierarchyItem> treeItem);

    /**
     * @return
     */
    Parent getRoot();

    /**
     * @return
     */
    double getContentTopY();

    /**
     * @return
     */
    double getContentBottomY();

}
