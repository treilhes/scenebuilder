package com.oracle.javafx.scenebuilder.kit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.prefs.Preferences;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.oracle.javafx.scenebuilder.api.preferences.RootPreferencesNode;
import com.oracle.javafx.scenebuilder.api.preferences.type.StringPreference;

public class StringPreferenceTest extends AbstractPreferencesTest {

	private static final String STRING_NAME_1 = "string1";
	private static final String STRING_VALUE_1 = "value1";
	private static final String DEFAULT_VALUE = "DEFAULT";
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
		StringPreference sp = new StringPreference(globalPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp.writeToJavaPreferences();
		assertEquals(DEFAULT_VALUE, globalPreferenceContext.getRootNode().getNode().get(STRING_NAME_1, NO_VALUE));
	}

	@Test
	void shouldCreateDocumentValue(TestInfo testInfo) throws Exception {
		StringPreference sp = new StringPreference(documentPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp.writeToJavaPreferences();
		assertEquals(DEFAULT_VALUE, documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(STRING_NAME_1, NO_VALUE));
	}

	@Test
	void shouldNotCreateRecordValue(TestInfo testInfo) throws Exception {
		StringPreference sp = new StringPreference(globalPreferenceContext, STRING_NAME_1, null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, globalPreferenceContext.getRootNode().getNode().get(STRING_NAME_1, NO_VALUE));
	}

	@Test
	void shouldNotCreateDocumentRecordValue(TestInfo testInfo) throws Exception {
		StringPreference sp = new StringPreference(documentPreferenceContext, STRING_NAME_1, null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(STRING_NAME_1, NO_VALUE));
	}

	@Test
	void shouldSaveRecordValue(TestInfo testInfo) throws Exception {
		StringPreference sp = new StringPreference(globalPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp.setValue(STRING_VALUE_1);
		sp.writeToJavaPreferences();
		assertEquals(STRING_VALUE_1, globalPreferenceContext.getRootNode().getNode().get(STRING_NAME_1, NO_VALUE));
	}

	@Test
	void shouldSaveDocumentRecordValue(TestInfo testInfo) throws Exception {
		StringPreference sp = new StringPreference(documentPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp.setValue(STRING_VALUE_1);
		sp.writeToJavaPreferences();
		assertEquals(STRING_VALUE_1, documentPreferenceContext.getDocumentsNode().getNode()
				.node(DOCUMENT_ITEM_NODE_NAME).get(STRING_NAME_1, NO_VALUE));
	}

	@Test
	public void shouldDeleteValue(TestInfo testInfo) throws Exception {
		StringPreference sp = new StringPreference(globalPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp.setValue(STRING_VALUE_1);
		sp.writeToJavaPreferences();

		sp.setValue(null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, globalPreferenceContext.getRootNode().getNode().get(STRING_NAME_1, NO_VALUE));
	}

	@Test
	public void shouldDeleteDocumentValue(TestInfo testInfo) throws Exception {
		StringPreference sp = new StringPreference(documentPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp.setValue(STRING_VALUE_1);
		sp.writeToJavaPreferences();

		sp.setValue(null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(STRING_NAME_1, NO_VALUE));
	}

	@Test
	public void shouldInitValue(TestInfo testInfo) throws Exception {
		StringPreference sp = new StringPreference(globalPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp.setValue(STRING_VALUE_1);
		sp.writeToJavaPreferences();

		StringPreference sp2 = new StringPreference(globalPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp2.readFromJavaPreferences();
		assertEquals(STRING_VALUE_1, sp2.getValue());
	}

	@Test
	public void shouldInitDocumentValue(TestInfo testInfo) throws Exception {
		StringPreference sp = new StringPreference(documentPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp.setValue(STRING_VALUE_1);
		sp.writeToJavaPreferences();

		StringPreference sp2 = new StringPreference(documentPreferenceContext, STRING_NAME_1, DEFAULT_VALUE);
		sp2.readFromJavaPreferences();
		assertEquals(STRING_VALUE_1, sp2.getValue());
	}
}
