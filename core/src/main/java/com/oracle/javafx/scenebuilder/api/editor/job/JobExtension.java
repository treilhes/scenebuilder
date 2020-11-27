package com.oracle.javafx.scenebuilder.api.editor.job;

public interface JobExtension<T extends Job> {

	void setExtendedJob(T job);

	public boolean isExecutable();

	public void preExecute();

	public void postExecute();

	public void preUndo();

	public void postUndo();

	public void preRedo();

	public void postRedo();

}
