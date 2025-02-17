package com.gluonhq.jfxapps.boot.splash.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class BootSplashScreenTest {

	@Test
	void test() {
		URL imageUrl = BootSplashScreenTest.class.getResource("splash_test1.png");
		URL imageUrl2 = BootSplashScreenTest.class.getResource("splash_test2.png");
		URL imageUrl3 = BootSplashScreenTest.class.getResource("splash_test3.png");


		BootLoadingProgress root = BootLoadingProgress.getInstance(UUID.randomUUID(), imageUrl);

		BootSplashScreen.getInstance(root);

		root.start();
		root.notifyProgress(0.2f);
		root.notifyProgress(0.4f);
		root.notifyProgress(0.6f);
		root.notifyProgress(0.8f);
		root.end();

		assertTrue(root.isDone(), "loading is not done");

	}

}
