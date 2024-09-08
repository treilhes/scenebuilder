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
package com.oracle.javafx.scenebuilder.document.hierarchy;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell.BorderSide;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.TreeItemFactory;

import javafx.scene.control.TreeItem;

/**
 * Used to schedule :
 *
 * - tree items expand tasks
 * - graphic tree item place holder
 *
 * when DND within hierarchy.
 *
 * p
 * @treatAsPrivate
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class HierarchyTaskScheduler {

    private final TreeItemFactory treeItemFactory;


    private final long timerDelay = 1000;
    private Timer timer;
    private TimerTask timerTask;
    private boolean isAddEmptyGraphicTaskScheduled = false;


    private final HierarchyCellAssignment cellAssignment;

    public HierarchyTaskScheduler(
            final TreeItemFactory treeItemFactory,
            final HierarchyCellAssignment cellAssignment) {
        super();
        this.treeItemFactory = treeItemFactory;
        this.cellAssignment = cellAssignment;
    }

    public void scheduleExpandTask(final TreeItem<HierarchyItem> treeItem) {
        timerTask = new HierarchyTimerTask(treeItem);
        assert timerTask != null;
        getTimer().schedule(timerTask, timerDelay);
    }

    public void scheduleAddEmptyGraphicTask(final TreeItem<HierarchyItem> treeItem) {
        final HierarchyItem item = treeItem.getValue();
        assert item != null;
        final HierarchyMask owner = item.getMask();
        assert owner != null;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // JavaFX data should only be accessed on the JavaFX thread.
                // => we must wrap the code into a Runnable object and call the SbPlatform.runLater
                JfxAppPlatform.runOnFxThread(() -> {

                    for (Accessory accessory:owner.getAccessories()) {
                      //TODO may be deletable
//                      final TreeItem<HierarchyItem> graphicTreeItem
//                              = panelController.makeTreeItemGraphic(owner, null);
                      final TreeItem<HierarchyItem> graphicTreeItem = treeItemFactory.makeTreeItemAccessory(owner, accessory);
                      // Add Graphic at first position
                      treeItem.getChildren().add(0, graphicTreeItem);
                      treeItem.setExpanded(true);

                      Optional<HierarchyCell> cell = cellAssignment.getCell(treeItem);
                      assert cell.isPresent();
                      cell.ifPresent((c) -> c.setBorder(BorderSide.TOP_RIGHT_BOTTOM_LEFT));

                      isAddEmptyGraphicTaskScheduled = false;
                    }
                });
            }
        };
        assert timerTask != null;
        getTimer().schedule(timerTask, timerDelay);
        isAddEmptyGraphicTaskScheduled = true;
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            isAddEmptyGraphicTaskScheduled = false;
            timer = null;
        }
    }

    public boolean isAddEmptyGraphicTaskScheduled() {
        return isAddEmptyGraphicTaskScheduled;
    }

    private Timer getTimer() {
        if (timer == null) {
            timer = new Timer(true);
        }
        return timer;
    }

    /**
     * *************************************************************************
     * Static inner class
     * *************************************************************************
     */
    private static class HierarchyTimerTask extends TimerTask {

        private final TreeItem<HierarchyItem> treeItem;

        HierarchyTimerTask(final TreeItem<HierarchyItem> treeItem) {
            super();
            this.treeItem = treeItem;
        }

        @Override
        public void run() {
            // JavaFX data should only be accessed on the JavaFX thread.
            // => we must wrap the code into a Runnable object and call the SbPlatform.runLater
            JfxAppPlatform.runOnFxThread(() -> treeItem.setExpanded(true));
        }
    }
}
