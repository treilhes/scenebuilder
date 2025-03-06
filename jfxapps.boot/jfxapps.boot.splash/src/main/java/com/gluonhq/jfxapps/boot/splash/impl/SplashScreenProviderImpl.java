package com.gluonhq.jfxapps.boot.splash.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.boot.api.splash.SplashScreen;
import com.gluonhq.jfxapps.boot.api.splash.SplashScreenProvider;

@Component
public class SplashScreenProviderImpl implements SplashScreenProvider {

	private final RegistryManager registryManager;

	public SplashScreenProviderImpl(RegistryManager registryManager) {
		super();
		this.registryManager = registryManager;
	}

	@Override
	public SplashScreen getSplashScreen(UUID id) {

		var info = registryManager.applicationInfo(id);

		if (info == null) {
			return null;
		}

		var url = info.getSplash();

		if (url == null) {
			return null;
		}

		var boot = BootLoadingProgress.getInstance(id, url);
		return BootSplashScreen.applicationSplashScreen(boot);
	}

}
