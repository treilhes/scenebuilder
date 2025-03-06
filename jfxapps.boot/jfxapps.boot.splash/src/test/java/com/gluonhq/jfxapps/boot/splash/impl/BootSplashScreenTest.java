package com.gluonhq.jfxapps.boot.splash.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BootSplashScreenTest {

	@Test
	void test() {

		var splash = BootSplashScreen.defaultSplashScreen();

		final int stepsNumber = 4;

		var steps = splash.asSubSteps(stepsNumber);

		for (int i = 0; i < stepsNumber; i++) {
			var step = steps.get(i);
			step.notifyStart();
			step.notifyProgress(0.2f);
			step.notifyProgress(0.4f);
			step.notifyProgress(0.6f);
			step.notifyProgress(0.8f);
			step.notifyFinish();
		}

		assertTrue(splash.isDone(), "loading is not done");

	}

}
