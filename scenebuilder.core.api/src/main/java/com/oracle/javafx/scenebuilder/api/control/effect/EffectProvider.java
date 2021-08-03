package com.oracle.javafx.scenebuilder.api.control.effect;

import java.util.List;

import javafx.scene.effect.Effect;

public interface EffectProvider {
    List<Class<? extends Effect>> effects();
}
