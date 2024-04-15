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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScopeContext<S extends ObjectScope<P, O, D>, P, O, D> {
    private final UUID id = UUID.randomUUID();
    private P parentScopedObject;
    private O scopedObject;
    private final S scopeHolder;
    private final List<S> scopeInstances = new ArrayList<>();
    private final List<D> dependentScopedObjects = new ArrayList<>();

    public ScopeContext(S scopeHolder) {
        this.scopeHolder = scopeHolder;
    }

    public P getParentScopedObject() {
        return parentScopedObject;
    }

    void setParentScopedObject(P parentScopedObject) {
        this.parentScopedObject = parentScopedObject;
    }

    public O getScopedObject() {
        return scopedObject;
    }

    void setScopedObject(O application) {
        this.scopedObject = application;
    }

    public UUID getId() {
        return id;
    }

    public List<S> getScopeInstances() {
        return scopeInstances;
    }

    public void addScopeInstance(S scope) {
        if (!scopeInstances.contains(scope)) {
            scopeInstances.add(scope);
        }
    }

    public List<D> getDependentScopedObjects() {
        return dependentScopedObjects;
    }

    public void addDependentScopedObject(D dependentScopedObject) {
        if (!dependentScopedObjects.contains(dependentScopedObject)) {
            dependentScopedObjects.add(dependentScopedObject);
        }
    }

    public S getScopeHolder() {
        return scopeHolder;
    }

}
