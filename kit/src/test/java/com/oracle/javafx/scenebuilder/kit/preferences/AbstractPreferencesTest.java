package com.oracle.javafx.scenebuilder.kit.preferences;

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
