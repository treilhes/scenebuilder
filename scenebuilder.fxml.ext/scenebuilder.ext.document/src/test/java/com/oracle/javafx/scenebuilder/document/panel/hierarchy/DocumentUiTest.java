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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.ApplicationContext;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.oracle.javafx.scenebuilder.api.ContextMenu;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.dnd.Drag;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.DocumentDragSource;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.ExternalDragSource;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyCellAssignment;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyController;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyDNDController;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyParentRing;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.MetadataInfoDisplayOption;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeCell;
import com.oracle.javafx.scenebuilder.document.preferences.document.ShowExpertByDefaultPreference;
import com.oracle.javafx.scenebuilder.job.editor.reference.UpdateReferencesJob;
import com.oracle.javafx.scenebuilder.test.TestContext;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The Class DocumentUiTest is at least for now a temp test to define starter test usage
 */
@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
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

    @Spy
    SceneBuilderManager scenebuilderManager = new SceneBuilderManager.SceneBuilderManagerImpl();

    @Spy
    FxmlDocumentManager documentManager = new DocumentManager.DocumentManagerImpl();

    @Mock
    UpdateReferencesJob.Factory updateReferencesJobFactory;

    @Mock
    InlineEdit inlineEdit;

    @Mock
    ContextMenu contextMenuw;

    @Mock
    JobManager jobManager;

    @Mock
    Drag drag;

    @Mock
    Selection selection;

    @Mock
    ShowExpertByDefaultPreference showExpertByDefaultPreference;

    @Mock
    DocumentDragSource.Factory documentDragSourceFactory;

    @Mock
    ExternalDragSource.Factory externalDragSourceFactory;

    @Mock
    DesignHierarchyMask.Factory designHierarchyMaskFactory;

    @Mock
    HierarchyTreeCell.Factory hierarchyTreeCellFactory;

    @Mock
    HierarchyDNDController.Factory hierarchyDNDControllerFactory;

    @Mock
    MetadataInfoDisplayOption metadataInfoDisplayOption;

    @Mock
    DesignHierarchyMask mask;

    @Mock
    ComponentClassMetadata ccm;

    @Mock
    HierarchyCellAssignment cellAssignments;

    @Mock
    HierarchyParentRing parentRing;

    @Mock
    Metadata metadata;

    //@Test
    void testForTest() {

        //metadata for mask
        Mockito.when(metadata.queryComponentMetadata(Pane.class)).thenReturn(ccm);
        Mockito.when(ccm.getAllSubComponentProperties()).thenReturn(Collections.emptySet());

        //setup
        Mockito.when(jobManager.revisionProperty()).thenReturn(new SimpleIntegerProperty());
        Mockito.when(designHierarchyMaskFactory.getMask(any())).thenReturn(mask);


        //Mockito.when(api.getMetadata().queryComponentMetadata(Panel.class)).thenReturn(ccm);


        HierarchyController controller = new HierarchyController(scenebuilderManager, documentManager,
                inlineEdit, contextMenuw, jobManager, drag, selection, cellAssignments, parentRing, showExpertByDefaultPreference,
                documentDragSourceFactory, externalDragSourceFactory, designHierarchyMaskFactory,
                hierarchyTreeCellFactory, hierarchyDNDControllerFactory, metadataInfoDisplayOption);

        FXOMDocument doc = new FXOMDocument();
        FXOMInstance inst = new FXOMInstance(doc, Pane.class);
        doc.setFxomRoot(inst);
        documentManager.fxomDocument().set(doc);

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

        HierarchyController controller = ctx.getBean(HierarchyController.class);

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
