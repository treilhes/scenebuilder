package com.oracle.javafx.scenebuilder.core.util;

import java.io.IOException;
import java.net.URL;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;

import javafx.fxml.FXMLLoader;

public class FXMLUtils {

	private FXMLUtils() {}

	public static <T> T load(Object controllerInstance, String fxml) {
		final URL fxmlURL = controllerInstance.getClass().getResource(fxml); //NOI18N
        final FXMLLoader loader = new FXMLLoader();

        loader.setController(controllerInstance);
        loader.setLocation(fxmlURL);
        loader.setResources(I18N.getBundle());
        try {
            return loader.load();
        } catch (RuntimeException | IOException x) {
            System.out.println("loader.getController()=" + loader.getController()); //NOI18N
            System.out.println("loader.getLocation()=" + loader.getLocation()); //NOI18N
            throw new RuntimeException("Failed to load " + fxmlURL.getFile(), x); //NOI18N
        }
	}
}
