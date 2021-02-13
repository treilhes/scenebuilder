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
package com.oracle.javafx.scenebuilder.drivers.window;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.drivers.node.AbstractNodePring;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class WindowPring extends AbstractNodePring<Node> {

    public WindowPring(Content contentPanelController) {
        super(contentPanelController, Node.class);
    }

    @Override
    public void initialize() {
    }
    
    @Override
    public void setFxomObject(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Window;
        DesignHierarchyMask windowDesignHierarchyMask = new DesignHierarchyMask(fxomObject);
        FXOMObject scene = windowDesignHierarchyMask.getAccessory(windowDesignHierarchyMask.getMainAccessory());
        assert scene != null : "makePring should have only been called if the Window has a scene";
        assert scene.getSceneGraphObject() instanceof Scene;
        assert scene instanceof FXOMInstance;
        DesignHierarchyMask sceneDesignHierarchyMask = new DesignHierarchyMask(scene);
        FXOMObject root = sceneDesignHierarchyMask.getAccessory(sceneDesignHierarchyMask.getMainAccessory());
        assert root != null;
        assert root.getSceneGraphObject() instanceof Node;
        assert root instanceof FXOMInstance;
        
        super.setFxomObject(root);
    }

    
}
