package com.gluonhq.jfxapps.boot.splash.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

class ContextLoadingMonitor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>, BeanFactoryPostProcessor {

    /**
	 *
	 */
	private final LoadingProgress loadingProgress;

	/**
	 * @param loadingProgress
	 */
	ContextLoadingMonitor(LoadingProgress loadingProgress) {
		this.loadingProgress = loadingProgress;
	}

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
        float stepProgress = createdBeanCount / singletonDefinitionCount;
        loadingProgress.step(stepProgress, beanName);
        return bean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadingProgress.end();
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

        this.beanFactory.addBeanPostProcessor(this);
    }
}