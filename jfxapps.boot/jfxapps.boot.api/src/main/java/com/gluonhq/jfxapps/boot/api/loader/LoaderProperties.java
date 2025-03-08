package com.gluonhq.jfxapps.boot.api.loader;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jfxapps.loader")
public class LoaderProperties {
	private LoadType defaultLoadType = LoadType.LastSuccessfull;

	public LoadType getDefaultLoadType() {
		return defaultLoadType;
	}

	public void setDefaultLoadType(LoadType defaultLoadType) {
		this.defaultLoadType = defaultLoadType;
	}
}
