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

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.dnd.Drag;
import com.oracle.javafx.scenebuilder.api.error.ErrorReport;
import com.oracle.javafx.scenebuilder.api.error.ErrorReport.ErrorReportEntry;
import com.oracle.javafx.scenebuilder.api.ui.misc.InlineEdit;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInclude;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.document.api.DisplayOption;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyController;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 */
@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class HierarchyTreeCellTest {

    private Stage stage;
    private Pane pane;

    @Mock
    private Drag drag;
    @Mock
    private InlineEdit inlineEdit;
    @Mock
    private ErrorReport errorReport;
    @Mock
    private TreeItem<HierarchyItem> treeItem;
    @Mock
    private HierarchyItem item;
    @Mock
    private DisplayOption displayOption;
    @Mock
    private SceneBuilderBeanFactory context;
    @Mock
    private HierarchyController panelController;
    @InjectMocks
    private HierarchyTreeCell.Factory factory;
    @InjectMocks
    private HierarchyTreeCell<HierarchyItem> cell;

    private FXOMDocument document = new FXOMDocument();
    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        this.stage = stage;
        pane = new Pane();
        stage.setScene(new Scene(pane, 800, 100, Color.BEIGE));
        stage.getScene().getStylesheets().add("file:///C:/SSDDrive/git/scenebuilder/scenebuilder.ext.sb/src/main/resources/com/oracle/javafx/scenebuilder/sb/css/ThemeDark.css");
        stage.show();
    }

    @BeforeEach
    public void setupMocking() {
        Mockito.when(panelController.displayOptionProperty()).thenReturn(new SimpleObjectProperty<DisplayOption>(displayOption));
        Mockito.when(panelController.getDisplayOption()).thenReturn(displayOption);

        Mockito.when(displayOption.hasValue(Mockito.any())).thenReturn(true);
        Mockito.when(displayOption.getResolvedValue(Mockito.any())).thenReturn("getResolvedValue");
        Mockito.when(displayOption.isReadOnly(Mockito.any())).thenReturn(true);

        Mockito.when(context.getBean(HierarchyTreeCell.class)).thenReturn(cell);

        Mockito.when(errorReport.query(Mockito.any(), Mockito.anyBoolean())).thenReturn(null);

        Mockito.when(treeItem.isExpanded()).thenReturn(false);
        Mockito.when(treeItem.expandedProperty()).thenReturn(new SimpleBooleanProperty());
        Mockito.when(treeItem.leafProperty()).thenReturn(new SimpleBooleanProperty());
        cell.updateTreeItem(treeItem);

    }
    /**
     * Test method for {@link com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeCell#HierarchyTreeCell(com.oracle.javafx.scenebuilder.api.ui.misc.InlineEdit, com.oracle.javafx.scenebuilder.api.error.ErrorReport, com.oracle.javafx.scenebuilder.api.dnd.Drag)}.
     */
    @Test
    void testHierarchyTreeCell(FxRobot robot) {

        //Mockito.when(item.getFxomObject()).thenReturn(new FXOMInstance(document, "sometag"));
        Mockito.when(item.getFxomObject()).thenReturn(new FXOMInclude(document, ""));

        Mockito.when(item.getPlaceHolderImage()).thenReturn(new Image(getClass().getResourceAsStream("icon.png")));
        Mockito.when(item.getPlaceHolderInfo()).thenReturn("getPlaceHolderInfo");

        Mockito.when(item.getClassNameIcon()).thenReturn(new Image(getClass().getResourceAsStream("icon.png")));
        Mockito.when(item.getClassNameInfo()).thenReturn("getClassNameInfo");

        // error
        Mockito.when(errorReport.query(Mockito.any(), Mockito.anyBoolean())).thenReturn(List.of(new ErrorReportEntry() {

            @Override
            public FXOMNode getFxomNode() {
                return null;
            }

            @Override
            public Type getType() {
                return null;
            }

            @Override
            public CSSParsingReport getCssParsingReport() {
                return null;
            }

        }));
        Mockito.when(errorReport.getText(Mockito.any())).thenReturn("some error text");

        final HierarchyTreeCell<HierarchyItem> cell = (HierarchyTreeCell<HierarchyItem>)factory.newCell(panelController);

        cell.updateItem(item, false);
        robot.interact(() -> pane.getChildren().add(cell.getGraphic()));

        System.out.println();
    }

    /**
     * Test method for {@link com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeCell#updateItem(com.oracle.javafx.scenebuilder.document.api.HierarchyItem, boolean)}.
     */
    @Test
    void testUpdateItemHierarchyItemBoolean() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeCell#updatePlaceHolder()}.
     */
    @Test
    void testUpdatePlaceHolder() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeCell#startEditingDisplayOption()}.
     */
    @Test
    void testStartEditingDisplayInfo() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeCell#toString()}.
     */
    @Test
    void testToString() {
        fail("Not yet implemented");
    }

}
