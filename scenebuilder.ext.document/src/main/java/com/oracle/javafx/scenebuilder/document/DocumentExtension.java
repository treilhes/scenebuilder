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
package com.oracle.javafx.scenebuilder.document;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.document.actions.AbstractShowAction;
import com.oracle.javafx.scenebuilder.document.actions.HierarchyMenuProvider;
import com.oracle.javafx.scenebuilder.document.actions.ShowFxIdAction;
import com.oracle.javafx.scenebuilder.document.actions.ShowInfoAction;
import com.oracle.javafx.scenebuilder.document.actions.ShowNodeIdAction;
import com.oracle.javafx.scenebuilder.document.actions.ToggleDocumentVisibilityAction;
import com.oracle.javafx.scenebuilder.document.actions.ToggleExpertViewAction;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyAnimationScheduler;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyCellAssignment;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyController;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyDNDController;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyDropTarget;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyInsertLine;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyParentRing;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyTaskScheduler;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.FxIdDisplayOption;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.MetadataInfoDisplayOption;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.NodeIdDisplayOption;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeCell;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeViewController;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.TreeItemFactory;
import com.oracle.javafx.scenebuilder.document.i18n.I18NDocument;
import com.oracle.javafx.scenebuilder.document.info.InfoPanelController;
import com.oracle.javafx.scenebuilder.document.preferences.document.ShowExpertByDefaultPreference;
import com.oracle.javafx.scenebuilder.document.preferences.global.DisplayOptionPreference;
import com.oracle.javafx.scenebuilder.document.view.DocumentPanelController;
import com.oracle.javafx.scenebuilder.extension.AbstractExtension;

public class DocumentExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("1eed13de-9e30-407a-822b-f6c350bea4c9");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                AbstractShowAction.ShowActionToggle.class,
                DisplayOptionPreference.class,
                DocumentPanelController.class,
                FxIdDisplayOption.class,
                HierarchyAnimationScheduler.class,
                HierarchyCellAssignment.class,
                HierarchyController.class,
                HierarchyDNDController.class,
                HierarchyDropTarget.class,
                HierarchyDropTarget.Factory.class,
                HierarchyInsertLine.class,
                HierarchyMenuProvider.class,
                HierarchyParentRing.class,
                HierarchyTaskScheduler.class,
                HierarchyTreeCell.class,
                HierarchyTreeCell.Factory.class,
                HierarchyController.class,
                HierarchyTreeViewController.class,
                InfoPanelController.class,
                I18NDocument.class,
                MetadataInfoDisplayOption.class,
                NodeIdDisplayOption.class,
                TreeItemFactory.class,
                ShowExpertByDefaultPreference.class,
                ShowFxIdAction.class,
                ShowInfoAction.class,
                ShowNodeIdAction.class,
                ToggleDocumentVisibilityAction.class,
                ToggleExpertViewAction.class
            );
     // @formatter:on
    }
}
