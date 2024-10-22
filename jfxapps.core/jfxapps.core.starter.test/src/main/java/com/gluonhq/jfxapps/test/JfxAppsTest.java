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
package com.gluonhq.jfxapps.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import com.gluonhq.jfxapps.boot.api.context.Application;
import com.gluonhq.jfxapps.boot.api.context.ApplicationInstance;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.test.JfxAppsExtension.JfxAppsTestContextBootstrapper;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@BootstrapWith(JfxAppsTestContextBootstrapper.class)
@ExtendWith({
    ApplicationExtension.class,
    MockitoExtension.class,
    SpringExtension.class,
    JfxAppsExtension.class
    })
public @interface JfxAppsTest {
    String[] properties() default {};
    boolean loadDefaultScopes() default true;

    @ApplicationSingleton
    public static class Application1Bean implements Application {
    }

    @ApplicationInstanceSingleton
    public static class Application1InstanceBean implements ApplicationInstance {
    }

    @ApplicationSingleton
    public static class Application2Bean implements Application {
    }

    @ApplicationInstanceSingleton
    public static class Application2InstanceBean implements ApplicationInstance {
    }
}
