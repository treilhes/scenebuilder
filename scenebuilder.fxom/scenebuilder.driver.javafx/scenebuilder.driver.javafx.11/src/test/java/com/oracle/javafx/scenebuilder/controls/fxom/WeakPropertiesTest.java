package com.oracle.javafx.scenebuilder.controls.fxom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import com.treilhes.jfxplace.fxom.model.FXOMNodes;
import com.treilhes.jfxplace.fxom.model.ext.WeakProperty;

class WeakPropertiesTest {

    //@Test
    void weakPropertiesMustBeLoaded() {
        Set<WeakProperty> weaks = FXOMNodes.getWeakProperties();
        assertEquals(true, weaks.stream().anyMatch(w -> w.getPropertyName().equals("labelFor")));
        assertEquals(true, weaks.stream().anyMatch(w -> w.getPropertyName().equals("expandedPane")));
        assertEquals(true, weaks.stream().anyMatch(w -> w.getPropertyName().equals("clip")));
    }

}
