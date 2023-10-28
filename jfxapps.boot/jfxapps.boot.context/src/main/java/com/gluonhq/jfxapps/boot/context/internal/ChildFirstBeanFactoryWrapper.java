/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.context.internal;

import java.beans.PropertyEditor;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.InstantiationStrategy;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.util.StringValueResolver;

@Deprecated
public class ChildFirstBeanFactoryWrapper extends DefaultListableBeanFactory {

    private DefaultListableBeanFactory parentFactory;
    private DefaultListableBeanFactory childFactory;

    public ChildFirstBeanFactoryWrapper(DefaultListableBeanFactory parentFactory, DefaultListableBeanFactory childFactory) {
        super(parentFactory.getParentBeanFactory());
        this.parentFactory = parentFactory;
        this.childFactory = childFactory;
    }


    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName,
            Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
        return childFactory.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
    }

    @Override
    public Object doResolveDependency(DependencyDescriptor descriptor, String beanName, Set<String> autowiredBeanNames,
            TypeConverter typeConverter) throws BeansException {
        return childFactory.doResolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException {
        return childFactory.resolveDependency(descriptor, requestingBeanName);
    }

    @Override
    public int hashCode() {
        return parentFactory.hashCode();
    }

    @Override
    public void registerAlias(String name, String alias) {
        parentFactory.registerAlias(name, alias);
    }

    @Override
    public boolean hasAlias(String name, String alias) {
        return parentFactory.hasAlias(name, alias);
    }

    @Override
    public boolean equals(Object obj) {
        return parentFactory.equals(obj);
    }

    @Override
    public void removeAlias(String alias) {
        parentFactory.removeAlias(alias);
    }

    @Override
    public boolean isAlias(String name) {
        return parentFactory.isAlias(name);
    }

    @Override
    public void resolveAliases(StringValueResolver valueResolver) {
        parentFactory.resolveAliases(valueResolver);
    }

    @Override
    public String canonicalName(String name) {
        return parentFactory.canonicalName(name);
    }

    @Override
    public Object getSingleton(String beanName) {
        return parentFactory.getSingleton(beanName);
    }

    @Override
    public void setSerializationId(String serializationId) {
        parentFactory.setSerializationId(serializationId);
    }

    @Override
    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        parentFactory.setInstantiationStrategy(instantiationStrategy);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return parentFactory.getBean(name);
    }

    @Override
    public InstantiationStrategy getInstantiationStrategy() {
        return parentFactory.getInstantiationStrategy();
    }

    @Override
    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        return parentFactory.getSingleton(beanName, singletonFactory);
    }

    @Override
    public String getSerializationId() {
        return parentFactory.getSerializationId();
    }

    @Override
    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        parentFactory.setParameterNameDiscoverer(parameterNameDiscoverer);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return parentFactory.getBean(name, requiredType);
    }

    @Override
    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        parentFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return parentFactory.getBean(name, args);
    }

    @Override
    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return parentFactory.getParameterNameDiscoverer();
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType, Object... args) throws BeansException {
        return parentFactory.getBean(name, requiredType, args);
    }

    @Override
    public void setAllowCircularReferences(boolean allowCircularReferences) {
        parentFactory.setAllowCircularReferences(allowCircularReferences);
    }

    @Override
    public boolean isAllowBeanDefinitionOverriding() {
        return parentFactory.isAllowBeanDefinitionOverriding();
    }

    @Override
    public void setAllowEagerClassLoading(boolean allowEagerClassLoading) {
        parentFactory.setAllowEagerClassLoading(allowEagerClassLoading);
    }

    @Override
    public boolean isAllowCircularReferences() {
        return parentFactory.isAllowCircularReferences();
    }

    @Override
    public boolean isAllowEagerClassLoading() {
        return parentFactory.isAllowEagerClassLoading();
    }

    @Override
    public void setAllowRawInjectionDespiteWrapping(boolean allowRawInjectionDespiteWrapping) {
        parentFactory.setAllowRawInjectionDespiteWrapping(allowRawInjectionDespiteWrapping);
    }

    @Override
    public void setDependencyComparator(Comparator<Object> dependencyComparator) {
        parentFactory.setDependencyComparator(dependencyComparator);
    }

    @Override
    public Comparator<Object> getDependencyComparator() {
        return parentFactory.getDependencyComparator();
    }

    @Override
    public void setAutowireCandidateResolver(AutowireCandidateResolver autowireCandidateResolver) {
        parentFactory.setAutowireCandidateResolver(autowireCandidateResolver);
    }

    @Override
    public boolean isAllowRawInjectionDespiteWrapping() {
        return parentFactory.isAllowRawInjectionDespiteWrapping();
    }

    @Override
    public void ignoreDependencyType(Class<?> type) {
        parentFactory.ignoreDependencyType(type);
    }

    @Override
    public AutowireCandidateResolver getAutowireCandidateResolver() {
        return parentFactory.getAutowireCandidateResolver();
    }

    @Override
    public void ignoreDependencyInterface(Class<?> ifc) {
        super.ignoreDependencyInterface(ifc);
    }

    @Override
    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        parentFactory.copyConfigurationFrom(otherFactory);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return parentFactory.containsSingleton(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        return parentFactory.getSingletonNames();
    }

    @Override
    public int getSingletonCount() {
        return parentFactory.getSingletonCount();
    }

    @Override
    public void setCurrentlyInCreation(String beanName, boolean inCreation) {
        parentFactory.setCurrentlyInCreation(beanName, inCreation);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return parentFactory.getBean(requiredType);
    }

    @Override
    public boolean isCurrentlyInCreation(String beanName) {
        return parentFactory.isCurrentlyInCreation(beanName);
    }

    @Override
    public <T> T createBean(Class<T> beanClass) throws BeansException {
        return parentFactory.createBean(beanClass);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return parentFactory.getBean(requiredType, args);
    }

    @Override
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return parentFactory.isSingletonCurrentlyInCreation(beanName);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        return parentFactory.getBeanProvider(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return parentFactory.getBeanProvider(requiredType);
    }

    @Override
    public void autowireBean(Object existingBean) {
        parentFactory.autowireBean(existingBean);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return parentFactory.containsBeanDefinition(beanName);
    }

    @Override
    public Object configureBean(Object existingBean, String beanName) throws BeansException {
        return parentFactory.configureBean(existingBean, beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return parentFactory.getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return parentFactory.getBeanDefinitionNames();
    }

    @Override
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        parentFactory.registerDisposableBean(beanName, bean);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        return parentFactory.getBeanProvider(requiredType, allowEagerInit);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit) {
        return parentFactory.getBeanProvider(requiredType, allowEagerInit);
    }

    @Override
    public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        return parentFactory.createBean(beanClass, autowireMode, dependencyCheck);
    }

    @Override
    public void registerContainedBean(String containedBeanName, String containingBeanName) {
        parentFactory.registerContainedBean(containedBeanName, containingBeanName);
    }

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        return parentFactory.autowire(beanClass, autowireMode, dependencyCheck);
    }

    @Override
    public void registerDependentBean(String beanName, String dependentBeanName) {
        parentFactory.registerDependentBean(beanName, dependentBeanName);
    }

    @Override
    public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
            throws BeansException {
        parentFactory.autowireBeanProperties(existingBean, autowireMode, dependencyCheck);
    }

    @Override
    public boolean containsBean(String name) {
        return parentFactory.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return parentFactory.isSingleton(name);
    }

    @Override
    public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
        parentFactory.applyBeanPropertyValues(existingBean, beanName);
    }

    @Override
    public Object initializeBean(Object existingBean, String beanName) {
        return parentFactory.initializeBean(existingBean, beanName);
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException {
        return parentFactory.applyBeanPostProcessorsBeforeInitialization(existingBean, beanName);
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException {
        return parentFactory.applyBeanPostProcessorsAfterInitialization(existingBean, beanName);
    }

    @Override
    public String[] getDependentBeans(String beanName) {
        return parentFactory.getDependentBeans(beanName);
    }

    @Override
    public void destroyBean(Object existingBean) {
        parentFactory.destroyBean(existingBean);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return parentFactory.isPrototype(name);
    }

    @Override
    public Object resolveBeanByName(String name, DependencyDescriptor descriptor) {
        return parentFactory.resolveBeanByName(name, descriptor);
    }

    @Override
    public String[] getDependenciesForBean(String beanName) {
        return parentFactory.getDependenciesForBean(beanName);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        return parentFactory.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        return parentFactory.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return parentFactory.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return parentFactory.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return parentFactory.isTypeMatch(name, typeToMatch);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return parentFactory.getBeansOfType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException {
        return parentFactory.getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return parentFactory.getBeanNamesForAnnotation(annotationType);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return parentFactory.isTypeMatch(name, typeToMatch);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return parentFactory.getType(name);
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return parentFactory.getType(name, allowFactoryBeanInit);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return parentFactory.getBeansWithAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
            throws NoSuchBeanDefinitionException {
        return parentFactory.findAnnotationOnBean(beanName, annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType,
            boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return parentFactory.findAnnotationOnBean(beanName, annotationType, allowFactoryBeanInit);
    }

    @Override
    public String[] getAliases(String name) {
        return parentFactory.getAliases(name);
    }

    @Override
    public <A extends Annotation> Set<A> findAllAnnotationsOnBean(String beanName, Class<A> annotationType,
            boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return parentFactory.findAllAnnotationsOnBean(beanName, annotationType, allowFactoryBeanInit);
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return parentFactory.getParentBeanFactory();
    }

    @Override
    public boolean containsLocalBean(String name) {
        return parentFactory.containsLocalBean(name);
    }

    @Override
    public void setParentBeanFactory(BeanFactory parentBeanFactory) {
        super.setParentBeanFactory(parentBeanFactory);
    }

    @Override
    public void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue) {
        parentFactory.registerResolvableDependency(dependencyType, autowiredValue);
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        parentFactory.setBeanClassLoader(beanClassLoader);
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return parentFactory.getBeanClassLoader();
    }

    @Override
    public void setTempClassLoader(ClassLoader tempClassLoader) {
        parentFactory.setTempClassLoader(tempClassLoader);
    }

    @Override
    public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
            throws NoSuchBeanDefinitionException {
        return parentFactory.isAutowireCandidate(beanName, descriptor);
    }

    @Override
    public ClassLoader getTempClassLoader() {
        return parentFactory.getTempClassLoader();
    }

    @Override
    public void setCacheBeanMetadata(boolean cacheBeanMetadata) {
        parentFactory.setCacheBeanMetadata(cacheBeanMetadata);
    }

    @Override
    public boolean isCacheBeanMetadata() {
        return parentFactory.isCacheBeanMetadata();
    }

    @Override
    public void setBeanExpressionResolver(BeanExpressionResolver resolver) {
        parentFactory.setBeanExpressionResolver(resolver);
    }

    @Override
    public BeanExpressionResolver getBeanExpressionResolver() {
        return parentFactory.getBeanExpressionResolver();
    }

    @Override
    public void setConversionService(ConversionService conversionService) {
        parentFactory.setConversionService(conversionService);
    }

    @Override
    public ConversionService getConversionService() {
        return parentFactory.getConversionService();
    }

    @Override
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
        parentFactory.addPropertyEditorRegistrar(registrar);
    }

    @Override
    public Set<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
        return parentFactory.getPropertyEditorRegistrars();
    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass) {
        parentFactory.registerCustomEditor(requiredType, propertyEditorClass);
    }

    @Override
    public void copyRegisteredEditorsTo(PropertyEditorRegistry registry) {
        parentFactory.copyRegisteredEditorsTo(registry);
    }

    @Override
    public Map<Class<?>, Class<? extends PropertyEditor>> getCustomEditors() {
        return parentFactory.getCustomEditors();
    }

    @Override
    public void setTypeConverter(TypeConverter typeConverter) {
        parentFactory.setTypeConverter(typeConverter);
    }

    @Override
    public TypeConverter getTypeConverter() {
        return parentFactory.getTypeConverter();
    }

    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        parentFactory.addEmbeddedValueResolver(valueResolver);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return parentFactory.getBeanDefinition(beanName);
    }

    @Override
    public boolean hasEmbeddedValueResolver() {
        return parentFactory.hasEmbeddedValueResolver();
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        return parentFactory.resolveEmbeddedValue(value);
    }

    @Override
    public Iterator<String> getBeanNamesIterator() {
        return parentFactory.getBeanNamesIterator();
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        parentFactory.addBeanPostProcessor(beanPostProcessor);
    }

    @Override
    public void clearMetadataCache() {
        parentFactory.clearMetadataCache();
    }

    @Override
    public void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors) {
        parentFactory.addBeanPostProcessors(beanPostProcessors);
    }

    @Override
    public void freezeConfiguration() {
        parentFactory.freezeConfiguration();
    }

    @Override
    public boolean isConfigurationFrozen() {
        return parentFactory.isConfigurationFrozen();
    }

    @Override
    public int getBeanPostProcessorCount() {
        return parentFactory.getBeanPostProcessorCount();
    }

    @Override
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return parentFactory.getBeanPostProcessors();
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {
        parentFactory.preInstantiateSingletons();
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException {
        parentFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void registerScope(String scopeName, Scope scope) {
        parentFactory.registerScope(scopeName, scope);
    }

    @Override
    public String[] getRegisteredScopeNames() {
        return parentFactory.getRegisteredScopeNames();
    }

    @Override
    public Scope getRegisteredScope(String scopeName) {
        return parentFactory.getRegisteredScope(scopeName);
    }

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        parentFactory.setApplicationStartup(applicationStartup);
    }

    @Override
    public ApplicationStartup getApplicationStartup() {
        return parentFactory.getApplicationStartup();
    }

    @Override
    public BeanDefinition getMergedBeanDefinition(String name) throws BeansException {
        return parentFactory.getMergedBeanDefinition(name);
    }

    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        parentFactory.removeBeanDefinition(beanName);
    }

    @Override
    public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
        return parentFactory.isFactoryBean(name);
    }

    @Override
    public boolean isActuallyInCreation(String beanName) {
        return parentFactory.isActuallyInCreation(beanName);
    }

    @Override
    public void destroyBean(String beanName, Object beanInstance) {
        parentFactory.destroyBean(beanName, beanInstance);
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        parentFactory.registerSingleton(beanName, singletonObject);
    }

    @Override
    public void destroySingletons() {
        parentFactory.destroySingletons();
    }

    @Override
    public void destroySingleton(String beanName) {
        parentFactory.destroySingleton(beanName);
    }

    @Override
    public void destroyScopedBean(String beanName) {
        parentFactory.destroyScopedBean(beanName);
    }

    @Override
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        return parentFactory.resolveNamedBean(requiredType);
    }


    @Override
    public boolean isBeanNameInUse(String beanName) {
        return parentFactory.isBeanNameInUse(beanName);
    }

    @Override
    public String toString() {
        return parentFactory.toString();
    }

}
