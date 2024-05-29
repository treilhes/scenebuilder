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
package com.oracle.javafx.scenebuilder.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.AbstractMetadata;
import com.gluonhq.jfxapps.core.metadata.MetadataIntrospector;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentClassMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentPropertyMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization.InspectorPathComparator;

public class SbMetadata extends
        AbstractMetadata<ComponentClassMetadataCustomization, ComponentPropertyMetadataCustomization, ValuePropertyMetadataCustomization> {

    private final List<String> sectionNames = new ArrayList<>();
    private final Map<String, List<String>> subSectionMap = new HashMap<>();

    public final InspectorPathComparator INSPECTOR_PATH_COMPARATOR
            = new InspectorPathComparator(sectionNames, subSectionMap);


    /**
     * parent related properties can be understood as transient properties that have a meaning only in the current parent
     * Changing the parent means those properties can be deleted because the meaning is lost
     * Ex: positioning/scaling/rotation
     */
    private final Set<PropertyName> parentRelatedProperties = new HashSet<>();

    protected SbMetadata(
            List<ComponentClassMetadata<?, ComponentClassMetadataCustomization, ComponentPropertyMetadataCustomization, ValuePropertyMetadataCustomization>> componentClassMetadatas,
            MetadataIntrospector<ComponentClassMetadataCustomization, ComponentPropertyMetadataCustomization, ValuePropertyMetadataCustomization> metadataIntrospector) {
        super(componentClassMetadatas, metadataIntrospector);

        // Populates parentRelatedProperties
//        parentRelatedProperties.add(PropertyNames.layoutXName);
//        parentRelatedProperties.add(PropertyNames.layoutYName);
//        parentRelatedProperties.add(PropertyNames.translateXName);
//        parentRelatedProperties.add(PropertyNames.translateYName);
//        parentRelatedProperties.add(PropertyNames.translateZName);
//        parentRelatedProperties.add(PropertyNames.scaleXName);
//        parentRelatedProperties.add(PropertyNames.scaleYName);
//        parentRelatedProperties.add(PropertyNames.scaleZName);
//        parentRelatedProperties.add(PropertyNames.rotationAxisName);
//        parentRelatedProperties.add(PropertyNames.rotateName);
        parentRelatedProperties.add(new PropertyName("layoutX"));
        parentRelatedProperties.add(new PropertyName("layoutY"));
        parentRelatedProperties.add(new PropertyName("translateX"));
        parentRelatedProperties.add(new PropertyName("translateY"));
        parentRelatedProperties.add(new PropertyName("translateZ"));
        parentRelatedProperties.add(new PropertyName("scaleX"));
        parentRelatedProperties.add(new PropertyName("scaleY"));
        parentRelatedProperties.add(new PropertyName("scaleZ"));
        parentRelatedProperties.add(new PropertyName("rotationAxis"));
        parentRelatedProperties.add(new PropertyName("rotate"));

        // Populates sectionNames
        sectionNames.add("Properties"); //NOCHECK
        sectionNames.add("Layout"); //NOCHECK
        sectionNames.add("Code"); //NOCHECK

        // Populates subSectionMap
        final List<String> ss0 = new ArrayList<>();
        ss0.add("Custom"); //NOCHECK
        ss0.add("Text"); //NOCHECK
        ss0.add("Specific"); //NOCHECK
        ss0.add("Graphic"); //NOCHECK
        ss0.add("3D"); //NOCHECK
        ss0.add("Pagination"); //NOCHECK
        ss0.add("Stroke"); //NOCHECK
        ss0.add("Node"); //NOCHECK
        ss0.add("JavaFX CSS"); //NOCHECK
        ss0.add("Extras"); //NOCHECK
        ss0.add("Accessibility"); //NOCHECK
        subSectionMap.put("Properties", ss0); //NOCHECK
        final List<String> ss1 = new ArrayList<>();
        ss1.add("Anchor Pane Constraints"); //NOCHECK
        ss1.add("Border Pane Constraints"); //NOCHECK
        ss1.add("Flow Pane Constraints"); //NOCHECK
        ss1.add("Grid Pane Constraints"); //NOCHECK
        ss1.add("HBox Constraints"); //NOCHECK
        ss1.add("Split Pane Constraints"); //NOCHECK
        ss1.add("Stack Pane Constraints"); //NOCHECK
        ss1.add("Tile Pane Constraints"); //NOCHECK
        ss1.add("VBox Constraints"); //NOCHECK
        ss1.add("Internal"); //NOCHECK
        ss1.add("Specific"); //NOCHECK
        ss1.add("Size"); //NOCHECK
        ss1.add("Position"); //NOCHECK
        ss1.add("Transforms"); //NOCHECK
        ss1.add("Bounds"); //NOCHECK
        ss1.add("Extras"); //NOCHECK
        ss1.add("Specific"); //NOCHECK
        subSectionMap.put("Layout", ss1); //NOCHECK
        final List<String> ss2 = new ArrayList<>();
        ss2.add("Main"); //NOCHECK
        ss2.add("Edit"); //NOCHECK
        ss2.add("DragDrop"); //NOCHECK
        ss2.add("Closing"); //NOCHECK
        ss2.add("HideShow"); //NOCHECK
        ss2.add("Keyboard"); //NOCHECK
        ss2.add("Mouse"); //NOCHECK
        ss2.add("Rotation"); //NOCHECK
        ss2.add("Swipe"); //NOCHECK
        ss2.add("Touch"); //NOCHECK
        ss2.add("Zoom"); //NOCHECK
        subSectionMap.put("Code", ss2); //NOCHECK
    }


    /**
     * During prune properties job a property is trimmed
     * if the property is static
     * if the property is transient (has a meaning in the current parent only)
     * @param name
     * @return
     */

    public boolean isPropertyTrimmingNeeded(PropertyName name) {
        final boolean result;

        if (name.getResidenceClass() != null) {
            // It's a static property eg GridPane.rowIndex
            // All static property are "parent related" and needs trimming
            result = true;
        } else {
            result = parentRelatedProperties.contains(name);
        }

        return result;
    }
}
