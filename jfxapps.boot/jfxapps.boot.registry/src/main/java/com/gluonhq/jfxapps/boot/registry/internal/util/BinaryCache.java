package com.gluonhq.jfxapps.boot.registry.internal.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;

@Component
public class BinaryCache {
	private static final Logger logger = LoggerFactory.getLogger(BinaryCache.class);
	private static final String CACHE_FOLDER_NAME = "JfxAppsCache";
	private JfxAppsPlatform platform;

	public BinaryCache(JfxAppsPlatform platform) {
		this.platform = platform;
		ensureCacheFolderExist();
	}

	public void add(UUID id, String key, InputStream content) {
		try {
			Files.copy(content, toPath(id, key), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error("Error copying binary to cache", e);
		} finally {
			try {
				content.close();
			} catch (IOException e) {
				logger.error("Error closing input stream", e);
			}
		}
	}

	public URL get(UUID id, String key) {
		try {
			var path = toPath(id, key);
			if (!Files.exists(path)) {
				return null;
			}
			return toPath(id, key).toUri().toURL();
		} catch (MalformedURLException e) {
			logger.error("Error creating URL from cache", e);
			return null;
		}
	}

	public InputStream getInputStream(UUID id, String key) {
		try {
			return Files.newInputStream(toPath(id, key));
		} catch (IOException e) {
			logger.error("Error creating input stream from cache", e);
			return null;
		}
	}

	private Path toPath(UUID id, String key) {
		return cacheFolder().toPath().resolve(id + "-" + key);
	}

	private File cacheFolder() {
		return new File(platform.rootFile(), CACHE_FOLDER_NAME);
	}

	private void ensureCacheFolderExist() {
		var cache = cacheFolder();
		if (!cache.exists()) {
			cache.mkdirs();
		}
	}
}
