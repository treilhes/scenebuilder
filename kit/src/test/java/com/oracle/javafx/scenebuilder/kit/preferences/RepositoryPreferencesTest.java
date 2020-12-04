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
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;

class RepositoryPreferencesTest extends AbstractPreferencesTest {

	static {
		defineRoot(new RootPreferencesNode() {
			@Override
			public Preferences getNode() {
				return Preferences.userNodeForPackage(RepositoryPreferencesTest.class)
						.node(RepositoryPreferencesTest.class.getSimpleName());
			}
		});
	}

	private static Repository validObject = new Repository();
	static {
		validObject.setId("id1");
		validObject.setType("x");
		validObject.setURL("y");
		validObject.setUser(null);
		validObject.setPassword(null);
	}

	private static Repository validObject2 = new Repository();
	static {
		validObject2.setId("id2");
		validObject2.setType("");
		validObject2.setURL("");
		validObject2.setUser("");
		validObject2.setPassword("");
	}

	private static Repository validObject3 = new Repository();
	static {
		validObject3.setId("id3");
		validObject3.setType("default");
		validObject3.setURL("url");
		validObject3.setUser("user");
		validObject3.setPassword("password");
	}

	private static Repository invalidFieldsObject = new Repository();
	private static Repository invalidCoordinatesObject = new Repository();

	protected static Stream<Repository> provideValidObjects() {
		return Stream.of(validObject, validObject2, validObject3);
	}

	protected static Stream<Repository> provideInvalidObjects() {
		return Stream.of(invalidFieldsObject, invalidCoordinatesObject);
	}

	@Test
	void shouldCreateRootNode(TestInfo testInfo) throws Exception {
		new MavenRepositoriesPreferences(globalPreferenceContext);
		globalPreferenceContext.getRootNode().getNode().flush();

		assertTrue(globalPreferenceContext.getRootNode().getNode().nodeExists(MavenRepositoriesPreferences.NODE_NAME),
				"Preferences root node should exists");
	}

	@Test
	void shouldCreateDocumentNode(TestInfo testInfo) throws Exception {
		new MavenRepositoriesPreferences(documentPreferenceContext);
		documentPreferenceContext.getDocumentsNode().getNode().flush();
		assertTrue(documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
				.nodeExists(MavenRepositoriesPreferences.NODE_NAME), "Preferences root node should exists");
	}

	@ParameterizedTest
	@MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.RepositoryPreferencesTest#provideValidObjects")
	void shouldCreateRecord(Repository artifact, TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(globalPreferenceContext);
		mp.getRecord(artifact).writeToJavaPreferences();

		assertTrue(globalPreferenceContext.getRootNode().getNode().node(MavenRepositoriesPreferences.NODE_NAME)
				.childrenNames().length == 1, "Preferences node should exists");
	}
	
	@ParameterizedTest
	@MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.RepositoryPreferencesTest#provideValidObjects")
	void shouldCreateDocumentRecord(Repository artifact, TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(documentPreferenceContext);
		mp.getRecord(artifact).writeToJavaPreferences();

		assertTrue(
				documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
						.node(MavenRepositoriesPreferences.NODE_NAME).childrenNames().length == 1,
				"Preferences node should exists");
	}

	@ParameterizedTest
	@MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.RepositoryPreferencesTest#provideInvalidObjects")
	public void shouldNotCreateRecord(Repository artifact, TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(globalPreferenceContext);
		mp.getRecord(artifact).writeToJavaPreferences();

		assertTrue(globalPreferenceContext.getRootNode().getNode().node(MavenRepositoriesPreferences.NODE_NAME)
				.childrenNames().length == 0, "Preferences node should exists");
	}
	
	@ParameterizedTest
	@MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.RepositoryPreferencesTest#provideInvalidObjects")
	public void shouldNotCreateDocumentRecord(Repository artifact, TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(documentPreferenceContext);
		mp.getRecord(artifact).writeToJavaPreferences();

		assertTrue(
				documentPreferenceContext.getDocumentsNode().getNode().node(DOCUMENT_ITEM_NODE_NAME)
						.node(MavenRepositoriesPreferences.NODE_NAME).childrenNames().length == 0,
				"Preferences node should exists");
	}

	@Test
	public void shouldSaveRecordData(TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(globalPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();

		Repository loaded = mp.getRecord(mp.getKeyProvider().newKey(validObject)).getValue();

		assertTrue(validObject.equals(loaded), "Repository objects should be equals");
	}
	
	@Test
	public void shouldSaveDocumentRecordData(TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(documentPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();

		Repository loaded = mp.getRecord(mp.getKeyProvider().newKey(validObject)).getValue();

		assertTrue(validObject.equals(loaded), "Repository objects should be equals");
	}

	@Test
	public void shouldCreateAllRecord(TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(globalPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		assertEquals(3, mp.getRecords().size());
	}
	
	@Test
	public void shouldCreateAllDocumentRecord(TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(documentPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		assertEquals(3, mp.getRecords().size());
	}

	@Test
	public void shouldDeleteOnlyOneRecord(TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(globalPreferenceContext);
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
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(documentPreferenceContext);
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
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(globalPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		MavenRepositoriesPreferences mpNew = new MavenRepositoriesPreferences(globalPreferenceContext);

		assertEquals(3, mpNew.getRecords().size());
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject)).getValue(), validObject);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject2)).getValue(), validObject2);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject3)).getValue(), validObject3);
	}
	
	@Test
	public void shouldInitDocumentRecords(TestInfo testInfo) throws Exception {
		MavenRepositoriesPreferences mp = new MavenRepositoriesPreferences(documentPreferenceContext);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();

		MavenRepositoriesPreferences mpNew = new MavenRepositoriesPreferences(documentPreferenceContext);

		assertEquals(3, mpNew.getRecords().size());
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject)).getValue(), validObject);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject2)).getValue(), validObject2);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject3)).getValue(), validObject3);
	}
}