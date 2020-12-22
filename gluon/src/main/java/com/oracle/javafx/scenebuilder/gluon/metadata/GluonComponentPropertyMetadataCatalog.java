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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ButtonBaseMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.NodeMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ToggleButtonMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.PropertyNames;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.gluon.metadata.GluonComponentClassMetadatas.ExpansionPanelMetadata;
import com.oracle.javafx.scenebuilder.gluon.metadata.GluonComponentClassMetadatas.OptionMetadata;

@Component
public class GluonComponentPropertyMetadataCatalog {

    //gluon
    public final ComponentPropertyMetadata content_EXPANDEDPANEL_PropertyMetadata;
    public final ComponentPropertyMetadata buttons_EXPANDEDPANEL_PropertyMetadata;
    public final ComponentPropertyMetadata expandedContentPropertyMetadata;
    public final ComponentPropertyMetadata collapsedContentPropertyMetadata;
    public final ComponentPropertyMetadata items_ExpansionPanel_PropertyMetadata;
    public final ComponentPropertyMetadata actionItems_Node_PropertyMetadata;
    public final ComponentPropertyMetadata options_Option_PropertyMetadata;
    public final ComponentPropertyMetadata toggles_ToggleButton_PropertyMetadata;
    public final ComponentPropertyMetadata titleNodes_Node_PropertyMetadata;
    
    public GluonComponentPropertyMetadataCatalog(
            @Lazy @Autowired NodeMetadata nodeMetadata,
            @Lazy @Autowired ButtonBaseMetadata buttonBaseMetadata,
            @Lazy @Autowired ExpansionPanelMetadata expansionPanelMetadata,
            @Lazy @Autowired OptionMetadata optionMetadata,
            @Lazy @Autowired ToggleButtonMetadata toggleButtonMetadata
            ) {
        actionItems_Node_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.actionItemsName,
                    nodeMetadata,
                    true, /* collection */
                    null,
                    null);
        buttons_EXPANDEDPANEL_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.buttonsName,
                    buttonBaseMetadata,
                    true, /* collection */
                    null,
                    null);
        collapsedContentPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.collapsedContentName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/Gluon_ExpansionPanel-collapsed_content.png"),
                    getClass().getResource("subcompicons/Gluon_ExpansionPanel-collapsed_content@2x.png"));
        content_EXPANDEDPANEL_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.contentName,
                    nodeMetadata,
                    false, /* unique */
                    null,
                    null);
        expandedContentPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.expandedContentName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/Gluon_ExpansionPanel-expanded_content.png"),
                    getClass().getResource("subcompicons/Gluon_ExpansionPanel-expanded_content@2x.png"));
        items_ExpansionPanel_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.itemsName,
                    expansionPanelMetadata,
                    true, /* collection */
                    null,
                    null);
        options_Option_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.optionsName,
                    optionMetadata,
                    true, /* collection */
                    null,
                    null);
        titleNodes_Node_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.titleNodesName,
                    nodeMetadata,
                    true, /* collection */
                    null,
                    null);
        toggles_ToggleButton_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.togglesName,
                    toggleButtonMetadata,
                    true, /* collection */
                    null,
                    null);
    }
}
