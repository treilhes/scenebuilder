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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.gluonhq.jfxapps.boot.api.context.Application;
import com.gluonhq.jfxapps.boot.api.context.ApplicationInstance;
import com.gluonhq.jfxapps.boot.api.context.ScopedExecutor;

public abstract class ObjectScopeHolder<P, O, D> implements ScopedExecutor<O> {

    private static final Logger logger = LoggerFactory.getLogger(ObjectScopeHolder.class);

    private static final String SCOPE_CREATED_MSG = "{} scope created : {} - {}";
    private static final String SCOPE_CHANGE_MSG = "{} scope changed : {} - {}";
    private static final String SCOPE_REMOVED_MSG = "{} scope removed : {} - {}";
    private static final String SCOPE_UNBOUND_MSG = "{} scope unbound : {} - {}";

    /**
     * The Constant MDC_SCOPE_NAME used for mapped diagnostic context (slf4j MDC).
     */
    private final String scopeName;

    /** The current scope id. */
    private ScopeContext<ObjectScope<P, O, D>, P, O, D> currentScope;

    /** The temporary thread scope id. */
    private final ThreadLocal<ScopeContext<ObjectScope<P, O, D>, P, O, D>> threadLocalContext;

    /** Map {@link ApplicationInstance} to scopes id. */
    private final Map<O, ScopeContext<ObjectScope<P, O, D>, P, O, D>> scopeObjectToContext = new ConcurrentHashMap<>();

    /** Map scopes id to {@link Application} . */
    private final Map<UUID, ScopeContext<ObjectScope<P, O, D>, P, O, D>> idToContext = new ConcurrentHashMap<>();

    private final ObjectScopeHolder<?, P, O> parentScopeHolder;

    private final Set<ObjectScopeHolder<O, D, ?>> dependentScopeHolders = new HashSet<>();

    protected ObjectScopeHolder(ObjectScopeHolder<?, P, O> parent, String scopeName, ThreadLocal<ScopeContext<ObjectScope<P, O, D>, P, O, D>> threadLocalContext) {
        super();
        this.parentScopeHolder = parent;
        this.scopeName = scopeName;
        this.threadLocalContext = threadLocalContext;
    }

    void registerDependentScopeHolder(ObjectScopeHolder<O, D, ?> dependentScopeHolder) {
        dependentScopeHolders.add(dependentScopeHolder);
    }
    /**
     * Sets the current scope.
     *
     * @param scopedApplication the new current scope
     */
    @Override
    public void setCurrentScope(O scopedObject) {

        if (scopedObject != null && !scopeObjectToContext.containsKey(scopedObject)) {
            throw new InvalidScopeException("Unknown scope " + scopedObject);
        }

        unbindScope();

        if (scopedObject == null) {
            return;
        }

        setCurrentScopeInternal(scopedObject);

    }

    private void setCurrentScopeInternal(O scopedObject) {
        var scopeContext = scopeObjectToContext.get(scopedObject);

        if (currentScope != scopeContext) {
            if (scopeContext == null) {
                throw new NullScopeException("Scope null is invalid this step");
            }

            if (scopeContext.getParentScopedObject() != null) {
                parentScopeHolder.setCurrentScopeInternal(scopeContext.getParentScopedObject());
            }

            idToContext.putIfAbsent(scopeContext.getId(), scopeContext);

            MDC.put(scopeName, toId(scopeContext));
            if (logger.isInfoEnabled()) {
                logger.info(SCOPE_CHANGE_MSG, getClass().getSimpleName(), toId(scopeContext), scopeContext.getScopedObject());
            }

            currentScope = scopeContext;


        }
    }

    public Map<UUID, ScopeContext<ObjectScope<P, O, D>, P, O, D>> getAvailableScopes() {
        return new HashMap<>(idToContext);
    }

    /**
     * Gets the current scope ignoring any thread local scope that may be active
     * return null during the scope creation
     *
     */
    public ScopeContext<ObjectScope<P, O, D>, P, O, D> getCurrentScope() {
        return currentScope;
    }

    /**
     * Gets the active scope taking into account thread local scope that may be
     * active
     *
     */
    public ScopeContext<ObjectScope<P, O, D>, P, O, D> getActiveScope() {
        var threadScopeUuid = threadLocalContext.get();
        var activeScope = threadScopeUuid != null ? threadScopeUuid : currentScope;
        return activeScope;
    }

    public boolean hasActiveScope() {
        return getActiveScope()!= null;
    }

    @Override
    public void unbindScope() {
        if (currentScope != null) {
            for (var dependent:dependentScopeHolders) {
                dependent.unbindScope();
            }

            MDC.put(scopeName, "");
            if (logger.isInfoEnabled()) {
                logger.info(SCOPE_UNBOUND_MSG, getClass().getSimpleName(), toId(currentScope), currentScope.getScopedObject());
            }
            currentScope = null;
        }
    }

    public void clear() {
        MDC.put(scopeName, null);
        currentScope = null;
        idToContext.clear();
        scopeObjectToContext.clear();
        dependentScopeHolders.forEach(ObjectScopeHolder::clear);
    }

    /**
     * Removes the scope and all the associated instances.
     *
     * @param application the application
     */
    @Override
    public void removeScope(O scopedObject) {

        if (scopedObject == null || !scopeObjectToContext.containsKey(scopedObject)) {
            return;
        }

        var scopeContext = scopeObjectToContext.get(scopedObject);

        idToContext.remove(scopeContext.getId());
        scopeObjectToContext.remove(scopeContext.getScopedObject());

        scopeContext.getScopeInstances().forEach(scope -> scope.removeScope(scopeContext));
        scopeContext.getScopeInstances().clear();

        scopeContext.getDependentScopedObjects().forEach(obj -> {
            dependentScopeHolders.forEach(dependentScopeHolder -> dependentScopeHolder.removeScope(obj));
        });

        scopeContext.getDependentScopedObjects().clear();

        if (currentScope == scopeContext) {
            currentScope = null;
            MDC.put(scopeName, "");
        }

        if (logger.isInfoEnabled()){
            logger.info(SCOPE_REMOVED_MSG, getClass().getSimpleName(), toId(scopeContext), scopedObject);
        }

    }

    @Override
    public void executeRunnable(Runnable runnable, UUID scopeId) {
        executeRunnable(runnable, getScope(scopeId));
    }
    public void executeRunnable(Runnable runnable, O scopeObject) {
        executeRunnable(runnable, getScope(scopeObject));
    }
    public void executeRunnable(Runnable runnable, ScopeContext<ObjectScope<P, O, D>, P, O, D> scope) {
        Runnable runMe = () -> {
            final var backupScope = threadLocalContext.get();

            try {
                threadLocalContext.set(scope);
                MDC.put(scopeName, toId(scope));
                runnable.run();
            } finally {
                threadLocalContext.set(backupScope);
                MDC.put(scopeName, toId(backupScope == null ? currentScope : backupScope));
            }
        };

        if (scope.getParentScopedObject() != null) {
            parentScopeHolder.executeRunnable(runMe, scope.getParentScopedObject());
        } else {
            runMe.run();
        }
    }

    @Override
    public <T> T executeSupplier(Supplier<T> runnable, UUID scopeId) {
        return executeSupplier(runnable, getScope(scopeId));
    }
    public <T> T executeSupplier(Supplier<T> runnable, O scopeObject) {
        return executeSupplier(runnable, getScope(scopeObject));
    }
    public <T> T executeSupplier(Supplier<T> runnable, ScopeContext<ObjectScope<P, O, D>, P, O, D> scope) {
        Supplier<T> runMe = () -> {
            final var backupScope = threadLocalContext.get();

            try {
                threadLocalContext.set(scope);
                MDC.put(scopeName, toId(scope));
                return runnable.get();
            } finally {
                threadLocalContext.set(backupScope);
                MDC.put(scopeName, toId(backupScope == null ? currentScope : backupScope));
            }
        };

        if (scope.getParentScopedObject() != null) {
            return parentScopeHolder.executeSupplier(runMe, scope.getParentScopedObject());
        } else {
            return runMe.get();
        }
    }

    public ScopeContext<ObjectScope<P, O, D>, P, O, D> getScope(O scopeObject) {
        return scopeObjectToContext.get(scopeObject);
    }

    public ScopeContext<ObjectScope<P, O, D>, P, O, D> getScope(UUID scopeId) {
        return idToContext.get(scopeId);
    }

    public void registerScope(ScopeContext<ObjectScope<P, O, D>, P, O, D> scope) {

        MDC.put(scopeName, toId(scope));

        if (logger.isInfoEnabled()) {
            logger.info(SCOPE_CREATED_MSG, getClass().getSimpleName(), toId(scope), scope.getScopedObject());
        }

        currentScope = scope;
        idToContext.put(scope.getId(), scope);
    }

    void registerScopedObject(ScopeContext<ObjectScope<P, O, D>, P, O, D> scope, O scopeObject) {
        scopeObjectToContext.put(scopeObject, scope);
        if (parentScopeHolder != null) {
            parentScopeHolder.registerDependentScopedObject(scopeObject);
        }
    }

    O registerDependentScopedObject(D dependentScopeObject) {
        var scopeContext  = getActiveScope();
        scopeContext.addDependentScopedObject(dependentScopeObject);
        return scopeContext.getScopedObject();
    }

    ObjectScopeHolder<?, P, O> getParentScopeHolder() {
        return parentScopeHolder;
    }

    public boolean hasParentScope() {
        return parentScopeHolder != null;
    }

    static String toId(ScopeContext<?,?,?,?> scope) {
        return scope == null ? null : scope.getId().toString();
    }

    @Override
    public UUID getActiveScopeId() {
        if (hasActiveScope()) {
            return getActiveScope().getId();
        }
        return null;
    }

    @Override
    public UUID getScopeId(O object) {
        var scope = getScope(object);
        return scope == null ? null : scope.getId();
    }

    @Override
    public O getCurrentScopedObject() {
        return getCurrentScope().getScopedObject();
    }

}