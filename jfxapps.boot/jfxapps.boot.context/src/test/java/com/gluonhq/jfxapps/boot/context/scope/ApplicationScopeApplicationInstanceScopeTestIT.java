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
package com.gluonhq.jfxapps.boot.context.scope;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;

import java.util.UUID;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import com.gluonhq.jfxapps.boot.api.context.Application;
import com.gluonhq.jfxapps.boot.api.context.ApplicationInstance;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.boot.context.impl.JfxAppContextImpl;

import jakarta.annotation.PreDestroy;

@SuppressWarnings("exports")
class ApplicationScopeApplicationInstanceScopeTestIT {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationScopeApplicationInstanceScopeTestIT.class);

    @BeforeEach
    public void init() {
        JfxAppContextImpl.applicationScope.clear();
    }

    @Test
    void removing_application_scope_must_remove_application_instance_scopes_and_destroy_beans_in_application_instance_scopes() {
        var context = new JfxAppContextImpl(UUID.randomUUID());
        context.register(new Class[] {ParentConfig.class});
        context.refresh();

        var context2 = new JfxAppContextImpl(UUID.randomUUID());
        context2.register(new Class[] {App1Config.class});
        context2.setParent(context);
        context2.refresh();

        var context3 = new JfxAppContextImpl(UUID.randomUUID());
        context3.register(new Class[] {App2Config.class});
        context3.setParent(context);
        context3.refresh();

        JfxAppContextImpl.applicationScope.unbindScope();
        var appScope1Bean = context2.getBean(AppBean.class);

        JfxAppContextImpl.applicationInstanceScope.unbindScope();
        var appInstScope1Bean = context2.getBean(AppInstBean.class);
        var appInstScope1BeanSomeBean1 = context2.getBean(SomeBean.class);

        JfxAppContextImpl.applicationInstanceScope.unbindScope();
        var appInstScope2Bean = context2.getBean(AppInstBean.class);
        var appInstScope2BeanSomeBean1 = context2.getBean(SomeBean.class);

        JfxAppContextImpl.applicationScope.unbindScope();
        var app2Scope1Bean = context3.getBean(App2Bean.class);

        JfxAppContextImpl.applicationInstanceScope.unbindScope();
        var app2InstScope1Bean = context3.getBean(App2InstBean.class);
        var app2InstScope1BeanSomeBean1 = context3.getBean(SomeBean.class);

        JfxAppContextImpl.applicationInstanceScope.unbindScope();
        var app2InstScope2Bean = context3.getBean(App2InstBean.class);
        var app2InstScope2BeanSomeBean1 = context3.getBean(SomeBean.class);


        assertEquals(2, JfxAppContextImpl.applicationScope.getAvailableScopes().size());
        assertEquals(4, JfxAppContextImpl.applicationInstanceScope.getAvailableScopes().size());

        JfxAppContextImpl.applicationScope.removeScope(appScope1Bean);

        assertEquals(1, JfxAppContextImpl.applicationScope.getAvailableScopes().size());
        assertEquals(2, JfxAppContextImpl.applicationInstanceScope.getAvailableScopes().size());

        Mockito.verify(appScope1Bean).destroyMe();
        Mockito.verify(appInstScope1Bean).destroyMe();
        Mockito.verify(appInstScope1BeanSomeBean1).destroyMe();
        Mockito.verify(appInstScope2Bean).destroyMe();
        Mockito.verify(appInstScope2BeanSomeBean1).destroyMe();

        //ensure other scope is untouched
        Mockito.verify(app2Scope1Bean, never()).destroyMe();
        Mockito.verify(app2InstScope1Bean, never()).destroyMe();
        Mockito.verify(app2InstScope1BeanSomeBean1, never()).destroyMe();
        Mockito.verify(app2InstScope2Bean, never()).destroyMe();
        Mockito.verify(app2InstScope2BeanSomeBean1, never()).destroyMe();


        Mockito.verifyNoMoreInteractions(appScope1Bean, appInstScope1Bean, appInstScope1BeanSomeBean1,
                appInstScope2Bean, appInstScope2BeanSomeBean1);

        JfxAppContextImpl.applicationScope.removeScope(app2Scope1Bean);

        assertEquals(0, JfxAppContextImpl.applicationScope.getAvailableScopes().size());
        assertEquals(0, JfxAppContextImpl.applicationInstanceScope.getAvailableScopes().size());

        Mockito.verify(app2Scope1Bean).destroyMe();
        Mockito.verify(app2InstScope1Bean).destroyMe();
        Mockito.verify(app2InstScope1BeanSomeBean1).destroyMe();
        Mockito.verify(app2InstScope2Bean).destroyMe();
        Mockito.verify(app2InstScope2BeanSomeBean1).destroyMe();

        Mockito.verifyNoMoreInteractions(app2Scope1Bean, app2InstScope1Bean, app2InstScope1BeanSomeBean1,
                app2InstScope2Bean, app2InstScope2BeanSomeBean1);

        context3.close();
        context2.close();
        context.close();
    }

    @Test
    void changing_application_instance_scope_must_change_application_scope() {
        var context = new JfxAppContextImpl(UUID.randomUUID());
        context.register(new Class[] {ParentConfig.class});
        context.refresh();

        var context2 = new JfxAppContextImpl(UUID.randomUUID());
        context2.register(new Class[] {App1Config.class});
        context2.setParent(context);
        context2.refresh();

        var context3 = new JfxAppContextImpl(UUID.randomUUID());
        context3.register(new Class[] {App2Config.class});
        context3.setParent(context);
        context3.refresh();

        JfxAppContextImpl.applicationScope.unbindScope();
        var appScope1Bean = context2.getBean(AppBean.class);

        JfxAppContextImpl.applicationInstanceScope.unbindScope();
        var appInstScope1Bean = context2.getBean(AppInstBean.class);

        JfxAppContextImpl.applicationScope.unbindScope();
        var app2Scope1Bean = context3.getBean(App2Bean.class);

        JfxAppContextImpl.applicationInstanceScope.unbindScope();
        var app2InstScope1Bean = context3.getBean(App2InstBean.class);



        assertEquals(app2Scope1Bean, JfxAppContextImpl.applicationScope.getActiveScope().getScopedObject());
        assertEquals(app2InstScope1Bean, JfxAppContextImpl.applicationInstanceScope.getActiveScope().getScopedObject());

        JfxAppContextImpl.applicationInstanceScope.setCurrentScope(appInstScope1Bean);

        assertEquals(appScope1Bean, JfxAppContextImpl.applicationScope.getActiveScope().getScopedObject());
        assertEquals(appInstScope1Bean, JfxAppContextImpl.applicationInstanceScope.getActiveScope().getScopedObject());

        context.close();

    }

    @Test
    void changing_application_scope_must_unbind_application_instance_scope() {
        var context = new JfxAppContextImpl(UUID.randomUUID());
        context.register(new Class[] {ParentConfig.class});
        context.refresh();

        var context2 = new JfxAppContextImpl(UUID.randomUUID());
        context2.register(new Class[] {App1Config.class});
        context2.setParent(context);
        context2.refresh();

        var context3 = new JfxAppContextImpl(UUID.randomUUID());
        context3.register(new Class[] {App2Config.class});
        context3.setParent(context);
        context3.refresh();

        JfxAppContextImpl.applicationScope.unbindScope();
        var appScope1Bean = context2.getBean(AppBean.class);

        JfxAppContextImpl.applicationInstanceScope.unbindScope();
        var appInstScope1Bean = context2.getBean(AppInstBean.class);

        JfxAppContextImpl.applicationScope.unbindScope();
        var app2Scope1Bean = context3.getBean(App2Bean.class);

        JfxAppContextImpl.applicationInstanceScope.unbindScope();
        var app2InstScope1Bean = context3.getBean(App2InstBean.class);


        assertEquals(app2Scope1Bean, JfxAppContextImpl.applicationScope.getActiveScope().getScopedObject());
        assertEquals(app2InstScope1Bean, JfxAppContextImpl.applicationInstanceScope.getActiveScope().getScopedObject());

        JfxAppContextImpl.applicationScope.setCurrentScope(appScope1Bean);

        assertEquals(appScope1Bean, JfxAppContextImpl.applicationScope.getActiveScope().getScopedObject());
        assertEquals(null, JfxAppContextImpl.applicationInstanceScope.getActiveScope());

        context.close();

    }
//
//    @Test
//    void destruct() {
//        var context = new JfxAppContextImpl(UUID.randomUUID());
//        context.register(new Class[] {ParentConfig.class});
//        context.refresh();
//
//        var context2 = new JfxAppContextImpl(UUID.randomUUID());
//        context2.register(new Class[] {App1Config.class});
//        context2.setParent(context);
//        context2.refresh();
//
//        var context3 = new JfxAppContextImpl(UUID.randomUUID());
//        context3.register(new Class[] {App2Config.class});
//        context3.setParent(context);
//        context3.refresh();
//
////        var scope1Bean = context2.getBean(AppBean.class);
////        var p = context2.getBean(ParentBean.class);
////        context2.getBean(CompositeBean.class);
////
////        JfxAppContextImpl.applicationScope.unbindScopes();
////
////        var scope2Bean = context2.getBean(AppBean.class);
////        var p2 = context2.getBean(ParentBean.class);
////        context2.getBean(CompositeBean.class);
//
//        //JfxAppContextImpl.applicationScope.removeScope(scope1Bean);
//
//        context3.close();
//        context2.close();
//        context.close();
//    }

    @Configuration
    public static class ParentConfig {
        @Bean
        @Scope(ApplicationInstanceScope.SCOPE_NAME)
        public SomeBean someBean() {
            return Mockito.spy(SomeBean.class);
        }
    }

    @Configuration
    public static class App1Config {
        @Bean
        @Scope(ApplicationScope.SCOPE_NAME)
        AppBean appBean() {
            return Mockito.spy(AppBean.class);
        }

        @Bean
        @Scope(ApplicationInstanceScope.SCOPE_NAME)
        @DependsOn("appBean")
        AppInstBean appInstBean() {
            return Mockito.spy(AppInstBean.class);
        }

        @Bean
        @Scope(ApplicationInstanceScope.SCOPE_NAME)
        SomeBean someAppInstBean() {
            return Mockito.spy(SomeBean.class);
        }
    }

    @Configuration
    public static class App2Config {
        @Bean
        @Scope(ApplicationScope.SCOPE_NAME)
        App2Bean app2Bean() {
            return Mockito.spy(App2Bean.class);
        }

        @Bean
        @Scope(ApplicationInstanceScope.SCOPE_NAME)
        @DependsOn("app2Bean")
        App2InstBean app2InstBean() {
            return Mockito.spy(App2InstBean.class);
        }

        @Bean
        @Scope(ApplicationInstanceScope.SCOPE_NAME)
        SomeBean someAppInstBean() {
            return Mockito.spy(SomeBean.class);
        }
    }

    public static class AppBean implements Application {
        @PreDestroy
        public void destroyMe() {
            logger.info("Destroying {}", getClass().getSimpleName());
        }
    }
    public static class AppInstBean implements ApplicationInstance {
        @PreDestroy
        public void destroyMe() {
            logger.info("Destroying {}", getClass().getSimpleName());
        }
    }
    public static class App2Bean implements Application {
        @PreDestroy
        public void destroyMe() {
            logger.info("Destroying {}", getClass().getSimpleName());
        }
    }
    public static class App2InstBean implements ApplicationInstance {
        @PreDestroy
        public void destroyMe() {
            logger.info("Destroying {}", getClass().getSimpleName());
        }
    }
    public static class SomeBean {
        @PreDestroy
        public void destroyMe() {
            logger.info("Destroying {}", getClass().getSimpleName());
        }
    }

}
