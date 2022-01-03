package com.oracle.javafx.scenebuilder.core.metadata.fx;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDefine;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;

@Component
public class DefineMetadata extends ComponentClassMetadata<FXOMDefine> {
    protected DefineMetadata() {
        super(FXOMDefine.class, null);

        getQualifiers().put("default",
                new Qualifier(
                        getClass().getResource("Define.fxml"),
                        "define",
                        "",
                        getClass().getResource("Define.png"),
                        getClass().getResource("Define@2x.png"),
                        "Fx",
                        null
                        ));
    }

    @Override
    public String toString() {
        return "fx:define";
    }


}