/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.document.panel.hierarchy;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyCellAssignment;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyController;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyDNDController;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyParentRing;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.MetadataInfoDisplayOption;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeViewController;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.TreeItemFactory;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.test.EmcInject;
import com.treilhes.emc4j.test.EmcInjectMock;
import com.treilhes.jfxplace.core.api.ctxmenu.ContextMenu;
import com.treilhes.jfxplace.core.api.job.JobManager;
import com.treilhes.jfxplace.core.api.tooltheme.ToolStylesheetProvider;
import com.treilhes.jfxplace.core.api.ui.controller.misc.InlineEdit;
import com.treilhes.jfxplace.fxom.api.editor.selection.FxomSelection;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMDocument;
import com.treilhes.jfxplace.fxom.model.FXOMInstance;
import com.treilhes.jfxplace.fxom.model.pipeline.FXOMDocumentFactory;
import com.treilhes.jfxplace.test.JfxPlaceTest;
import com.treilhes.jfxplace.test.builder.StageBuilder;
import com.treilhes.jfxplace.test.builder.StageType;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

/**
 * The Class DocumentUiTest is at least for now a temp test to define starter test usage
 */
@JfxPlaceTest(classes = HierarchyController.class)
class DocumentUiTest {

    @EmcInjectMock
    ContextMenu contextMenu;

    @EmcInjectMock
    HierarchyCellAssignment cellAssignments;

    @EmcInjectMock
    HierarchyDNDController dndController;

    @EmcInjectMock
    HierarchyParentRing parentRing;

    @EmcInjectMock
    HierarchyTreeViewController hierarchyTreeView;

    @EmcInjectMock
    InlineEdit inlineEdit;

    @EmcInjectMock
    JobManager jobManager;

    @EmcInjectMock
    MetadataInfoDisplayOption defaultDisplayOptions;

    @EmcInjectMock
    FxomSelection selection;

    @EmcInjectMock
    TreeItemFactory rootTreeItemFactory;

    @EmcInject
    FxomEvents fxomEvents;

    @EmcInject
    StageBuilder builder;


//    @Spy
//    FxomEvents fxomEvents = new FxomEvents.FxomEventsImpl();
//
//    @Spy
//    ApplicationInstanceEvents documentManager = new ApplicationInstanceEvents.ApplicationInstanceEventsImpl();
//
////    @Mock
////    UpdateReferencesJob.Factory updateReferencesJobFactory;
//
//    @Mock
//    InlineEdit inlineEdit;
//
//    @Mock
//    ContextMenu contextMenu;
//
//    @Mock
//    JobManager jobManager;
//
//    @Mock
//    Drag drag;
//
//    @Mock
//    FxomSelection selection;
//
//    @Mock
//    ShowExpertByDefaultPreference showExpertByDefaultPreference;
//
//    @Mock
//    DocumentDragSource.Factory documentDragSourceFactory;
//
//    @Mock
//    ExternalDragSource.Factory externalDragSourceFactory;
//
//    @Mock
//    FXOMObjectMask.Factory designHierarchyMaskFactory;
//
//    @Mock
//    HierarchyTreeCell.Factory hierarchyTreeCellFactory;
//
//    @Mock
//    HierarchyDNDController dndController;
//
//    @Mock
//    MetadataInfoDisplayOption defaultDisplayOptions;
//
//    @Mock
//    FXOMObjectMask mask;
//
//    @Mock
//    ComponentClassMetadata ccm;
//
//    @Mock
//    HierarchyCellAssignment cellAssignments;
//
//    @Mock
//    HierarchyParentRing parentRing;
//
//    @Mock
//    SbMetadata metadata;

    @Test
    void testForTest(EmContext ctx) {

        //Mockito.when(metadata.queryComponentMetadata(Panel.class)).thenReturn(ccm);

//        //metadata for mask
//        Mockito.doReturn(ccm).when(metadata).queryComponentMetadata(Pane.class);
//        //Mockito.when(metadata.queryComponentMetadata(Pane.class)).thenReturn(ccm);
//        Mockito.when(ccm.getAllSubComponentProperties()).thenReturn(Collections.emptySet());
//
//        //setup
        Mockito.when(jobManager.revisionProperty()).thenReturn(new SimpleIntegerProperty());

        var pane = new Pane();
        var treeView = new TreeView<HierarchyItem>();
        pane.getChildren().add(treeView);

        Mockito.when(hierarchyTreeView.getTreeView()).thenReturn(treeView);
        Mockito.when(hierarchyTreeView.getRoot()).thenReturn(pane);
        Mockito.when(rootTreeItemFactory.makeRootItem(any())).thenReturn(new TreeItem<>());
        Mockito.when(hierarchyTreeView.getSelectedItems()).thenReturn(FXCollections.observableArrayList());

//        Mockito.when(designHierarchyMaskFactory.getMask(any())).thenReturn(mask);


        //Mockito.when(api.getMetadata().queryComponentMetadata(Panel.class)).thenReturn(ccm);

        try(var testStage = builder
                .controller()
                .css(ToolStylesheetProvider.builder()
                        //.stylesheet(CssPanelController.class.getResource("css/ThemeDark_common.css").toExternalForm())
                        //.stylesheet(CssPanelController.class.getResource("css/ThemeDark_SBKIT-css-panel.css").toExternalForm())
                        .build())
                .setup(StageType.Fill)
                .size(800, 600).show()) {

            var hc = ctx.getBean(HierarchyController.class);
            var stage = testStage.getStage();
            stage.getScene().setRoot(hc.getRoot());

            FXOMDocument doc = FXOMDocumentFactory.DEFAULT.newDocument();
            FXOMInstance inst = new FXOMInstance(doc, Pane.class);
            doc.setFxomRoot(inst);
            fxomEvents.fxomDocument().set(doc);

            System.out.println();
        }

    }


}
