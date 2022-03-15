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

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Drag;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.DocumentDragSource;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.ExternalDragSource;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.MetadataInfoDisplayOption;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeCell;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeViewController;
import com.oracle.javafx.scenebuilder.document.preferences.document.ShowExpertByDefaultPreference;

/**
 * Concrete class to create and control the Hierarchy Panel of Scene Builder
 * Kit. Update this class inheritance in order to use either a TreeView or a
 * TreeTableView in hierarchy.
 *
 * p
 */
@Component
@Scope(value = SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class HierarchyPanelController extends HierarchyTreeViewController {

    public HierarchyPanelController(
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager,
            Editor editor,
            JobManager jobManager,
            Drag drag,
            Selection selection,
            ShowExpertByDefaultPreference showExpertByDefaultPreference,
            DocumentDragSource.Factory documentDragSourceFactory,
            ExternalDragSource.Factory externalDragSourceFactory,
            DesignHierarchyMask.Factory designHierarchyMaskFactory,
            HierarchyTreeCell.Factory hierarchyTreeCellFactory,
            HierarchyDNDController.Factory hierarchyDNDControllerFactory,
            MetadataInfoDisplayOption defaultDisplayOptions) {
        super(scenebuilderManager, documentManager, editor, jobManager, drag, selection, showExpertByDefaultPreference,
                documentDragSourceFactory, externalDragSourceFactory, designHierarchyMaskFactory,
                hierarchyTreeCellFactory, hierarchyDNDControllerFactory, defaultDisplayOptions);
    }
}
