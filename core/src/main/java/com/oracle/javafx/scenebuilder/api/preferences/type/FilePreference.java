package com.oracle.javafx.scenebuilder.api.preferences.type;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public class FilePreference extends AbstractPreference<File> {

	public FilePreference(PreferencesContext preferencesContext, String name, File defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), false);
	}

	@Override
	public void write() {
		try {
			getNode().put(getName(), getValue().getCanonicalPath());
		} catch (IOException e) {
			Logger.getLogger(FilePreference.class.getName()).log(Level.SEVERE, "Unable to save file preference " + getName(), e);
		}
	}

	@Override
	public void read() {
		assert getName() != null;

		try {
			String filepath = getNode().get(getName(), getDefault().getCanonicalPath());
			File file = new File(filepath);
			if (file.exists()) {
				setValue(file);
			} else {
				setValue(getDefault());
			}

		} catch (IOException e) {
			Logger.getLogger(FilePreference.class.getName()).log(Level.SEVERE, "Unable to load file preference " + getName(), e);
		}

	}

	@Override
	public boolean isValid() {
		return getValue() != null && getValue().exists();
	}

}
