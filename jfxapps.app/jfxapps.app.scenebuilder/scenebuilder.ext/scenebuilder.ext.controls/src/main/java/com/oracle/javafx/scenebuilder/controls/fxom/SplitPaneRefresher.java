/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.controls.fxom;

import java.net.URL;
import java.util.List;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.collector.SceneGraphCollector;
import com.gluonhq.jfxapps.core.fxom.ext.FXOMRefresher;
import com.gluonhq.jfxapps.core.metadata.property.value.DoubleArrayPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.list.ListValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.javafx.containers.SplitPaneMetadata;

import javafx.scene.control.SplitPane;

/**
 * The case of SplitPane.dividerPositions property
 * -----------------------------------------------
 *
 * When user adds a child to a SplitPane, this adds a new entry in
 * SplitPane.children property but also adds a new value to
 * SplitPane.dividerPositions by side-effect.
 *
 * The change in SplitPane.dividerPositions is performed at scene graph
 * level by FX. Thus it is unseen by FXOM.
 *
 * So in that case we perform a special operation which copies value of
 * SplitPane.dividerPositions into FXOMProperty representing
 * dividerPositions in FXOM.
 */
public class SplitPaneRefresher implements FXOMRefresher {

    @Override
    public void refresh(FXOMDocument document) {
        final DoubleArrayPropertyMetadata davpm = SplitPaneMetadata.dividerPositionsPropertyMetadata;
        final FXOMObject fxomRoot = document.getFxomRoot();
        if (fxomRoot != null) {
            final List<FXOMObject> candidates = fxomRoot
                    .collect(SceneGraphCollector.sceneGraphObjectByClass(SplitPane.class));

            for (FXOMObject fxomObject : candidates) {
                if (fxomObject instanceof FXOMInstance) {
                    final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
                    assert fxomInstance.getSceneGraphObject() instanceof SplitPane;

                    final SplitPane splitPane = (SplitPane) fxomInstance.getSceneGraphObject();
                    splitPane.layout();

                    assert davpm instanceof ListValuePropertyMetadata
                            : "vpm.getClass()=" + davpm.getClass().getSimpleName();
                    davpm.synchronizeWithSceneGraphObject(fxomInstance);
                }
            }
        }
    }

}
