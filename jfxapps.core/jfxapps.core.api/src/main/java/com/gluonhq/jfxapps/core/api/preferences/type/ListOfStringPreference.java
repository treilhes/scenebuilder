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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gluonhq.jfxapps.core.api.preferences.AbstractPreference;
import com.gluonhq.jfxapps.core.api.preferences.Preference;
import com.gluonhq.jfxapps.core.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ListOfStringPreference extends AbstractPreference<ObservableList<String>> {

	private final static String JOIN_SEPARATOR = "\\" + File.pathSeparator;  // NOI18N
	private final static String REGEX_JOIN_SEPARATOR = "\\"+ JOIN_SEPARATOR;  // NOI18N
	public ListOfStringPreference(PreferencesContext preferencesContext, String name, List<String> defaultValue) {
		super(preferencesContext, name, defaultValue == null ? null : FXCollections.observableArrayList(defaultValue), new SimpleObjectProperty<>(), false);
	}

	@Override
	public void write() {
		getNode().put(getName(), String.join(JOIN_SEPARATOR, getValue()));
	}

	@Override
	public void read() {
		assert getName() != null;
		String defaultValue = getDefault() == null || getDefault().isEmpty() ? "" : String.join(JOIN_SEPARATOR, getDefault()); // NOI18N
		String value = getNode().get(getName(), defaultValue);
		final String[] items = value.isEmpty() ? new String[0] : value.split(REGEX_JOIN_SEPARATOR);
		final List<String> newValue = new ArrayList<>();
		newValue.addAll(Arrays.asList(items));
		setValue(FXCollections.observableList(newValue));
	}

	public Preference<ObservableList<String>> setValue(List<String> value) {
	    if (value == null) {
	        super.setValue(null);
	    } else {
	        super.getValue().clear();
	        super.getValue().addAll(value);
	    }
	    
        return this;
	}

	@Override
    public Preference<ObservableList<String>> setValue(ObservableList<String> value) {
	    if (value == null) {
            super.setValue(null);
        } else {
            super.getValue().clear();
            super.getValue().addAll(value);
        }
        return this;
    }

    @Override
	public boolean isValid() {
		return getValue() != null;
	}

}
