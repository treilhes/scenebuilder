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
package com.gluonhq.jfxapps.core.api.preferences.type;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.api.preferences.AbstractPreference;
import com.gluonhq.jfxapps.core.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public abstract class ListItemObjectPreference<T> extends AbstractPreference<T> {

    private final static Logger logger = LoggerFactory.getLogger(ListItemObjectPreference.class);

    Preferences valueNode;

    public ListItemObjectPreference(PreferencesContext preferencesContext, String name, T defaultValue) {
        super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), true);
    }

    public abstract String computeKey(T object);

    public abstract void writeToNode(String key, Preferences node);

    public abstract void readFromNode(String key, Preferences node);

    @Override
    public void write() {
        assert getNode() != null;

        T value = getValue();
        String key = computeKey(value);

        if (isValid()) {
            writeToNode(key, getNode());
        } else {
            try {
                getNode().removeNode();
            } catch (BackingStoreException e) {
                logger.error("", e);
            }
            logger.error("Invalid object, data can't be saved (key: {}, value: {})", key, value);
        }

    }

    @Override
    public void read() {
        String key = getName();
        readFromNode(key, getNode());
    }

    @Override
    public boolean isValid() {
        return getValue() != null;
    }

    @Override
    public void remove() {
        try {
            getNode().removeNode();
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
