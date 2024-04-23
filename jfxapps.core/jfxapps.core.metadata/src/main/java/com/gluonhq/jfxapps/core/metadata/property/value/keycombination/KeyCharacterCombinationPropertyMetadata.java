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
package com.gluonhq.jfxapps.core.metadata.property.value.keycombination;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.ComplexPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.EnumerationPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;

import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;

/**
 *
 */
public class KeyCharacterCombinationPropertyMetadata<VC> extends ComplexPropertyMetadata<KeyCharacterCombination, VC> {

    /*
     * NOTE : KeyCharacterCombination singularity
     *
     * Same as KeyCodeCombination => see comments in KeyCodeCombination
     */
    private static final String DUMMY = "dummy"; // NOCHECK

    private final EnumerationPropertyMetadata<Void> altMetadata = new EnumerationPropertyMetadata.Builder<KeyCombination.ModifierValue, Void>(KeyCombination.ModifierValue.class)
            .name(new PropertyName("alt")) //NOCHECK
            .readWrite(true)
            .nullEquivalent(DUMMY)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EnumerationPropertyMetadata<Void> controlMetadata = new EnumerationPropertyMetadata.Builder<KeyCombination.ModifierValue, Void>(KeyCombination.ModifierValue.class)
            .name(new PropertyName("control")) //NOCHECK
            .readWrite(true)
            .nullEquivalent(DUMMY)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EnumerationPropertyMetadata<Void> metaMetadata = new EnumerationPropertyMetadata.Builder<KeyCombination.ModifierValue, Void>(KeyCombination.ModifierValue.class)
            .name(new PropertyName("meta")) //NOCHECK
            .readWrite(true)
            .nullEquivalent(DUMMY)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EnumerationPropertyMetadata<Void> shiftMetadata = new EnumerationPropertyMetadata.Builder<KeyCombination.ModifierValue, Void>(KeyCombination.ModifierValue.class)
            .name(new PropertyName("shift")) //NOCHECK
            .readWrite(true)
            .nullEquivalent(DUMMY)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EnumerationPropertyMetadata<Void> shortcutMetadata = new EnumerationPropertyMetadata.Builder<KeyCombination.ModifierValue, Void>(KeyCombination.ModifierValue.class)
            .name(new PropertyName("shortcut")) //NOCHECK
            .readWrite(true)
            .nullEquivalent(DUMMY)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final I18nStringPropertyMetadata<Void> characterMetadata = new I18nStringPropertyMetadata.Builder<Void>()
            .name(new PropertyName("character")) //NOCHECK
            .readWrite(true)
            .defaultValue(null)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

//    public KeyCharacterCombinationPropertyMetadata(PropertyName name, boolean readWrite,
//            KeyCharacterCombination defaultValue, InspectorPath inspectorPath) {
//        super(name, KeyCharacterCombination.class, readWrite, defaultValue, inspectorPath);
//    }

    protected KeyCharacterCombinationPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
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

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC>
            extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, KeyCharacterCombination, VC> {
        public AbstractBuilder() {
            super();
            valueClass(KeyCharacterCombination.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, KeyCharacterCombinationPropertyMetadata<VC>, VC> {
        @Override
        public KeyCharacterCombinationPropertyMetadata<VC> build() {
            return new KeyCharacterCombinationPropertyMetadata<VC>(this);
        }
    }
}
