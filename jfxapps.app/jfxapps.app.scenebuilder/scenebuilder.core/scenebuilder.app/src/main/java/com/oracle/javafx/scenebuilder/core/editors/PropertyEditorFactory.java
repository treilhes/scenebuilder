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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionState;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

@ApplicationSingleton
public class PropertyEditorFactory {

    // Map metadata class to editor class
    private final HashMap<Class<? extends PropertyMetadata>, List<Class<? extends PropertyEditor>>> metadataToEditors;

    // Map of editor pools
    private final HashMap<Class<? extends PropertyEditor>, Stack<PropertyEditor>> editorPools;

    /** The spring context. */
    private final JfxAppContext context;

    public PropertyEditorFactory(
            JfxAppContext context,
            List<EditorMapProvider> editorMapProviders
            ) {
        this.editorPools = new HashMap<>();
        this.metadataToEditors = new HashMap<>();
        this.context = context;
        populateMetadataToEditors(editorMapProviders);
    }

    private void populateMetadataToEditors(List<EditorMapProvider> editorMapProviders) {
        editorMapProviders.stream()
        .filter(emp -> emp != null && emp.getMap().size() > 0)
        .map(emp -> emp.getMap().entrySet())
        .forEach((s) -> {
            s.forEach(e -> {
                Class<? extends PropertyMetadata> key = e.getKey();
                Class<? extends PropertyEditor> value = e.getValue();
                List<Class<? extends PropertyEditor>> list = metadataToEditors.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                }
                if (!list.contains(value)) {
                    list.add(value);
                }
                metadataToEditors.putIfAbsent(key, list);
            });
        });
    }

    /**
     * Find editor class from the provided mapping.
     *
     * @param valueClass the value class
     * @return the class$lt;? extends abstract editor>
     */
    private Class<? extends PropertyEditor> findEditorClass(Class<? extends PropertyMetadata> valueClass) {
        Class<?> uncheckedValueClass = valueClass;
        Class<? extends PropertyEditor> editorClass = null;

        while(editorClass == null && PropertyMetadata.class.isAssignableFrom(uncheckedValueClass)) {
            List<Class<? extends PropertyEditor>> possibleEditors = metadataToEditors.get(uncheckedValueClass);

            if (possibleEditors != null && possibleEditors.size() > 0) {
                //TODO handling multiple editors may be a good thing
                editorClass = possibleEditors.get(0);
            }

            if (editorClass != null) {
                return editorClass;
            }

            uncheckedValueClass = uncheckedValueClass.getSuperclass();
        }
        return null;
    }
    public PropertyEditor newEditor(PropertyMetadata propMeta) {
        Class<? extends PropertyEditor> editorClass = findEditorClass(propMeta.getClass());

        if (editorClass == null) {
            return null;
        }

        if (!editorPools.containsKey(editorClass)) {
            editorPools.put(editorClass, new Stack<>());
        }

        Stack<PropertyEditor> editorPool = editorPools.get(editorClass);
        if ((editorPool != null) && !editorPool.isEmpty()) {
            return editorPool.pop();
        } else {
            return context.getBean(editorClass);
        }
    }

    public void releaseEditors(List<PropertyEditor> editorsInUse) {
     // Put all the editors used in the editor pools
        for (PropertyEditor editor : editorsInUse) {
            releaseEditor(editor);
        }
    }
    public void releaseEditor(PropertyEditor editor) {
        Stack<PropertyEditor> editorPool = editorPools.get(editor.getClass());
        assert editorPool != null;
        editorPool.push(editor);
        // remove all editor listeners
        editor.removeAllListeners();
    }

    public class PropertyEditorFactorySession {
        // Editors currently in use
        //   Could be a HashMap<SectionId, PropertyEditor>
        //   if we want to optimize a bit more the property editors usage,
        //   by re-using them directly in the GridPane, instead of using the pools.
        private final List<PropertyEditor> editorsInUse = new ArrayList<>();

        protected PropertyEditorFactorySession() {}

        public PropertyEditor getEditor(ValuePropertyMetadata propMeta, SelectionState selectionState) {
            assert propMeta != null;

            PropertyEditor editor = newEditor(propMeta);

            assert editor != null;
            editor.setUpdateFromModel(true);
            editor.reset(propMeta, selectionState);
            editor.setUpdateFromModel(false);
            editorsInUse.add(editor);
            return editor;
        }

        public void clear() {
            releaseEditors(editorsInUse);
            editorsInUse.clear();
        }

        public void reset(SelectionState selectionState, PropertyEditor... excludedEditors) {
            List<PropertyEditor> excluded = Arrays.asList(excludedEditors);
            editorsInUse.stream()
                .filter(e -> !excluded.contains(e))
                .forEach(e -> e.reset(e.getPropertyMeta(), selectionState));
        }

        public void forEach(Consumer<PropertyEditor> doSomething, PropertyEditor... excludedEditors) {
            List<PropertyEditor> excluded = Arrays.asList(excludedEditors);
            editorsInUse.stream()
                .filter(e -> !excluded.contains(e))
                .forEach(e -> doSomething.accept(e));
        }

        public PropertyEditor getFxIdEditor(SelectionState selectionState) {
            return getEditor(CoreEditors.FXID_EDITOR, selectionState);
        }

        public PropertyEditor getControllerClassEditor(SelectionState selectionState) {
            return getEditor(CoreEditors.FXCONTROLLER_EDITOR, selectionState);
        }

        public PropertyEditor find(PropertyName propName) {
            try {
                return editorsInUse.stream().filter(e -> e.getPropertyName().equals(propName)).findFirst().get();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public PropertyEditorFactorySession newSession() {
        return new PropertyEditorFactorySession();
    }
}
