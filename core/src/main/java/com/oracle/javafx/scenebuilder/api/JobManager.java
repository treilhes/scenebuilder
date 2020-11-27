package com.oracle.javafx.scenebuilder.api;

import java.util.List;

import com.oracle.javafx.scenebuilder.api.editor.job.Job;

import javafx.beans.property.ReadOnlyIntegerProperty;

public interface JobManager {

	List<Job> getUndoStack();

	void push(Job job);

	Job getCurrentJob();

	ReadOnlyIntegerProperty revisionProperty();

	boolean canRedo();

	String getRedoDescription();

	void redo();

	void clear();

	boolean canUndo();

	String getUndoDescription();

	void undo();

	List<Job> getRedoStack();

}
