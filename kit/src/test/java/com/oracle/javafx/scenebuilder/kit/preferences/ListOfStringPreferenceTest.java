package com.oracle.javafx.scenebuilder.kit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.oracle.javafx.scenebuilder.api.preferences.RootPreferencesNode;
import com.oracle.javafx.scenebuilder.api.preferences.type.ListOfStringPreference;

public class ListOfStringPreferenceTest extends AbstractPreferencesTest {

	private static final String LIST_NAME_1 = "string1";
	private static final List<String> LIST_VALUE_1 = new ArrayList<>(Arrays.asList("value1","value2","value3"));
	private static final List<String> DEFAULT_VALUE = new ArrayList<>();
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
		ListOfStringPreference sp = new ListOfStringPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.writeToJavaPreferences();
		assertEquals("", globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldCreateDocumentValue(TestInfo testInfo) throws Exception {
		ListOfStringPreference sp = new ListOfStringPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.writeToJavaPreferences();
		assertEquals("", documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldNotCreateRecordValue(TestInfo testInfo) throws Exception {
		ListOfStringPreference sp = new ListOfStringPreference(globalPreferenceContext, LIST_NAME_1, null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldNotCreateDocumentRecordValue(TestInfo testInfo) throws Exception {
		ListOfStringPreference sp = new ListOfStringPreference(documentPreferenceContext, LIST_NAME_1, null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	void shouldSaveRecordValue(TestInfo testInfo) throws Exception {
		ListOfStringPreference sp = new ListOfStringPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();
		assertFalse(globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE).isEmpty());
	}

	@Test
	void shouldSaveDocumentRecordValue(TestInfo testInfo) throws Exception {
		ListOfStringPreference sp = new ListOfStringPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();
		assertFalse(documentPreferenceContext.getDocumentsNode().getNode()
				.node(DOCUMENT_ITEM_NODE_NAME).get(LIST_NAME_1, NO_VALUE).isEmpty());
	}

	@Test
	public void shouldDeleteValue(TestInfo testInfo) throws Exception {
		ListOfStringPreference sp = new ListOfStringPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		sp.setValue(null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, globalPreferenceContext.getRootNode().getNode().get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	public void shouldDeleteDocumentValue(TestInfo testInfo) throws Exception {
		ListOfStringPreference sp = new ListOfStringPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		sp.setValue(null);
		sp.writeToJavaPreferences();
		assertEquals(NO_VALUE, documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.get(LIST_NAME_1, NO_VALUE));
	}

	@Test
	public void shouldInitValue(TestInfo testInfo) throws Exception {
		ListOfStringPreference sp = new ListOfStringPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		ListOfStringPreference sp2 = new ListOfStringPreference(globalPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp2.readFromJavaPreferences();
		assertEquals(LIST_VALUE_1, sp2.getValue());
	}

	@Test
	public void shouldInitDocumentValue(TestInfo testInfo) throws Exception {
		ListOfStringPreference sp = new ListOfStringPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp.setValue(LIST_VALUE_1);
		sp.writeToJavaPreferences();

		ListOfStringPreference sp2 = new ListOfStringPreference(documentPreferenceContext, LIST_NAME_1, DEFAULT_VALUE);
		sp2.readFromJavaPreferences();
		assertEquals(LIST_VALUE_1, sp2.getValue());
	}
}
