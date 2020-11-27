package com.oracle.javafx.scenebuilder.api.editor.job;

public abstract class AbstractJobExtension<T extends Job> implements JobExtension<T> {

	private T extendedJob;

	public AbstractJobExtension() {
	}

	@Override
	public void setExtendedJob(T job) {
		extendedJob = job;
	}

	protected T getExtendedJob() {
		return extendedJob;
	}

	@Override
	public boolean isExecutable() { return extendedJob.isExecutable(); }

	@Override
	public void preExecute() {}

	@Override
	public void postExecute() {}

	@Override
	public void preUndo() {}

	@Override
	public void postUndo() {}

	@Override
	public void preRedo() {}

	@Override
	public void postRedo() {}


}
