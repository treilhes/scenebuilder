package com.gluonhq.jfxapps.boot.context.internal;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.gluonhq.jfxapps.boot.api.context.MultipleProgressListener;

public class ContextProgressHandler implements DestructionAwareBeanPostProcessor,
        ApplicationListener<ContextRefreshedEvent>, BeanFactoryPostProcessor {

    private final static Logger logger = LoggerFactory.getLogger(ContextProgressHandler.class);

    private final UUID taskId;
    private final MultipleProgressListener progressListener;
    private DefaultListableBeanFactory beanFactory;
    private float singletonDefinitionCount;
    private float createdBeanCount;
    private float progress;

    public ContextProgressHandler(UUID taskId, MultipleProgressListener progressListener) {
        super();
        this.progressListener = progressListener;
        this.taskId = taskId;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // progress is not perfectly accurate but fair enough
        progress = ++createdBeanCount / singletonDefinitionCount;

        if (logger.isDebugEnabled()) {
            logger.debug("Notify progress from {} after bean {} / {} with progress {}", this, beanName, bean.getClass(),
                    progress);
        }

        if (progressListener != null) {
            progressListener.notifyProgress(taskId, progress);
        }
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (logger.isDebugEnabled()) {
            logger.debug("Destructing bean {} / {}", beanName, bean.getClass());
        }

        beanFactory.clearMetadataCache();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        progress = 1f;
        if (progressListener != null && event.getApplicationContext().getAutowireCapableBeanFactory() == beanFactory) {
            progressListener.notifyDone(taskId);
        }
        logger.info("Initial loading of singletons beans done {}", event);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
        this.beanFactory.addBeanPostProcessor(this);

        if (progressListener != null) {
            progressListener.notifyStart(taskId);
        }

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
