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
package com.gluonhq.jfxapps.metadata.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionUtils {

    private static final Map<Class<?>, Object> primitiveDefaultValue = new HashMap<>();
    static {
        primitiveDefaultValue.put(Boolean.TYPE, false);
        primitiveDefaultValue.put(Byte.TYPE, (byte) 0);
        primitiveDefaultValue.put(Character.TYPE, '\u0000');
        primitiveDefaultValue.put(Short.TYPE, (short) 0);
        primitiveDefaultValue.put(Integer.TYPE, 0);
        primitiveDefaultValue.put(Long.TYPE, 0L);
        primitiveDefaultValue.put(Double.TYPE, 0.0);
        primitiveDefaultValue.put(Float.TYPE, 0.0f);
        primitiveDefaultValue.put(Void.TYPE, null);
    }

    private static final Map<Class<?>, String> primitiveDeclarationFormat = new HashMap<>();
    static {
        primitiveDeclarationFormat.put(Boolean.TYPE, "%s");
        primitiveDeclarationFormat.put(Byte.TYPE, "%s");
        primitiveDeclarationFormat.put(Character.TYPE, "'%s'");
        primitiveDeclarationFormat.put(Short.TYPE, "%s");
        primitiveDeclarationFormat.put(Integer.TYPE, "%s");
        primitiveDeclarationFormat.put(Long.TYPE, "%sL");
        primitiveDeclarationFormat.put(Double.TYPE, "%sd");
        primitiveDeclarationFormat.put(Float.TYPE, "%sf");
        primitiveDeclarationFormat.put(String.class, "\"%s\"");
        primitiveDeclarationFormat.put(Void.TYPE, null);
    }

    public static Object getPrimitiveDefaultValue(Class<?> cls) {
        return primitiveDefaultValue.get(cls);
    }

    public static String getPrimitiveDeclaration(Class<?> cls, Object value) {
        return String.format(primitiveDeclarationFormat.get(cls), value);
    }

    public static String findStaticMemberByValue(Class<?> holder, Object value) {
        for (Field field : holder.getDeclaredFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers()) && field.get(null).equals(value)) {
                    return holder.getName() + "." + field.getName();
                }
            } catch (Exception e) {
            }
        }
        return holder.getEnclosingClass() == null ? null : findStaticMemberByValue(holder.getEnclosingClass(), value);
    }

    public static String findStaticMemberByType(Class<?> holder, Class<?> type) {
        for (Field field : holder.getDeclaredFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers()) && field.get(null) != null
                        && field.get(null).getClass().equals(type)) {
                    return holder.getName() + "." + field.getName();
                }
            } catch (Exception e) {
            }
        }
        return holder.getEnclosingClass() == null ? null : findStaticMemberByType(holder.getEnclosingClass(), type);
    }

    public static Object findStaticMemberValueByType(Class<?> holder, Class<?> type) {
        for (Field field : holder.getDeclaredFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers()) && field.get(null) != null
                        && field.get(null).getClass().equals(type)) {
                    return field.get(null);
                }
            } catch (Exception e) {
            }
        }
        return holder.getEnclosingClass() == null ? null
                : findStaticMemberValueByType(holder.getEnclosingClass(), type);
    }

    public static String findStaticGetMethodByValue(Class<?> holder, Object value) {
        for (Method method : holder.getDeclaredMethods()) {
            try {
                if (Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0
                        && method.invoke(null).equals(value)) {
                    return holder.getName() + "." + method.getName() + "()";
                }
            } catch (Exception e) {
            }
        }
        return holder.getEnclosingClass() == null ? null
                : findStaticGetMethodByValue(holder.getEnclosingClass(), value);
    }

    public static Object[] convertToObjectArray(Object array) {
        Class<?> ofArray = array.getClass().getComponentType();
        if (ofArray.isPrimitive()) {
            List<Object> ar = new ArrayList<>();
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                ar.add(Array.get(array, i));
            }
            return ar.toArray();
        } else {
            return (Object[]) array;
        }
    }

    public static List<Class<?>> findGenericTypes(Class<?> owner, String fieldName)
            throws NoSuchFieldException, SecurityException {

        List<Class<?>> result = new ArrayList<>();
        Field field = findField(owner, fieldName);

        if (field == null) {
            return null;
        }
        Type type = field.getGenericType();

        if (type instanceof ParameterizedType) {

            ParameterizedType pType = (ParameterizedType) type;
            Type[] arr = pType.getActualTypeArguments();

            for (Type tp : arr) {
                if (ParameterizedType.class.isAssignableFrom(tp.getClass())) {
                    ParameterizedType prmType = ParameterizedType.class.cast(tp);
                    result.add((Class<?>) prmType.getRawType());
                } else {
                    result.add((Class<?>) tp);
                }
            }
            return result;
        }
        return null;
    }

    public static List<Class<?>> findGenericTypes(Method method) {

        List<Class<?>> result = new ArrayList<>();
        Type type = method.getGenericReturnType();

        if (type instanceof ParameterizedType) {

            ParameterizedType pType = (ParameterizedType) type;
            Type[] arr = pType.getActualTypeArguments();

            for (Type tp : arr) {
                result.add(findClass(tp));
            }
            return result;
        }
        return null;
    }

    private static Class<?> findClass(Type type) {
        if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            ParameterizedType prmType = ParameterizedType.class.cast(type);
            return (Class<?>) prmType.getRawType();
        } else if (WildcardType.class.isAssignableFrom(type.getClass())) {
            WildcardType wcType = WildcardType.class.cast(type);
            if (wcType.getUpperBounds().length > 0) {
                return findClass(wcType.getUpperBounds()[0]);
            } else if (wcType.getLowerBounds().length > 0) {
                return findClass(wcType.getLowerBounds()[0]);
            } else {
                return Object.class;
            }
        } else {
            return (Class<?>) type;
        }
    }

    public static Field findField(Class<?> owner, String fieldName) {

        Class<?> current = owner;

        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                if (field != null) {
                    return field;
                }
            } catch (Exception e) {
            }

            current = current.getSuperclass();
        }
        return null;
    }
}
