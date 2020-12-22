/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

