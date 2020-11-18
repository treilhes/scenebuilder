package com.oracle.javafx.scenebuilder.api.action.editor;

import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;

public class KeyboardModifier {

	public static Modifier control() {
		if (EditorPlatform.IS_MAC) {
	        return KeyCombination.META_DOWN;
	    } else {
	        // Should cover Windows, Solaris, Linux
	    	return KeyCombination.CONTROL_DOWN;
	    }
	}
	
}
