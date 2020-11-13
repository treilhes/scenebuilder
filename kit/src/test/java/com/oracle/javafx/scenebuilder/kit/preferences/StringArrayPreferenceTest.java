package com.oracle.javafx.scenebuilder.kit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.prefs.Preferences;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

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
		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.writeToJavaPreferences();
		assertEquals("", globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldCreateDocumentValue(TestInfo testInfo) throws Exception {
		StringArrayPreference sp = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.writeToJavaPreferences();
		assertEquals("", documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldNotCreateRecordValue(TestInfo testInfo) throws Exception {
		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldNotCreateDocumentRecordValue(TestInfo testInfo) throws Exception {
		StringArrayPreference sp = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldSaveRecordValue(TestInfo testInfo) throws Exception {
		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();
		assertFalse(globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE).isEmpty());
	}

	@Test
	void shouldSaveDocumentRecordValue(TestInfo testInfo) throws Exception {
		StringArrayPreference sp = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();
		assertFalse(documentPreferenceContext.getDocumentsNode().getNode()
				.node(DOCUMENT_ITEM_NODE_NAME).get(LIST_NAME_1, NO_VALUE).isEmpty());
	}

	@Test
	public void shouldDeleteValue(TestInfo testInfo) throws Exception {
		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		sp.setValue(null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	public void shouldDeleteDocumentValue(TestInfo testInfo) throws Exception {
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
		StringArrayPreference sp = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		StringArrayPreference sp2 = new StringArrayPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp2.readFromJavaPreferences();
		assertTrue(Arrays.equals(LIST_VALUE_1, sp2.getValue()));
	}

	@Test
	public void shouldInitDocumentValue(TestInfo testInfo) throws Exception {
		StringArrayPreference sp = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		StringArrayPreference sp2 = new StringArrayPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp2.readFromJavaPreferences();
		assertTrue(Arrays.equals(LIST_VALUE_1, sp2.getValue()));
	}
}
