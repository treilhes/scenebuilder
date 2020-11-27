package com.oracle.javafx.scenebuilder.api;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

public interface DropTarget {

	Job makeDropJob(ApplicationContext context, DragSource dragSource, Editor editorController);

	FXOMObject getTargetObject();

	boolean acceptDragSource(DragSource dragSource);

	boolean isSelectRequiredAfterDrop();

}
