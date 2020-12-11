package com.oracle.javafx.scenebuilder.api.util;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javafx.scene.Parent;

/**
 * This interface must be used in conjunction with Spring Dependency injection framework. When implementing this interface you're delegating
 * the fxml loading task to the Spring context using {@link #getFxmlURL()} and {@link #getResources()} as parameters.
 * You will then be notified of the result with calls to {@link #setRoot(Parent)} and {@link #controllerDidLoadFxml()}
 * May be used with  {@link Component} annotation or any spring annotation making the implementing class a spring bean
 * @author ptreilhes
 *
 */
public interface FxmlController {
	/**
	 * Must return a valid URL to an Fxml file. The instance implementing this interface will be used as a javafx controller
	 * for this fxml file. Which means any fields or methods annotated with @FXML will be binded using this fxml
	 * @return the url to the fxml that must be binded to this controller
	 */
    @NonNull
	URL getFxmlURL();

	/**
	 * May return a valid ResourceBundle containing all the necessary keys to translate every i18n expression
	 * ("%xxx.xxx") contained in the fxml file provided by {@link #getFxmlURL()}
	 * @return
	 */
	ResourceBundle getResources();


	/**
	 * This method is automatically called by {@link com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.FxmlControllerBeanPostProcessor#postProcessAfterInitialization(Object, String)}
	 * after a successful call to {@link javafx.fxml.FXMLLoader#load()} using {@link #getFxmlURL()} and {@link #getResources()} as parameters. The return value of {@link javafx.fxml.FXMLLoader#load()}
	 * will be used as parameter for this function
	 * @param root the {@link javafx.scene.Parent} root of the provided fxml file
	 */
	void setRoot(Parent root);


	/**
     * This method is automatically called by {@link com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.FxmlControllerBeanPostProcessor#postProcessAfterInitialization(Object, String)}
     * after a successful call to {@link javafx.fxml.FXMLLoader#load()} using {@link #getFxmlURL()} and {@link #getResources()} as parameters and a successful call to {@link #setRoot(Parent)}
     * It notify the controller that all loading activities have ended successfully
     */
	void controllerDidLoadFxml();
}
