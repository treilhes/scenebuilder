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
package com.oracle.javafx.scenebuilder.editor.fxml;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.core.ui.controller.ContentPanelController;
import com.gluonhq.jfxapps.core.ui.controller.ContextMenuController;
import com.oracle.javafx.scenebuilder.api.SbApiExtension;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.BringForwardAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.BringToFrontAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.FocusContentAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.SendBackwardAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.SendToBackAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.SetRootSizeAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.ToggleGuidesVisibilityAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.ToggleOutlinesVisibilityAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.ToggleSampleDataAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.ZoomAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.ZoomInAction;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.ZoomOutAction;
import com.oracle.javafx.scenebuilder.editor.fxml.controller.EditModeController;
import com.oracle.javafx.scenebuilder.editor.fxml.gesture.DragGesture;
import com.oracle.javafx.scenebuilder.editor.fxml.gesture.ZoomGesture;
import com.oracle.javafx.scenebuilder.editor.fxml.gesture.mouse.DebugMouseGesture;
import com.oracle.javafx.scenebuilder.editor.fxml.gesture.mouse.SelectAndMoveGesture;
import com.oracle.javafx.scenebuilder.editor.fxml.gesture.mouse.SelectWithMarqueeGesture;
import com.oracle.javafx.scenebuilder.editor.fxml.i18n.I18NContentEditor;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.key.MoveWithKeyGesture;

public class FxmlEditorExtension implements OpenExtension {

    public static final UUID ID = UUID.fromString("6f7b35c8-8883-4e10-bb7f-c6e85d1b54be");

    @Override
    public UUID getParentId() {
        return SbApiExtension.ID;
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
     // @formatter:off
        return Arrays.asList(
                BringForwardAction.class,
                BringToFrontAction.class,
                ContentModeProvider.class,
                ContentPanelController.class,
                ContextMenuController.class,
                DebugMouseGesture.class,
                DebugMouseGesture.Factory.class,
                DragGesture.class,
                DragGesture.Factory.class,
                EditModeController.class,
                FocusContentAction.class,
                I18NContentEditor.class,
                MoveWithKeyGesture.class,
                MoveWithKeyGesture.Factory.class,
                SelectAndMoveGesture.class,
                SelectAndMoveGesture.Factory.class,
                SelectWithMarqueeGesture.class,
                SelectWithMarqueeGesture.Factory.class,
                SendBackwardAction.class,
                SendToBackAction.class,
                SetRootSizeAction.class,
                SetRootSizeAction.MenuProvider.class,
                ToggleGuidesVisibilityAction.class,
                ToggleOutlinesVisibilityAction.class,
                ToggleSampleDataAction.class,
                ZoomGesture.class,
                ZoomGesture.Factory.class,
                ZoomAction.class,
                ZoomAction.MenuProvider.class,
                ZoomInAction.class,
                ZoomOutAction.class
            );
     // @formatter:on
    }

}
