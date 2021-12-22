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
package com.oracle.javafx.scenebuilder.core.di;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

/**
 * A factory for creating SceneBuilderBean objects.
 */
@Component
public class SceneBuilderBeanFactory {

    /**
     * Scope identifier for the standard singleton scope: {@value}.
     * <p>
     * Custom scopes can be added via {@code registerScope}.
     *
     * @see ConfigurableListableBeanFactory#registerScope
     */
    public static final String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

    /**
     * Scope identifier for the standard prototype scope: {@value}.
     * <p>
     * Custom scopes can be added via {@code registerScope}.
     *
     * @see ConfigurableListableBeanFactory#registerScope
     */
    public static final String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    /**
     * Scope identifier for the custom document scope: {@value}.
     * <p>
     * Custom scopes can be added via {@code registerScope}.
     *
     * @see ConfigurableListableBeanFactory#registerScope
     */
    public static final String SCOPE_DOCUMENT = "document";

    /**
     * Scope identifier for the custom thread scope: {@value}.
     * <p>
     * Custom scopes can be added via {@code registerScope}.
     *
     * @see ConfigurableListableBeanFactory#registerScope
     */
    public static final String SCOPE_THREAD = "thread";

    /** The context. */
    private final GenericApplicationContext context;

    /**
     * Instantiates a new scene builder bean factory.
     */
    public SceneBuilderBeanFactory(@Autowired GenericApplicationContext context) {
        this.context = context;
    }

    /**
     * Gets the.
     *
     * @param <C> the generic type
     * @param cls the cls
     * @return the c
     */
    public <C> C getBean(Class<C> cls) {
        return context.getBean(cls);
    }

    public Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    /**
     * Gets the.
     *
     * @param <C> the generic type
     * @param cls the cls
     * @return the c
     */
    public <C> C getBean(Class<C> cls, Object... parameters) {
        return context.getBean(cls, parameters);
    }


    public String[] getBeanNamesForType(ResolvableType resolvable) {
        return context.getBeanNamesForType(resolvable);
    }

    /**
     * Creates a new SceneBuilderBean object.
     *
     * @param label the label
     * @param toggleGroup the toggle group
     * @return the radio menu item
     */
    public RadioMenuItem createViewRadioMenuItem(String label, ToggleGroup toggleGroup) {
        RadioMenuItem r = new RadioMenuItem(label);
        if (toggleGroup != null) {
            r.setToggleGroup(toggleGroup);
        }
        return r;
    }

    /**
     * Creates a new SceneBuilderBean object.
     *
     * @return the separator menu item
     */
    public SeparatorMenuItem createSeparatorMenuItem() {
        return new SeparatorMenuItem();
    }

    /**
     * Creates a new SceneBuilderBean object.
     *
     * @param label the label
     * @return the menu item
     */
    public MenuItem createViewMenuItem(String label) {
        return new MenuItem(label);
    }

    /**
     * Creates a new SceneBuilderBean object.
     *
     * @param label the label
     * @return the menu
     */
    public Menu createViewMenu(String label) {
        return new Menu(label);
    }

    public String[] getBeanNamesForType(Class<?> cls) {
        return context.getBeanNamesForType(cls);
    }

    public BeanDefinition getBeanDefinition(String name) {
        return context.getBeanDefinition(name);
    }

    public Class<?> getType(String name) {
        return context.getType(name);
    }

    public Object getBean(String string, Object... parameters) {
        return context.getBean(string, parameters);
    }

}
