package com.oracle.javafx.scenebuilder.api;

public interface Decoration<T> {
	/**
     * @treatAsPrivate
     */
    public enum State {
        CLEAN,
        NEEDS_RECONCILE,
        NEEDS_REPLACE
    }

	State getState();

	void reconcile();
}
