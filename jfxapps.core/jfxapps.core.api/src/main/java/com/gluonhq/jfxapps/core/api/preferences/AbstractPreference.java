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
package com.gluonhq.jfxapps.core.api.preferences;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

public abstract class AbstractPreference<T> implements Preference<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPreference.class);
    
    private String name;
    private final T defaultValue;
    private final Property<T> value;
    private PreferencesContext preferencesContext;

    public AbstractPreference(PreferencesContext preferencesContext, String name, T defaultValue,
            Property<T> propertyHolder, boolean isNode) {
        this.name = name == null ? "" : name;
        this.value = propertyHolder;
        this.defaultValue = defaultValue;
        this.preferencesContext = preferencesContext;

        // handle document scoped value
        if (preferencesContext.isDocumentScope(getClass()) && !preferencesContext.isDocumentAlreadyInPathScope()) {
            this.preferencesContext = this.preferencesContext.nodeContext(this,
                    preferencesContext.computeDocumentNodeName());
        }
        if (isNode) {
            this.preferencesContext = this.preferencesContext.nodeContext(this, this.name);
        }

        this.value.setValue(defaultValue);
    }

    protected abstract void write();

    protected abstract void read();

    @Override
    public Preferences getNode() {
        if (preferencesContext.isDocumentScope(getClass())) {
            return preferencesContext.getDocumentsNode().getNode();
        } else {
            return preferencesContext.getRootNode().getNode();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected PreferencesContext getPreferencesContext() {
        return preferencesContext;
    }

    @Override
    public T getValue() {
        return value.getValue();
    }

    @Override
    public Preference<T> setValue(T value) {
        this.value.setValue(value);
        return this;
    }

    @Override
    public ObservableValue<T> getObservableValue() {
        return this.value;
    }

    @Override
    public T getDefault() {
        return defaultValue;
    }

    @Override
    public Preference<T> reset() {
        setValue(getDefault());
        return this;
    }

    @Override
    public void writeToJavaPreferences() {
        if (logger.isDebugEnabled()) {
            boolean docScoped = preferencesContext.isDocumentScope(this.getClass());
            logger.debug("writing preference valid: {}, documentScope: {}, docName: {}, node: {}, name: {}, value: {}",
                    isValid(), docScoped, docScoped ? preferencesContext.getCurrentFilePath() : "GLOBAL",
                    getNode().absolutePath(), getName(), getValue() == null ? "<null>" : getValue().toString());
        }
        
        if (isValid() && (!preferencesContext.isDocumentScope(this.getClass())
                || preferencesContext.isDocumentNameDefined())) {
            write();
        } else {
            remove();
        }
    }

    @Override
    public void readFromJavaPreferences() {
        assert getName() != null;
        read();
        
        if (logger.isDebugEnabled()) {
            boolean docScoped = preferencesContext.isDocumentScope(this.getClass());
            logger.debug("read preference documentScope: {}, docName: {}, node: {}, name: {}, value: {}",
                    docScoped, docScoped ? preferencesContext.getCurrentFilePath() : "GLOBAL",
                    getNode().absolutePath(), getName(), getValue() == null ? "<null>" : getValue().toString());
        }
    }

    public void remove() {
        getNode().remove(getName());
    }
}
