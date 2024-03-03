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
package com.gluonhq.jfxapps.boot.context.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;

import com.gluonhq.jfxapps.boot.context.DocumentScope;

public class SbBeanFactoryImpl extends DefaultListableBeanFactory {

    public SbBeanFactoryImpl() {
        DocumentScope scope = new DocumentScope(this);
        registerScope(DocumentScope.SCOPE_NAME, scope);
        //addBeanPostProcessor(new FxmlControllerBeanPostProcessor());
        setAutowireCandidateResolver(new SbContextAnnotationAutowireCandidateResolver());
        //registerSingleton(DocumentScope.class.getName(), scope);
    }

    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
        Class<?> rawClass = mbd.getResolvableType().getRawClass();
        try {
            return super.createBean(beanName, mbd, args);
        } catch (Exception e) {
            if (e instanceof BeanCreationException) {
                rawClass.getModule().addOpens(rawClass.getPackage().getName(), BeanUtils.class.getModule());
                return super.createBean(beanName, mbd, args);
            } else {
                throw e;
            }
        }
    }


//    @Override
//    public BeanFactory getParentBeanFactory() {
//        DefaultListableBeanFactory parent = (DefaultListableBeanFactory)super.getParentBeanFactory();
//
//        if (parent == null) {
//            return null;
//        }
//
//        ChildFirstBeanFactoryWrapper bf = new ChildFirstBeanFactoryWrapper(parent, this);
//        return bf;
//    }


    private class SbContextAnnotationAutowireCandidateResolver extends ContextAnnotationAutowireCandidateResolver {
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
                    Object target = doResolveDependency(descriptor, beanName, autowiredBeanNames, null);
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
                            if (containsBean(autowiredBeanName)) {
                                registerDependentBean(autowiredBeanName, beanName);
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
            return pf.getProxy(getBeanClassLoader());
        }

    }



}
