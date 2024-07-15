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
package com.gluonhq.jfxapps.core.fxom.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

/**
 * The goal of this class is to instrument the FXMLLoader class in order to
 * replace static loading. <br/>
 * Why? Because the FXMLLoader class is not designed to be lenient with errors
 * while loading FXML files. If an error occurs, the FXMLLoader class will throw
 * an exception and stop the loading process. This is not what we want. We want
 * the FXMLLoader class to be lenient with errors and to continue loading the
 * FXML file even if an error occurs.<br/>
 * <br/>
 * Normal behavior:<br/>
 * - static properties with unknown source class generate an exception<br/>
 * - event handlers must match with controller class methods/script/object or an
 * exception is thrown<br/>
 * - unknown element type generate an exception<br/>
 * - script engine is loaded if processing instruction is provided<br/>
 * - root element must be set<br/>
 * - if fx:script source is null,empty,duplicated or invalid, an exception is
 * thrown<br/>
 * - controller must be instanciable<br/>
 * - unknown reference generate an exception<br/>
 * <br/>
 * StaticLoad behavior:<br/>
 * - static properties with unknown source class delegated to the custom load
 * listener<br/>
 * - event handlers are disabled<br/>
 * - unknown element type delegated to the custom load listener<br/>
 * - script engine is disabled<br/>
 * - root element is generated if not set<br/>
 * - fx:script are ignored<br/>
 * - controller is disabled<br/>
 * - unknown reference generate an exception<br/>
 * <br/>
 * Targeted behavior:<br/>
 * - static properties with unknown source class delegate to the custom load
 * listener<br/>
 * - event handlers must be handled by some proxy object to enable error
 * reporting<br/>
 * - unknown element type delegated to the custom load listener<br/>
 * - script engine is loaded if processing instruction is provided<br/>
 * - root element is generated if not set<br/>
 * - if fx:script source is null,empty,duplicated or invalid, reporting must be
 * done<br/>
 * - controller must be proxyfied to enable error reporting<br/>
 * - intercept calls to the namespace map and return a default value if the key
 * is not found to prevent undefined reference exceptions. reporting must be
 * done<br/>
 *
 */
// TODO : enable and finish this class, when the time comes
public class FXMLLoaderInstrument {

    // TODO uncomment me when the time comes
//    static {
//        com.gluonhq.jfxapps.javafx.fxml.patch.PatchLink.addOpen(FXMLLoaderInstrument.class.getModule(), "javafx.fxml");
//    }

    public static void preload() {

        try {
            // Make the inner class accessible via reflection
            Field field = FXMLLoader.class.getDeclaredField("controllerAccessor");
            field.setAccessible(true);

            // Obtain the private inner class via reflection
            Class<?> outerClass = FXMLLoader.class;
            Class<?> innerClass = null;
            for (Class<?> clazz : outerClass.getDeclaredClasses()) {
                if ("ControllerAccessor".equals(clazz.getSimpleName())) {
                    innerClass = clazz;
                    break;
                }
            }

            if (innerClass == null) {
                throw new ClassNotFoundException("InnerClass not found");
            }

            // Use ByteBuddy to change the visibility of the inner class
            Class<?> dynamicType = new ByteBuddy().redefine(innerClass).modifiers(Visibility.PUBLIC).make()
                    .load(FXMLLoader.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();

            // Create an instance of the now-public inner class
            Object outerInstance = outerClass.getDeclaredConstructor().newInstance();
            Object instance = dynamicType.getDeclaredConstructor(outerClass).newInstance(outerInstance);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static FXMLLoaderInstrument of(FXMLLoader fxmlLoader) {
        return new FXMLLoaderInstrument(fxmlLoader);
    }

    private final FXMLLoader fxmlLoader;
    private final FXMLLoader instrumentedLoader;
    private ObservableMap<String, Object> namespace;
    private ScriptEngineManager scriptEngineManager;

    private FXMLLoaderInstrument(FXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
        this.instrumentedLoader = createdInstrumented();
        setup();
    }

    private FXMLLoader createdInstrumented() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *
     */
    private void setup() {
        try {
            Field namespaceField = FXMLLoader.class.getDeclaredField("namespace");
            namespaceField.setAccessible(true);

            namespace = (ObservableMap<String, Object>) namespaceField.get(fxmlLoader);

            ObservableMap<String, Object> proxyInstance = (ObservableMap<String, Object>) Proxy.newProxyInstance(
                    FXMLLoaderInstrument.class.getClassLoader(), new Class[] { ObservableMap.class },
                    new NamespaceInvocationHandler(namespace));

            namespaceField.set(fxmlLoader, proxyInstance);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Field scriptEngineManagerField = FXMLLoader.class.getDeclaredField("scriptEngineManager");
            scriptEngineManagerField.setAccessible(true);

            scriptEngineManager = new JfxAppsScriptEngineManager();
            scriptEngineManager.setBindings(new SimpleBindings(namespace));

            scriptEngineManagerField.set(fxmlLoader, scriptEngineManager);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            // Make the inner class accessible via reflection
            Field field = FXMLLoader.class.getDeclaredField("controllerAccessor");
            field.setAccessible(true);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public FXMLLoader getInstrumentedLoader() {
        return instrumentedLoader;
    }

    public class NamespaceInvocationHandler implements InvocationHandler {

        private static Logger LOGGER = LoggerFactory.getLogger(NamespaceInvocationHandler.class);
        private final ObservableMap<String, Object> source;

        public NamespaceInvocationHandler(ObservableMap<String, Object> source) {
            this.source = source;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            LOGGER.info("Invoked method: {} args: {}", method.getName(), args);

            if (method.getName().equals("containsKey") && !source.containsKey(args[0])) {
                return true;
            }
            if (method.getName().equals("get") && source.get(args[0]) == null) {
                LOGGER.info("Undeclared id: {}", args[0]);
                return new Pane();
            }
            return method.invoke(source, args);
        }
    }

    public class JfxAppsScriptEngineManager extends ScriptEngineManager {

    }

    public class PrivateMethodInterceptor {
        @Advice.OnMethodEnter
        public static void onEnter() {
            System.out.println("Intercepted before private method");
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void onExit(@Advice.Return(readOnly = false) Object returnValue,
                @Advice.Thrown Throwable throwable) {
            if (throwable != null) {
                System.out.println("Exception caught: " + throwable.getMessage());
                returnValue = "Default Value After Exception";
            } else {
                System.out.println("Intercepted after private method");
                returnValue = "Modified Return Value";
            }
        }
    }
}
