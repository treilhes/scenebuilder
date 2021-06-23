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
package com.oracle.javafx.scenebuilder.api.preferences;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public abstract class MapPreferences<K, V>
        extends AbstractPreference<ObservableMap<K, V>> {

    public MapPreferences(PreferencesContext preferencesContext, String listName) {
        super(preferencesContext, listName, FXCollections.observableHashMap(),
                new SimpleObjectProperty<ObservableMap<K, V>>(), true);

        // create initial map of existing artifacts
        read();
    }

    public abstract String keyString(K key);
    public abstract String valueString(V value);
    public abstract K fromKeyString(String key);
    public abstract V fromValueString(String value);
    
    /***************************************************************************
     * * Methods * *
     **************************************************************************/

    public V get(K key) {
        V value = getValue().get(key);
        return value;
    }

    public Map<K, V> getUnmodifiableMap() {
        return Collections.unmodifiableMap(getValue());
    }

    public void put(K key, V value) {
        getValue().put(key, value);
    }

    public void remove(K key) {
        getValue().remove(key);
    }

    @Override
    public void write() {
        try {
            getNode().clear();
            getValue().forEach((k, v) -> {
                String key = keyString(k);
                String value = valueString(v);
                getNode().put(key, value);
            });
        } catch (BackingStoreException e) {
            Logger.getLogger(MapPreferences.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void read() {
        getValue().clear();
        // create initial map of existing artifacts
        try {
            final String[] childrenNames = getNode().keys();
            for (String child : childrenNames) {
                K key = fromKeyString(child);
                V value = fromValueString(getNode().get(child, null));
                put(key, value);
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(MapPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isValid() {
        return getValue() != null;
    }

}
