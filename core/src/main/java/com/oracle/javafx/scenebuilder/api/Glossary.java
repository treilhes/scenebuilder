package com.oracle.javafx.scenebuilder.api;

import java.net.URL;
import java.util.List;

public interface Glossary {

	List<String> queryFxIds(URL fxmlLocation, String controllerClass, Class<?> targetType);

	List<String> queryControllerClasses(URL location);

	List<String> queryEventHandlers(URL location, String controllerClass);

}
