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
package com.oracle.javafx.scenebuilder.library.preferences.global;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultProvider;
import com.oracle.javafx.scenebuilder.api.preferences.KeyProvider;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.ListItemObjectPreference;
import com.oracle.javafx.scenebuilder.library.maven.repository.Repository;

/**
 * Defines repository preferences global to the application.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class MavenRepositoryPreferences extends ListItemObjectPreference<Repository> {
    
    public final static String REPO_ID  = "ID";
    public final static String REPO_TYPE  = "type";
    public final static String REPO_URL  = "URL";
    public final static String REPO_USER = "User";
    public final static String REPO_PASS = "Password";   
    
    public MavenRepositoryPreferences(PreferencesContext preferencesContext, String name, Repository defaultValue) {
		super(preferencesContext, name, defaultValue);
	}
    
    public static KeyProvider<Repository> keyProvider() {
		return (r) -> r.getId();
	}
	
	public static DefaultProvider<MavenRepositoryPreferences> defaultProvider() {
		return (pc, name) -> new MavenRepositoryPreferences(pc, name, new Repository());
	}
	
	public static boolean isValid(Repository object) {
		if (object == null) {
			Logger.getLogger(MavenRepositoryPreferences.class.getName()).log(Level.SEVERE, "Repository can't be null");
			return false;
		}
		if (object.getId() == null || object.getId().isEmpty()) {
			Logger.getLogger(MavenRepositoryPreferences.class.getName()).log(Level.SEVERE, "Repository id can't be null or empty");
			return false;
		}
		if (object.getType() == null || object.getURL() == null) {
			Logger.getLogger(MavenRepositoryPreferences.class.getName()).log(Level.SEVERE, "Repository fields type and url can't be null");
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isValid() {
		return MavenRepositoryPreferences.isValid(getValue());
	}
	
	@Override
	public String computeKey(Repository object) {
		return keyProvider().newKey(object);
	}
	
	@Override
	public void writeToNode(String key, Preferences node) {
		assert key != null;
		assert node != null;
		assert getValue().getId() != null;

		Repository repository = getValue();
		
		node.put(REPO_ID, repository.getId());
		node.put(REPO_TYPE, repository.getType());
		node.put(REPO_URL, repository.getURL());
        if (repository.getUser() != null) {
        	node.put(REPO_USER, repository.getUser());
        }
        if (repository.getPassword() != null) {
        	node.put(REPO_PASS, repository.getPassword());
        }
	}
	
	@Override
	public void readFromNode(String key, Preferences node) {
		assert key != null;
		assert node != null;
				
		Repository repository = getValue();
		
		repository.setId(node.get(REPO_ID, null));
        repository.setType(node.get(REPO_TYPE, null));
        repository.setURL(node.get(REPO_URL, null));
        repository.setUser(node.get(REPO_USER, null));
        repository.setPassword(node.get(REPO_PASS, null));
		
	}
	
}
