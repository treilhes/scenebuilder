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
package com.oracle.javafx.scenebuilder.gluon.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.BorderPaneMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ButtonMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ControlMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.RegionMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentPropertyMetadataCatalog;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;

@Component
public class GluonComponentClassMetadatas {

    public static final String TAG_GLUON = "Gluon";

    // GLuon

    @Component
    public static class ExpandedPanelMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ExpansionPanel.ExpandedPanel> {
        protected ExpandedPanelMetadata(@Autowired RegionMetadata parent,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog) {
            super(com.gluonhq.charm.glisten.control.ExpansionPanel.ExpandedPanel.class, parent);
            getProperties().add(componentCatalog.content_EXPANDEDPANEL_PropertyMetadata);
            getProperties().add(componentCatalog.buttons_EXPANDEDPANEL_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT, new Qualifier(
                    getClass().getResource("fxml/Gluon_ExpandedPanel.fxml"), null, null, null, null, TAG_GLUON));
        }
    }

    @Component
    public static class ExpansionPanelMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ExpansionPanel> {
        protected ExpansionPanelMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog) {
            super(com.gluonhq.charm.glisten.control.ExpansionPanel.class, parent);
            getProperties().add(componentCatalog.expandedContentPropertyMetadata);
            getProperties().add(componentCatalog.collapsedContentPropertyMetadata);
            getProperties().add(valueCatalog.expandedPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_ExpansionPanel.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_ExpansionPanel.png"),
                            getClass().getResource("nodeicons/Gluon_ExpansionPanel@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class ExpansionPanelContainerMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ExpansionPanelContainer> {
        protected ExpansionPanelContainerMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog) {
            super(com.gluonhq.charm.glisten.control.ExpansionPanelContainer.class, parent);
            getProperties().add(componentCatalog.items_ExpansionPanel_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_ExpansionPanelContainer.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_ExpansionPanelContainer.png"),
                            getClass().getResource("nodeicons/Gluon_ExpansionPanelContainer@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class CardPaneMetadata extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.CardPane> {
        protected CardPaneMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(com.gluonhq.charm.glisten.control.CardPane.class, parent);
            getProperties().add(componentCatalog.items_Node_PropertyMetadata);
            getProperties().add(valueCatalog.onPullToRefreshPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_CardPane.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_CardPane.png"),
                            getClass().getResource("nodeicons/Gluon_CardPane@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class BottomNavigationMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.BottomNavigation> {
        protected BottomNavigationMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog) {
            super(com.gluonhq.charm.glisten.control.BottomNavigation.class, parent);
            getProperties().add(valueCatalog.bottomNavigationTypePropertyMetadata);
            getProperties().add(componentCatalog.actionItems_Node_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_BottomNavigation.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_BottomNavigation.png"),
                            getClass().getResource("nodeicons/Gluon_BottomNavigation@2x.png"), TAG_GLUON + "XXX"));
        }
    }

    @Component
    public static class DropdownButtonMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.DropdownButton> {
        protected DropdownButtonMetadata(@Autowired ControlMetadata parent,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(com.gluonhq.charm.glisten.control.DropdownButton.class, parent);
            getProperties().add(componentCatalog.items_MenuItem_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_DropdownButton.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_DropdownButton.png"),
                            getClass().getResource("nodeicons/Gluon_DropdownButton@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class SettingsPaneMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.SettingsPane> {
        protected SettingsPaneMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog) {
            super(com.gluonhq.charm.glisten.control.SettingsPane.class, parent);
            getProperties().add(valueCatalog.searchBoxVisiblePropertyMetadata);
            getProperties().add(valueCatalog.titleFilterPropertyMetadata);
            getProperties().add(componentCatalog.options_Option_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_SettingsPane.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_SettingsPane.png"),
                            getClass().getResource("nodeicons/Gluon_SettingsPane@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class ToggleButtonGroupMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ToggleButtonGroup> {
        protected ToggleButtonGroupMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog) {
            super(com.gluonhq.charm.glisten.control.ToggleButtonGroup.class, parent);
            getProperties().add(componentCatalog.toggles_ToggleButton_PropertyMetadata);
            getProperties().add(valueCatalog.selectionTypePropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_ToggleButtonGroup.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_ToggleButtonGroup.png"),
                            getClass().getResource("nodeicons/Gluon_ToggleButtonGroup@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class CollapsedPanelMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel> {
        protected CollapsedPanelMetadata(@Autowired RegionMetadata parent,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog) {
            super(com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel.class, parent);
            getProperties().add(componentCatalog.titleNodes_Node_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT, new Qualifier(
                    getClass().getResource("fxml/Gluon_CollapsedPanel.fxml"), null, null, null, null, TAG_GLUON));
        }
    }

    @Component
    public static class OptionMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.settings.Option> {
        protected OptionMetadata() {
            super(com.gluonhq.charm.glisten.control.settings.Option.class, null);

        }
    }

    @Component
    public static class AppBarMetadata extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.AppBar> {
        protected AppBarMetadata(@Autowired ControlMetadata parent) {
            super(com.gluonhq.charm.glisten.control.AppBar.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_AppBar.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_AppBar.png"),
                            getClass().getResource("nodeicons/Gluon_AppBar@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class AutoCompleteTextFieldMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.AutoCompleteTextField> {
        protected AutoCompleteTextFieldMetadata(
                @Autowired com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.TextFieldMetadata parent) {
            super(com.gluonhq.charm.glisten.control.AutoCompleteTextField.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_AutoCompleteTextField.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_AutoCompleteTextField.png"),
                            getClass().getResource("nodeicons/Gluon_AutoCompleteTextField@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class AvatarMetadata extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.Avatar> {
        protected AvatarMetadata(@Autowired ControlMetadata parent) {
            super(com.gluonhq.charm.glisten.control.Avatar.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_Avatar.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_Avatar.png"),
                            getClass().getResource("nodeicons/Gluon_Avatar@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class BottomNavigationButtonMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.BottomNavigationButton> {
        protected BottomNavigationButtonMetadata(@Autowired ButtonMetadata parent) {
            super(com.gluonhq.charm.glisten.control.BottomNavigationButton.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_BottomNavigationButton.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_BottomNavigationButton.png"),
                            getClass().getResource("nodeicons/Gluon_BottomNavigationButton@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class CharmListViewMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.CharmListView> {
        protected CharmListViewMetadata(@Autowired ControlMetadata parent) {
            super(com.gluonhq.charm.glisten.control.CharmListView.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_CharmListView.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_CharmListView.png"),
                            getClass().getResource("nodeicons/Gluon_CharmListView@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class ChipMetadata extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.Chip> {
        protected ChipMetadata(@Autowired ControlMetadata parent) {
            super(com.gluonhq.charm.glisten.control.Chip.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_Chip.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_Chip.png"),
                            getClass().getResource("nodeicons/Gluon_Chip@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class IconMetadata extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.Icon> {
        protected IconMetadata(@Autowired ControlMetadata parent) {
            super(com.gluonhq.charm.glisten.control.Icon.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_Icon.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_Icon.png"),
                            getClass().getResource("nodeicons/Gluon_Icon@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class LayerMetadata extends ComponentClassMetadata<com.gluonhq.charm.glisten.layout.Layer> {
        protected LayerMetadata(@Autowired RegionMetadata parent) {
            super(com.gluonhq.charm.glisten.layout.Layer.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_Layer.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_Layer.png"),
                            getClass().getResource("nodeicons/Gluon_Layer@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class NavigationDrawerMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.NavigationDrawer> {
        protected NavigationDrawerMetadata(@Autowired ControlMetadata parent) {
            super(com.gluonhq.charm.glisten.control.NavigationDrawer.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_NavigationDrawer.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_NavigationDrawer.png"),
                            getClass().getResource("nodeicons/Gluon_NavigationDrawer@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class ProgressBarMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ProgressBar> {
        protected ProgressBarMetadata(
                @Autowired com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ProgressIndicatorMetadata parent) {
            super(com.gluonhq.charm.glisten.control.ProgressBar.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_ProgressBar.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_ProgressBar.png"),
                            getClass().getResource("nodeicons/Gluon_ProgressBar@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class ProgressIndicatorMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ProgressIndicator> {
        protected ProgressIndicatorMetadata(
                @Autowired com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ProgressIndicatorMetadata parent) {
            super(com.gluonhq.charm.glisten.control.ProgressIndicator.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_ProgressIndicator.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_ProgressIndicator.png"),
                            getClass().getResource("nodeicons/Gluon_ProgressIndicator@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class SplashViewMetadata extends ComponentClassMetadata<com.gluonhq.charm.glisten.mvc.SplashView> {
        protected SplashViewMetadata(@Autowired ViewMetadata parent) {
            super(com.gluonhq.charm.glisten.mvc.SplashView.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_SplashView.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_SplashView.png"),
                            getClass().getResource("nodeicons/Gluon_SplashView@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class TextFieldMetadata extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.TextField> {
        protected TextFieldMetadata(@Autowired ControlMetadata parent) {
            super(com.gluonhq.charm.glisten.control.TextField.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_TextField.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_TextField.png"),
                            getClass().getResource("nodeicons/Gluon_TextField@2x.png"), TAG_GLUON));
        }
    }

    @Component
    public static class ViewMetadata extends ComponentClassMetadata<com.gluonhq.charm.glisten.mvc.View> {
        protected ViewMetadata(@Autowired BorderPaneMetadata parent) {
            super(com.gluonhq.charm.glisten.mvc.View.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Gluon_View.fxml"), null, null,
                            getClass().getResource("nodeicons/Gluon_View.png"),
                            getClass().getResource("nodeicons/Gluon_View@2x.png"), TAG_GLUON));
        }
    }

    //Commented components in previous version
//    addCustomizedItem(com.gluonhq.charm.glisten.control.Dialog.class, TAG_GLUON);
//    addCustomizedItem(com.gluonhq.charm.glisten.layout.layer.FloatingActionButton.class, TAG_GLUON);
//    addCustomizedItem(com.gluonhq.charm.glisten.layout.responsive.grid.GridLayout.class, TAG_GLUON);
//    addCustomizedItem(com.gluonhq.charm.glisten.layout.responsive.grid.GridRow.class, TAG_GLUON);
//    addCustomizedItem(com.gluonhq.charm.glisten.layout.responsive.grid.GridSpan.class, TAG_GLUON);
//    addCustomizedItem(com.gluonhq.charm.glisten.control.ListTile.class, TAG_GLUON);
//    addCustomizedItem(com.gluonhq.charm.glisten.layout.layer.MenuPopupView.class, TAG_GLUON);
//    addCustomizedItem(com.gluonhq.charm.glisten.layout.layer.MenuSidePopupView.class, TAG_GLUON);
//    addCustomizedItem(com.gluonhq.charm.glisten.layout.layer.PopupView.class, TAG_GLUON);
//    addCustomizedItem(com.gluonhq.charm.glisten.layout.layer.SidePopupView.class, TAG_GLUON);

}
