package com.oracle.javafx.scenebuilder.core.loader.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader.Provider;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.loader.api.AbstractExtension;
import com.oracle.javafx.scenebuilder.core.loader.api.Application;
import com.oracle.javafx.scenebuilder.core.loader.api.Extension;

@Component
public class ApplicationImpl implements Application, InitializingBean {

    private ApplicationContext context;

    private Map<Class<?>, AbstractApplicationContext> activeContexts;
    private Map<Class<?>, ModuleLayer> activeLayers;
//
//    public static void main(String[] args) {
//        start(args);
//    }

    public ApplicationImpl(ApplicationContext context) {
        super();
        this.context = context;
        this.activeContexts = new HashMap<>();
        this.activeLayers = new HashMap<>();

        System.out.println("ApplicationImpl > ApplicationContext " + context);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //PluginListener.setApplication(this);
        AbstractExtension.load(context, RootExtension.class);
    }

    private void loadEditors() {

    }

    @Override
    public ApplicationContext getContext() {
        return context;
    }


//    @Override
//    public void pluginAdded(PluginDescriptor plugin) {
//        ServiceLoader.load(plugin.getModuleLayer(), Extension.class).stream().forEach(this::loadPlugin);
//    }
//
//    @Override
//    public void pluginRemoved(PluginDescriptor plugin) {
//        ServiceLoader.load(plugin.getModuleLayer(), Extension.class).stream().forEach(this::unloadPlugin);
//    }

    private <T extends Extension> void loadPlugin(Provider<T> provider) {
        Class<? extends T> pluginClass = provider.type();
        loadPlugin(pluginClass);
    }

    private <T extends Extension> void loadPlugin(Class<T> pluginClass) {
        try {
            T plugin = pluginClass.getDeclaredConstructor().newInstance();

            if (plugin.explicitClassToRegister() != null) {
                ApplicationContext parentContext = getContext();

                AnnotationConfigApplicationContext pluginContext = new AnnotationConfigApplicationContext();
                pluginContext.setParent(parentContext);
                pluginContext.register(plugin.explicitClassToRegister().toArray(new Class[0]));
                pluginContext.refresh();
                pluginContext.start();

                activeContexts.put(pluginClass, pluginContext);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private <T extends Extension> void unloadPlugin(Provider<T> provider) {
        Class<? extends T> pluginClass = provider.type();
        unloadPlugin(pluginClass);
    }

    private <T extends Extension> void unloadPlugin(Class<T> pluginClass) {
        AbstractApplicationContext pluginContext = activeContexts.remove(pluginClass);

        if (pluginContext != null) {
            pluginContext.close();
        }
    }


    public static class RootExtension extends AbstractExtension{
        @Override
        public UUID id() {
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        }

        @Override
        public List<Class<?>> explicitClassToRegister() {
            return List.of(RootComponent.class);
        }
    }

    @Component
    public static class RootComponent{}

}