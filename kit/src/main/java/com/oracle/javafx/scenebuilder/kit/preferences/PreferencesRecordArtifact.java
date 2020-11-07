package com.oracle.javafx.scenebuilder.kit.preferences;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultProvider;
import com.oracle.javafx.scenebuilder.api.preferences.KeyProvider;
import com.oracle.javafx.scenebuilder.api.preferences.type.ObjectPreference;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;

public class PreferencesRecordArtifact extends ObjectPreference<MavenArtifact> {
	
	private final static String GROUPID  = "groupID";
    private final static String ARTIFACTID  = "artifactId";
    private final static String VERSION  = "version";
    public final static String DEPENDENCIES = "dependencies";
    public final static String FILTER = "filter";
    public final static String PATH = "path";
    
    private Preferences artifactNode;

	public PreferencesRecordArtifact(Preferences node, MavenArtifact defaultValue) {
		super(node, null, defaultValue);
	}

	public static KeyProvider<MavenArtifact> keyProvider() {
		return (m) -> m.getCoordinates();
	}
	
	public static DefaultProvider<PreferencesRecordArtifact> defaultProvider() {
		return (node) -> new PreferencesRecordArtifact(node, new MavenArtifact());
	}
	
	@Override
	public void writeToJavaPreferences() {
		assert getNode() != null;
        assert getValue().getCoordinates() != null;
        
        MavenArtifact mavenArtifact = getValue();
        String key = keyProvider().newKey(mavenArtifact);
        
        if (artifactNode == null) {
            try {
            	
                assert getNode().nodeExists(key) == false;
                // Create a new document preference node under the document root node
                artifactNode = getNode().node(key);
            } catch(BackingStoreException ex) {
                Logger.getLogger(PreferencesRecordArtifact.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        assert artifactNode != null;
            
        String[] items = mavenArtifact.getCoordinates().split(":");
        artifactNode.put(GROUPID, items[0]);
        artifactNode.put(ARTIFACTID, items[1]);
        artifactNode.put(VERSION, items[2]);
        artifactNode.put(DEPENDENCIES, mavenArtifact.getDependencies());
        artifactNode.put(FILTER, mavenArtifact.getFilter());
        artifactNode.put(PATH, mavenArtifact.getPath());
	}

	@Override
	public void readFromJavaPreferences(String key) {
		assert artifactNode == null;

		MavenArtifact mavenArtifact = getValue();
		
        // Check if there are some preferences for this artifact
        try {
            final String[] childrenNames = getNode().childrenNames();
            for (String child : childrenNames) {
                if (child.equals(key)) {
                	artifactNode = getNode().node(child);
                }
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesRecordArtifact.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        if (artifactNode == null) {
            return;
        }
        
        mavenArtifact.setCoordinates(key);
        mavenArtifact.setDependencies(artifactNode.get(DEPENDENCIES, null));
        mavenArtifact.setFilter(artifactNode.get(FILTER, null));
        mavenArtifact.setPath(artifactNode.get(PATH, null));
	}

	public String getPath() {
		return getValue().getPath();
	}

	public String getCoordinates() {
		return getValue().getCoordinates();
	}

	public String getDependencies() {
		return getValue().getDependencies();
	}

	public String getFilter() {
		return getValue().getFilter();
	}
	
}

