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
 *  - Neither the name of Oracle Corporation nor the names of its
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

import com.oracle.javafx.scenebuilder.controls.metadata.ComponentPropertyMetadataCatalog;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ControlMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.RegionMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;

@Component
public class GluonComponentClassMetadatas {

    // GLuon

    @Component
    public static class ExpandedPanelMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ExpansionPanel.ExpandedPanel> {
        protected ExpandedPanelMetadata(@Autowired RegionMetadata parent,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog
                ) {
            super(com.gluonhq.charm.glisten.control.ExpansionPanel.ExpandedPanel.class, parent);
            getProperties().add(componentCatalog.content_EXPANDEDPANEL_PropertyMetadata);
            getProperties().add(componentCatalog.buttons_EXPANDEDPANEL_PropertyMetadata);
        }
    }

    @Component
    public static class ExpansionPanelMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ExpansionPanel> {
        protected ExpansionPanelMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog
                ) {
            super(com.gluonhq.charm.glisten.control.ExpansionPanel.class, parent);
            getProperties().add(componentCatalog.expandedContentPropertyMetadata);
            getProperties().add(componentCatalog.collapsedContentPropertyMetadata);
            getProperties().add(valueCatalog.expandedPropertyMetadata);
        }
    }

    @Component
    public static class ExpansionPanelContainerMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ExpansionPanelContainer> {
        protected ExpansionPanelContainerMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog
                ) {
            super(com.gluonhq.charm.glisten.control.ExpansionPanelContainer.class, parent);
            getProperties().add(componentCatalog.items_ExpansionPanel_PropertyMetadata);
        }
    }

    @Component
    public static class CardPaneMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.CardPane> {
        protected CardPaneMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog
                ) {
            super(com.gluonhq.charm.glisten.control.CardPane.class, parent);
            getProperties().add(componentCatalog.items_Node_PropertyMetadata);
            getProperties().add(valueCatalog.onPullToRefreshPropertyMetadata);
        }
    }

    @Component
    public static class BottomNavigationMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.BottomNavigation> {
        protected BottomNavigationMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog
                ) {
            super(com.gluonhq.charm.glisten.control.BottomNavigation.class, parent);
            getProperties().add(valueCatalog.bottomNavigationTypePropertyMetadata);
            getProperties().add(componentCatalog.actionItems_Node_PropertyMetadata);
        }
    }

    @Component
    public static class DropdownButtonMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.DropdownButton> {
        protected DropdownButtonMetadata(@Autowired ControlMetadata parent,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog
                ) {
            super(com.gluonhq.charm.glisten.control.DropdownButton.class, parent);
            getProperties().add(componentCatalog.items_MenuItem_PropertyMetadata);
        }
    }

    @Component
    public static class SettingsPaneMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.SettingsPane> {
        protected SettingsPaneMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog
                ) {
            super(com.gluonhq.charm.glisten.control.SettingsPane.class, parent);
            getProperties().add(valueCatalog.searchBoxVisiblePropertyMetadata);
            getProperties().add(valueCatalog.titleFilterPropertyMetadata);
            getProperties().add(componentCatalog.options_Option_PropertyMetadata);
        }
    }

    @Component
    public static class ToggleButtonGroupMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ToggleButtonGroup> {
        protected ToggleButtonGroupMetadata(@Autowired ControlMetadata parent,
                @Autowired GluonValuePropertyMetadataCatalog valueCatalog,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog
                ) {
            super(com.gluonhq.charm.glisten.control.ToggleButtonGroup.class, parent);
            getProperties().add(componentCatalog.toggles_ToggleButton_PropertyMetadata);
            getProperties().add(valueCatalog.selectionTypePropertyMetadata);
        }
    }

    @Component
    public static class CollapsedPanelMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel> {
        protected CollapsedPanelMetadata(@Autowired RegionMetadata parent,
                @Autowired GluonComponentPropertyMetadataCatalog componentCatalog
                ) {
            super(com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel.class, parent);
            getProperties().add(componentCatalog.titleNodes_Node_PropertyMetadata);
        }
    }

    @Component
    public static class OptionMetadata
            extends ComponentClassMetadata<com.gluonhq.charm.glisten.control.settings.Option> {
        protected OptionMetadata() {
            super(com.gluonhq.charm.glisten.control.settings.Option.class, null);
        }
    }

}
