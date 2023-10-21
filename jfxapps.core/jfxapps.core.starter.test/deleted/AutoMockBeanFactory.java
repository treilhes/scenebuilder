/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.test;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author ptreilhes
 *
 */
public class AutoMockBeanFactory extends DefaultListableBeanFactory {

    @Override
    protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
        // TODO Auto-generated method stub

        try {
            return super.instantiateBean(beanName, mbd);
        } catch (Exception e) {
            BeanWrapper bw = new BeanWrapperImpl(Mockito.mock(mbd.getBeanClass()));
            initBeanWrapper(bw);
            return bw;
        }
    }

    @Override
    protected Map<String, Object> findAutowireCandidates(final String beanName, final Class<?> requiredType, final DependencyDescriptor descriptor) {
        System.out.println("XXXXXXXXXXXXXXXXx " + beanName);
        String mockBeanName = Introspector.decapitalize(requiredType.getSimpleName());
        Map<String, Object> autowireCandidates = new HashMap<>();
        try {
            autowireCandidates = super.findAutowireCandidates(beanName, requiredType, descriptor);
        } catch (UnsatisfiedDependencyException e) {
            if (e.getCause() != null && e.getCause().getCause() instanceof NoSuchBeanDefinitionException) {
                mockBeanName = ((NoSuchBeanDefinitionException) e.getCause().getCause()).getBeanName();
            }
            this.registerBeanDefinition(mockBeanName, BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition());
        }
        if (autowireCandidates.isEmpty()) {
            System.out.println("Mocking bean: " + mockBeanName);
            final Object mock = Mockito.mock(requiredType);
            autowireCandidates.put(mockBeanName, mock);
            this.addSingleton(mockBeanName, mock);
        }
        return autowireCandidates;
    }
}