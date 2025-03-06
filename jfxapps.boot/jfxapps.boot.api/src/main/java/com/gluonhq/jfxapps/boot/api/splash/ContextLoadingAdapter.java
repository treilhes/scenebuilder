package com.gluonhq.jfxapps.boot.api.splash;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.gluonhq.jfxapps.boot.api.utils.ProgressListener;

public class ContextLoadingAdapter implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>, BeanFactoryPostProcessor {

    /**
	 *
	 */
	private final ProgressListener loadingProgress;

	/**
	 * @param loadingProgress
	 */
	public ContextLoadingAdapter(ProgressListener loadingProgress) {
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
        loadingProgress.notifyProgress(stepProgress);
        return bean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadingProgress.notifyFinish();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    	loadingProgress.notifyStart();
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