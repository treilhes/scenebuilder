/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.api.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Document;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

/**
 * A factory for creating SceneBuilderBean objects.
 */
@Component
public class SceneBuilderBeanFactory {

    /**
     * Scope identifier for the standard singleton scope: {@value}.
     * <p>
     * Custom scopes can be added via {@code registerScope}.
     *
     * @see ConfigurableListableBeanFactory#registerScope
     */
    public static final String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

    /**
     * Scope identifier for the standard prototype scope: {@value}.
     * <p>
     * Custom scopes can be added via {@code registerScope}.
     *
     * @see ConfigurableListableBeanFactory#registerScope
     */
    public static final String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    /**
     * Scope identifier for the custom document scope: {@value}.
     * <p>
     * Custom scopes can be added via {@code registerScope}.
     *
     * @see ConfigurableListableBeanFactory#registerScope
     */
    public static final String SCOPE_DOCUMENT = "document";

    /**
     * Scope identifier for the custom thread scope: {@value}.
     * <p>
     * Custom scopes can be added via {@code registerScope}.
     *
     * @see ConfigurableListableBeanFactory#registerScope
     */
    public static final String SCOPE_THREAD = "thread";

    /** The context. */
    @Autowired
    ApplicationContext context;

    /**
     * Instantiates a new scene builder bean factory.
     */
    public SceneBuilderBeanFactory() {
    }

    /**
     * Gets the.
     *
     * @param <C> the generic type
     * @param cls the cls
     * @return the c
     */
    public <C> C get(Class<C> cls) {
        return context.getBean(cls);
    }

    /**
     * The Class SceneBuilderBeanFactoryPostProcessor.
     */
    @Component
    public static class SceneBuilderBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

        /**
         * Instantiates a new scene builder bean factory post processor.
         */
        public SceneBuilderBeanFactoryPostProcessor() {
            super();
        }

        /**
         * Post process bean factory.
         *
         * @param factory the factory
         * @throws BeansException the beans exception
         */
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
            factory.registerScope(SCOPE_DOCUMENT, new DocumentScope());
            factory.registerScope(SCOPE_THREAD, new ThreadScope());
            factory.addBeanPostProcessor(new FxmlControllerBeanPostProcessor());

            DefaultListableBeanFactory bf = (DefaultListableBeanFactory) factory;
            bf.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver() {

                @Override
                protected Object buildLazyResolutionProxy(DependencyDescriptor descriptor, String beanName) {
                    TargetSource ts = new TargetSource() {
                        private Object savedTarget = null;

                        @Override
                        public Class<?> getTargetClass() {
                            return descriptor.getDependencyType();
                        }

                        @Override
                        public boolean isStatic() {
                            return false;
                        }

                        @Override
                        public Object getTarget() {
                            if (savedTarget != null) {
                                return savedTarget;
                            }
                            Set<String> autowiredBeanNames = (beanName != null ? new LinkedHashSet<>(1) : null);
                            Object target = bf.doResolveDependency(descriptor, beanName, autowiredBeanNames, null);
                            if (target == null) {
                                Class<?> type = getTargetClass();
                                if (Map.class == type) {
                                    return Collections.emptyMap();
                                } else if (List.class == type) {
                                    return Collections.emptyList();
                                } else if (Set.class == type || Collection.class == type) {
                                    return Collections.emptySet();
                                }
                                throw new NoSuchBeanDefinitionException(descriptor.getResolvableType(),
                                        "Optional dependency not present for lazy injection point");
                            }
                            if (autowiredBeanNames != null) {
                                for (String autowiredBeanName : autowiredBeanNames) {
                                    if (bf.containsBean(autowiredBeanName)) {
                                        bf.registerDependentBean(autowiredBeanName, beanName);
                                    }
                                }
                            }
                            savedTarget = target;
                            return target;
                        }

                        @Override
                        public void releaseTarget(Object target) {
                        }
                    };
                    ProxyFactory pf = new ProxyFactory();
                    pf.setTargetSource(ts);
                    Class<?> dependencyType = descriptor.getDependencyType();
                    if (dependencyType.isInterface()) {
                        pf.addInterface(dependencyType);
                    }
                    return pf.getProxy(bf.getBeanClassLoader());
                }

            });
        }
    }

    /**
     * The Class FxmlControllerBeanPostProcessor.
     */
    public static class FxmlControllerBeanPostProcessor implements BeanPostProcessor {

        /**
         * Instantiates a new fxml controller bean post processor.
         */
        public FxmlControllerBeanPostProcessor() {
            super();
        }

        /**
         * This implementation loads the FXML file using the URL and ResourceBundle
         * passed by {@link FxmlController} if the bean is an instance of
         * {@link FxmlController}. This method can be invoked outside of the JavaFX
         * thread
         *
         * @param bean     the bean
         * @param beanName the bean name
         * @return the bean binded to the fxml
         * @throws BeansException the beans exception
         * @throws RuntimeException exception thrown when the fxml file failed to load
         */
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            bean = BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

            if (FxmlController.class.isAssignableFrom(bean.getClass())) {
                FxmlController controller = (FxmlController) bean;
                FXMLLoader loader = new FXMLLoader();
                loader.setController(controller);
                loader.setLocation(controller.getFxmlURL());
                loader.setResources(controller.getResources());
                loader.setClassLoader(bean.getClass().getClassLoader());
                try {
                    controller.setRoot((Parent) loader.load());
                    controller.controllerDidLoadFxml();
                } catch (RuntimeException | IOException x) {
                    throw new RuntimeException(
                            String.format("Failed to load %s with %s",
                                    loader.getLocation(), loader.getController()), x); // NOI18N
                }
            }

            return bean;
        }
    }

    /**
     * The Class DocumentScope is a Spring scope.
     * The scoped document change when a new Document window is instantiated
     * The scoped document change when a Document window gain focus
     * The document scope is removed when a Document window is closed
     */
    public static class DocumentScope implements Scope {

        /** The Constant SCOPE_OBJECT_NAME. */
        private static final String SCOPE_OBJECT_NAME = "documentWindowController";

        /** The current scope id. */
        private static UUID currentScope;

        /** Map {@link Document} to scopes id. */
        private static Map<Document, UUID> scopesId = new ConcurrentHashMap<>();

        /** Map scopes id to bean instances. */
        private static Map<UUID, Map<String, Object>> scopes = new ConcurrentHashMap<>();

        /**
         * Sets the current scope.
         *
         * @param scopedDocument the new current scope
         */
        public static synchronized void setCurrentScope(Document scopedDocument) {
            if (scopedDocument == null) {
                if (currentScope != null) {
                    currentScope = null;
                    Logger.getLogger(DocumentScope.class.getName()).log(Level.INFO, "DocumentScope is null");
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
                String msg = "DocumentScope changed to : %s (unused: %s, dirty: %s, content: %s, name %s)";
                if (scopedDocument.isInited()) {
                    Logger.getLogger(DocumentScope.class.getName()).log(Level.INFO,
                            String.format(msg, scopedDocument, scopedDocument.isUnused(),
                            scopedDocument.isDocumentDirty(), scopedDocument.hasName(), scopedDocument.getName()));
                } else {
                    Logger.getLogger(DocumentScope.class.getName()).log(Level.INFO,
                            String.format(msg, scopedDocument, "", "", "", ""));
                }
            }
        }

        /**
         * Removes the scope and all the associated instances.
         *
         * @param document the document
         */
        public static void removeScope(Document document) {
            System.out.println("REMOVING SCOPE " + document);
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
        public synchronized Object get(String name, ObjectFactory<?> objectFactory) {

            if (SCOPE_OBJECT_NAME.equals(name) && ((currentScope == null)
                    || (currentScope != null && !scopes.get(currentScope).containsKey(name)))) {
                // new document, so create a new document scope
                // Using an UUID as scope key instead of a Document to allow bean creation
                // while the Document instantiation is ongoing
                UUID scopeId = UUID.randomUUID();
                scopes.put(scopeId, new ConcurrentHashMap<>());
                currentScope = scopeId;

                Document scopeDocument = (Document) objectFactory.getObject();

                scopesId.put(scopeDocument, scopeId);
                setCurrentScope(scopeDocument);

                Map<String, Object> scopedObjects = scopes.get(currentScope);
                scopedObjects.put(name, scopeDocument);
                return scopeDocument;

            } else {
                // simple bean instantiation or retrieve it from the existing beans
                assert currentScope != null;
                Map<String, Object> scopedObjects = scopes.get(currentScope);
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

    /**
     * The Class ThreadScope.
     */
    public static class ThreadScope implements Scope {

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

    /**
     * Creates a new SceneBuilderBean object.
     *
     * @param label the label
     * @param toggleGroup the toggle group
     * @return the radio menu item
     */
    public RadioMenuItem createViewRadioMenuItem(String label, ToggleGroup toggleGroup) {
        RadioMenuItem r = new RadioMenuItem(label);
        if (toggleGroup != null) {
            r.setToggleGroup(toggleGroup);
        }
        return r;
    }

    /**
     * Creates a new SceneBuilderBean object.
     *
     * @return the separator menu item
     */
    public SeparatorMenuItem createSeparatorMenuItem() {
        return new SeparatorMenuItem();
    }

    /**
     * Creates a new SceneBuilderBean object.
     *
     * @param label the label
     * @return the menu item
     */
    public MenuItem createViewMenuItem(String label) {
        return new MenuItem(label);
    }

    /**
     * Creates a new SceneBuilderBean object.
     *
     * @param label the label
     * @return the menu
     */
    public Menu createViewMenu(String label) {
        return new Menu(label);
    }
}
