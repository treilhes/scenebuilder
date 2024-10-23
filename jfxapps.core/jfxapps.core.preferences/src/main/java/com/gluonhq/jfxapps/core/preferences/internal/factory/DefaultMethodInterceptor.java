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
package com.gluonhq.jfxapps.core.preferences.internal.factory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ConcurrentReferenceHashMap.ReferenceType;

public class DefaultMethodInterceptor implements MethodInterceptor {

    private static final Lookup LOOKUP = MethodHandles.lookup();
    private final Map<Method, MethodHandle> methodCache = new ConcurrentReferenceHashMap<>(10, ReferenceType.WEAK);

    @Nullable
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        if (!method.isDefault()) {
            return invocation.proceed();
        }

        Object[] arguments = invocation.getArguments();
        Object proxy = ((ProxyMethodInvocation) invocation).getProxy();

        return getMethodHandle(method).bindTo(proxy).invokeWithArguments(arguments);
    }

    private MethodHandle getMethodHandle(Method method) throws Exception {

        MethodHandle handle = methodCache.get(method);

        if (handle == null) {

            handle = lookup(method);
            methodCache.put(method, handle);
        }

        return handle;
    }

    private static MethodHandle lookup(Method method) throws ReflectiveOperationException {

        Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), LOOKUP);
        MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
        Class<?> declaringClass = method.getDeclaringClass();

        return Modifier.isStatic(method.getModifiers())
                ? lookup.findStatic(declaringClass, method.getName(), methodType)
                : lookup.findSpecial(declaringClass, method.getName(), methodType, declaringClass);
    }
}