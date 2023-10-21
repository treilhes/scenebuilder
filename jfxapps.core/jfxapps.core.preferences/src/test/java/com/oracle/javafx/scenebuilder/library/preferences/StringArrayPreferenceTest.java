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
package com.oracle.javafx.scenebuilder.library.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.prefs.Preferences;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.RootPreferencesNode;
import com.oracle.javafx.scenebuilder.api.preferences.type.StringArrayPreference;

public class StringArrayPreferenceTest extends AbstractPreferencesTest {

	private static final String LIST_NAME_1 = "string1";
	private static final String[] LIST_VALUE_1 = new String[] {"value1","value2","value3"};
	private static final String[] DEFAULT_VALUE = new String[0];
	private static final String NO_VALUE = "NO_VALUE";

	static {
		defineRoot(new RootPreferencesNode() {
			@Override
			public Preferences getNode() {
				return Preferences.userNodeForPackage(StringPreferenceTest.class)
						.node(StringPreferenceTest.class.getSimpleName());
			}
		});
	}

	@Test
	void shouldCreateValue(TestInfo testInfo) throws Exception {
	    PreferencesContext globalPreferenceContext = testGlobalPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.writeToJavaPreferences();
		assertEquals("", globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldCreateDocumentValue(TestInfo testInfo) throws Exception {
	    PreferencesContext documentPreferenceContext = testDocumentPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.writeToJavaPreferences();
		assertEquals("", documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldNotCreateRecordValue(TestInfo testInfo) throws Exception {
	    PreferencesContext globalPreferenceContext = testGlobalPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldNotCreateDocumentRecordValue(TestInfo testInfo) throws Exception {
	    PreferencesContext documentPreferenceContext = testDocumentPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldSaveRecordValue(TestInfo testInfo) throws Exception {
	    PreferencesContext globalPreferenceContext = testGlobalPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();
		assertFalse(globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE).isEmpty());
	}

	@Test
	void shouldSaveDocumentRecordValue(TestInfo testInfo) throws Exception {
	    PreferencesContext documentPreferenceContext = testDocumentPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();
		assertFalse(documentPreferenceContext.getDocumentsNode().getNode()
				.node(DOCUMENT_ITEM_NODE_NAME).get(LIST_NAME_1, NO_VALUE).isEmpty());
	}

	@Test
	public void shouldDeleteValue(TestInfo testInfo) throws Exception {
	    PreferencesContext globalPreferenceContext = testGlobalPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		sp.setValue(null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	public void shouldDeleteDocumentValue(TestInfo testInfo) throws Exception {
	    PreferencesContext documentPreferenceContext = testDocumentPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		sp.setValue(null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	public void shouldInitValue(TestInfo testInfo) throws Exception {
	    PreferencesContext globalPreferenceContext = testGlobalPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		StringArrayPreference sp2 = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp2.readFromJavaPreferences();
		assertTrue(Arrays.equals(LIST_VALUE_1, sp2.getValue()));
	}

	@Test
	public void shouldInitDocumentValue(TestInfo testInfo) throws Exception {
	    PreferencesContext documentPreferenceContext = testDocumentPreferencesContext(testInfo);

		StringArrayPreference sp = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		StringArrayPreference sp2 = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp2.readFromJavaPreferences();
		assertTrue(Arrays.equals(LIST_VALUE_1, sp2.getValue()));
	}
}
