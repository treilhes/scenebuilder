package com.oracle.javafx.scenebuilder.core.di;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * The Class FxmlControllerBeanPostProcessor.
 */
public class FxmlControllerBeanPostProcessor implements BeanPostProcessor {

    /**
     * Instantiates a new fxml controller bean post processor.
     */
    public FxmlControllerBeanPostProcessor() {
        super();
    }

    /**
     * This implementation loads the FXML file using the URL and ResourceBundle
     * passed by {@link FxmlController} if the bean is an instance of
     * {@link FxmlController}. This method can be invoked outside of the JavaFX
     * thread
     *
     * @param bean     the bean
     * @param beanName the bean name
     * @return the bean binded to the fxml
     * @throws BeansException the beans exception
     * @throws RuntimeException exception thrown when the fxml file failed to load
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        bean = BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

        if (FxmlController.class.isAssignableFrom(bean.getClass())) {
            FxmlController controller = (FxmlController) bean;
            FXMLLoader loader = new FXMLLoader();
            loader.setController(controller);
            loader.setLocation(controller.getFxmlURL());
            loader.setResources(controller.getResources());
            loader.setClassLoader(bean.getClass().getClassLoader());
            
            try {
                controller.setRoot((Parent) loader.load());
                controller.controllerDidLoadFxml();
            } catch (RuntimeException | IOException x) {
                throw new RuntimeException(
                        String.format("Failed to load %s with %s",
                                loader.getLocation(), loader.getController()), x); // NOI18N
            }
        }

        return bean;
    }
}