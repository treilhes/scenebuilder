package com.oracle.javafx.scenebuilder.app.preferences;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DocumentPreferencesNode;
import com.oracle.javafx.scenebuilder.app.preferences.document.PathPreference;

@Component
public class DocumentPreferencesNodeImpl implements DocumentPreferencesNode {

	// PREFERENCES NODE NAME
    static final String NODE_NAME = "DOCUMENTS"; //NOI18N
    
	@Override
	public Preferences getNode() {
		return Preferences.userNodeForPackage(getClass())
				.node(RootPreferencesNodeImpl.SB_RELEASE_NODE).node(NODE_NAME);
	}
	
	@Override
	public void cleanupCorruptedNodes() {
		// Cleanup document preferences at start time : 
		// Remove document preferences node if needed
        try {
            final String[] childrenNames = getNode().childrenNames();
            // Check among the document root chidlren if there is a child
            // which path matches the specified one
            for (String child : childrenNames) {
                final Preferences documentPreferences = getNode().node(child);
                final String nodePath = documentPreferences.get(PathPreference.PREFERENCE_KEY, null);
                // Each document node defines a path
                // If path is null or empty, this means preferences DB has been corrupted
                if (nodePath == null || nodePath.isEmpty()) {
                    documentPreferences.removeNode();
                }
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	@Override
	public void clearAllDocumentNodes() {
        try {
            final String[] childrenNames = getNode().childrenNames();
            for (String child : childrenNames) {
                getNode().node(child).removeNode();
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesController.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

}
