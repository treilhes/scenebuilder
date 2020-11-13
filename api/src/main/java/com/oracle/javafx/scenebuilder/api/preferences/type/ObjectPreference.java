package com.oracle.javafx.scenebuilder.api.preferences.type;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public abstract class ObjectPreference<T> extends AbstractPreference<T> {
	
	Preferences valueNode;
	
	public ObjectPreference(PreferencesContext preferencesContext, String name, T defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), true);
	}

	public abstract String computeKey(T object);
	
	public abstract void writeToNode(String key, Preferences node);
	
	public abstract void readFromNode(String key, Preferences node);
	
	@Override
	public void writeToJavaPreferences() {
		assert getNode() != null;
        
        T value = getValue();
        String key = computeKey(value);
        
        if (isValid(value)) {
//	        if (valueNode == null) {
//	            try {
//	                assert getNode().nodeExists(key) == false;
//	                // Create a new node under the root node
//	                valueNode = getNode().node(key);
//	            } catch(BackingStoreException ex) {
//	                Logger.getLogger(ObjectPreference.class.getName()).log(Level.SEVERE, null, ex);
//	                return;
//	            }
//	        }
//	        assert valueNode != null;
//        
//        	writeToNode(key, valueNode);
        	writeToNode(key, getNode());
        } else {
        	try {
				getNode().removeNode();
			} catch (BackingStoreException e) {
				Logger.getLogger(ObjectPreference.class.getName()).log(Level.SEVERE, null, e);
			}
        	Logger.getLogger(ObjectPreference.class.getName()).log(Level.SEVERE, "Invalid object, data can't be saved");
        }
        
	}

	@Override
	public void readFromJavaPreferences() {
//		assert valueNode == null;
		String key = getName();
//        // Check if there are some preferences for this artifact
//        try {
//            final String[] childrenNames = getNode().childrenNames();
//            for (String child : childrenNames) {
//                if (child.equals(key)) {
//                	valueNode = getNode().node(child);
//                }
//            }
//        } catch (BackingStoreException ex) {
//            Logger.getLogger(ObjectPreference.class.getName()).log(Level.SEVERE, null, ex);
//        }
//            
//        if (valueNode == null) {
//            return;
//        }
//        
//        readFromNode(key, valueNode);
		readFromNode(key, getNode());
	}
	
}
