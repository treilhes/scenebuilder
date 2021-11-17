package com.oracle.javafx.scenebuilder.metadata.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Deprecated
public class ComponentImage {

	private URL source;
	private URL sourceX2;

	public static ComponentImage from(URL source) {
		return new ComponentImage(source);
	}

	private ComponentImage(URL source) {
		this.source = source;
		init();
	}

	public File getSourceFolder() {
		try {
			File src = new File(source.toURI());
			return src.getParentFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public File target(File root, Class<?> component, String qualifier) {
		return new File(targetFolder(root, component), component.getSimpleName() + "_" + qualifier + "." + getExtension());
	}

	public File targetX2(File root, Class<?> component, String qualifier) {
		return new File(targetFolder(root, component), component.getSimpleName() + "_" + qualifier + "@2x." + getExtension());
	}

	private File targetFolder(File root, Class<?> component) {
		return new File(root, component.getPackageName().replace('.', '/') + "/" + component.getSimpleName().toLowerCase());
	}

	public boolean exists() {
		return existsFile(source);
	}

	public boolean existsX2() {
		return existsFile(sourceX2);
	}

	public InputStream stream() {
		try {
			return source.openStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public InputStream streamX2() {
		try {
			return sourceX2.openStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String getExtension() {
		String fileName = source.getFile();
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		return extension;
	}


	private void init() {
		try {
			String src = source.toExternalForm();
			int lastDotIndex = src.lastIndexOf(".");
			sourceX2 = new URL(src.substring(0, lastDotIndex) + "@2x" + src.substring(lastDotIndex));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean existsFile(URL url) {
		try {
			File src = new File(url.toURI());
			return src.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
