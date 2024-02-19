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

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.gluonhq.jfxapps.boot.context.ContextManager;
import com.gluonhq.jfxapps.boot.context.Document;
import com.gluonhq.jfxapps.boot.context.DocumentScope;
import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.context.impl.ContextManagerImpl;

/**
 * In case of switching the dependency injection framework, those tests exhibit some of the needed features of the new framework
 * when using jakarta.inject
 */
class SbContextScopeTest {

    private ApplicationContext bootContext = null;

    @Test
    void ensure_singleton_scope_return_same_instance() {
        ContextManager mng = new ContextManagerImpl(bootContext);

        Class<?>[] classes = { Singleton1.class, Singleton2.class };
        JfxAppContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);

        Singleton1 s1_1 = ctx.getBean(Singleton1.class);
        Singleton1 s1_2 = ctx.getBean(Singleton1.class);
        Singleton2 s2_1 = ctx.getBean(Singleton2.class);
        Singleton2 s2_2 = ctx.getBean(Singleton2.class);
        assertNotNull(s1_1);
        assertNotNull(s1_2);
        assertNotNull(s2_1);
        assertNotNull(s2_2);
        assertEquals(s1_1, s1_2);
        assertEquals(s2_1, s2_2);
        assertEquals(s2_1.singleton1, s2_2.singleton1);
    }

    @Test
    void ensure_named_singleton_return_same_instance() {
        ContextManager mng = new ContextManagerImpl(bootContext);

        Class<?>[] classes = { QualifiedSingleton1.class, QualifiedSingleton2.class };
        JfxAppContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);

        QualifiedSingleton1 s1 = ctx.getBean(QualifiedSingleton1.class);
        QualifiedSingleton2 s2 = ctx.getBean(QualifiedSingleton2.NAME);
        Object named1 = ctx.getBean(QualifiedSingleton1.NAME);
        Object named2 = ctx.getBean(QualifiedSingleton2.NAME);
        assertNotNull(s1);
        assertNotNull(s2);
        assertNotNull(named1);
        assertNotNull(named2);
        assertEquals(s1, named1);
        assertEquals(s2, named2);
    }

    @Test
    void ensure_prototype_scope_return_different_instances() {
        ContextManager mng = new ContextManagerImpl(bootContext);

        Class<?>[] classes = { Prototype1.class, Prototype2.class };
        JfxAppContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);

        Prototype1 d1_1 = ctx.getBean(Prototype1.class);
        Prototype1 d1_2 = ctx.getBean(Prototype1.class);
        Prototype2 d2_1 = ctx.getBean(Prototype2.class);
        Prototype2 d2_2 = ctx.getBean(Prototype2.class);
        assertNotNull(d1_1);
        assertNotNull(d1_2);
        assertNotNull(d2_1);
        assertNotNull(d2_2);
        assertNotEquals(d1_1, d1_2);
        assertNotEquals(d2_1, d2_2);
        assertNotEquals(d2_1.prototype1, d2_2.prototype1);
    }

    @Test
    void ensure_window_scope_return_rightly_scoped_instances() {
        ContextManager mng = new ContextManagerImpl(bootContext);

        Class<?>[] classes = { MainWindow.class, WindowComponent.class };
        JfxAppContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);

        DocumentScope.setCurrentScope(null);
        MainWindow mw1 = ctx.getBean(MainWindow.class);

        assertEquals(DocumentScope.getCurrentScope(), mw1);
        MainWindow mw2 = ctx.getBean(MainWindow.class);

        assertEquals(mw1, mw2);

        DocumentScope.setCurrentScope(null);
        MainWindow mw1_2 = ctx.getBean(MainWindow.class);

        assertEquals(DocumentScope.getCurrentScope(), mw1_2);
        MainWindow mw2_2 = ctx.getBean(MainWindow.class);

        assertEquals(mw1_2, mw2_2);
        assertNotEquals(mw1, mw1_2);


        DocumentScope.setCurrentScope(mw1);
        WindowComponent wc1 = ctx.getBean(WindowComponent.class);
        assertEquals(mw1.windowComponent, wc1);

        DocumentScope.setCurrentScope(mw2);
        WindowComponent wc2 = ctx.getBean(WindowComponent.class);
        assertEquals(mw2.windowComponent, wc2);
    }

    @Test
    void ensure_singleton_scope_return_singleton_scoped_instances_from_bean_list() {
        ContextManager mng = new ContextManagerImpl(bootContext);

        Class<?>[] classes = { BeanList.class, WindowComponent.class, MainWindow.class };
        JfxAppContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);

        BeanList.Main mw1 = ctx.getBean(BeanList.Main.class);
        DocumentScope.setCurrentScope(null);
        BeanList.Main mw2 = ctx.getBean(BeanList.Main.class);

        DocumentScope.setCurrentScope(mw1);
        WindowComponent wc1 = ctx.getBean(BeanList.SINGLETON_NAME1);
        WindowComponent wc2 = ctx.getBean(BeanList.SINGLETON_NAME2);
        assertNotEquals(wc1, wc2);

        DocumentScope.setCurrentScope(mw2);
        WindowComponent wc1_2 = ctx.getBean(BeanList.SINGLETON_NAME1);
        WindowComponent wc2_2 = ctx.getBean(BeanList.SINGLETON_NAME2);

        assertEquals(wc1, wc1_2);
        assertEquals(wc2, wc2_2);
    }

    @Test
    void ensure_window_scope_return_window_scoped_instances_from_bean_list() {
        ContextManager mng = new ContextManagerImpl(bootContext);

        Class<?>[] classes = { BeanList.class, WindowComponent.class, MainWindow.class };
        JfxAppContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);

        //DocumentScope sc = ctx.getBean(DocumentScope.class.getName());
        BeanList.Main mw1 = ctx.getBean(BeanList.Main.class);
        DocumentScope.setCurrentScope(null);
        BeanList.Main mw2 = ctx.getBean(BeanList.Main.class);

        DocumentScope.setCurrentScope(mw1);
        WindowComponent wc1 = ctx.getBean(BeanList.DOCUMENT_NAME1);
        WindowComponent wc2 = ctx.getBean(BeanList.DOCUMENT_NAME2);
        assertNotEquals(wc1, wc2);

        DocumentScope.setCurrentScope(mw2);
        WindowComponent wc1_2 = ctx.getBean(BeanList.DOCUMENT_NAME1);
        WindowComponent wc2_2 = ctx.getBean(BeanList.DOCUMENT_NAME2);

        DocumentScope.setCurrentScope(mw1);
        WindowComponent wc1_3 = ctx.getBean(BeanList.DOCUMENT_NAME1);
        WindowComponent wc2_3 = ctx.getBean(BeanList.DOCUMENT_NAME2);
        assertEquals(wc1, wc1_3);
        assertEquals(wc2, wc2_3);

        DocumentScope.setCurrentScope(mw2);
        WindowComponent wc1_4 = ctx.getBean(BeanList.DOCUMENT_NAME1);
        WindowComponent wc2_4 = ctx.getBean(BeanList.DOCUMENT_NAME2);

        assertEquals(wc1_2, wc1_4);
        assertEquals(wc2_2, wc2_4);
    }

    static interface SomeService {}

    @Singleton(QualifiedSingleton1.NAME)
    static class QualifiedSingleton1 implements SomeService {
        public final static String NAME = "namedSingleton1";
    }

    @Singleton(QualifiedSingleton2.NAME)
    static class QualifiedSingleton2 implements SomeService {
        public final static String NAME = "namedSingleton2";
    }

    @Singleton
    static class Singleton1 {}

    @Singleton
    static class Singleton2 {
        Singleton1 singleton1;

        public Singleton2(Singleton1 singleton1) {
            super();
            this.singleton1 = singleton1;
        }
    }

    @Prototype
    static class Prototype1 {}

    @Prototype
    static class Prototype2 {
        Prototype1 prototype1;

        public Prototype2(Prototype1 prototype1) {
            super();
            this.prototype1 = prototype1;
        }
    }

    @ApplicationInstanceSingleton
    static class WindowComponent {}

    @ApplicationInstanceSingleton
    static class MainWindow implements Document {
        WindowComponent windowComponent;

        public MainWindow(WindowComponent windowComponent) {
            super();
            this.windowComponent = windowComponent;
        }
    }

    static class BeanList {
        public final static String SINGLETON_NAME1 = "myWindowComponent1";
        public final static String SINGLETON_NAME2 = "myWindowComponent2";
        public final static String DOCUMENT_NAME1 = "myWindowComponent3";
        public final static String DOCUMENT_NAME2 = "myWindowComponent4";

        //@Bean
        @ApplicationInstanceSingleton
        public Main mainComponent() {
            return new Main();
        }

        @Bean(SINGLETON_NAME1)
        public WindowComponent windowComponent1() {
            return new WindowComponent();
        }

        @Bean(SINGLETON_NAME2)
        public WindowComponent windowComponent2() {
            return new WindowComponent();
        }

        @Bean(DOCUMENT_NAME1)
        @ApplicationInstanceSingleton
        public WindowComponent windowComponent3() {
            return new WindowComponent();
        }

        @Bean(DOCUMENT_NAME2)
        @ApplicationInstanceSingleton
        public WindowComponent windowComponent4() {
            return new WindowComponent();
        }

        @ApplicationInstanceSingleton
        static class Main implements Document{

            public Main() {
                super();
            }

        }
    }
}
