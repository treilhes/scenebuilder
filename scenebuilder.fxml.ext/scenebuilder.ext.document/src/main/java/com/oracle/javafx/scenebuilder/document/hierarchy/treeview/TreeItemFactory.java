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
package com.oracle.javafx.scenebuilder.document.hierarchy.treeview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.scenebuilder.fxml.api.HierarchyMask;
import org.scenebuilder.fxml.api.HierarchyMask.Accessory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.hierarchy.item.HierarchyItemAccessory;
import com.oracle.javafx.scenebuilder.document.hierarchy.item.HierarchyItemBase;
import com.oracle.javafx.scenebuilder.document.preferences.document.ShowExpertByDefaultPreference;

import javafx.scene.control.TreeItem;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class TreeItemFactory {

    private final static Logger logger = LoggerFactory.getLogger(TreeItemFactory.class);

    private final Map<ExpandedKey, Boolean> treeItemsExpandedMapProperty = new HashMap<>();

    private final HierarchyTreeViewController hierarchyTreeView;
    private final DesignHierarchyMask.Factory designHierarchyMaskFactory;
    private final ShowExpertByDefaultPreference showExpertByDefaultPreference;

    public TreeItemFactory(
            HierarchyTreeViewController hierarchyTreeView,
            ShowExpertByDefaultPreference showExpertByDefaultPreference,
            DesignHierarchyMask.Factory designHierarchyMaskFactory) {
        super();
        this.hierarchyTreeView = hierarchyTreeView;
        this.showExpertByDefaultPreference = showExpertByDefaultPreference;
        this.designHierarchyMaskFactory = designHierarchyMaskFactory;
    }

    public void clearExpandedMapCache() {
        treeItemsExpandedMapProperty.clear();
    }

    public void updateExpandedMapCache() {
        clearExpandedMapCache();
        treeItemsExpandedMapProperty.putAll(hierarchyTreeView.makeExpandedMap());
    }

    public TreeItem<HierarchyItem> makeRootItem(FXOMObject root) {
        TreeItem<HierarchyItem> rootTreeItem = makeTreeItem(root);
        rootTreeItem.setExpanded(true);
        return rootTreeItem;
    }

    /**
     * Make tree item accessory.
     * 3 possible cases: <br/>
     * - empty accessory : create an empty accessory placeholder
     * - accessory with one item : create a filed accessory placeholder
     * - accessory with >1 items : create an empty accessory placeholder with children treeItems
     *
     * @param owner the owner
     * @param accessory the accessory
     * @return the tree item
     */
    public TreeItem<HierarchyItem> makeTreeItemAccessory(
            final HierarchyMask owner,
            final Accessory accessory) {

        List<FXOMObject> values = owner.getAccessories(accessory, true);

        if (values.isEmpty()) {

            final HierarchyItemAccessory item = new HierarchyItemAccessory(designHierarchyMaskFactory, owner, null, accessory);
            return new TreeItem<>(item);
//        } else if (values.size() == 1) {
//
//            final FXOMObject firstFxomObject = values.get(0);
//            final HierarchyItemAccessory item = new HierarchyItemAccessory(designHierarchyMaskFactory, owner, firstFxomObject, accessory);
//            final TreeItem<HierarchyItem> treeItem = new TreeItem<>(item);
//            // Set back the TreeItem expanded property if any
//            Boolean expanded = treeItemsExpandedMapProperty.get(firstFxomObject);
//            if (expanded != null) {
//                treeItem.setExpanded(expanded);
//            }
//            // Mask may be null for empty place holder
//            if (item.getMask() != null) {
//                updateTreeItem(treeItem);
//            }
//            return treeItem;
        } else {
            final HierarchyItemAccessory item = new HierarchyItemAccessory(designHierarchyMaskFactory, owner, null, accessory);
            final TreeItem<HierarchyItem> treeItem = new TreeItem<>(item);

            // Set back the TreeItem expanded property if any
            Boolean expanded = treeItemsExpandedMapProperty.get(new ExpandedKey(owner.getFxomObject(), accessory));
            if (expanded != null) {
                treeItem.setExpanded(expanded);
            }

            for (final FXOMObject child:values) {
                 treeItem.getChildren().add(makeTreeItem(child));
            }
            return treeItem;
        }
    }

    private TreeItem<HierarchyItem> makeTreeItem(final FXOMObject fxomObject) {
        final HierarchyItem item = new HierarchyItemBase(designHierarchyMaskFactory, fxomObject);
        final TreeItem<HierarchyItem> treeItem = new TreeItem<>(item);
        // Set back the TreeItem expanded property if any
        Boolean expanded = treeItemsExpandedMapProperty.get(new ExpandedKey(fxomObject, null));
        if (expanded != null) {
            treeItem.setExpanded(expanded);
        }
        updateTreeItem(treeItem);
        return treeItem;
    }

    private void updateTreeItem(final TreeItem<HierarchyItem> treeItem) {

        final HierarchyMask mask = treeItem.getValue().getMask();
        assert mask != null;
        assert mask.getFxomObject() != null;

//        // get the current properties order
//        FXOMElement element = (FXOMElement)mask.getFxomObject().getP;
//
//        element.getProperties().values().stream()
//            .map(c -> c.getName())
//            .collect(Collectors.toList());


        Predicate<Accessory> canSeeExpertOrNotEmpty = Predicate.not(Accessory::isExpert)
                .or(a -> showExpertByDefaultPreference.getValue())
                .or(a -> !mask.getAccessories(a, true).isEmpty());

        List<Accessory> accessories = mask.getAccessories();
        //showExpertByDefaultPreference

        accessories.stream()
        .filter(Predicate.not(Accessory::isHidden))
        .filter(canSeeExpertOrNotEmpty)
        .peek(accessory -> logger.debug("Processing accessory {}", accessory.getName()))
        .forEach(accessory -> treeItem.getChildren().add(makeTreeItemAccessory(mask, accessory)));

        // Sub components
        //---------------------------------
        if (mask.hasMainAccessory()) {
            mask.getAccessories(mask.getMainAccessory(), true)
                .forEach(v -> treeItem.getChildren().add(makeTreeItem(v)));
        }
    }
}
