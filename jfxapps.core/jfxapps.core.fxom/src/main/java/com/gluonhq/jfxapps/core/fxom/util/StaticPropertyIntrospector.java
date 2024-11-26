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
package com.gluonhq.jfxapps.core.fxom.util;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */

class StaticPropertyIntrospector {
    private Object object;
    private Class<?> residenceClass;
    private Map<String, StaticPropertyDescriptor> propertyDescriptors;

    public StaticPropertyIntrospector(Object object, Class<?> residenceClass) {
        assert object != null;
        this.object = object;
        this.residenceClass = residenceClass;
        this.propertyDescriptors = new HashMap<>();
    }

    public Object getTargetObject() {
        return object;
    }

    public Class<?> getResidenceClass() {
        return residenceClass;
    }

    public Object getValue(String propertyName) {
        final StaticPropertyDescriptor d = propertyDescriptors.computeIfAbsent(propertyName, k -> findDescriptor(propertyName));
        final Object result;

        if (d != null) {
            try {
                result = d.getReadMethod().invoke(null, object);
            } catch(InvocationTargetException|IllegalAccessException x) {
                throw new RuntimeException(x);
            }
        } else {
            throw new RuntimeException(propertyName + " not found"); //NOCHECK
        }

        return result;
    }

    public void setValue(String propertyName, Object value) {
        // So far we have no use for this : we'll implement when needed.
        throw new UnsupportedOperationException("Not yet implemented"); //NOCHECK
    }

    private StaticPropertyDescriptor findDescriptor(String propertyName) {
        assert propertyDescriptors != null;
        try {
            return new StaticPropertyDescriptor(propertyName, residenceClass);
        } catch (IntrospectionException e) {
            return null;
        }
    }

}
