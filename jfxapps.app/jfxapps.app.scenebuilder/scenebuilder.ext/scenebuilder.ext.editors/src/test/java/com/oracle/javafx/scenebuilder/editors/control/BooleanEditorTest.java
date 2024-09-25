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
package com.oracle.javafx.scenebuilder.editors.control;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.BooleanPropertyMetadata;
import com.oracle.javafx.scenebuilder.api.Documentation;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization.InspectorPath;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
public class BooleanEditorTest {

    private I18N i18n = new I18N(List.of(), true);

    @Mock
    Dialog dialog;

    @Mock
    Documentation documentation;

    @Mock
    FileSystem fileSystem;

    static BooleanPropertyMetadata someBoolProp() {
        return new BooleanPropertyMetadata.Builder()
                .name(new PropertyName("boolean"))
                .readWrite(true)
                .defaultValue(false)
                .inspectorPath(InspectorPath.UNUSED).build();
    }

    @Test
    public void shouldCreateAnEmptyInstance() {
        BooleanEditor o = new BooleanEditor(dialog, documentation, fileSystem);

        assertNotNull(o);
    }

    @Test
    public void shouldCreateAnEmptyMenu() {
        BooleanEditor o = new BooleanEditor(dialog, documentation, fileSystem);

        assertNotNull(o.getMenu());
    }

    @Test
    public void shouldResetTheInstance() {
        BooleanEditor o = new BooleanEditor(dialog, documentation, fileSystem);

        o.reset(someBoolProp(), null);
    }

}
