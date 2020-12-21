package com.oracle.javafx.scenebuilder.kit.preferences;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultProvider;
import com.oracle.javafx.scenebuilder.api.preferences.KeyProvider;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.ListItemObjectPreference;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.MavenArtifact;

public class MavenArtifactPreferences extends ListItemObjectPreference<MavenArtifact> {
	
	private final static String GROUPID  = "groupID";
    private final static String ARTIFACTID  = "artifactId";
    private final static String VERSION  = "version";
    public final static String DEPENDENCIES = "dependencies";
    public final static String FILTER = "filter";
    public final static String PATH = "path";

	public MavenArtifactPreferences(PreferencesContext preferencesContext, String name, MavenArtifact defaultValue) {
		super(preferencesContext, name, defaultValue);
	}

	public static KeyProvider<MavenArtifact> keyProvider() {
		return (m) -> m.getCoordinates();
	}
	
	public static DefaultProvider<MavenArtifactPreferences> defaultProvider() {
		return (pc, name) -> new MavenArtifactPreferences(pc, name, new MavenArtifact());
	}

	public static boolean isValid(MavenArtifact object) {
		boolean valid = true;
		
		if (object == null) {
			Logger.getLogger(MavenArtifactPreferences.class.getName()).log(Level.SEVERE, "MavenArtifact can't be null");
			return false;
		}
		if (object.getCoordinates() == null) {
			Logger.getLogger(MavenArtifactPreferences.class.getName()).log(Level.SEVERE, "MavenArtifact coordinates can't be null or empty");
			valid &= false;
		} else {
			String[] items = object.getCoordinates().split(":");
			if (items.length != 3) {
				Logger.getLogger(MavenArtifactPreferences.class.getName()).log(Level.SEVERE, 
						"Wrong MavenArtifact coordinates format, it must be \"groupId:artifactId:version\" but it is \"{0}\"", 
						object.getCoordinates());
				valid &= false;
			}
		}
		
		if (object.getDependencies() == null || object.getFilter() == null || object.getPath() == null) {
			Logger.getLogger(MavenArtifactPreferences.class.getName()).log(Level.SEVERE, "MavenArtifact fields can't be null");
			valid &= false;
		}
		return valid;
	}
	
	@Override
	public boolean isValid() {
		return MavenArtifactPreferences.isValid(getValue());
	}
	
	@Override
	public String computeKey(MavenArtifact object) {
		return keyProvider().newKey(object);
	}
	
	@Override
	public void writeToNode(String key, Preferences node) {
		assert key != null;
		assert node != null;
		assert getValue().getCoordinates() != null;

		MavenArtifact mavenArtifact = getValue();
		
		String[] items = mavenArtifact.getCoordinates().split(":");
		node.put(GROUPID, items[0]);
		node.put(ARTIFACTID, items[1]);
		node.put(VERSION, items[2]);
		node.put(DEPENDENCIES, mavenArtifact.getDependencies());
		node.put(FILTER, mavenArtifact.getFilter());
		node.put(PATH, mavenArtifact.getPath());
	}
	
	@Override
	public void readFromNode(String key, Preferences node) {
		assert key != null;
		assert node != null;
				
		MavenArtifact mavenArtifact = getValue();
		
		mavenArtifact.setCoordinates(key);
        mavenArtifact.setDependencies(node.get(DEPENDENCIES, null));
        mavenArtifact.setFilter(node.get(FILTER, null));
        mavenArtifact.setPath(node.get(PATH, null));
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

