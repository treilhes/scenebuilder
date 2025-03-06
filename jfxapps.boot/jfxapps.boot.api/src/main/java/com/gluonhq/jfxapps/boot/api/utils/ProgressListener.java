package com.gluonhq.jfxapps.boot.api.utils;

/**
 * A generic listener interface for receiving progress events.
 */
public interface ProgressListener {

	/**
	 * Notify the start of the task
	 */
	void notifyStart();
    /**
     * Notify the progress of the task
     * @param progress the progress of the task (0.0 to 1.0)
     */
    void notifyProgress(float progress);
    /**
     * Notify a delta value to the progress of the task
     * @param the delta progress of the task (0.0 to 1.0)
     */
    void notifyProgressDelta(float delta);
    /**
     * Notify the finish of the task
     */
    void notifyFinish();
}
