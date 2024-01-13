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
package com.oracle.javafx.scenebuilder.tools.driver.window;

import java.util.List;

import org.scenebuilder.fxml.api.Content;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.SelectWithPringGesture;
import com.oracle.javafx.scenebuilder.tools.driver.node.AbstractNodePring;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class WindowPring extends AbstractNodePring<Node> {

    private final DesignHierarchyMask.Factory maskFactory;

    public WindowPring(
            Content contentPanelController,
            FxmlDocumentManager documentManager,
            SelectWithPringGesture.Factory selectWithPringGestureFactory,
            DesignHierarchyMask.Factory maskFactory) {
        super(contentPanelController, documentManager, selectWithPringGestureFactory, Node.class);
        this.maskFactory = maskFactory;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void setFxomObject(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Window;
        HierarchyMask windowDesignHierarchyMask = maskFactory.getMask(fxomObject);
        List<FXOMObject> sceneContent = windowDesignHierarchyMask.getAccessories(windowDesignHierarchyMask.getMainAccessory(), false);

        assert !sceneContent.isEmpty();

        FXOMObject scene = sceneContent.get(0);
        assert scene != null : "makePring should have only been called if the Window has a scene";
        assert scene.getSceneGraphObject() instanceof Scene;
        assert scene instanceof FXOMInstance;
        HierarchyMask sceneDesignHierarchyMask = maskFactory.getMask(scene);
        List<FXOMObject> rootContent = sceneDesignHierarchyMask.getAccessories(sceneDesignHierarchyMask.getMainAccessory(), false);

        assert !rootContent.isEmpty();

        FXOMObject root = rootContent.get(0);
        assert root != null;
        assert root.getSceneGraphObject() instanceof Node;
        assert root instanceof FXOMInstance;

        super.setFxomObject(root);
    }


}