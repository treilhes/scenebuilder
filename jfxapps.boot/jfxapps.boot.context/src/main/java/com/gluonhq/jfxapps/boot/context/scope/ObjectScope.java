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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.Scope;

import com.gluonhq.jfxapps.boot.context.impl.JfxAppContextImpl.SbBeanFactoryImpl;

/**
 * The Class ApplicationScope is a Spring scope. The scope owner is a bean named
 * {@link #SCOPE_OBJECT_NAME} The scoped application change when a new bean named
 * {@link #SCOPE_OBJECT_NAME} is instantiated and currentScope is null So to
 * create a new scope {@link #unbindScope()} {@link #setCurrentScope(null)}  must be called before
 * context.getBean(Application.class) The scoped application change when an Application
 * window gain focus The application scope is removed when the ApplicationInstance window are
 * closed
 */

public abstract class ObjectScope<P, O, D> implements Scope {

    private static final Logger logger = LoggerFactory.getLogger(ObjectScope.class);

    private final Class<P> parentScopeClass;

    private final Class<O> scopeClass;

    //private final String scopeName;

    /** The scope holder */
    private final ObjectScopeHolder<P, O, D> scopeHolder;

    /** Map scopes id to bean instances. */
    private final Map<ScopeContext<ObjectScope<P, O, D>, P, O, D>, ScopedData> scopeContextToData;

    private final SbBeanFactoryImpl beanFactory;


    /**
     * Instantiates a new document scope.
     */
    protected ObjectScope(Class<P> parentScopeClass, Class<O> scopeClass, SbBeanFactoryImpl sbBeanFactoryImpl, ObjectScopeHolder<P, O, D> scopeHolder) {
        super();
        this.parentScopeClass = parentScopeClass;
        this.scopeClass = scopeClass;
        this.beanFactory = sbBeanFactoryImpl;
        this.scopeHolder = scopeHolder;
        this.scopeContextToData = new ConcurrentHashMap<>();
    }


    /**
     * Get an application scoped bean
     *
     * @param name          the bean name to instantiate
     * @param objectFactory the object factory
     * @return the instantiated bean
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {

        BeanDefinition definition = beanFactory.getMergedBeanDefinition(name);
        Class<?> rawClass = definition.getResolvableType().getRawClass();
        boolean isScopeMaster = scopeClass.isAssignableFrom(rawClass);

        P parentScopeObject = null;
        if (scopeHolder.hasParentScope()) {
            var parentScope = scopeHolder.getParentScopeHolder().getCurrentScope();
            if (parentScope == null) {
                throw new NullScopeException("parent scope null");
            } else {
                parentScopeObject = parentScope.getScopedObject();
            }
        }

        var currentScope = scopeHolder.getCurrentScope();

        if (isScopeMaster && ((currentScope == null) || (currentScope != null
                && !scopeContextToData.computeIfAbsent(currentScope, k -> new ScopedData()).containsBean(name)))) {

            // new document, so create a new document scope
            // Using an UUID as scope key instead of a Document to allow bean creation
            // while the Document instantiation is ongoing
            var scope = new ScopeContext<>(this);

            scopeContextToData.put(scope, new ScopedData());

            scopeHolder.registerScope(scope);

            @SuppressWarnings("unchecked")
            var beanObject = (O) objectFactory.getObject();
            var scopeObject = beanObject;

            scopeObject = unproxyIfNeeded(scopeObject);

            scope.setScopedObject(scopeObject);
            scope.setParentScopedObject(parentScopeObject);
            scope.addScopeInstance(this);

            scopeHolder.registerScopedObject(scope, scopeObject);

            ScopedData scopedObjects = scopeContextToData.get(scopeHolder.getCurrentScope());
            scopedObjects.putBean(name, scopeObject);
            return beanObject;

        } else {

            var activeScope = scopeHolder.getActiveScope();

            if (activeScope == null) {
                throw new NullScopeException(String.format("Scope null is invalid for bean %s", name));
            }

            // simple bean instantiation or retrieve it from the existing beans
            ScopedData scopedObjects = scopeContextToData.computeIfAbsent(activeScope, k -> new ScopedData());

            Object bean = scopedObjects.getBean(name);

            if (bean == null) {
                if (logger.isDebugEnabled()){
                    logger.debug("Creating bean {} in scope {}", name, activeScope.getId());
                }

                bean = objectFactory.getObject();
                scopedObjects.putBean(name, bean);

                if (bean != null) {
                    activeScope.addScopeInstance(this);
                }
            }
            return bean;
        }
    }


    @SuppressWarnings("unchecked")
    private O unproxyIfNeeded(O scopeObject) {
        if (scopeObject instanceof Advised advised) {
            TargetSource targetSource = advised.getTargetSource();
            try {
                scopeObject = (O)targetSource.getTarget();
            } catch (Exception e) {
                logger.error("unproxying failure", e);
            }
        }
        return scopeObject;
    }

    /**
     * Gets the conversation id.
     *
     * @return the conversation id
     */
    @Override
    public String getConversationId() {
        return toId(scopeHolder.getActiveScope());
    }

    /**
     * Register destruction callback.
     *
     * @param name     the name
     * @param callback the callback
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        scopeContextToData.get(scopeHolder.getCurrentScope()).putDestructionCallbacks(name, callback);
    }

    /**
     * Removes bean instance by name from the current DocumentScope.
     *
     * @param name the bean name
     * @return the deleted bean instance
     */
    @Override
    public Object remove(String name) {

        if (name == null || name.isEmpty()) {
            return null;
        }

        var currentScope = scopeHolder.getCurrentScope();
        var scopeData = scopeContextToData.get(currentScope);
        var destructedObject = scopeData.removeBean(name);
        var destructedObjectClass = destructedObject != null ? destructedObject.getClass() : null;

        if (destructedObjectClass != null && destructedObjectClass.isAssignableFrom(scopeClass)) {
            var castedObject = scopeClass.cast(destructedObject);
            scopeHolder.removeScope(castedObject);
        }

        return destructedObject;
    }

    public void removeScope(ScopeContext<ObjectScope<P, O, D>, P, O, D> scope) {
        if (scope == null) {
            return;
        }

        var scopedData = scopeContextToData.remove(scope);

        if (scopedData != null) {
            scopedData.removeAllBean();
        }
    }

    /**
     * Resolve contextual object.
     *
     * @param arg0 the arg 0
     * @return the object
     */
    @Override
    public Object resolveContextualObject(String arg0) {
        return null;
    }

    public Set<ScopeContext<ObjectScope<P, O, D>, P, O, D>> getAllContext() {
        return scopeContextToData.keySet();
    }

    static String toId(ScopeContext<?,?,?,?> scope) {
        return scope == null ? null : scope.getId().toString();
    }

}