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
package com.oracle.javafx.scenebuilder.cssanalyser.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.scenicview.ScenicView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.core.api.dnd.Drag;
import com.gluonhq.jfxapps.core.api.dnd.DragSource;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.preferences.AbstractPreference;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolStylesheetProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewSearch;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.menu.controller.ViewMenuController;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;
import com.oracle.javafx.scenebuilder.api.SbEditor;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelController.Delegate;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.NodeCssState.CssProperty;
import com.oracle.javafx.scenebuilder.cssanalyser.preferences.global.CssTableColumnsOrderingReversedPreference;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

@JfxAppsTest
@ContextConfiguration(classes = { CssPanelControllerTest.Config.class, CssPanelController.class })
class CssPanelControllerTest {

    @TestConfiguration
    static class Config {
//        @Bean
//        IconSetting iconSetting() {
//            return Mockito.mock(IconSetting.class);
//        }
        @Bean
        SbMetadata metadata() {
            return Mockito.mock(SbMetadata.class);
        }

        @Bean
        Selection selection() {
            return Mockito.mock(Selection.class);
        }

        @Bean
        SbEditor editor() {
            return Mockito.mock(SbEditor.class);
        }

        @Bean
        Delegate delegate() {
            return Mockito.mock(Delegate.class);
        }

        @Bean
        CssTableColumnsOrderingReversedPreference cssTableColumnsOrderingReversedPreference() {
            return Mockito.mock(CssTableColumnsOrderingReversedPreference.class);
        }

        @Bean
        Drag drag() {
            return Mockito.mock(Drag.class);
        }

        @Bean
        FileSystem fileSystem() {
            return Mockito.mock(FileSystem.class);
        }

        @Bean
        ViewSearch viewSearch() {
            return Mockito.mock(ViewSearch.class);
        }

        @Bean
        ViewMenu viewMenuController() {
            return Mockito.mock(ViewMenuController.class);
        }
    }

    @Autowired
    CssTableColumnsOrderingReversedPreference cssTableColumnsOrderingReversedPreference;

    @Autowired
    ViewSearch viewSearch;

    @Autowired
    SbEditor editor;

    @Autowired
    Drag drag;

    @Autowired
    Selection selection;

    @Mock
    SelectionGroup group;

    @Autowired
    ApplicationInstanceEvents instanceEvents;

    @Test
    void load_ui_success(StageBuilder builder, FxRobot robot) {

        when(cssTableColumnsOrderingReversedPreference.getValue()).thenReturn(true);
        when(cssTableColumnsOrderingReversedPreference.getObservableValue()).thenReturn(new SimpleBooleanProperty(true));
        when(viewSearch.textProperty()).thenReturn(new SimpleStringProperty(""));
        when(editor.pickModeEnabledProperty()).thenReturn(new SimpleBooleanProperty(false));
        when(drag.dragSourceProperty()).thenReturn(new SimpleObjectProperty<DragSource>(null));

        CssPanelController controller = builder
                .controller(CssPanelController.class)
                .css(ToolStylesheetProvider.builder()
                        //.stylesheet(CssPanelController.class.getResource("css/ThemeDark_common.css").toExternalForm())
                        //.stylesheet(CssPanelController.class.getResource("css/ThemeDark_SBKIT-css-panel.css").toExternalForm())
                        .build())
                .setup(StageType.Fill)
                .size(800, 600).show();

        assertNotNull("Controller must load successfully", controller);
    }

    @Test
    void inline_style_must_be_shown_in_table(StageBuilder builder, FxRobot robot) throws IOException {

        var document = new FXOMDocument("""
                <?import javafx.scene.control.Label?>
                <Label xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1"
                    text="txt" style="-fx-background-color:Orange;"/>
                """);

        when(cssTableColumnsOrderingReversedPreference.getValue()).thenReturn(true);
        when(cssTableColumnsOrderingReversedPreference.getObservableValue()).thenReturn(new SimpleBooleanProperty(true));
        when(viewSearch.textProperty()).thenReturn(new SimpleStringProperty(""));
        when(editor.pickModeEnabledProperty()).thenReturn(new SimpleBooleanProperty(false));
        when(drag.dragSourceProperty()).thenReturn(new SimpleObjectProperty<DragSource>(null));
        when(group.getItems()).thenReturn(Set.of(document.getFxomRoot()));
        when(selection.getGroup()).thenReturn(group);

        CssPanelController controller = builder
                .document(document)
                .controller(CssPanelController.class)
                .setup(StageType.Fill)
                .size(1024, 600).show();

        var items = robot.lookup("#table").queryTableView().getItems();

        //get the -fx-background-color property
        var fxBackgroundColor = items.stream().filter(Objects::nonNull)
            .filter(i -> CssProperty.class.isAssignableFrom(i.getClass()))
            .map(CssProperty.class::cast)
            .filter(p -> "-fx-background-color".equals(p.propertyName().get()))
            .findAny();

        // check that the -fx-background-color property is present
        assertTrue(fxBackgroundColor.isPresent());

        // check that the inline -fx-background-color property has the correct value
        assertTrue(fxBackgroundColor.get().inlineState().get().getCssValue().equalsIgnoreCase("Orange"));
        // check that the stylesheet -fx-background-color property is undefined
        assertTrue(fxBackgroundColor.get().authorState().isNull().get());
        // check that the editor -fx-background-color property is undefined
        assertTrue(fxBackgroundColor.get().modelState().isNull().get());
        // check that the builtin -fx-background-color property is defined
        assertTrue(fxBackgroundColor.get().builtinState().isNotNull().get());

        System.out.println();
    }

}
