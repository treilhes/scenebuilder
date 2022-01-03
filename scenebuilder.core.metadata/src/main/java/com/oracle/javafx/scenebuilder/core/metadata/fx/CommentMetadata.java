package com.oracle.javafx.scenebuilder.core.metadata.fx;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMComment;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;

@Component
public class CommentMetadata extends ComponentClassMetadata<FXOMComment> {
    protected CommentMetadata() {
        super(FXOMComment.class, null);

        getQualifiers().put("default",
                new Qualifier(
                        getClass().getResource("Comment.fxml"),
                        "comment",
                        "",
                        getClass().getResource("Comment.png"),
                        getClass().getResource("Comment@2x.png"),
                        "Fx",
                        null
                        ));
    }
}