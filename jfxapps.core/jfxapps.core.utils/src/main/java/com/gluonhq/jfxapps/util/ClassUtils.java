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
package com.gluonhq.jfxapps.util;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;

public final class ClassUtils {

    private ClassUtils(){}

    public static <T> Class<?> findSharedBaseClass(Collection<T> objects, Function<T, Class<?>> getClassFunc){
        Deque<Class<?>> orderedBaseClasses = new LinkedList<>();

        for (T o:objects) {
            Class<?> cls = getClassFunc.apply(o);

            if (cls == null) {
                // null object, no need to check there is no base class
                return null;
            }
            if (orderedBaseClasses.isEmpty()) {
                // first class found, fill the hierarchy
                while (cls != null) {
                    orderedBaseClasses.addFirst(cls);
                    cls = cls.getSuperclass();
                }
            } else if (orderedBaseClasses.size() == 1) {
                // nothing to do the remaining object in list is Object.class
                // if no need to check for null then the function can end here
            } else {
                // for other class, find the first assignable parent
                Iterator<Class<?>> it = orderedBaseClasses.descendingIterator(); // get the last item

                while (!it.next().isAssignableFrom(cls)) {
                    it.remove();
                }
            }
        }

        return orderedBaseClasses.getLast();
    }
}
