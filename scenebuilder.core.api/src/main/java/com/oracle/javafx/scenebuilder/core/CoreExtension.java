/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactoryPostProcessor;
import com.oracle.javafx.scenebuilder.core.doc.DocumentationImpl;
import com.oracle.javafx.scenebuilder.core.dock.DockNameHelper;
import com.oracle.javafx.scenebuilder.core.dock.DockPanelController;
import com.oracle.javafx.scenebuilder.core.dock.DockTypeAccordion;
import com.oracle.javafx.scenebuilder.core.dock.DockTypeSplitH;
import com.oracle.javafx.scenebuilder.core.dock.DockTypeSplitV;
import com.oracle.javafx.scenebuilder.core.dock.DockTypeTab;
import com.oracle.javafx.scenebuilder.core.dock.DockViewController;
import com.oracle.javafx.scenebuilder.core.dock.DockWindowController;
import com.oracle.javafx.scenebuilder.core.dock.DockWindowFactory;
import com.oracle.javafx.scenebuilder.core.dock.ViewMenuProvider;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockDockTypePreference;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockUuidPreference;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastViewVisibilityPreference;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.editors.ControllerClassEditor;
import com.oracle.javafx.scenebuilder.core.editors.CoreEditors;
import com.oracle.javafx.scenebuilder.core.editors.FxIdEditor;
import com.oracle.javafx.scenebuilder.core.editors.PropertyEditorFactory;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.extension.AbstractExtension;

public class CoreExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("e000402f-89dc-499d-afae-36149efc2537");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                SceneBuilderBeanFactoryPostProcessor.class,
                PropertyEditorFactory.class,
                CoreEditors.class,
                ControllerClassEditor.class,
                FxIdEditor.class,
                
                Selection.class,
                Metadata.class,
                DocumentationImpl.class,
                DockPanelController.class,
                DockTypeAccordion.class,
                DockTypeSplitH.class,
                DockTypeSplitV.class,
                DockTypeTab.class,
                DockWindowController.class,
                DockWindowFactory.class,
                LastDockUuidPreference.class,
                LastViewVisibilityPreference.class,
                LastDockDockTypePreference.class,
                DockViewController.class,
                ViewMenuProvider.class,
                DockNameHelper.class
            );
     // @formatter:on
    }
}
