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
package com.gluonhq.jfxapps.boot.context.bpp;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.annotation.OverrideBean;
import com.gluonhq.jfxapps.boot.context.annotation.OverridedBeanAware;
import com.gluonhq.jfxapps.boot.context.impl.JfxAppContextImpl;

class OverridedBeanPostProcessorTest {

    private final static String ORIGINAL = "ORIGINAL";
    private final static String OVERRIDE = "OVERRIDE";
    private final static String PRM = "PRM";

    @Test
    void must_override_only_second_method() {
        JfxAppContextImpl ctx = new JfxAppContextImpl(UUID.randomUUID());
        ctx.register(OverridedBeanPostProcessor.class, OriginalComponent.class, OverrideComponent.class);
        ctx.refresh();

        OriginalComponent modified = ctx.getBean(OriginalComponent.class);

        assertEquals(ORIGINAL + ":" + PRM, modified.method1(PRM));
        assertEquals(OVERRIDE + ":" + PRM, modified.method2(PRM));

        OverrideComponent override = ctx.getBean(OverrideComponent.class);

        assertNotNull(override.getOverridedBean());
        assertNotEquals(modified, override.getOverridedBean());
        assertNotEquals(modified.method2(PRM), override.getOverridedBean().method2(PRM));

        ctx.close();
    }

    @Component
    public static class OriginalComponent {
        public String method1(String prm) {
            return ORIGINAL + ":" + prm;
        }

        public String method2(String prm) {
            return ORIGINAL + ":" + prm;
        }
    }

    @Component
    @OverrideBean(OriginalComponent.class)
    public static class OverrideComponent implements OverridedBeanAware<OriginalComponent> {
        private OriginalComponent overridedBean;
        @Override
        public void setOverridedBean(OriginalComponent overridedBean) {
            this.overridedBean = overridedBean;
        }

        public String method2(String prm) {
            return OVERRIDE + ":" + prm;
        }

        public OriginalComponent getOverridedBean() {
            return overridedBean;
        }

    }
}
