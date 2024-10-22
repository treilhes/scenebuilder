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
package com.gluonhq.jfxapps.boot.api.context;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.api.layer.Layer;

public class ContextConfiguration {
    UUID parentContextId;
    Layer layer;
    Set<Class<?>> classes;
    Set<Class<?>> deportedClasses;
    List<Object> singletonInstances;
    MultipleProgressListener progressListener;

    public UUID getParentContextId() {
        return parentContextId;
    }
    public void setParentContextId(UUID parentContextId) {
        this.parentContextId = parentContextId;
    }
    public Layer getLayer() {
        return layer;
    }
    public void setLayer(Layer layer) {
        this.layer = layer;
    }
    public Set<Class<?>> getClasses() {
        return classes;
    }
    public void setClasses(Set<Class<?>> classes) {
        this.classes = classes;
    }
    public Set<Class<?>> getDeportedClasses() {
        return deportedClasses;
    }
    public void setDeportedClasses(Set<Class<?>> deportedClasses) {
        this.deportedClasses = deportedClasses;
    }
    public List<Object> getSingletonInstances() {
        return singletonInstances;
    }
    public void setSingletonInstances(List<Object> singletonInstances) {
        this.singletonInstances = singletonInstances;
    }
    public MultipleProgressListener getProgressListener() {
        return progressListener;
    }
    public void setProgressListener(MultipleProgressListener progressListener) {
        this.progressListener = progressListener;
    }

}
