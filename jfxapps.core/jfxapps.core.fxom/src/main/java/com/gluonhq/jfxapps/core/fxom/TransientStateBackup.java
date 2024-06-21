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

package com.gluonhq.jfxapps.core.fxom;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

/**
 *
 */
class TransientStateBackup {
    
    private final FXOMDocument fxomDocument;
    private final ServiceLoader<com.gluonhq.jfxapps.core.fxom.ext.TransientStateBackup> extensions;

    public TransientStateBackup(FXOMDocument fxomDocument) {
        assert fxomDocument != null;
        
        this.fxomDocument = fxomDocument;
        this.extensions = ServiceLoader.load(com.gluonhq.jfxapps.core.fxom.ext.TransientStateBackup.class);
        
        final List<FXOMObject> candidates = new ArrayList<>();
        if (this.fxomDocument.getFxomRoot() != null) {
            candidates.add(this.fxomDocument.getFxomRoot());
        }
        
        while (candidates.isEmpty() == false) {
            final FXOMObject candidate = candidates.get(0);
            candidates.remove(0);
            
            this.extensions
                .stream()
                .map(Provider::get)
                .filter(f -> f != null && f.canHandle(candidate))
                .findFirst()
                .ifPresent(f -> f.backup(candidate));
            
//            final Object sceneGraphObject = candidate.getSceneGraphObject();
//            if (sceneGraphObject instanceof TabPane) {
//                final TabPane tabPane = (TabPane) sceneGraphObject;
//                final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
//                if (currentTab != null) {
//                    final FXOMObject tabObject 
//                            = candidate.searchWithSceneGraphObject(currentTab);
//                    if (tabObject != null) {
//                        tabPaneMap.put(candidate, tabObject);
//                    }
//                }
//            } else if (sceneGraphObject instanceof Accordion) {
//                final Accordion accordion  = (Accordion) sceneGraphObject;
//                final TitledPane currentTitledPane = accordion.getExpandedPane();
//                if (currentTitledPane != null) {
//                    final FXOMObject titledPaneObject
//                            = candidate.searchWithSceneGraphObject(currentTitledPane);
//                    if (titledPaneObject != null) {
//                        accordionMap.put(candidate, titledPaneObject);
//                    }
//                }
//            }
//            
            candidates.addAll(candidate.getChildObjects());
        }
    }
    
    public void restore() {
        final List<FXOMObject> candidates = new ArrayList<>();
        if (this.fxomDocument.getFxomRoot() != null) {
            candidates.add(this.fxomDocument.getFxomRoot());
        }
        
        while (candidates.isEmpty() == false) {
            final FXOMObject candidate = candidates.get(0);
            candidates.remove(0);
            
            this.extensions
            .stream()
            .map(Provider::get)
            .filter(f -> f != null && f.canHandle(candidate))
            .findFirst()
            .ifPresent(f -> f.restore(candidate));
            
//            final Object sceneGraphObject = candidate.getSceneGraphObject();
//            if (sceneGraphObject instanceof TabPane) {
//                final TabPane tabPane = (TabPane) sceneGraphObject;
//                final FXOMObject tabObject = tabPaneMap.get(candidate);
//                if ((tabObject != null) && (tabObject.getParentObject() == candidate)) {
//                    assert tabObject.getSceneGraphObject().isInstanceOf(Tab.class);
//                    final Tab tab = (Tab) tabObject.getSceneGraphObject();
//                    assert tabPane.getTabs().contains(tab);
//                    tabPane.getSelectionModel().select(tab);
//                }
//            } else if (sceneGraphObject instanceof Accordion) {
//                final Accordion accordion  = (Accordion) sceneGraphObject;
//                final FXOMObject titlePaneObject = accordionMap.get(candidate);
//                if ((titlePaneObject != null) && (titlePaneObject.getParentObject() == candidate)) {
//                    assert titlePaneObject.getSceneGraphObject().isInstanceOf(TitledPane.class);
//                    final TitledPane titledPane = (TitledPane) titlePaneObject.getSceneGraphObject();
//                    assert accordion.getPanes().contains(titledPane);
//                    accordion.setExpandedPane(titledPane);
//                }
//            }
            
            candidates.addAll(candidate.getChildObjects());
        }
    }
}
