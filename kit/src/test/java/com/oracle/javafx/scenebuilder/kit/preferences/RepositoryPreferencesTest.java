package com.oracle.javafx.scenebuilder.kit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.prefs.Preferences;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.oracle.javafx.scenebuilder.api.preferences.RootPreferencesNode;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository.Repository;


class RepositoryPreferencesTest {

	private static RootPreferencesNode root = new RootPreferencesNode() {
		@Override
		public Preferences getNode() {
			return Preferences.userNodeForPackage(RepositoryPreferencesTest.class).node(RepositoryPreferencesTest.class.getSimpleName());
		}
	};
	private static RootPreferencesNode getNode(String qualifier) {
		return new RootPreferencesNode() {
			@Override
			public Preferences getNode() {
				return root.getNode().node(qualifier);
			}
		};
	}
	private static void removeNode(String qualifier) throws Exception {
		root.getNode().node(qualifier).removeNode();
		root.getNode().flush();
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
	
	private static Stream<Repository> provideValidObjects() {
	    return Stream.of(validObject, validObject2, validObject3);
	}
	private static Stream<Repository> provideInvalidObjects() {
	    return Stream.of(invalidFieldsObject, invalidCoordinatesObject);
	}

	@AfterAll
	public static void end() throws Exception {
		Preferences.userNodeForPackage(RepositoryPreferencesTest.class).removeNode();
	}
	
	@Test
	void shouldCreateRootNode(TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		new RepositoryPreferences(testRoot);
		testRoot.getNode().flush();
		
		assertTrue(testRoot.getNode().nodeExists(RepositoryPreferences.NODE_NAME), "Preferences root node should exists");
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
	@ParameterizedTest
    @MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.RepositoryPreferencesTest#provideValidObjects")
	void shouldCreateRecord(Repository artifact, TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		RepositoryPreferences mp = new RepositoryPreferences(testRoot);
		mp.getRecord(artifact).writeToJavaPreferences();
		
		assertTrue(testRoot.getNode().node(RepositoryPreferences.NODE_NAME).childrenNames().length == 1, "Preferences node should exists");
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
	@ParameterizedTest
    @MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.RepositoryPreferencesTest#provideInvalidObjects")
	public void shouldNotCreateRecord(Repository artifact, TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		RepositoryPreferences mp = new RepositoryPreferences(testRoot);
		mp.getRecord(artifact).writeToJavaPreferences();
		
		assertTrue(testRoot.getNode().node(RepositoryPreferences.NODE_NAME).childrenNames().length == 0, "Preferences node should exists");
		
		removeNode(testInfo.getTestMethod().get().getName());
	}

	@Test
	public void shouldSaveRecordData(TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		RepositoryPreferences mp = new RepositoryPreferences(testRoot);
		mp.getRecord(validObject).writeToJavaPreferences();
		
		Repository loaded = mp.getRecord(mp.getKeyProvider().newKey(validObject)).getValue();
		
		assertTrue(validObject.equals(loaded), "Repository objects should be equals");
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
	@Test
	public void shouldCreateAllRecord(TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		RepositoryPreferences mp = new RepositoryPreferences(testRoot);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();
		
		assertEquals(3, mp.getRecords().size());
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
	@Test
	public void shouldDeleteOnlyOneRecord(TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		RepositoryPreferences mp = new RepositoryPreferences(testRoot);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();
		
		mp.removeRecord(mp.getKeyProvider().newKey(validObject2));
		
		assertEquals(2, mp.getRecords().size());
		assertEquals(mp.getRecord(mp.getKeyProvider().newKey(validObject)).getValue(), validObject);
		assertEquals(mp.getRecord(mp.getKeyProvider().newKey(validObject3)).getValue(), validObject3);
		
		assertTrue(!mp.getRecord(mp.getKeyProvider().newKey(validObject2)).getValue().equals(validObject2));
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
	@Test
	public void shouldInitRecords(TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		RepositoryPreferences mp = new RepositoryPreferences(testRoot);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();
		
		RepositoryPreferences mpNew = new RepositoryPreferences(testRoot);
		
		assertEquals(3, mpNew.getRecords().size());
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject)).getValue(), validObject);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject2)).getValue(), validObject2);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject3)).getValue(), validObject3);
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
}
