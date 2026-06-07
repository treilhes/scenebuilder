/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;

import com.oracle.javafx.scenebuilder.document.api.DisplayOption;
import com.oracle.javafx.scenebuilder.document.api.Hierarchy;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.api.HierarchyPanel;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyCellAssignment;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyDNDController;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyParentRing;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.test.EmcInject;
import com.treilhes.emc4j.test.EmcInjectMock;
import com.treilhes.jfxplace.core.api.tooltheme.ToolStylesheetProvider;
import com.treilhes.jfxplace.core.api.ui.controller.misc.InlineEdit;
import com.treilhes.jfxplace.fxom.api.dnd.Drag;
import com.treilhes.jfxplace.fxom.api.error.ErrorReport;
import com.treilhes.jfxplace.fxom.api.error.ErrorReportEntry;
import com.treilhes.jfxplace.fxom.model.FXOMDocument;
import com.treilhes.jfxplace.fxom.model.FXOMInstance;
import com.treilhes.jfxplace.fxom.model.pipeline.FXOMDocumentFactory;
import com.treilhes.jfxplace.test.JfxPlaceTest;
import com.treilhes.jfxplace.test.builder.StageBuilder;
import com.treilhes.jfxplace.test.builder.StageType;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;

/**
 *
 */
@JfxPlaceTest(classes = { HierarchyTreeCell.class, HierarchyTreeCell.Factory.class })
class HierarchyTreeCellTest {

    @EmcInjectMock
    private Drag drag;
    @EmcInjectMock
    private InlineEdit inlineEdit;
    @EmcInjectMock
    private ErrorReport errorReport;
    @EmcInjectMock
    private TreeItem<HierarchyItem> treeItem;
    @EmcInjectMock
    private HierarchyItem item;
    @EmcInjectMock
    private DisplayOption displayOption;
//    @EmcInjectMock
//    private EmContext context;
    @EmcInjectMock
    private Hierarchy panelController;

    @EmcInjectMock
    HierarchyPanel hierarchyPanel;
    @EmcInjectMock
    HierarchyCellAssignment cellAssignment;
    @EmcInjectMock
    HierarchyParentRing parentRing;
    @EmcInjectMock
    HierarchyDNDController dndController;

    @EmcInject
    HierarchyTreeCell.Factory factory;

    @EmcInject
    StageBuilder builder;

    @Mock
    ErrorReportEntry errorReportEntry;

    private FXOMDocument document = FXOMDocumentFactory.DEFAULT.newDocument();

    @BeforeEach
    void setupMocking() {
        Mockito.when(panelController.displayOptionProperty())
                .thenReturn(new SimpleObjectProperty<DisplayOption>(displayOption));
        Mockito.when(panelController.getDisplayOption()).thenReturn(displayOption);

        Mockito.when(displayOption.hasValue(Mockito.any())).thenReturn(true);
        Mockito.when(displayOption.getResolvedValue(Mockito.any())).thenReturn("getResolvedValue");
        Mockito.when(displayOption.isReadOnly(Mockito.any())).thenReturn(false);

    }
    /**
     * Test method for {@link com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeCell#HierarchyTreeCell(com.treilhes.jfxplace.core.api.ui.controller.misc.InlineEdit, com.treilhes.jfxplace.fxom.api.error.ErrorReport, com.treilhes.jfxplace.fxom.api.dnd.Drag)}.
     */
    @Test
    void testHierarchyTreeCell(EmContext context, FxRobot robot) {

        Mockito.when(item.getFxomObject()).thenReturn(new FXOMInstance(document, "sometag"));
//        Mockito.when(item.getFxomObject()).thenReturn(new FXOMInclude(document, ""));

        Mockito.when(item.getPlaceHolderImage()).thenReturn(new Image(getClass().getResourceAsStream("icon.png")));
        Mockito.when(item.getPlaceHolderInfo()).thenReturn("getPlaceHolderInfo");

        Mockito.when(item.getClassNameIcon()).thenReturn(new Image(getClass().getResourceAsStream("icon.png")));
        Mockito.when(item.getClassNameInfo()).thenReturn("getClassNameInfo");

        // error
        Mockito.when(errorReport.query(Mockito.any(), Mockito.anyBoolean())).thenReturn(List.of(errorReportEntry));
        Mockito.when(errorReportEntry.getText()).thenReturn("some error text");

        try(var testStage = builder
                .controller()
                .css(ToolStylesheetProvider.builder()
                        //.stylesheet(CssPanelController.class.getResource("css/ThemeDark_common.css").toExternalForm())
                        //.stylesheet(CssPanelController.class.getResource("css/ThemeDark_SBKIT-css-panel.css").toExternalForm())
                        .build())
                .setup(StageType.Fill)
                .size(800, 600).show()) {

            var treeView = new TreeView<HierarchyItem>();
            treeView.setCellFactory(t -> factory.newCell(hierarchyPanel));
            treeView.setRoot(new TreeItem<>(item));

            var stage = testStage.getStage();

            robot.interact(() -> stage.getScene().setRoot(treeView));

            System.out.println();
        }
    }
}
