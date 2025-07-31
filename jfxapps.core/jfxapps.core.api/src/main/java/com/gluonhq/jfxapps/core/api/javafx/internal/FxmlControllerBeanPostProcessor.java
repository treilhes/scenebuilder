/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.jfxapps.core.api.javafx.internal;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.lang.NonNull;

import com.gluonhq.jfxapps.boot.api.context.annotation.DeportedSingleton;
import com.gluonhq.jfxapps.core.api.javafx.DisableAutomaticFxmlLoading;
import com.gluonhq.jfxapps.core.api.javafx.FxmlController;
import com.gluonhq.jfxapps.core.api.javafx.LoadInFxThread;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * The Class FxmlControllerBeanPostProcessor.
 */
@DeportedSingleton
public class FxmlControllerBeanPostProcessor implements PriorityOrdered, BeanPostProcessor, BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(FxmlControllerBeanPostProcessor.class);

    // The order of the BeanPostProcessor is set to HIGHEST_PRECEDENCE + 1 to ensure that
    // the FxmlController is loaded before any BeanPostProcessor creating a proxy
    private static final int order = Ordered.HIGHEST_PRECEDENCE + 1;
    /**
     * Instantiates a new fxml controller bean post processor.
     */

    private ConfigurableListableBeanFactory beanFactory;

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

        if (bean instanceof FxmlController controller) {

            boolean disabled = false;
            boolean loadOnFxThread = false;
            // Get bean definition to access metadata
            if (beanFactory != null && beanFactory.containsBeanDefinition(beanName)) {
                var beanDefinition = beanFactory.getBeanDefinition(beanName);
                var disableAnnotation = findAnnotation(beanDefinition, DisableAutomaticFxmlLoading.class);
                if (disableAnnotation != null) {
                    disabled = true;
                } else {
                    var loadInFxThreadAnnotation = findAnnotation(beanDefinition, LoadInFxThread.class);
                    if (loadInFxThreadAnnotation != null) {
                        loadOnFxThread = true;
                    }
                }
            }

            if (disabled) { // If the controller is annotated with @DisableAutomaticFxmlLoading, we do not load the FXML
                return bean;
            }

            if (controller.getFxmlURL() == null && !controller.isFxmlFromStream()) {
                logger.error("""
                        FxmlController {} does not have a valid FXML URL set and is not using a stream.
                        If it is intended please annotate it with @DisableAutomaticFxmlLoading to prevent this error
                        """,
                        beanName);
                return bean;
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setController(controller);
            loader.setLocation(controller.getFxmlURL());
            loader.setResources(controller.getResources());
            loader.setClassLoader(bean.getClass().getClassLoader());

            try {

                final Parent parent;
                if (loadOnFxThread && !Platform.isFxApplicationThread()) {
                    var future = new FutureTask<>(() -> handleLoad(controller, loader));
                    Platform.runLater(future);
                    parent = future.get();
                } else {
                    parent = handleLoad(controller, loader);
                }
                controller.setRoot(parent);
                controller.controllerDidLoadFxml();
            } catch (Exception x) {
                logger.error("Failed to load {} with {}", loader.getLocation(), loader.getController(), x); // NOI18N
                throw new RuntimeException(
                        String.format("Failed to load %s with %s",
                                loader.getLocation(), loader.getController()), x); // NOI18N
            }
        }

        return bean;
    }

    private Parent handleLoad(FxmlController controller, FXMLLoader loader) throws IOException {
        if (controller.isFxmlFromStream()) {
            try (var inputStream = controller.getFxmlStream()) {
                return (Parent) loader.load(inputStream);
            } catch (Exception e) {
                logger.error("Failed to load FXML from stream for controller: {}", controller.getClass().getName(), e);
            }

        }
        return (Parent) loader.load();
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableListableBeanFactory clbf) {
            this.beanFactory = clbf;
        }
    }

    private static <A extends Annotation> A findAnnotation(BeanDefinition beanDefinition, Class<A> annotationType) {
        if (beanDefinition instanceof AnnotatedGenericBeanDefinition annotatedBeanDefinition) {
            AnnotationMetadata metadata = annotatedBeanDefinition.getMetadata();
            if (metadata.isAnnotated(annotationType.getName())) {
                return AnnotationUtils.synthesizeAnnotation(metadata.getAnnotationAttributes(annotationType.getName()), annotationType, null);
            }
        }
        if (beanDefinition instanceof RootBeanDefinition rootBeanDefinition
                && rootBeanDefinition.getSource() instanceof StandardMethodMetadata metadata) {
            return metadata.getIntrospectedMethod().getAnnotation(annotationType);
        }
        return null;
    }
}