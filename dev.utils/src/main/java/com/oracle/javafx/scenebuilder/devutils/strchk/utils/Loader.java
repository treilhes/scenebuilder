package com.oracle.javafx.scenebuilder.devutils.strchk.utils;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;

public class Loader {
    
    public static <T> T load(Object controllerInstance, String fxml) {
        return load(controllerInstance, controllerInstance.getClass(), fxml);
    }
    
    public static <T> T load(Object controllerInstance, Class<?> resourceLoadingClass, String fxml) {
        final URL fxmlURL = resourceLoadingClass.getResource(fxml);
        final FXMLLoader loader = new FXMLLoader();

        loader.setController(controllerInstance);
        loader.setLocation(fxmlURL);
        
        // setting ClassLoader for OSGi environments
        loader.setClassLoader(resourceLoadingClass.getClassLoader());
        
        try {
            return loader.load();
        } catch (RuntimeException | IOException x) {
            System.out.println("loader.getClassLoader()=" + resourceLoadingClass.getName()); //NOCHECK
            System.out.println("loader.getController()=" + loader.getController()); //NOCHECK
            System.out.println("loader.getLocation()=" + loader.getLocation()); //NOCHECK
            throw new RuntimeException("Failed to load " + fxmlURL.getFile(), x); //NOCHECK
        }
    }
}
