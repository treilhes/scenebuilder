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
package com.oracle.javafx.scenebuilder.document.panel.hierarchy;

import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.oracle.javafx.scenebuilder.api.ContextMenu;
import com.oracle.javafx.scenebuilder.api.Drag;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.DocumentDragSource;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.ExternalDragSource;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.document.panel.hierarchy.treeview.HierarchyTreeCell;
import com.oracle.javafx.scenebuilder.document.preferences.document.ShowExpertByDefaultPreference;
import com.oracle.javafx.scenebuilder.test.TestContext;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class DocumentUiTest {

//    static {
//        I18N.initForTest();
//    }

    private Stage stage;
    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        this.stage = stage;
    }

    @Test
    void testIfJavaFXIssueHasBeenSolved() {

        //ContextMenu
        ContextMenu ctxMenu = Mockito.mock(ContextMenu.class);

        //metadata for mask
        Metadata metadata = Mockito.mock(Metadata.class);
        ComponentClassMetadata ccm = Mockito.mock(ComponentClassMetadata.class);
        Mockito.when(metadata.queryComponentMetadata(Pane.class)).thenReturn(ccm);
        Mockito.when(ccm.getAllSubComponentProperties()).thenReturn(Collections.emptySet());

        DesignHierarchyMask mask = Mockito.mock(DesignHierarchyMask.class);

        //HierarchyPanelController constructor
        SceneBuilderManager scenebuilderManager = new SceneBuilderManager.SceneBuilderManagerImpl();
        DocumentManager documentManager = new DocumentManager.DocumentManagerImpl();
        Editor editor = Mockito.mock(Editor.class);
        JobManager jobManager = Mockito.mock(JobManager.class);
        Drag drag = Mockito.mock(Drag.class);
        Selection selection = Mockito.mock(Selection.class);
        ShowExpertByDefaultPreference showExpertByDefaultPreference = Mockito.mock(ShowExpertByDefaultPreference.class);
        DocumentDragSource.Factory documentDragSourceFactory = Mockito.mock(DocumentDragSource.Factory.class);
        ExternalDragSource.Factory externalDragSourceFactory = Mockito.mock(ExternalDragSource.Factory.class);
        DesignHierarchyMask.Factory designHierarchyMaskFactory = Mockito.mock(DesignHierarchyMask.Factory.class);
        HierarchyTreeCell.Factory hierarchyTreeCellFactory = Mockito.mock(HierarchyTreeCell.Factory.class);
        HierarchyDNDController.Factory hierarchyDNDControllerFactory = Mockito.mock(HierarchyDNDController.Factory.class);

        //setup
        Mockito.when(editor.getContextMenuController()).thenReturn(ctxMenu);
        Mockito.when(jobManager.revisionProperty()).thenReturn(new SimpleIntegerProperty());
        Mockito.when(designHierarchyMaskFactory.getMask(any())).thenReturn(mask);


        //Mockito.when(api.getMetadata().queryComponentMetadata(Panel.class)).thenReturn(ccm);


        FXOMDocument doc = new FXOMDocument();
        FXOMInstance inst = new FXOMInstance(doc, Pane.class);
        doc.setFxomRoot(inst);
        documentManager.fxomDocument().set(doc);

        HierarchyPanelController controller = new HierarchyPanelController(
                scenebuilderManager,
                documentManager,
                editor,
                jobManager,
                drag,
                selection,
                showExpertByDefaultPreference,
                documentDragSourceFactory,
                externalDragSourceFactory,
                designHierarchyMaskFactory,
                hierarchyTreeCellFactory,
                hierarchyDNDControllerFactory);

        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(controller.getFxmlURL());
        loader.setResources(controller.getResources());
        loader.setClassLoader(this.getClass().getClassLoader());

        try {
            controller.setRoot((Parent) loader.load());
            controller.controllerDidLoadFxml();
        } catch (RuntimeException | IOException x) {
            throw new RuntimeException(
                    String.format("Failed to load %s with %s",
                            loader.getLocation(), loader.getController()), x); // NOI18N
        }

        SbPlatform.runLater(() -> {
            controller.getRoot().getStylesheets().add("file:///C:/SSDDrive/git/scenebuilder/scenebuilder.ext.sb/src/main/resources/com/oracle/javafx/scenebuilder/sb/css/ThemeDark.css");
            Scene scene = new Scene(controller.getRoot(), 300, 600);
            stage.setScene(scene);
            stage.show();
        });

        System.out.println();
        System.out.println();


    }


    @Test
    void other() {

        ApplicationContext ctx = TestContext.get();


        FXOMDocument doc = new FXOMDocument();
        FXOMInstance inst = new FXOMInstance(doc, Pane.class);
        doc.setFxomRoot(inst);
        //documentManager.fxomDocument().set(doc);

        HierarchyPanelController controller = ctx.getBean(HierarchyPanelController.class);

        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(controller.getFxmlURL());
        loader.setResources(controller.getResources());
        loader.setClassLoader(this.getClass().getClassLoader());

        try {
            controller.setRoot((Parent) loader.load());
            controller.controllerDidLoadFxml();
        } catch (RuntimeException | IOException x) {
            throw new RuntimeException(
                    String.format("Failed to load %s with %s",
                            loader.getLocation(), loader.getController()), x); // NOI18N
        }

        SbPlatform.runLater(() -> {
            controller.getRoot().getStylesheets().add("file:///C:/SSDDrive/git/scenebuilder/scenebuilder.ext.sb/src/main/resources/com/oracle/javafx/scenebuilder/sb/css/ThemeDark.css");
            Scene scene = new Scene(controller.getRoot(), 300, 600);
            stage.setScene(scene);
            stage.show();
        });

        System.out.println();
        System.out.println();


    }

}