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
package com.oracle.javafx.scenebuilder.core.editors;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scenebuilder.fxml.api.Documentation;
import org.testfx.framework.junit5.ApplicationExtension;

import com.gluonhq.jfxapps.core.api.editor.selection.SelectionState;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.PropertyGroupMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.IntegerPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata.MultilineI18nStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.api.editors.AbstractPropertyEditor;

import javafx.scene.Node;

@ExtendWith(ApplicationExtension.class)
public class AbstractPropertyEditorTest {

    private I18N i18n = new I18N(List.of(), true);

    @Test
    public void shouldCreateAnEmptyInstance() {
        AbstractPropertyEditorImpl o = new AbstractPropertyEditorImpl(null, null, null);
        assertNotNull(o);
    }

    @Test
    public void shouldCreateAnEmptyMenu() {
        AbstractPropertyEditorImpl o = new AbstractPropertyEditorImpl(null, null, null);
        assertNotNull(o.getMenu());
    }

    @Test
    public void shouldResetTheInstance() {
        AbstractPropertyEditorImpl o = new AbstractPropertyEditorImpl(null, null, null);
        o.reset(someIntProp(), null);
    }

    @Test
    public void shouldResetTheInstanceForGroup() {
        AbstractPropertyEditorImpl o = new AbstractPropertyEditorImpl(null, null, null);
        o.reset(someGroupProp(), null);
    }


    static IntegerPropertyMetadata someIntProp() {
        return new IntegerPropertyMetadata.Builder<Void>()
                .name(new PropertyName("int")) //NOCHECK
                .readWrite(true)
                .defaultValue(123).build();
    }
    static StringPropertyMetadata someStringProp() {
        return new MultilineI18nStringPropertyMetadata.Builder<Void>()
                .name(new PropertyName("string")) //NOCHECK
                .readWrite(true)
                .defaultValue("123").build();
    }
    static PropertyGroupMetadata someGroupProp() {
        return new PropertyGroupMetadata.Builder<Void>()
                .name(new PropertyName("group")) //NOCHECK
                .readWrite(true)
                .property("someInt", someIntProp())
                .property("someString", someStringProp()).build();
    }


    private class AbstractPropertyEditorImpl extends AbstractPropertyEditor {
        boolean localIndeterminate = false;

        public AbstractPropertyEditorImpl(Dialog dialog,
                Documentation documentation,
                FileSystem fileSystem) {
            super(i18n, dialog, documentation, fileSystem);
        }

        @Override
        public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {}

        @Override
        public Object getValue() {
            return valueProperty().get();
        }

        @Override
        public void setValue(Object value) {
            setValueGeneric(value);
        }

        @Override
        protected void valueIsIndeterminate() {
            localIndeterminate = isIndeterminate();
        }

        @Override
        public void requestFocus() {}

        @Override
        public Node getValueEditor() {
            return null;
        }
    }
}
