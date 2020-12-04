package com.oracle.javafx.scenebuilder.fs.preference.global;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.FilePreference;

@Component
public class InitialDirectoryPreference extends FilePreference implements ManagedGlobalPreference {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "initialDirectory"; //NOI18N
    public static final File PREFERENCE_DEFAULT_VALUE = FileSystem.USER_HOME;

	public InitialDirectoryPreference(
			@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}
}
