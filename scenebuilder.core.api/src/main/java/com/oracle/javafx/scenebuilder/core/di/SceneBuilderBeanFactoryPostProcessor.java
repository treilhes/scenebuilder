package com.oracle.javafx.scenebuilder.core.di;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.util.SceneBuilderLoadingProgress;

/**
 * The Class SceneBuilderBeanFactoryPostProcessor.
 */
@Component
public class SceneBuilderBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    /**
     * Instantiates a new scene builder bean factory post processor.
     */
    public SceneBuilderBeanFactoryPostProcessor() {
        super();
    }

    /**
     * Post process bean factory.
     *
     * @param factory the factory
     * @throws BeansException the beans exception
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        factory.registerScope(SceneBuilderBeanFactory.SCOPE_DOCUMENT, new DocumentScope());
        factory.registerScope(SceneBuilderBeanFactory.SCOPE_THREAD, new ThreadScope());
        factory.addBeanPostProcessor(new FxmlControllerBeanPostProcessor());
        factory.addBeanPostProcessor(SceneBuilderLoadingProgress.get().getProgressListener());
        DefaultListableBeanFactory bf = (DefaultListableBeanFactory) factory;
        bf.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver() {

            @Override
            protected Object buildLazyResolutionProxy(DependencyDescriptor descriptor, String beanName) {
                TargetSource ts = new TargetSource() {
                    private Object savedTarget = null;

                    @Override
                    public Class<?> getTargetClass() {
                        return descriptor.getDependencyType();
                    }

                    @Override
                    public boolean isStatic() {
                        return false;
                    }

                    @Override
                    public Object getTarget() {
                        if (savedTarget != null) {
                            return savedTarget;
                        }
                        Set<String> autowiredBeanNames = (beanName != null ? new LinkedHashSet<>(1) : null);
                        Object target = bf.doResolveDependency(descriptor, beanName, autowiredBeanNames, null);
                        if (target == null) {
                            Class<?> type = getTargetClass();
                            if (Map.class == type) {
                                return Collections.emptyMap();
                            } else if (List.class == type) {
                                return Collections.emptyList();
                            } else if (Set.class == type || Collection.class == type) {
                                return Collections.emptySet();
                            }
                            throw new NoSuchBeanDefinitionException(descriptor.getResolvableType(),
                                    "Optional dependency not present for lazy injection point");
                        }
                        if (autowiredBeanNames != null) {
                            for (String autowiredBeanName : autowiredBeanNames) {
                                if (bf.containsBean(autowiredBeanName)) {
                                    bf.registerDependentBean(autowiredBeanName, beanName);
                                }
                            }
                        }
                        savedTarget = target;
                        return target;
                    }

                    @Override
                    public void releaseTarget(Object target) {
                    }
                };
                ProxyFactory pf = new ProxyFactory();
                pf.setTargetSource(ts);
                Class<?> dependencyType = descriptor.getDependencyType();
                if (dependencyType.isInterface()) {
                    pf.addInterface(dependencyType);
                }
                return pf.getProxy(bf.getBeanClassLoader());
            }

        });
    }
}