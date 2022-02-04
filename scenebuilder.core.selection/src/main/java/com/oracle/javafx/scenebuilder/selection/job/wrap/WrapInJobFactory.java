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
package com.oracle.javafx.scenebuilder.selection.job.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@Lazy
public final class WrapInJobFactory {

    private final WrapInAnchorPaneJob.Factory wrapInAnchorPaneJobFactory;
    private final WrapInBorderPaneJob.Factory wrapInBorderPaneJobFactory;
    private final WrapInButtonBarJob.Factory wrapInButtonBarJobFactory;
    private final WrapInDialogPaneJob.Factory wrapInDialogPaneJobFactory;
    private final WrapInFlowPaneJob.Factory wrapInFlowPaneJobFactory;
    private final WrapInGridPaneJob.Factory wrapInGridPaneJobFactory;
    private final WrapInGroupJob.Factory wrapInGroupJobFactory;
    private final WrapInHBoxJob.Factory wrapInHBoxJobFactory;
    private final WrapInPaneJob.Factory wrapInPaneJobFactory;
    private final WrapInScrollPaneJob.Factory wrapInScrollPaneJobFactory;
    private final WrapInSplitPaneJob.Factory wrapInSplitPaneJobFactory;
    private final WrapInStackPaneJob.Factory wrapInStackPaneJobFactory;
    private final WrapInTabPaneJob.Factory wrapInTabPaneJobFactory;
    private final WrapInTextFlowJob.Factory wrapInTextFlowJobFactory;
    private final WrapInTilePaneJob.Factory wrapInTilePaneJobFactory;
    private final WrapInTitledPaneJob.Factory wrapInTitledPaneJobFactory;
    private final WrapInToolBarJob.Factory wrapInToolBarJobFactory;
    private final WrapInSceneJob.Factory wrapInSceneJobFactory;
    private final WrapInStageJob.Factory wrapInStageJobFactory;
    private final WrapInVBoxJob.Factory wrapInVBoxJobFactory;

    protected WrapInJobFactory(
            WrapInAnchorPaneJob.Factory wrapInAnchorPaneJobFactory,
            WrapInBorderPaneJob.Factory wrapInBorderPaneJobFactory,
            WrapInButtonBarJob.Factory wrapInButtonBarJobFactory,
            WrapInDialogPaneJob.Factory wrapInDialogPaneJobFactory,
            WrapInFlowPaneJob.Factory wrapInFlowPaneJobFactory,
            WrapInGridPaneJob.Factory wrapInGridPaneJobFactory,
            WrapInGroupJob.Factory wrapInGroupJobFactory,
            WrapInHBoxJob.Factory wrapInHBoxJobFactory,
            WrapInPaneJob.Factory wrapInPaneJobFactory,
            WrapInScrollPaneJob.Factory wrapInScrollPaneJobFactory,
            WrapInSplitPaneJob.Factory wrapInSplitPaneJobFactory,
            WrapInStackPaneJob.Factory wrapInStackPaneJobFactory,
            WrapInTabPaneJob.Factory wrapInTabPaneJobFactory,
            WrapInTextFlowJob.Factory wrapInTextFlowJobFactory,
            WrapInTilePaneJob.Factory wrapInTilePaneJobFactory,
            WrapInTitledPaneJob.Factory wrapInTitledPaneJobFactory,
            WrapInToolBarJob.Factory wrapInToolBarJobFactory,
            WrapInSceneJob.Factory wrapInSceneJobFactory,
            WrapInStageJob.Factory wrapInStageJobFactory,
            WrapInVBoxJob.Factory wrapInVBoxJobFactory
            ) {
        this.wrapInAnchorPaneJobFactory = wrapInAnchorPaneJobFactory;
        this.wrapInBorderPaneJobFactory = wrapInBorderPaneJobFactory;
        this.wrapInButtonBarJobFactory = wrapInButtonBarJobFactory;
        this.wrapInDialogPaneJobFactory = wrapInDialogPaneJobFactory;
        this.wrapInFlowPaneJobFactory = wrapInFlowPaneJobFactory;
        this.wrapInGridPaneJobFactory = wrapInGridPaneJobFactory;
        this.wrapInGroupJobFactory = wrapInGroupJobFactory;
        this.wrapInHBoxJobFactory = wrapInHBoxJobFactory;
        this.wrapInPaneJobFactory = wrapInPaneJobFactory;
        this.wrapInScrollPaneJobFactory = wrapInScrollPaneJobFactory;
        this.wrapInSplitPaneJobFactory = wrapInSplitPaneJobFactory;
        this.wrapInStackPaneJobFactory = wrapInStackPaneJobFactory;
        this.wrapInTabPaneJobFactory = wrapInTabPaneJobFactory;
        this.wrapInTextFlowJobFactory = wrapInTextFlowJobFactory;
        this.wrapInTilePaneJobFactory = wrapInTilePaneJobFactory;
        this.wrapInTitledPaneJobFactory = wrapInTitledPaneJobFactory;
        this.wrapInToolBarJobFactory = wrapInToolBarJobFactory;
        this.wrapInSceneJobFactory = wrapInSceneJobFactory;
        this.wrapInStageJobFactory = wrapInStageJobFactory;
        this.wrapInVBoxJobFactory = wrapInVBoxJobFactory;
    }

  //TODO find who use this method and make them extend the result
    //TODO remove "ToExtend" to get the original method name (was added to generate compilation errors and find users)
    //TODO or delete if not used
    public AbstractWrapInJob getWrapInJob(Class<?> wrappingClass) {

        assert getClassesSupportingWrapping().contains(wrappingClass);
        final AbstractWrapInJob job;
        if (wrappingClass == javafx.scene.layout.AnchorPane.class) {
            job = wrapInAnchorPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.layout.BorderPane.class) {
            job = wrapInBorderPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.control.ButtonBar.class) {
            job = wrapInButtonBarJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.control.DialogPane.class) {
            job = wrapInDialogPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.layout.FlowPane.class) {
            job = wrapInFlowPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.layout.GridPane.class) {
            job = wrapInGridPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.Group.class) {
            job = wrapInGroupJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.layout.HBox.class) {
            job = wrapInHBoxJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.layout.Pane.class) {
            job = wrapInPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.control.ScrollPane.class) {
            job = wrapInScrollPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.control.SplitPane.class) {
            job = wrapInSplitPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.layout.StackPane.class) {
            job = wrapInStackPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.control.TabPane.class) {
            job = wrapInTabPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.text.TextFlow.class) {
            job = wrapInTextFlowJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.layout.TilePane.class) {
            job = wrapInTilePaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.control.TitledPane.class) {
            job = wrapInTitledPaneJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.control.ToolBar.class) {
            job = wrapInToolBarJobFactory.getJob();
        } else if (wrappingClass == javafx.scene.Scene.class) {
            job = wrapInSceneJobFactory.getJob();
        } else if (wrappingClass == javafx.stage.Stage.class) {
            job = wrapInStageJobFactory.getJob();
        } else {
            assert wrappingClass == javafx.scene.layout.VBox.class; // Because of (1)
            job = wrapInVBoxJobFactory.getJob();
        }
        return job;
    }

    private static List<Class<?>> classesSupportingWrapping;

    //TODO reactivate {link EditorController#performWrap(java.lang.Class)} after refactoring wrapping feature
    /**
     * Return the list of classes that can be passed to
     * {link EditorController#performWrap(java.lang.Class)}.
     *
     * @return the list of classes.
     */
    public static synchronized Collection<Class<?>> getClassesSupportingWrapping() {
        if (classesSupportingWrapping == null) {
            classesSupportingWrapping = new ArrayList<>();
            classesSupportingWrapping.add(javafx.scene.layout.AnchorPane.class);
            classesSupportingWrapping.add(javafx.scene.layout.BorderPane.class);
            classesSupportingWrapping.add(javafx.scene.control.ButtonBar.class);
            classesSupportingWrapping.add(javafx.scene.control.DialogPane.class);
            classesSupportingWrapping.add(javafx.scene.layout.FlowPane.class);
            classesSupportingWrapping.add(javafx.scene.layout.GridPane.class);
            classesSupportingWrapping.add(javafx.scene.Group.class);
            classesSupportingWrapping.add(javafx.scene.layout.HBox.class);
            classesSupportingWrapping.add(javafx.scene.layout.Pane.class);
            classesSupportingWrapping.add(javafx.scene.control.ScrollPane.class);
            classesSupportingWrapping.add(javafx.scene.control.SplitPane.class);
            classesSupportingWrapping.add(javafx.scene.layout.StackPane.class);
            classesSupportingWrapping.add(javafx.scene.control.TabPane.class);
            classesSupportingWrapping.add(javafx.scene.text.TextFlow.class);
            classesSupportingWrapping.add(javafx.scene.layout.TilePane.class);
            classesSupportingWrapping.add(javafx.scene.control.TitledPane.class);
            classesSupportingWrapping.add(javafx.scene.control.ToolBar.class);
            classesSupportingWrapping.add(javafx.scene.layout.VBox.class);
            classesSupportingWrapping.add(javafx.scene.Scene.class);
            classesSupportingWrapping.add(javafx.stage.Stage.class);
            classesSupportingWrapping = Collections.unmodifiableList(classesSupportingWrapping);
        }

        return classesSupportingWrapping;
    }
}
