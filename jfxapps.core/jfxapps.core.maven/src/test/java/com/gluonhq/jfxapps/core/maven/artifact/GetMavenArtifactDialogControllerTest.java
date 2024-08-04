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
package com.gluonhq.jfxapps.core.maven.artifact;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.settings.MavenSetting;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.InstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.core.maven.impl.MavenClientController;
import com.gluonhq.jfxapps.core.maven.preferences.global.MavenRepositoriesPreferences;
import com.gluonhq.jfxapps.test.FxmlControllerLoader;

import javafx.scene.Parent;
import javafx.stage.Stage;

@ExtendWith({ ApplicationExtension.class, MockitoExtension.class })
class GetMavenArtifactDialogControllerTest {

    private MavenClientController mc = new MavenClientController(null, null, null, null, null);

    private SceneBuilderManager sbm = new SceneBuilderManager.SceneBuilderManagerImpl();

    private I18N i18n = new I18N(List.of(), true);

    @Mock
    private IconSetting iconSetting;

    @Mock
    private MessageLogger messageLogger;

    @Mock
    private MavenSetting mavenSetting;

    @Mock
    private MavenRepositoriesPreferences repositoryPreferences;

    @Mock
    private InstanceWindow owner;

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

    @Test
    void should_load_the_hud_fxml() {
        Parent ui = FxmlControllerLoader.controller(new GetMavenArtifactDialogController(i18n, mc, sbm, iconSetting,
                messageLogger, mavenSetting, repositoryPreferences, owner)).loadFxml();
        assertNotNull(ui);
    }

    @Test
    void show_ui(FxRobot robot) {
        Mockito.when(owner.getStage()).thenReturn(stage);

        GetMavenArtifactDialogController dialog = FxmlControllerLoader
                .controller(new GetMavenArtifactDialogController(i18n, mc, sbm, iconSetting, messageLogger, mavenSetting,
                        repositoryPreferences, owner))
                .darkTheme(sbm).load();

        robot.interact(() -> {
            dialog.openWindow(null);
        });

        System.out.println();
    }

}
