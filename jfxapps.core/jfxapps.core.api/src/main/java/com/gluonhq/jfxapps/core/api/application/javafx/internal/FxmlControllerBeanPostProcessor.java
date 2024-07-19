/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.application.javafx.internal;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.gluonhq.jfxapps.boot.context.annotation.DeportedSingleton;
import com.gluonhq.jfxapps.core.api.di.FxmlController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * The Class FxmlControllerBeanPostProcessor.
 */
@DeportedSingleton
public class FxmlControllerBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FxmlControllerBeanPostProcessor.class);
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
                logger.error("Failed to load {} with {}", loader.getLocation(), loader.getController(), x); // NOI18N
                throw new RuntimeException(
                        String.format("Failed to load %s with %s",
                                loader.getLocation(), loader.getController()), x); // NOI18N
            }
        }

        return bean;
    }
}