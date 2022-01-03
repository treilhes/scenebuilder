package com.oracle.javafx.scenebuilder.core.metadata.fx;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMScript;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;

@Component
public class ScriptMetadata extends ComponentClassMetadata<FXOMScript> {
    protected ScriptMetadata() {
        super(FXOMScript.class, null);

        getQualifiers().put("default",
                new Qualifier(
                        getClass().getResource("Script.fxml"),
                        "script",
                        "",
                        getClass().getResource("Script.png"),
                        getClass().getResource("Script@2x.png"),
                        "Fx",
                        null
                        ));
    }
}