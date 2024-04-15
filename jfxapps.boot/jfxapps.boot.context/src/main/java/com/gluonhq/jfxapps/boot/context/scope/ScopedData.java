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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ScopedData {
    /** Map bean name to bean instances. */
    private Map<String, Object> beans = new ConcurrentHashMap<>();

    /** Map bean name to destruction callback. */
    private Map<String, Runnable> destructionCallbacks = new ConcurrentHashMap<>();

    public ScopedData() {
        super();
    }

    protected void putBean(String beanName, Object instance) {
        beans.put(beanName, instance);
    }

    public boolean containsBean(String name) {
        return beans.containsKey(name);
    }

    protected Object getBean(String beanName) {
        return beans.get(beanName);
    }

    protected Object removeBean(String beanName) {
        return removeBean(beanName, true);
    }

    protected Object removeBean(String beanName, boolean executeCallback) {
        Object object = beans.remove(beanName);

        if (object != null) {
            Runnable callback = destructionCallbacks.remove(beanName);

            if (executeCallback && callback != null) {
                callback.run();
            }
        }

        return object;
    }

    protected void removeAllBean() {
        beans.keySet().forEach(b -> this.removeBean(b, true));
    }

    protected void putDestructionCallbacks(String beanName, Runnable callback) {
        destructionCallbacks.put(beanName, callback);
    }
}