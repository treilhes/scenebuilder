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
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;

class MavenPreferencesTest {

	private static RootPreferencesNode root = new RootPreferencesNode() {
		@Override
		public Preferences getNode() {
			return Preferences.userNodeForPackage(MavenPreferencesTest.class).node(MavenPreferencesTest.class.getSimpleName());
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
	
	private static Stream<MavenArtifact> provideValidObjects() {
	    return Stream.of(validObject, validObject2, validObject3);
	}
	private static Stream<MavenArtifact> provideInvalidObjects() {
	    return Stream.of(invalidFieldsObject, invalidCoordinatesObject);
	}

	@AfterAll
	public static void end() throws Exception {
		Preferences.userNodeForPackage(MavenPreferencesTest.class).removeNode();
	}
	
	@Test
	void shouldCreateRootNode(TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		new MavenPreferences(testRoot);
		testRoot.getNode().flush();
		
		assertTrue(testRoot.getNode().nodeExists(MavenPreferences.NODE_NAME), "Preferences root node should exists");
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
	@ParameterizedTest
    @MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferencesTest#provideValidObjects")
	void shouldCreateRecord(MavenArtifact artifact, TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		MavenPreferences mp = new MavenPreferences(testRoot);
		mp.getRecord(artifact).writeToJavaPreferences();
		
		assertTrue(testRoot.getNode().node(MavenPreferences.NODE_NAME).childrenNames().length == 1, "Preferences node should exists");
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
	@ParameterizedTest
    @MethodSource("com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferencesTest#provideInvalidObjects")
	public void shouldNotCreateRecord(MavenArtifact artifact, TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		MavenPreferences mp = new MavenPreferences(testRoot);
		mp.getRecord(artifact).writeToJavaPreferences();
		
		assertTrue(testRoot.getNode().node(MavenPreferences.NODE_NAME).childrenNames().length == 0, "Preferences node should exists");
		
		removeNode(testInfo.getTestMethod().get().getName());
	}

	@Test
	public void shouldSaveRecordData(TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		MavenPreferences mp = new MavenPreferences(testRoot);
		mp.getRecord(validObject).writeToJavaPreferences();
		
		MavenArtifact loaded = mp.getRecord(mp.getKeyProvider().newKey(validObject)).getValue();
		
		assertTrue(validObject.equals(loaded), "MavenArtifact objects should be equals");
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
	@Test
	public void shouldCreateAllRecord(TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		MavenPreferences mp = new MavenPreferences(testRoot);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();
		
		assertEquals(3, mp.getRecords().size());
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
	
	@Test
	public void shouldDeleteOnlyOneRecord(TestInfo testInfo) throws Exception {
		RootPreferencesNode testRoot = getNode(testInfo.getTestMethod().get().getName());
		
		MavenPreferences mp = new MavenPreferences(testRoot);
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
		
		MavenPreferences mp = new MavenPreferences(testRoot);
		mp.getRecord(validObject).writeToJavaPreferences();
		mp.getRecord(validObject2).writeToJavaPreferences();
		mp.getRecord(validObject3).writeToJavaPreferences();
		
		MavenPreferences mpNew = new MavenPreferences(testRoot);
		
		assertEquals(3, mpNew.getRecords().size());
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject)).getValue(), validObject);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject2)).getValue(), validObject2);
		assertEquals(mpNew.getRecord(mpNew.getKeyProvider().newKey(validObject3)).getValue(), validObject3);
		
		removeNode(testInfo.getTestMethod().get().getName());
	}
}
