package com.gluonhq.jfxapps.metadata.finder.api;

import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.metadata.finder.SearchContext;
import com.gluonhq.jfxapps.metadata.model.Component;
import com.gluonhq.jfxapps.metadata.model.Property;

public interface Executor {

    public void preExecute(SearchContext searchContext) throws Exception;

    public void execute(SearchContext searchContext, Map<Component, Set<Property>> components, Map<Class<?>, Component> descriptors) throws Exception;
}
