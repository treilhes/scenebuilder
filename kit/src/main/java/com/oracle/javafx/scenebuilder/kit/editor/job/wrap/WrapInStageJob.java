package com.oracle.javafx.scenebuilder.kit.editor.job.wrap;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;

import javafx.stage.Stage;

/**
 * Job used to wrap selection in a Stage.
 */
public class WrapInStageJob extends AbstractWrapInWindowJob {

    public WrapInStageJob(ApplicationContext context, Editor editor) {
        super(context, editor);
        newContainerClass = Stage.class;
    }

}
