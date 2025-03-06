package com.gluonhq.jfxapps.boot.api.splash;

import java.util.List;

import com.gluonhq.jfxapps.boot.api.utils.ProgressListener;

public interface SplashScreen {

	List<ProgressListener> asSubSteps(int i);

	boolean isDone();

}
