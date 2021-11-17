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
package com.oracle.javafx.scenebuilder.core.metadata.property.value.keycombination;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 *
 */
public class KeyCodeCombinationPropertyMetadata extends ComplexPropertyMetadata<KeyCodeCombination> {

    /*
     * NOTE : KeyCodeCombination singularity
     * 
     * Default value for 'alt', 'control', 'meta', 'shift' and 'shortcut' is 'UP'. 
     * However FXMLLoader refuses to load:
     * 
     *     <KeyCombinationCode code='PASTE' />
     * 
     * Properties must be explicitely specified even when they have the default value.
     * 
     *     <KeyCombinationCode code='PASTE' alt='UP' control='UP' meta='UP' shift='UP' shortcut='UP' />
     * 
     * To force this behavior, the EnumerationPropertyMetadata below are all
     * set with a dummy default value.
     */
    private static final String DUMMY = "dummy"; //NOCHECK
    
    private final EnumerationPropertyMetadata altMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("alt")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(getInspectorPath()).build();

    private final EnumerationPropertyMetadata controlMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("control")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(getInspectorPath()).build();

    private final EnumerationPropertyMetadata metaMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("meta")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(getInspectorPath()).build();

    private final EnumerationPropertyMetadata shiftMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("shift")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(getInspectorPath()).build();

    private final EnumerationPropertyMetadata shortcutMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("shortcut")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(getInspectorPath()).build();

    private final EnumerationPropertyMetadata codeMetadata = new EnumerationPropertyMetadata.Builder<>(KeyCombination.ModifierValue.class)
            .withName(new PropertyName("code")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(DUMMY)
            .withInspectorPath(getInspectorPath()).build();

    protected KeyCodeCombinationPropertyMetadata(PropertyName name, boolean readWrite, 
            KeyCodeCombination defaultValue, InspectorPath inspectorPath) {
        super(name, KeyCodeCombination.class, readWrite, defaultValue, inspectorPath);
    }

    protected KeyCodeCombinationPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }
    
    /*
     * ComplexPropertyMetadata
     */
    @Override
    public FXOMInstance makeFxomInstanceFromValue(KeyCodeCombination value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());
        
        altMetadata.setValue(result, value.getAlt().toString());
        controlMetadata.setValue(result, value.getControl().toString());
        metaMetadata.setValue(result, value.getMeta().toString());
        shiftMetadata.setValue(result, value.getShift().toString());
        shortcutMetadata.setValue(result, value.getShortcut().toString());
        codeMetadata.setValue(result, value.getCode().toString());

        return result;
    }
    
    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, KeyCodeCombination> {
        public AbstractBuilder() {
            super();
            withValueClass(KeyCodeCombination.class);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, KeyCodeCombinationPropertyMetadata> {
        @Override
        public KeyCodeCombinationPropertyMetadata build() {
            return new KeyCodeCombinationPropertyMetadata(this);
        }
    }
}
