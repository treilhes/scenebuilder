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
package com.oracle.javafx.scenebuilder.core.metadata.property.value.keycombination;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;

/**
 *
 */
public class KeyCharacterCombinationPropertyMetadata extends ComplexPropertyMetadata<KeyCharacterCombination> {

    /*
     * NOTE : KeyCharacterCombination singularity
     *
     * Same as KeyCodeCombination => see comments in KeyCodeCombination
     */
    private static final String DUMMY = "dummy"; // NOCHECK

    private final EnumerationPropertyMetadata altMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("alt")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final EnumerationPropertyMetadata controlMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("control")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final EnumerationPropertyMetadata metaMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("meta")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final EnumerationPropertyMetadata shiftMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("shift")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final EnumerationPropertyMetadata shortcutMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("shortcut")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final I18nStringPropertyMetadata characterMetadata = new I18nStringPropertyMetadata.Builder()
            .withName(new PropertyName("character")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(null)
            .withInspectorPath(InspectorPath.UNUSED).build();

//    public KeyCharacterCombinationPropertyMetadata(PropertyName name, boolean readWrite,
//            KeyCharacterCombination defaultValue, InspectorPath inspectorPath) {
//        super(name, KeyCharacterCombination.class, readWrite, defaultValue, inspectorPath);
//    }

    protected KeyCharacterCombinationPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(KeyCharacterCombination value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());

        altMetadata.setValue(result, value.getAlt().toString());
        controlMetadata.setValue(result, value.getControl().toString());
        metaMetadata.setValue(result, value.getMeta().toString());
        shiftMetadata.setValue(result, value.getShift().toString());
        shortcutMetadata.setValue(result, value.getShortcut().toString());
        characterMetadata.setValue(result, value.getCharacter());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD>
            extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, KeyCharacterCombination> {
        public AbstractBuilder() {
            super();
            withValueClass(KeyCharacterCombination.class);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, KeyCharacterCombinationPropertyMetadata> {
        @Override
        public KeyCharacterCombinationPropertyMetadata build() {
            return new KeyCharacterCombinationPropertyMetadata(this);
        }
    }
}
