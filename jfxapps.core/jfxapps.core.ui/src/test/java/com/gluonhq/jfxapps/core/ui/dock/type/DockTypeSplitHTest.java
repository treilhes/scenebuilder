/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.ui.dock.type;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.subjects.ViewManager;
import com.gluonhq.jfxapps.core.api.subjects.ViewManager.DockRequest;
import com.gluonhq.jfxapps.core.api.ui.DockActionFactory;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.ui.dock.DockPanelController;
import com.gluonhq.jfxapps.core.ui.dock.preference.DockMinimizedPreference;
import com.gluonhq.jfxapps.core.ui.dock.preference.LastDockDockTypePreference;
import com.gluonhq.jfxapps.core.ui.dock.preference.LastDockUuidPreference;
import com.gluonhq.jfxapps.test.JfxAppMock;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

import javafx.collections.FXCollections;

@JfxAppsTest
@ContextConfiguration(classes = {
        DockTypeSplitHTest.Config.class,
        TestApp.class, // a fake app to load a dock
        TestViewUnbounded.class, // a fake view to load some content
        TestViewFixed100x100.class, // another fake view to load some content
        DockPanelController.class, // the controller that manages the dock
        DockTypeSplitH.class // the dock type that will be used during this test
        })
class DockTypeSplitHTest {

    @TestConfiguration
    static class Config {

        @Bean
        ViewMenu viewMenuController() {
            return Mockito.mock(ViewMenu.class);
        }

        @Bean
        IconSetting iconSetting() {
            return Mockito.mock(IconSetting.class);
        }

        @Bean
        LastDockUuidPreference lastDockUuidPreference() {
            return JfxAppMock.mockPreference(LastDockUuidPreference.class,
                    () -> FXCollections.observableMap(new HashMap<UUID, UUID>()));
            //value.put(VIEW_UUID, TestApp.DOCK_UUID);
        }
        @Bean
        LastDockDockTypePreference lastDockDockTypePreference() {
            return JfxAppMock.mockPreference(LastDockDockTypePreference.class,
                    () -> FXCollections.observableMap(new HashMap<UUID, String>()));
            //value.put(TestApp.DOCK_UUID, DockTypeLatestOnly.class.getName());
        }
        @Bean
        DockMinimizedPreference dockMinimizedPreference() {
            return JfxAppMock.mockPreference(DockMinimizedPreference.class,
                    () -> FXCollections.observableMap(new HashMap<UUID, Boolean>()));
            //value.put(TestApp.DOCK_UUID, false);
        }
        @Bean
        DockActionFactory dockActionFactory() {
            return Mockito.mock(DockActionFactory.class);
        }
    }

    @Autowired
    ViewManager viewManager;

    @Test
    void should_load_the_fxml(StageBuilder stageBuilder) {
        try (var testStage = stageBuilder.controller(TestApp.class).show()) {
            assertNotNull(testStage.getController().getRoot());
        }
    }

    @Test
    void must_show_the_view(StageBuilder stageBuilder, FxRobot robot, JfxAppContext context) {

        try (var testStage = stageBuilder.controller(TestApp.class)
                .size(800, 600)
                .setup(StageType.Fill)
                .show()) {


            var controller = testStage.getController();

            robot.interact(controller::composeWindow);

            var testView = context.getBean(TestViewUnbounded.class);

            viewManager.dock().onNext(new DockRequest(ViewAttachment.of(testView), testView, TestApp.DOCK_UUID));

            robot.interact(() -> null);// wait for the dock to be created

            //robot.interact(() -> ScenicView.show(testView.getRoot().getScene()));

            var testView2 = context.getBean(TestViewFixed100x100.class);

            viewManager.dock().onNext(new DockRequest(ViewAttachment.of(testView2), testView2, TestApp.DOCK_UUID));

            System.out.println();
        }

    }

}