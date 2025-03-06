package com.gluonhq.jfxapps.boot.api.splash;

import java.util.UUID;

public interface SplashScreenProvider {
	SplashScreen getSplashScreen(UUID id);
}
