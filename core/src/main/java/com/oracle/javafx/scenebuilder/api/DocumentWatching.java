package com.oracle.javafx.scenebuilder.api;

import java.io.File;

public interface DocumentWatching {

	void watch(File file, WatchingCallback callback);
	void unwatch(File file);

	public interface WatchingCallback{
		void deleted();
		void modified();
	}
}
