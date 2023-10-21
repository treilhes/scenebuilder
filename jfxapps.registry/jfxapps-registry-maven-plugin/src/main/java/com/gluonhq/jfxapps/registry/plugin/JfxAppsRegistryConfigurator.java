package com.gluonhq.jfxapps.registry.plugin;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;

import com.gluonhq.jfxapps.registry.plugin.converter.CustomUUIDConverter;

@Named("jfxapps-mojo-component-configurator")
public class JfxAppsRegistryConfigurator extends BasicComponentConfigurator {
    @PostConstruct
    public void init() {
        converterLookup.registerConverter(new CustomUUIDConverter());
    }

    @PreDestroy
    public void destroy() { }
}