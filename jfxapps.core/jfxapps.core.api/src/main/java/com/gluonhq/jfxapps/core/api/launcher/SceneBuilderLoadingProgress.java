/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class SceneBuilderLoadingProgress {

    private static SceneBuilderLoadingProgress instance;

    public static SceneBuilderLoadingProgress get() {
        if (instance == null) {
            instance = new SceneBuilderLoadingProgress();
        }
        return instance;
    }

    private static final Logger log = LoggerFactory.getLogger(SceneBuilderLoadingProgress.class);

    private static final Float START_PROGRESS = -1f;
    private static final Float INIT_CONTEXT_PROGRESS = -1f;
    private static final Float INIT_UI_PROGRESS = 0.95f;
    private static final Float DONE_PROGRESS = 1.0f;

    private final ProgressListener progressListener = new ProgressListener();
    private TextChange onTextChange;
    private ProgressChange onProgressChange;
    private LoadingDone onLoadingDone;

    private float currentProgress;

    private void step(Float value, String text) {
        if (onProgressChange != null && value != null) {
            currentProgress = value;
            onProgressChange.onProgressChange(value);
        }
        if (onTextChange != null && text != null) {
            onTextChange.onTextChange(text);
        }
    }

    public void start() {
        step(START_PROGRESS, "Start loading");
    }

    public void initContext() {
        step(INIT_CONTEXT_PROGRESS, "Scanning classpath");
    }
    public void initUI() {
        step(INIT_UI_PROGRESS, "Init UI");
    }

    public void end() {
        step(DONE_PROGRESS, "");
        if (onLoadingDone != null) {
            onLoadingDone.loadingDone();
        }
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setOnTextChange(TextChange onTextChange) {
        this.onTextChange = onTextChange;
    }

    public void setOnProgressChange(ProgressChange onProgressChange) {
        this.onProgressChange = onProgressChange;
    }

    public void setOnLoadingDone(LoadingDone onLoadingDone) {
        this.onLoadingDone = onLoadingDone;
    }

    @FunctionalInterface
    public interface TextChange{
        void onTextChange(String text);
    }

    @FunctionalInterface
    public interface ProgressChange{
        void onProgressChange(float progress);
    }

    @FunctionalInterface
    public interface LoadingDone{
        void loadingDone();
    }

    private class ProgressListener implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>, BeanFactoryPostProcessor {


        private DefaultListableBeanFactory beanFactory;
        //private int step;
        private float singletonDefinitionCount;
        private float createdBeanCount;


        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            createdBeanCount++;

            if (currentProgress < INIT_UI_PROGRESS &&  createdBeanCount < singletonDefinitionCount) {
                float stepProgress = INIT_UI_PROGRESS;
                stepProgress = (createdBeanCount / singletonDefinitionCount) * stepProgress;
                step(stepProgress, beanName);
            } else if (currentProgress >= INIT_UI_PROGRESS) {
                float stepProgress = DONE_PROGRESS - INIT_UI_PROGRESS;
                stepProgress = (createdBeanCount / beanFactory.getBeanDefinitionCount()) * stepProgress;
                step(INIT_UI_PROGRESS + stepProgress, beanName);
            }
            return bean;
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            log.info("Initial loading of singletons beans done");
            SceneBuilderLoadingProgress.get().initUI();
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            this.beanFactory = (DefaultListableBeanFactory)beanFactory;

            singletonDefinitionCount = 0;
            String[] beanNames = this.beanFactory.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                 BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
                if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                    singletonDefinitionCount++;
                }
            }
        }
    }
}
