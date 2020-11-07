/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public class AbstractListPreferences<T extends AbstractPreference<U>, U> {
	
	/***************************************************************************
     *                                                                         *
     * Instance fields                                                         *
     *                                                                         *
     **************************************************************************/
	
	private final Preferences recordsRootPreferences;
	
    private final Map<String, T> records;

	private final KeyProvider<U> keyProvider;
	
	private final DefaultProvider<T> defaultProvider;

    /***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

    public AbstractListPreferences(RootPreferencesNode root, String listName, KeyProvider<U> keyProvider, DefaultProvider<T> defaultProvider) {
    	
    	this.keyProvider = keyProvider;
    	this.defaultProvider = defaultProvider;
    	// Preferences specific to the record
        // Create the root node for all artifacts preferences
        this.recordsRootPreferences = root.getNode().node(listName);
        this.records = new HashMap<>();

        // create initial map of existing artifacts
        try {
            final String[] childrenNames = recordsRootPreferences.childrenNames();
            for (String child : childrenNames) {
                T artifactPreference = defaultProvider.newDefault(recordsRootPreferences);
                artifactPreference.readFromJavaPreferences(child);
                addRecord(artifactPreference);
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(AbstractListPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    public T getRecord(String key) {
    	T o = records.get(key);
    	if (o == null) {
    		o = defaultProvider.newDefault(recordsRootPreferences);
    		addRecord(o);
    	}
        return o;
    }
    
    public T getRecord(U record) {
    	String key = keyProvider.newKey(record);
    	T o = records.get(key);
    	if (o == null) {
    		o = defaultProvider.newDefault(recordsRootPreferences);
    		o.setValue(record);
    		addRecord(o);
    	}
        return o;
    }
    
    public Map<String, T> getRecords() {
    	return Collections.unmodifiableMap(records);
    }
    
    public String addRecord(T object) {
    	String key = keyProvider.newKey(object.getValue());
        records.put(key, object);
        return key;
    }
    
    public void removeRecord(String key) {
        if (key != null && !key.isEmpty() && getRecord(key) != null) {
            Preferences node = recordsRootPreferences.node(key);
            try {
                node.removeNode();
                records.remove(key);
            } catch (BackingStoreException ex) {
                Logger.getLogger(AbstractListPreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

	public KeyProvider<U> getKeyProvider() {
		return keyProvider;
	}

	public DefaultProvider<T> getDefaultProvider() {
		return defaultProvider;
	}
    
}
