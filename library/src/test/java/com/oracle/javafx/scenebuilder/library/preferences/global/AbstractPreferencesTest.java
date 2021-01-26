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
package com.oracle.javafx.scenebuilder.library.preferences.global;

import java.util.prefs.Preferences;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import com.oracle.javafx.scenebuilder.api.preferences.DocumentPreferencesNode;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.RootPreferencesNode;

class AbstractPreferencesTest {

	private static RootPreferencesNode root = null;
	
	protected static String DOCUMENT_NODE_NAME = "DOCUMENTS";
	
	protected static String DOCUMENT_ITEM_NODE_NAME = "DOCUMENTID";
	
	public static void defineRoot(RootPreferencesNode newRoot) {
		root = newRoot;
	}
	
	private static RootPreferencesNode testRootNode(String qualifier) {
		return new RootPreferencesNode() {
			@Override
			public Preferences getNode() {
				return root.getNode().node(qualifier);
			}
		};
	}
	
	private static DocumentPreferencesNode testDocumentNode(String qualifier) {
		return new DocumentPreferencesNode() {
			@Override
			public Preferences getNode() {
				return root.getNode().node(qualifier).node(DOCUMENT_NODE_NAME);
			}

			@Override
			public void cleanupCorruptedNodes() {}

			@Override
			public void clearAllDocumentNodes() {}
		};
	}

	private static PreferencesContext testGlobalPreferencesContext(TestInfo testInfo) {
		return new PreferencesContext(
				null, 
				testRootNode(testInfo.getTestMethod().get().getName()), 
				testDocumentNode(testInfo.getTestMethod().get().getName())) {
			@Override
			public boolean isDocumentScope(Class<?> cls) {
				return false;
			}
		};
	}
	
	private static PreferencesContext testDocumentPreferencesContext(TestInfo testInfo) {
		
		return new PreferencesContext(
				null, 
				testRootNode(testInfo.getTestMethod().get().getName()), 
				testDocumentNode(testInfo.getTestMethod().get().getName())) {
			@Override
			public boolean isDocumentScope(Class<?> cls) {
				return true;
			}

			@Override
			public String computeDocumentNodeName() {
				return DOCUMENT_ITEM_NODE_NAME;
			}
			
		};
	}
	
	private static void removeNode(String qualifier) throws Exception {
		root.getNode().node(qualifier).removeNode();
		root.getNode().flush();
	}
	
	@AfterAll
	public static void end() throws Exception {
		Preferences.userNodeForPackage(AbstractPreferencesTest.class).removeNode();
	}
	
	protected PreferencesContext globalPreferenceContext;
	protected PreferencesContext documentPreferenceContext;
	
	@BeforeEach
	public void setUp(TestInfo testInfo) {
		globalPreferenceContext = testGlobalPreferencesContext(testInfo);
		documentPreferenceContext = testDocumentPreferencesContext(testInfo);
	}
	
	@AfterEach
	public void tearDown(TestInfo testInfo) throws Exception {
		globalPreferenceContext = null;
		documentPreferenceContext = null;
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
}
