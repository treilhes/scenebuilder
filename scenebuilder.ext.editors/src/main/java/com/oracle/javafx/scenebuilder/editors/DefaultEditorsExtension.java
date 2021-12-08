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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.editors.actions.SetEffectAction;
import com.oracle.javafx.scenebuilder.editors.control.BooleanEditor;
import com.oracle.javafx.scenebuilder.editors.control.BoundedDoubleEditor;
import com.oracle.javafx.scenebuilder.editors.control.ButtonTypeEditor;
import com.oracle.javafx.scenebuilder.editors.control.CharsetEditor;
import com.oracle.javafx.scenebuilder.editors.control.ColorPopupEditor;
import com.oracle.javafx.scenebuilder.editors.control.ColumnResizePolicyEditor;
import com.oracle.javafx.scenebuilder.editors.control.CursorEditor;
import com.oracle.javafx.scenebuilder.editors.control.DefaultEditors;
import com.oracle.javafx.scenebuilder.editors.control.DividerPositionsEditor;
import com.oracle.javafx.scenebuilder.editors.control.DoubleEditor;
import com.oracle.javafx.scenebuilder.editors.control.DurationEditor;
import com.oracle.javafx.scenebuilder.editors.control.EnumEditor;
import com.oracle.javafx.scenebuilder.editors.control.EventHandlerEditor;
import com.oracle.javafx.scenebuilder.editors.control.FunctionalInterfaceEditor;
import com.oracle.javafx.scenebuilder.editors.control.GenericEditor;
import com.oracle.javafx.scenebuilder.editors.control.I18nStringEditor;
import com.oracle.javafx.scenebuilder.editors.control.ImageEditor;
import com.oracle.javafx.scenebuilder.editors.control.IncludeFxmlEditor;
import com.oracle.javafx.scenebuilder.editors.control.InsetsEditor;
import com.oracle.javafx.scenebuilder.editors.control.IntegerEditor;
import com.oracle.javafx.scenebuilder.editors.control.NullableDoubleEditor;
import com.oracle.javafx.scenebuilder.editors.control.Point3DEditor;
import com.oracle.javafx.scenebuilder.editors.control.RotateEditor;
import com.oracle.javafx.scenebuilder.editors.control.StringEditor;
import com.oracle.javafx.scenebuilder.editors.control.StringListEditor;
import com.oracle.javafx.scenebuilder.editors.control.StyleClassEditor;
import com.oracle.javafx.scenebuilder.editors.control.StyleEditor;
import com.oracle.javafx.scenebuilder.editors.control.StylesheetEditor;
import com.oracle.javafx.scenebuilder.editors.control.TextAlignmentEditor;
import com.oracle.javafx.scenebuilder.editors.control.ToggleGroupEditor;
import com.oracle.javafx.scenebuilder.editors.control.effect.JavaFxEffectsProvider;
import com.oracle.javafx.scenebuilder.editors.menu.SetEffectsMenuProvider;
import com.oracle.javafx.scenebuilder.editors.popupeditors.BoundsPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.EffectPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.FontPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.GenericPaintPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.KeyCombinationPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.Rectangle2DPopupEditor;
import com.oracle.javafx.scenebuilder.editors.popupeditors.StringPopupEditor;
import com.oracle.javafx.scenebuilder.extension.AbstractExtension;

public class DefaultEditorsExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("c53b7a09-ad00-4774-b0a1-0d67773ab2b4");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
            SetEffectAction.class,
            SetEffectsMenuProvider.class,
            BooleanEditor.class,
            BoundedDoubleEditor.class,
            ButtonTypeEditor.class,
            CharsetEditor.class,
            ColorPopupEditor.class,
            ColumnResizePolicyEditor.class,
            CursorEditor.class,
            DefaultEditors.class,
            DividerPositionsEditor.class,
            DoubleEditor.class,
            DurationEditor.class,
            EnumEditor.class,
            EventHandlerEditor.class,
            FunctionalInterfaceEditor.class,
            GenericEditor.class,
            I18nStringEditor.class,
            ImageEditor.class,
            IncludeFxmlEditor.class,
            InsetsEditor.class,
            IntegerEditor.class,
            NullableDoubleEditor.class,
            Point3DEditor.class,
            RotateEditor.class,
            StringEditor.class,
            StringListEditor.class,
            StyleClassEditor.class,
            StyleEditor.class,
            StylesheetEditor.class,
            TextAlignmentEditor.class,
            ToggleGroupEditor.class,

            BoundsPopupEditor.class,
            EffectPopupEditor.class,
            FontPopupEditor.class,
            GenericPaintPopupEditor.class,
            KeyCombinationPopupEditor.class,
            Rectangle2DPopupEditor.class,
            StringPopupEditor.class,
            JavaFxEffectsProvider.class
            );
     // @formatter:on
    }
}
