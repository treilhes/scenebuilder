package com.oracle.javafx.scenebuilder.core.metadata.fx;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic.Type;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;

@Component
public class IntrinsicElementMetadata extends ComponentClassMetadata<FXOMIntrinsic> {
    protected IntrinsicElementMetadata() {
        super(FXOMIntrinsic.class, null);

        // TODO associated image are only valid for Type.FX_INCLUDE
        // other types does not have associated images
        // FX_REFERENCE,
        // FX_COPY,
        // UNDEFINED

        getQualifiers().put("include",
                new Qualifier(
                        getClass().getResource("Include.fxml"),
                        "include",
                        "",
                        getClass().getResource("Include.png"),
                        getClass().getResource("Include@2x.png"),
                        "Fx",
                        (FXOMIntrinsic o) -> o.getType() == Type.FX_INCLUDE
                        ));

        getQualifiers().put("copy",
                new Qualifier(
                        getClass().getResource("Copy.fxml"),
                        "copy",
                        "",
                        getClass().getResource("Copy.png"),
                        getClass().getResource("Copy@2x.png"),
                        "Fx",
                        (FXOMIntrinsic o) -> o.getType() == Type.FX_COPY
                        ));

        getQualifiers().put("reference",
                new Qualifier(
                        getClass().getResource("Reference.fxml"),
                        "reference",
                        "",
                        getClass().getResource("Reference.png"),
                        getClass().getResource("Reference@2x.png"),
                        "Fx",
                        (FXOMIntrinsic o) -> o.getType() == Type.FX_REFERENCE
                        ));
    }
}