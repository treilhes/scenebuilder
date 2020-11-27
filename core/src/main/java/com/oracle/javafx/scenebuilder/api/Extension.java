package com.oracle.javafx.scenebuilder.api;

import java.util.Map;

import javafx.scene.effect.Effect;
import javafx.scene.input.KeyCombination;

public interface Extension {
	Map<Effect, EffectEditor> effects();
	Map<KeyCombination, Action> accelerarors();
	//List<ViewManager> views;
	//ExtensionPreference preferences;
	
	public class EffectEditor{
		
	}
	public class Action{
		
	}
}
