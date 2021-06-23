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

import org.springframework.stereotype.Component;

import com.gluonhq.charm.glisten.control.BottomNavigation;
import com.oracle.javafx.scenebuilder.core.metadata.PropertyNames;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BooleanPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EventHandlerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.control.SelectionMode;

@Component
public class GluonValuePropertyMetadataCatalog {

    //Gluon
    public final ValuePropertyMetadata expandedPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.expandedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 0)
            );
    public final ValuePropertyMetadata onPullToRefreshPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.    onPullToRefreshName,
                    true, /* readWrite */
                    null, /* defaultValue */
                    new InspectorPath("Code", "Specific", 0)
            );
    public final ValuePropertyMetadata bottomNavigationTypePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.bottomNavigationTypeName,
                BottomNavigation.Type.class,
                true, /* readWrite */
                BottomNavigation.Type.FIXED, /* defaultValue */
                new InspectorPath("Properties", "Specific", 0));
    public final ValuePropertyMetadata searchBoxVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.searchBoxVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 0));
    public final ValuePropertyMetadata titleFilterPropertyMetadata =
            new I18nStringPropertyMetadata(
                PropertyNames.titleFilterName,
                true, /* readWrite */
                "",
                new InspectorPath("Properties", "Specific", 1));
    public final ValuePropertyMetadata selectionTypePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.selectionTypeName,
                javafx.scene.control.SelectionMode.class,
                true, /* readWrite */
                SelectionMode.SINGLE,
                new InspectorPath("Properties", "Specific", 0));
    
}
