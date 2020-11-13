package com.oracle.javafx.scenebuilder.kit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.prefs.Preferences;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.oracle.javafx.scenebuilder.api.preferences.RootPreferencesNode;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;

class MavenPreferencesTest extends AbstractPreferencesTest {

	static {
		defineRoot(new RootPreferencesNode() {
			@Override
			public Preferences getNode() {
				return Preferences.userNodeForPackage(MavenPreferencesTest.class)
						.node(MavenPreferencesTest.class.getSimpleName());
			}
		});
	}

	private static MavenArtifact validObject = new MavenArtifact("xxx:xxx:xxx");
	static {
		validObject.setDependencies("a");
		validObject.setFilter("b");
		validObject.setPath("c");
	}

	private static MavenArtifact validObject2 = new MavenArtifact("xxx:yyy:xxx");
	static {
		validObject2.setDependencies("");
		validObject2.setFilter("");
		validObject2.setPath("c2");
	}

	private static MavenArtifact validObject3 = new MavenArtifact("xxx:zzz:xxx");
	static {
		validObject3.setDependencies("");
		validObject3.setFilter("b3");
		validObject3.setPath("c3");
	}

	private static MavenArtifact invalidFieldsObject = new MavenArtifact("xxx:xxx:xxx");
	private static MavenArtifact invalidCoordinatesObject = new MavenArtifact("xxx");

	protected static Stream<MavenArtifact> provideValidObjects() {
		return Stream.of(validObject, validObject2, validObject3);
	}

	protected static Stream<MavenArtifact> provideInvalidObjects() {
		return Stream.of(invalidFieldsObject, invalidCoordinatesObject);
	}

	@Test
	void shouldCreateRootNode(TestInfo testInfo) throws Exception {
		new MavenArtifactsPreferences(globalPreferenceContext);
		globalPreferenceContext.getRootNode().getNode().flush();
		assertTrue(globalPreferenceContext.getRootNode().getNode().nodeExists(MavenArtifactsPreferences.NODE_NAME),
				"Preferences root node should exists");
	}

	@Test
	void shouldCreateDocumentNode(TestInfo testInfo) throws Exception {
		new MavenArtifactsPreferences(documentPreferenceContext);
		documentPreferenceContext.getDocumentsNode().getNode().flush();
		assertTrue(documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.nodeExists(MavenArtifactsPreferences.NODE_NAME), "Preferences root node should exists");
	}

	@ParameterizedTest
	@MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferencesTest#provideValidObjects")
	void shouldCreateRecord(MavenArtifact artifact, TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(globalPreferenceContext);
		mp.getRecord(artifact).writeToJavaPreferences();

		assertTrue(globalPreferenceContext.getRootNode().getNode().node(MavenArtifactsPreferences.NODE_NAME)
				.childrenNames().length == 1, "Preferences node should exists");
	}

	@ParameterizedTest
	@MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferencesTest#provideValidObjects")
	void shouldCreateDocumentRecord(MavenArtifact artifact, TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(documentPreferenceContext);
		mp.getRecord(artifact).writeToJavaPreferences();

		assertTrue(
				documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
						.node(MavenArtifactsPreferences.NODE_NAME).childrenNames().length == 1,
				"Preferences node should exists");
	}

	@ParameterizedTest
	@MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferencesTest#provideInvalidObjects")
	public void shouldNotCreateRecord(MavenArtifact artifact, TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(globalPreferenceContext);
		mp.getRecord(artifact).writeToJavaPreferences();

		assertTrue(globalPreferenceContext.getRootNode().getNode().node(MavenArtifactsPreferences.NODE_NAME)
				.childrenNames().length == 0, "Preferences node should exists");
	}

	@ParameterizedTest
	@MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferencesTest#provideInvalidObjects")
	public void shouldNotCreateDocumentRecord(MavenArtifact artifact, TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(documentPreferenceContext);
		mp.getRecord(artifact).writeToJavaPreferences();

		assertTrue(
				documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
						.node(MavenArtifactsPreferences.NODE_NAME).childrenNames().length == 0,
				"Preferences node should exists");
	}

	@Test
	public void shouldSaveRecordData(TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(globalPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();

		MavenArtifact loaded = mp.getRecord(mp.getKeyProvider().newKey(validObject)).getValue();

		assertTrue(validObject.equals(loaded), "MavenArtifact objects should be equals");
	}

	@Test
	public void shouldSaveDocumentRecordData(TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(documentPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();

		MavenArtifact loaded = mp.getRecord(mp.getKeyProvider().newKey(validObject)).getValue();

		assertTrue(validObject.equals(loaded), "MavenArtifact objects should be equals");
	}

	@Test
	public void shouldCreateAllRecord(TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(globalPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		assertEquals(3, mp.getRecords().size());
	}

	@Test
	public void shouldCreateAllDocumentRecord(TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(documentPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		assertEquals(3, mp.getRecords().size());
	}

	@Test
	public void shouldDeleteOnlyOneRecord(TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(globalPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		mp.removeRecord(mp.getKeyProvider().newKey(validObject2));

		assertEquals(2, mp.getRecords().size());
		assertEquals(mp.getRecord(mp.getKeyProvider().newKey(validObject)).getValue(), validObject);
		assertEquals(mp.getRecord(mp.getKeyProvider().newKey(validObject3)).getValue(), validObject3);

		assertTrue(!mp.getRecord(mp.getKeyProvider().newKey(validObject2)).getValue().equals(validObject2));
	}

	@Test
	public void shouldDeleteOnlyOneDocumentRecord(TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(documentPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		mp.removeRecord(mp.getKeyProvider().newKey(validObject2));

		assertEquals(2, mp.getRecords().size());
		assertEquals(mp.getRecord(mp.getKeyProvider().newKey(validObject)).getValue(), validObject);
		assertEquals(mp.getRecord(mp.getKeyProvider().newKey(validObject3)).getValue(), validObject3);

		assertTrue(!mp.getRecord(mp.getKeyProvider().newKey(validObject2)).getValue().equals(validObject2));
	}

	@Test
	public void shouldInitRecords(TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(globalPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		MavenArtifactsPreferences mpNew = new MavenArtifactsPreferences(globalPreferenceContext);

		assertEquals(3, mpNew.getRecords().size());
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject)).getValue(), validObject);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject2)).getValue(), validObject2);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject3)).getValue(), validObject3);
	}

	@Test
	public void shouldInitDocumentRecords(TestInfo testInfo) throws Exception {
		MavenArtifactsPreferences mp = new MavenArtifactsPreferences(documentPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		MavenArtifactsPreferences mpNew = new MavenArtifactsPreferences(documentPreferenceContext);

		assertEquals(3, mpNew.getRecords().size());
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject)).getValue(), validObject);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject2)).getValue(), validObject2);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject3)).getValue(), validObject3);
	}
}
