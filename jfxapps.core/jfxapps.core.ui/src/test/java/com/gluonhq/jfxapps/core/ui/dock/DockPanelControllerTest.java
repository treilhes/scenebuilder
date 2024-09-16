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
package com.gluonhq.jfxapps.core.ui.dock;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.subjects.DockManager;
import com.gluonhq.jfxapps.core.api.subjects.ViewManager;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockType;
import com.gluonhq.jfxapps.core.ui.dock.DockPanelController;
import com.gluonhq.jfxapps.core.ui.dock.DockTypeSplitH;
import com.gluonhq.jfxapps.core.ui.dock.preferences.document.DockMinimizedPreference;
import com.gluonhq.jfxapps.core.ui.dock.preferences.document.LastDockDockTypePreference;
import com.gluonhq.jfxapps.core.ui.dock.preferences.document.LastDockUuidPreference;

import javafx.collections.FXCollections;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class DockPanelControllerTest {

    private ViewManager viewManager = new ViewManager.ViewManagerImpl();
    private DockManager dockManager = new DockManager.DockManagerImpl();

    @Mock
    private JfxAppPlatform platform;
    @Mock
    private JfxAppContext context;
    @Mock
    private LastDockUuidPreference lastDockUuidPreference;
    @Mock
    private LastDockDockTypePreference lastDockDockTypePreference;
    @Mock
    private DockMinimizedPreference dockMinimizedPreference;

    private List<DockType<?>> dockTypes;

    private DockPanelController getInstance() {
        Mockito.when(lastDockDockTypePreference.getValue()).thenReturn(FXCollections.observableHashMap());

        dockTypes = List.of(new DockTypeSplitH(context));

        DockPanelController dpc = new DockPanelController(
            platform,
            dockManager,
            viewManager,
            lastDockUuidPreference,
            lastDockDockTypePreference,
            dockMinimizedPreference,
            dockTypes);
        return dpc;
    }

    @Test
    void should_load_the_fxml() {
        DockPanelController dpc = getInstance();
        assertNotNull(dpc);
    }

}
