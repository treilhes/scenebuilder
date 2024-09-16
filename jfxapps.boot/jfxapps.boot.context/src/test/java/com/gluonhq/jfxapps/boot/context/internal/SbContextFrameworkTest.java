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
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import com.gluonhq.jfxapps.boot.api.context.ContextManager;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Lazy;
import com.gluonhq.jfxapps.boot.api.context.annotation.PreferedConstructor;
import com.gluonhq.jfxapps.boot.api.context.annotation.Primary;
import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.boot.context.impl.ContextManagerImpl;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Provider;


/**
 * In case of switching the dependency injection framework, those tests exhibit some of the needed features of the new framework
 * when using jakarta.inject
 */
class SbContextFrameworkTest {

    private ApplicationContext bootContext = null;

    @Test
    void test_injection_feature_optional() {
        ContextManager mng = new ContextManagerImpl(bootContext);
        Set<Class<?>> classes = Set.of(OptionalFeature.class);
        JfxAppContext ctx = mng.create(null, null, classes, Set.of(), null, null);
        assertNotNull(ctx.getBean(OptionalFeature.class));
    }

    @Test
    void test_injection_feature_generic() {
        ContextManager mng = new ContextManagerImpl(bootContext);
        Set<Class<?>> classes = Set.of(Component1.class, Component2.class, Component3.class, GenericFeature.class);
        JfxAppContext ctx = mng.create(null, null, classes, Set.of(), null, null);
        assertNotNull(ctx.getBean(GenericFeature.class));
    }

    @Test
    void test_injection_feature_list() {
        ContextManager mng = new ContextManagerImpl(bootContext);
        Set<Class<?>> classes = Set.of(Component1.class, Component2.class, Component3.class, ListFeature.class);
        JfxAppContext ctx = mng.create(null, null, classes, Set.of(),null, null);
        assertNotNull(ctx.getBean(ListFeature.class));
    }

    @Test
    void test_injection_feature_lazy_injection() {
        ContextManager mng = new ContextManagerImpl(bootContext);
        Set<Class<?>> classes = Set.of(Component1.class, Component2.class, Component3.class, LazyInjectionFeature.class);
        JfxAppContext ctx = mng.create(null, null, classes, Set.of(),null, null);
        assertNotNull(ctx.getBean(LazyInjectionFeature.class));
    }

    @Test
    void test_injection_feature_multiple_constructor() {
        ContextManager mng = new ContextManagerImpl(bootContext);
        Set<Class<?>> classes = Set.of(Component1.class, MultiConstructorFeature.class);
        JfxAppContext ctx = mng.create(null, null, classes, Set.of(),null, null);
        assertNotNull(ctx.getBean(MultiConstructorFeature.class));
    }

    @Test
    void test_injection_feature_primary() {
        ContextManager mng = new ContextManagerImpl(bootContext);
        Set<Class<?>> classes = Set.of(PrimaryComponent.class, SecondaryComponent.class, PrimaryInjectionFeature.class);
        JfxAppContext ctx = mng.create(null, null, classes, Set.of(), null, null);
        assertNotNull(ctx.getBean(PrimaryInjectionFeature.class));
    }

    @Test
    void test_injection_feature_lazy_initialization() {
        ContextManager mng = new ContextManagerImpl(bootContext);
        Set<Class<?>> classes = Set.of(LazyInitComponent.class, LazyInitFeature.class);
        JfxAppContext ctx = mng.create(null, null, classes, Set.of(), null, null);
        LazyInitFeature lif = ctx.getBean(LazyInitFeature.class);

        assertEquals(0, LazyInitComponent.instanciated);
        LazyInitComponent cmp = lif.lazy.get();
        assertEquals(1, LazyInitComponent.instanciated);
        assertNotNull(cmp);;
    }

    static interface SomeService<T> {}
    static interface UnknownService {}
    static interface OtherService {}
    static interface Generic1 {}
    static interface Generic2 {}

    @Singleton
    static class Component1 implements SomeService<Generic1> {}

    @Singleton
    static class Component2 implements SomeService<Generic2> {}

    @Singleton
    static class Component3 implements SomeService<Generic2> {}

    @Singleton
    @Primary
    static class PrimaryComponent implements OtherService {}

    @Singleton
    static class SecondaryComponent implements OtherService {}

    @Singleton
    @Lazy
    static class LazyInitComponent {
        public static int instanciated = 0;
        public LazyInitComponent() {
            super();
            instanciated++;
        }
    }

    @Singleton
    static class MultiConstructorFeature {
        private Component1 test;
        public MultiConstructorFeature() {}
        @PreferedConstructor
        public MultiConstructorFeature(Component1 test) {
            this.test = test;
        }
        @PostConstruct
        public void then() {
            assertNotNull(test);
        }
    }

    @Singleton
    static class OptionalFeature {
        public OptionalFeature(Optional<UnknownService> optional) {
            assertTrue(optional.isEmpty());
        }
    }

    @Singleton
    static class GenericFeature {
        public GenericFeature(SomeService<Generic1> service) {
            assertNotNull(service);
        }
    }

    @Singleton
    static class ListFeature {
        public ListFeature(
                List<SomeService<Generic2>> services,
                Optional<List<SomeService<?>>> optionalList) {
            assertEquals(2, services.size());
            optionalList.orElseThrow();
            assertEquals(3, optionalList.get().size());
        }
    }

    @Singleton
    static class LazyInjectionFeature {
        public LazyInjectionFeature(Provider<SomeService<Generic1>> service) {
            assertNotNull(service.get());
        }
    }

    @Singleton
    static class PrimaryInjectionFeature {
        public PrimaryInjectionFeature(OtherService service) {
            assertNotNull(service);
        }
    }

    @Singleton
    static class LazyInitFeature {
        public Provider<LazyInitComponent> lazy;
        public LazyInitFeature(Provider<LazyInitComponent> lazy) {
            this.lazy = lazy;
        }
    }
}
