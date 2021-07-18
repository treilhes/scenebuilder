package com.oracle.javafx.scenebuilder.core.di;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * The Class ThreadScope.
 */
public class ThreadScope implements Scope {

    /** The scoped objects thread local. */
    ScopedObjectsThreadLocal scopedObjectsThreadLocal = new ScopedObjectsThreadLocal();

    /**
     * Instantiates a new thread scope.
     */
    public ThreadScope() {
        super();
    }

    /**
     * Gets the.
     *
     * @param name the name
     * @param objectFactory the object factory
     * @return the object
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> scopedObjects = scopedObjectsThreadLocal.get();
        if (!scopedObjects.containsKey(name)) {
            scopedObjects.put(name, objectFactory.getObject());
        }
        return scopedObjects.get(name);
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

    }

    /**
     * Removes the.
     *
     * @param str the str
     * @return the object
     */
    @Override
    public Object remove(String str) {
        return scopedObjectsThreadLocal.get().remove(str);
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

    /**
     * The Class ScopedObjectsThreadLocal.
     */
    class ScopedObjectsThreadLocal extends ThreadLocal<Map<String, Object>> {

        /**
         * Initial value.
         *
         * @return the map
         */
        @Override
        protected Map<String, Object> initialValue() {
            return Collections.synchronizedMap(new HashMap<String, Object>());
        }
    }

}