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
package com.oracle.javafx.scenebuilder.core.di;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.oracle.javafx.scenebuilder.api.di.DocumentScope;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstance;

import javafx.stage.Stage;

/**
 * @See https://github.com/spring-projects/spring-framework/issues/28651
 * @author ptreilhes
 *
 */
public class PrimaryTest {

    private ApplicationContext context;

    @BeforeEach
    void setupContext() {
        this.context = new SpringApplicationBuilder().sources(new Class[] {
                FirstImpl.class,
                SecondImpl.class,
                ThirdImpl.class
                }).build().run(new String[0]);
    }

    //@Test
    void primeAndSecondMustBeUsable() {
        First p = context.getBean(First.class);
        Second s = context.getBean(Second.class);
        Third t = context.getBean(Third.class);

        System.out.println(p.firstFunc());
        System.out.println(s.firstFunc());
        System.out.println(s.secondFunc());
        System.out.println(t.firstFunc());
        System.out.println(t.secondFunc());
        System.out.println(t.thirdFunc());


        assertEquals(p.firstFunc(), s.firstFunc(), "eq");
    }

    public interface First {
        String firstFunc();
    }

    public interface Second extends First {
        String secondFunc();
    }

    public interface Third extends Second {
        String thirdFunc();
    }

    @Component
    @Primary
    public static class FirstImpl implements First {

        @Override
        public String firstFunc() {
            return "firstFunc";
        }

    }

    @Component
    public static class SecondImpl implements Second {

        private First first;

        public SecondImpl(First first) {
            super();
            this.first = first;
        }

        @Override
        public String firstFunc() {
            return first.firstFunc();
        }

        @Override
        public String secondFunc() {
            return "secondFunc";
        }

    }

    @Component
    public static class ThirdImpl implements Third {

        private Second second;

        public ThirdImpl(Second second) {
            super();
            this.second = second;
        }

        @Override
        public String firstFunc() {
            return second.firstFunc();
        }

        @Override
        public String secondFunc() {
            return second.secondFunc();
        }

        @Override
        public String thirdFunc() {
            return "thirdFunc";
        }

    }
}
