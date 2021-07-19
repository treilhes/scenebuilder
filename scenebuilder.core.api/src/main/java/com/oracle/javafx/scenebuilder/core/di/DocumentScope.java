package com.oracle.javafx.scenebuilder.core.di;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    /** The Constant SCOPE_OBJECT_NAME. */
    protected static final String SCOPE_OBJECT_NAME = "documentController";

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
     *
     */
    public static Document getActiveScope() {
        UUID threadScopeUuid = threadScope.get();
        UUID activeScope = threadScopeUuid != null ? threadScopeUuid : currentScope;
        return (Document)scopes.get(activeScope).get(SCOPE_OBJECT_NAME);
    }
    
    /**
     * Gets the current scope ignoring any thread local scope that may be active
     *
     */
    public static Document getCurrentScope() {
        return (Document)scopes.get(currentScope).get(SCOPE_OBJECT_NAME);
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
     * Sets the current scope.
     *
     * @param scopedDocument the new current scope
     */
    protected static void executeLaterWithScope(Document scopedDocument, Runnable runnable) {
        UUID backupScope = threadScope.get();
        UUID documentUuid = scopesId.get(scopedDocument);
        if (documentUuid == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");//NOCHECK
        }
        Platform.runLater(() -> {
            try {
                threadScope.set(documentUuid);
                runnable.run();
            } finally {
                threadScope.set(backupScope);
            }
        });
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
            logger.info(String.format(SCOPE_CHANGE_MSG, scopeId, null, "", "", "", ""));

            Document scopeDocument = (Document) objectFactory.getObject();

            scopesId.put(scopeDocument, scopeId);
            setCurrentScope(scopeDocument);

            Map<String, Object> scopedObjects = scopes.get(currentScope);
            scopedObjects.put(name, scopeDocument);
            return scopeDocument;

        } else {
            // simple bean instantiation or retrieve it from the existing beans
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