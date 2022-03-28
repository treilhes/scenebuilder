/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package org.scenebuilder.ext.javafx.customization.anchorpane;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scenebuilder.ext.javafx.customization.anchorpane.AnchorPaneConstraintsEditor.ConstraintEditor;
import org.testfx.framework.junit5.ApplicationExtension;

import com.oracle.javafx.scenebuilder.api.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.NullableCoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
public class AnchorPaneConstraintsEditorTest {

    static {
        I18N.initForTest();
    }

    static DoublePropertyMetadata someAnchorProp(String name) {
        return new NullableCoordinateDoublePropertyMetadata.Builder()
                .withName(new PropertyName(name))
                .withReadWrite(true)
                .withDefaultValue(null)
                .withInspectorPath(InspectorPath.UNUSED).build();
    }
    static AnchorPropertyGroupMetadata someAnchorGroupProp() {
        return new AnchorPropertyGroupMetadata.Builder()
                .withName(new PropertyName("anchors"))
                .withTopAnchorProperty(someAnchorProp("top"))
                .withRightAnchorProperty(someAnchorProp("right"))
                .withBottomAnchorProperty(someAnchorProp("bottom"))
                .withLeftAnchorProperty(someAnchorProp("left")).build();
    }

    @Mock
    AnchorPaneConstraintsEditor.ConstraintEditor.Factory constraintEditorFactory;

    @BeforeEach
    public void setup() {
        Supplier<ConstraintEditor> supplier = () -> {
            ConstraintEditor ct = new ConstraintEditor(null, null, null);
            ct.initialize(new TextField(), new ToggleButton(), (ob, o, n) -> {});
            return ct;
        };

        Mockito.when(constraintEditorFactory.getEditor(Mockito.any(), Mockito.any(), Mockito.any()))
            .thenReturn(supplier.get());
    }

    @Test
    public void shouldCreateAnEmptyInstance() {
        AnchorPaneConstraintsEditor o = new AnchorPaneConstraintsEditor(constraintEditorFactory);

        assertNotNull(o);
    }

    @Test
    public void shouldCreateAnEmptyMenu() {
        AnchorPaneConstraintsEditor o = new AnchorPaneConstraintsEditor(constraintEditorFactory);

        assertNotNull(o.getMenu());
    }

    @Test
    public void shouldResetTheInstance() {
        SelectionState selectionState = Mockito.mock(SelectionState.class);
        Mockito.when(selectionState.getSelectedInstances()).thenReturn(new HashSet<>());

        AnchorPaneConstraintsEditor o = new AnchorPaneConstraintsEditor(constraintEditorFactory);

        o.reset(someAnchorGroupProp(), selectionState);
    }

}
