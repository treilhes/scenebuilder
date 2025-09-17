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
package com.gluonhq.jfxapps.app.manager.source.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.app.manager.api.ManagerApiExtension;
import com.gluonhq.jfxapps.app.manager.registries.controller.EditSourceItemController;
import com.gluonhq.jfxapps.app.manager.registries.controller.SourceController;
import com.gluonhq.jfxapps.app.manager.registries.controller.SourceItemController;
import com.gluonhq.jfxapps.app.manager.registries.model.SourceModelController;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.maven.UniqueArtifact;
import com.gluonhq.jfxapps.boot.api.registry.RegistryArtifactManager;
import com.gluonhq.jfxapps.boot.api.registry.model.RegistryArtifact;
import com.gluonhq.jfxapps.boot.api.registry.model.RegistryInfo;
import com.gluonhq.jfxapps.boot.api.registry.model.RegistrySourceInfo;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

import javafx.scene.control.Button;

@JfxAppsTest
@ContextConfiguration(classes = { SourceControllerTest.Config.class, SourceController.class, SourceItemController.class,
        SourceModelController.class, EditSourceItemController.class })
class SourceControllerTest {

    @TestConfiguration
    static class Config {

        @Bean
        ViewMenu viewMenuController() {
            return Mockito.mock(ViewMenu.class);
        }

        @Bean
        RegistryArtifactManager registryArtifactManager() {
            return Mockito.mock(RegistryArtifactManager.class);
        }

        @Bean
        RepositoryClient repositoryClient() {
            return Mockito.mock(RepositoryClient.class);
        }
    }

    @Autowired
    RegistryArtifactManager registryArtifactManager;

    @Autowired
    RepositoryClient mavenClient;

    @Test
    void should_load_the_fxml(StageBuilder stageBuilder) {
        var testStage = stageBuilder.controller(SourceController.class).show();
        assertNotNull(testStage.getController().getRoot());
        testStage.close();
    }

    @Test
    void must_load_two_sources(StageBuilder stageBuilder, FxRobot robot) {
        var b = new AtomicReference<Button>();

        var reg1 = new RegistryInfo();
        reg1.setUuid(UUID.randomUUID());
        reg1.setImage(SourceControllerTest.class.getResource("image1.png"));
        reg1.setTitle("Scene Builder");
        reg1.setText(
                "Scene Builder is an open source tool that allows for drag and drop design of JavaFX user interfaces.");
        reg1.setVersion("X.X.X");

        var regSrc1 = new RegistrySourceInfo();
        regSrc1.setArtifact(new RegistryArtifact("reg1.group", "reg1.artifact", null, false));
        regSrc1.setRegistryInfo(reg1);

        var reg2 = new RegistryInfo();
        reg2.setUuid(UUID.randomUUID());
        reg2.setImage(SourceControllerTest.class.getResource("image2.png"));
        reg2.setTitle("App2");
        reg2.setText("Description2");
        reg2.setVersion("X.X.X");

        var regSrc2 = new RegistrySourceInfo();
        regSrc2.setArtifact(new RegistryArtifact("reg2.group", "reg2.artifact", null, true));
        regSrc2.setRegistryInfo(reg2);

        Mockito.when(registryArtifactManager.listRegistrySourceInfo()).thenReturn(Set.of(regSrc1, regSrc2));
        Mockito.when(mavenClient.getAvailableVersions(any(), any())).thenAnswer((call) -> {
            var group = call.getArgument(0).toString();
            var artifact = call.getArgument(1).toString();
            var artifacts = List.of(
                    UniqueArtifact.builder().artifact(group, artifact).version("1.0.0").build(),
                    UniqueArtifact.builder().artifact(group, artifact).version("2.0.0").build(),
                    UniqueArtifact.builder().artifact(group, artifact).version("3.0.0").build()
                    );
            return new ArrayList<>(artifacts);
        });

        var loopForEdit = false;

        do {
            var testStage = stageBuilder
                    .controller(SourceController.class)
                    .size(800, 600)
                    .css(ManagerApiExtension.class.getResource("/com/gluonhq/jfxapps/app/manager/api/ui/Manager.css"))
                    .i18n("""
                            manager.source.search=Search
                            manager.source.results=Application Registries
                            manager.source.edit.groupid=GroupId :
                            manager.source.edit.artifactid=ArtifactId :
                            manager.source.edit.version=Version :
                            """)
                    //.i18n(this.getClass().getResource("/com/gluonhq/jfxapps/app/manager/source/i18n/ManagerSource.properties"))
                    .setup(StageType.Fill).show();

            var controller = testStage.getController();

            addTestBackground(robot, controller);

            robot.interact(controller::onShow);
            // robot.interact(() -> ScenicView.show(controller.getRoot().getScene()));

            System.out.println();

            testStage.close();
        } while (loopForEdit);

    }

    private void addTestBackground(FxRobot robot, SourceController controller) {
        robot.interact(() -> {
            controller.getRoot().getScene().getRoot().setStyle(
                    "-fx-background-color:  radial-gradient(focus-angle 0deg , focus-distance -80% , center 0% -10% , radius 100% , #d5e3e6 30%, #72adaa 80%, #293950)");
        });
    }

}
