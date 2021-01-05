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
package com.oracle.javafx.scenebuilder.editors;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.editors.EditorMapProvider;
import com.oracle.javafx.scenebuilder.core.editors.PropertyEditor;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.AnchorPropertyGroupMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BooleanPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BoundsPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.CursorPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoubleArrayPropertyMetadata.DividerPositionsDoubleArrayPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoubleBoundedPropertyGroupMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.AngleDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.OpacityDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.ProgressDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DurationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata.TextAlignmentEnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EventHandlerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.FontPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.FunctionalInterfacePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ImagePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.InsetsPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.Point3DPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.Rectangle2DPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.CharsetStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.IdStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.StyleStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.TableViewResizePolicyPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ToggleGroupPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.TreeTableViewResizePolicyPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.effect.EffectPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.keycombination.KeyCombinationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.ButtonTypeListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.ListValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StringListPropertyMetadata.SourceStringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StringListPropertyMetadata.StyleClassStringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StringListPropertyMetadata.StylesheetsStringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.paint.ColorPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.paint.PaintPropertyMetadata;
import com.oracle.javafx.scenebuilder.editors.EnumEditor.GenericEnumEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.BoundsPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.EffectPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.FontPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.GenericPaintPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.KeyCombinationPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.Rectangle2DPopupEditor;

@Component
public class DefaultEditors implements EditorMapProvider {
    
    Map<Class<? extends PropertyMetadata>, Class<? extends PropertyEditor>> editorsMap;

    
    public DefaultEditors() {
        super();
        editorsMap = new HashMap<>();
        editorsMap.put(StyleStringPropertyMetadata.class, StyleEditor.class);
        
        editorsMap.put(IdStringPropertyMetadata.class, StringEditor.class);
        
        editorsMap.put(CharsetStringPropertyMetadata.class, CharsetEditor.class);
        editorsMap.put(I18nStringPropertyMetadata.class, I18nStringEditor.class);
        editorsMap.put(ButtonTypeListPropertyMetadata.class, ButtonTypeEditor.class);
        editorsMap.put(StyleClassStringListPropertyMetadata.class, StyleClassEditor.class);
        editorsMap.put(StylesheetsStringListPropertyMetadata.class, StylesheetEditor.class);
        editorsMap.put(SourceStringListPropertyMetadata.class, IncludeFxmlEditor.class);
        editorsMap.put(DividerPositionsDoubleArrayPropertyMetadata.class, DividerPositionsEditor.class);
        
        editorsMap.put(StringListPropertyMetadata.class, StringListEditor.class);
        editorsMap.put(ListValuePropertyMetadata.class, GenericEditor.class);
        
        editorsMap.put(ProgressDoublePropertyMetadata.class, BoundedDoubleEditor.class);
        editorsMap.put(OpacityDoublePropertyMetadata.class, BoundedDoubleEditor.class);
        
//    } else if (propMeta instanceof  || propMeta instanceof  || isBoundedByProperties(propMeta)) {
//        propertyEditor = makePropertyEditor(BoundedDoubleEditor.class, propMeta);
//        
        
        editorsMap.put(AngleDoublePropertyMetadata.class, RotateEditor.class);
        editorsMap.put(DoublePropertyMetadata.class, DoubleEditor.class);
        editorsMap.put(IntegerPropertyMetadata.class, IntegerEditor.class);
        
        editorsMap.put(BooleanPropertyMetadata.class, BooleanEditor.class);
        editorsMap.put(TextAlignmentEnumerationPropertyMetadata.class, TextAlignmentEditor.class);
        editorsMap.put(EnumerationPropertyMetadata.class, GenericEnumEditor.class);
        editorsMap.put(InsetsPropertyMetadata.class, InsetsEditor.class);
        editorsMap.put(CursorPropertyMetadata.class, CursorEditor.class);
        editorsMap.put(EventHandlerPropertyMetadata.class, EventHandlerEditor.class);
        
        editorsMap.put(FunctionalInterfacePropertyMetadata.class, FunctionalInterfaceEditor.class);
        editorsMap.put(EffectPropertyMetadata.class, EffectPopupEditor.class);
        
        editorsMap.put(FontPropertyMetadata.class, FontPopupEditor.class);
        editorsMap.put(PaintPropertyMetadata.class, GenericPaintPopupEditor.class);
        editorsMap.put(ImagePropertyMetadata.class, ImageEditor.class);
        editorsMap.put(BoundsPropertyMetadata.class, BoundsPopupEditor.class);
        editorsMap.put(Point3DPropertyMetadata.class, Point3DEditor.class);
        editorsMap.put(KeyCombinationPropertyMetadata.class, KeyCombinationPopupEditor.class);
        editorsMap.put(TableViewResizePolicyPropertyMetadata.class, ColumnResizePolicyEditor.class);
        editorsMap.put(TreeTableViewResizePolicyPropertyMetadata.class, ColumnResizePolicyEditor.class);
        editorsMap.put(Rectangle2DPropertyMetadata.class, Rectangle2DPopupEditor.class);
        editorsMap.put(ToggleGroupPropertyMetadata.class, ToggleGroupEditor.class);
        editorsMap.put(DurationPropertyMetadata.class, DurationEditor.class);
        editorsMap.put(ColorPropertyMetadata.class, ColorPopupEditor.class);
        editorsMap.put(PropertyMetadata.class, GenericEditor.class);
        
        //groups
        editorsMap.put(AnchorPropertyGroupMetadata.class, AnchorPaneConstraintsEditor.class);
        editorsMap.put(DoubleBoundedPropertyGroupMetadata.class, BoundedDoubleEditor.class);
        
    
    }


    @Override
    public Map<Class<? extends PropertyMetadata>, Class<? extends PropertyEditor>> getMap() {
        return editorsMap;
    }

}
