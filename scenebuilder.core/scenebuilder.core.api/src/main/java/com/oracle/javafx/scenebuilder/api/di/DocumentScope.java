/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.di;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;

import javafx.application.Platform;

/**
 * The Class DocumentScope is a Spring scope.
 * The scope owner is a bean named {@link #SCOPE_OBJECT_NAME}
 * The scoped document change when a new bean named {@link #SCOPE_OBJECT_NAME} is instantiated and currentScope is null
 * So to create a new scope DocumentScope.setCurrentScope(null) must be called before context.getBean(Document.class)
 * The scoped document change when a Document window gain focus
 * The document scope is removed when a Document window is closed
 */
public class DocumentScope implements Scope {

    private static final Logger logger = LoggerFactory.getLogger(DocumentScope.class);

    private static final String SCOPE_CHANGE_MSG = "DocumentScope changed to : %s - %s (unused: %s, dirty: %s, content: %s, name %s)";

    /** The Constant SCOPE_OBJECT_NAME. Creation of bean named with SCOPE_OBJECT_NAME will create a new {@link DocumentScope} */
    public static final String SCOPE_OBJECT_NAME = "documentController";

    /** The Constant MDC_SCOPE_NAME used for mapped diagnostic context (slf4j MDC). */
    protected static final String MDC_SCOPE_NAME = "scope";

    /** The current scope id. */
    private static UUID currentScope;

    /** The temporary thread scope id. */
    private static ThreadLocal<UUID> threadScope = new ThreadLocal<>();

    /** Map {@link DocumentWindow} to scopes id. */
    private static Map<Document, UUID> scopesId = new ConcurrentHashMap<>();

    /** Map scopes id to bean instances. */
    private static Map<UUID, Map<String, Object>> scopes = new ConcurrentHashMap<>();

    /**
     * Gets the active scope taking into account thread local scope that may be active
     * return null during the scope creation
     *
     */
    public static Document getActiveScope() {
        UUID threadScopeUuid = threadScope.get();
        UUID activeScope = threadScopeUuid != null ? threadScopeUuid : currentScope;
        return (Document)scopes.get(activeScope).get(SCOPE_OBJECT_NAME);
    }

    /**
     * Gets the current scope ignoring any thread local scope that may be active
     * return null during the scope creation
     *
     */
    public static Document getCurrentScope() {
        return (Document)scopes.get(currentScope).get(SCOPE_OBJECT_NAME);
    }

    /**
     * Gets the active scope uuid taking into account thread local scope that may be active
     *
     */
    public static UUID getActiveScopeUUID() {
        UUID threadScopeUuid = threadScope.get();
        UUID activeScope = threadScopeUuid != null ? threadScopeUuid : currentScope;
        return activeScope;
    }

    /**
     * Gets the current scope uuid ignoring any thread local scope that may be active
     *
     */
    public static UUID getCurrentScopeUUID() {
        return currentScope;
    }

    /**
     * Sets the current scope.
     *
     * @param scopedDocument the new current scope
     */
    public static void setCurrentScope(Document scopedDocument) {
        if (scopedDocument == null) {
            if (currentScope != null) {
                currentScope = null;
                MDC.put(MDC_SCOPE_NAME, "");
                logger.info("DocumentScope is null");
            }
            return;
        }
        if (!scopesId.containsKey(scopedDocument)) {
            scopesId.put(scopedDocument, UUID.randomUUID());
        }
        UUID scopeId = scopesId.get(scopedDocument);
        if (!scopes.containsKey(scopeId)) {
            scopes.put(scopeId, new ConcurrentHashMap<>());
        }
        if (DocumentScope.currentScope != scopeId) {
            DocumentScope.currentScope = scopeId;
            MDC.put(MDC_SCOPE_NAME, currentScope.toString());

            if (scopedDocument.isInited()) {
                logger.info(
                        String.format(SCOPE_CHANGE_MSG, scopeId, scopedDocument, scopedDocument.isUnused(),
                        scopedDocument.isDocumentDirty(), scopedDocument.hasName(), scopedDocument.getName()));
            } else {
                logger.info(
                        String.format(SCOPE_CHANGE_MSG, scopeId, scopedDocument, "", "", "", ""));
            }
        }
    }

    /**
     * Execute the runnable later on the fx thread
     * @param scopedDocument the document scope
     * @param runnable the code to run
     */
    protected static void executeLaterWithScope(Document scopedDocument, Runnable runnable) {
        UUID documentUuid = scopesId.get(scopedDocument);
        executeLaterWithScope(documentUuid, runnable);
    }
    /**
     * Execute the runnable later on the fx thread
     * @param scopedDocument the document scope uuid
     * @param runnable the code to run
     */
    protected static void executeLaterWithScope(UUID scopedDocument, Runnable runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");//NOCHECK
        }
        Platform.runLater(() -> {
            executeRunnable(runnable, scopedDocument);
        });
    }

    /**
     * Execute the runnable on the same thread ensuring an unchanging scope
     * @param scopedDocument the document scope
     * @param runnable the code to run
     */
    protected static void executeWithScope(Document scopedDocument, Runnable runnable) {
        UUID documentUuid = scopesId.get(scopedDocument);
        executeWithScope(documentUuid, runnable);
    }
    protected static <T> T executeWithScope(Document scopedDocument, Supplier<T> runnable) {
        UUID documentUuid = scopesId.get(scopedDocument);
        return executeWithScope(documentUuid, runnable);
    }

    /**
     * Execute the runnable on the same thread ensuring an unchanging scope
     * @param scopedDocument the document scope uuid
     * @param runnable the code to run
     */
    protected static void executeWithScope(UUID scopedDocument, Runnable runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");//NOCHECK
        }
        executeRunnable(runnable, scopedDocument);
    }
    protected static <T> T executeWithScope(UUID scopedDocument, Supplier<T> runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");//NOCHECK
        }
        return executeSupplier(runnable, scopedDocument);
    }


    /**
     * Execute the runnable on a dedicated thread ensuring an unchanging scope
     * @param scopedDocument the document scope
     * @param runnable the code to run
     */
    protected static void executeOnThreadWithScope(Document scopedDocument, Runnable runnable) {
        UUID documentUuid = scopesId.get(scopedDocument);
        executeOnThreadWithScope(documentUuid, runnable);

    }

    /**
     * Execute the runnable on a dedicated thread ensuring an unchanging scope
     * @param scopedDocument the document scope
     * @param runnable the code to run
     */
    protected static void executeOnThreadWithScope(UUID scopedDocument, Runnable runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");//NOCHECK
        }
        Thread t = new Thread(() -> {
            executeRunnable(runnable, scopedDocument);
        });
        t.run();
    }

    private static void executeRunnable(Runnable runnable, UUID scopedDocument) {
        final UUID backupScope = threadScope.get() == null ? currentScope : threadScope.get();

        try {
            threadScope.set(scopedDocument);
            MDC.put(MDC_SCOPE_NAME, scopedDocument.toString());
            runnable.run();
        } finally {
            threadScope.set(backupScope);
            MDC.put(MDC_SCOPE_NAME, backupScope == null ? "" : backupScope.toString());
        }
    }

    private static <T> T executeSupplier(Supplier<T> runnable, UUID scopedDocument) {
        final UUID backupScope = threadScope.get() == null ? currentScope : threadScope.get();

        try {
            threadScope.set(scopedDocument);
            MDC.put(MDC_SCOPE_NAME, scopedDocument.toString());
            return runnable.get();
        } finally {
            threadScope.set(backupScope);
            MDC.put(MDC_SCOPE_NAME, backupScope == null ? "" : backupScope.toString());
        }
    }

    /**
     * Removes the scope and all the associated instances.
     *
     * @param document the document
     */
    public static void removeScope(Document document) {
        logger.debug("REMOVING SCOPE " + document);
        UUID scopeId = scopesId.get(document);
        if (currentScope == scopeId) {
            currentScope = null;
            MDC.put(MDC_SCOPE_NAME, "");
        }
        scopes.remove(scopeId);
        scopesId.remove(document);
    }

    /**
     * Instantiates a new document scope.
     */
    public DocumentScope() {
        super();
    }

    /**
     * Get a Document scoped bean.
     *
     * @param name the bean name to instantiate
     * @param objectFactory the object factory
     * @return the instantiated bean
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {

        if (SCOPE_OBJECT_NAME.equals(name) && ((currentScope == null)
                || (currentScope != null && !scopes.get(currentScope).containsKey(name)))) {
            // new document, so create a new document scope
            // Using an UUID as scope key instead of a Document to allow bean creation
            // while the Document instantiation is ongoing
            UUID scopeId = UUID.randomUUID();
            scopes.put(scopeId, new ConcurrentHashMap<>());
            currentScope = scopeId;
            MDC.put(MDC_SCOPE_NAME, currentScope.toString());
            logger.info(String.format(SCOPE_CHANGE_MSG, scopeId, null, "", "", "", ""));

            Document scopeDocument = (Document) objectFactory.getObject();

            scopesId.put(scopeDocument, scopeId);
            setCurrentScope(scopeDocument);

            Map<String, Object> scopedObjects = scopes.get(currentScope);
            scopedObjects.put(name, scopeDocument);
            return scopeDocument;

        } else {
            // simple bean instantiation or retrieve it from the existing beans
            if (currentScope == null) {
                logger.error("Scope null is invalid for bean {}", name);
            }

            assert currentScope != null;

            UUID threadScopeUuid = threadScope.get();
            UUID activeScope = threadScopeUuid != null ? threadScopeUuid : currentScope;

            Map<String, Object> scopedObjects = scopes.get(activeScope);
            if (!scopedObjects.containsKey(name)) {
                scopedObjects.put(name, objectFactory.getObject());
            }
            return scopedObjects.get(name);
        }
    }

    /**
     * Gets the conversation id.
     *
     * @return the conversation id
     */
    @Override
    public String getConversationId() {
        return Thread.currentThread().getName();
    }

    /**
     * Register destruction callback.
     *
     * @param name the name
     * @param callback the callback
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        //not implemented
    }

    /**
     * Removes bean instance by name from the current DocumentScope.
     *
     * @param name the bean name
     * @return the deleted bean instance
     */
    @Override
    public Object remove(String name) {
        return scopes.get(currentScope).remove(name);
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

}