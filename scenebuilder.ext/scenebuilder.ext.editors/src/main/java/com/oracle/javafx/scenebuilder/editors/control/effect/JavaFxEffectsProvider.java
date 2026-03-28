package com.oracle.javafx.scenebuilder.editors.control.effect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.control.effect.EffectProvider;

import javafx.scene.effect.Effect;

@Component
public class JavaFxEffectsProvider implements EffectProvider {
    
    private static List<Class<? extends Effect>> effectClasses;

    @Override
    public List<Class<? extends Effect>> effects() {
        if (effectClasses == null) {
            effectClasses = new ArrayList<>();
            effectClasses.add(javafx.scene.effect.Blend.class);
            effectClasses.add(javafx.scene.effect.Bloom.class);
            effectClasses.add(javafx.scene.effect.BoxBlur.class);
            effectClasses.add(javafx.scene.effect.ColorAdjust.class);
            effectClasses.add(javafx.scene.effect.ColorInput.class);
            effectClasses.add(javafx.scene.effect.DisplacementMap.class);
            effectClasses.add(javafx.scene.effect.DropShadow.class);
            effectClasses.add(javafx.scene.effect.GaussianBlur.class);
            effectClasses.add(javafx.scene.effect.Glow.class);
            effectClasses.add(javafx.scene.effect.ImageInput.class);
            effectClasses.add(javafx.scene.effect.InnerShadow.class);
            effectClasses.add(javafx.scene.effect.Lighting.class);
            effectClasses.add(javafx.scene.effect.MotionBlur.class);
            effectClasses.add(javafx.scene.effect.PerspectiveTransform.class);
            effectClasses.add(javafx.scene.effect.Reflection.class);
            effectClasses.add(javafx.scene.effect.SepiaTone.class);
            effectClasses.add(javafx.scene.effect.Shadow.class);
            effectClasses = Collections.unmodifiableList(effectClasses);
        }

        return effectClasses;
    }

}
