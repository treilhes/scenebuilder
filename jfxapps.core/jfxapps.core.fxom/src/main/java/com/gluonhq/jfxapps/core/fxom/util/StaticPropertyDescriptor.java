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

import static java.util.Locale.ENGLISH;

import java.beans.IntrospectionException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


public class StaticPropertyDescriptor {

    private Reference<? extends Class<?>> typeRef;
    private Reference<? extends Class<?>> propertyTypeRef;
    private final MethodRef readMethodRef = new MethodRef();
    private final MethodRef writeMethodRef = new MethodRef();

    // The base name of the method name which will be prefixed with the
    // read and write method. If name == "foo" then the baseName is "Foo"
    private String baseName;

    private String writeMethodName;
    private String readMethodName;

    public StaticPropertyDescriptor(String propertyName, Class<?> beanClass)
                throws IntrospectionException {
        if (beanClass == null) {
            throw new IntrospectionException("Target Bean class is null");
        }
        if (propertyName == null || propertyName.length() == 0) {
            throw new IntrospectionException("bad property name");
        }
        if ("".equals(readMethodName) || "".equals(writeMethodName)) {
            throw new IntrospectionException("read or write method name should not be the empty string");
        }
        baseName = propertyName;
        typeRef = new WeakReference<>(beanClass);

        if (!findStaticCoupleMethods()) {
            throw new IntrospectionException("Method not found: " + writeMethodName + "," + readMethodName);
        }

    }


    private boolean findStaticCoupleMethods() {
        Method readMethod = this.readMethodRef.get();
        Method writeMethod = this.writeMethodRef.get();
        if (readMethod == null && writeMethod == null) {
            Class<?> cls = typeRef.get();
            var capitilizedName = capitalize(baseName);

            var setterName = "set" + capitilizedName;
            var getterName = "get" + capitilizedName;
            var isName = "is" + capitilizedName;
            var hasName = "has" + capitilizedName;

            List<Method> readMethods = new ArrayList<>();
            List<Method> writeMethods = new ArrayList<>();

            // find static methods matching names and parameter count
            for (Method m:cls.getMethods()) {
                if (!Modifier.isStatic(m.getModifiers())) {
                    continue;
                }

                if (m.getName().equals(getterName) || m.getName().equals(isName) || m.getName().equals(hasName)) {
                    if (m.getParameterCount() != 1) {
                        continue;
                    }
                    readMethods.add(m);
                }
                if (m.getName().equals(setterName)) {
                    if (m.getParameterCount() != 2) {
                        continue;
                    }
                    writeMethods.add(m);
                }
            }

            if (readMethods.isEmpty() || writeMethods.isEmpty()) {
                return false;
            }

            Class<?> propertyType = null;
            for (Method read : readMethods) {
                for (Method write : writeMethods) {
                    if (read.getParameterTypes()[0].isAssignableFrom(write.getParameterTypes()[0])
                            && read.getReturnType().isAssignableFrom(write.getParameterTypes()[1])) {
                        readMethod = read;
                        writeMethod = write;
                        propertyType = read.getReturnType();
                        break;
                    }
                }
            }

            setReadMethod(readMethod);
            setWriteMethod(writeMethod);
            setPropertyType(propertyType);
            return true;
        }
        return false;
    }

    private void setPropertyType(Class<?> type) {
        this.propertyTypeRef = new WeakReference<>(type);
    }

    public Class<?> getPropertyType() {
        return (this.propertyTypeRef != null)
                ? this.propertyTypeRef.get()
                : null;
    }

    public Method getReadMethod() {
        return this.readMethodRef.get();
    }

    private void setReadMethod(Method readMethod) {
        this.readMethodRef.set(readMethod);
        if (readMethod == null) {
            readMethodName = null;
            return;
        }

        readMethodName = readMethod.getName();
    }

    /**
     * Gets the method that should be used to write the property value.
     *
     * @return The method that should be used to write the property value.
     * May return null if the property can't be written.
     */
    public synchronized Method getWriteMethod() {
        return this.writeMethodRef.get();
    }

    private void setWriteMethod(Method writeMethod) {
        this.writeMethodRef.set(writeMethod);
        if (writeMethod == null) {
            writeMethodName = null;
            return;
        }

        writeMethodName = writeMethod.getName();

    }

    /**
     * Returns the property type that corresponds to the read and write method.
     * The type precedence is given to the readMethod.
     *
     * @return the type of the property descriptor or null if both
     *         read and write methods are null.
     * @throws IntrospectionException if the read or write method is invalid
     */
    private Class<?> findPropertyType(Method readMethod, Method writeMethod)
        throws IntrospectionException {
        Class<?> propertyType = null;
        try {
            if (readMethod != null) {
                Class<?>[] params = readMethod.getParameterTypes();
                if (params.length != 0) {
                    throw new IntrospectionException("bad read method arg count: "
                                                     + readMethod);
                }
                propertyType = readMethod.getReturnType();
                if (propertyType == Void.TYPE) {
                    throw new IntrospectionException("read method " +
                                        readMethod.getName() + " returns void");
                }
            }
            if (writeMethod != null) {
                Class<?>[] params = writeMethod.getParameterTypes();
                if (params.length != 2) {
                    throw new IntrospectionException("bad write method arg count: "
                                                     + writeMethod);
                }
                if (propertyType != null && !params[0].isAssignableFrom(propertyType)) {
                    throw new IntrospectionException("type mismatch between read and write methods");
                }
                propertyType = params[0];
            }
        } catch (IntrospectionException ex) {
            throw ex;
        }
        return propertyType;
    }

    /**
     * Returns a String which capitalizes the first letter of the string.
     */
    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    public static class MethodRef {

        private String signature;
        private SoftReference<Method> methodRef;
        private WeakReference<Class<?>> typeRef;

        void set(Method method) {
            if (method == null) {
                this.signature = null;
                this.methodRef = null;
                this.typeRef = null;
            }
            else {
                this.signature = method.toGenericString();
                this.methodRef = new SoftReference<>(method);
                this.typeRef = new WeakReference<Class<?>>(method.getDeclaringClass());
            }
        }

        boolean isSet() {
            return this.methodRef != null;
        }

        Method get() {
            if (this.methodRef == null) {
                return null;
            }
            Method method = this.methodRef.get();
            if (method == null) {
                method = find(this.typeRef.get(), this.signature);
                if (method == null) {
                    this.signature = null;
                    this.methodRef = null;
                    this.typeRef = null;
                    return null;
                }
                this.methodRef = new SoftReference<>(method);
            }
            return method;
        }

        private static Method find(Class<?> type, String signature) {
            if (type != null) {
                for (Method method : type.getMethods()) {
                    if (type.equals(method.getDeclaringClass())) {
                        if (method.toGenericString().equals(signature)) {
                            return method;
                        }
                    }
                }
            }
            return null;
        }

    }
}
