package com.oracle.javafx.scenebuilder.metadata.finder.api;

import java.util.Map;
import java.util.Set;

import com.oracle.javafx.scenebuilder.metadata.finder.SearchContext;
import com.oracle.javafx.scenebuilder.metadata.model.Component;
import com.oracle.javafx.scenebuilder.metadata.model.Property;

public interface Executor {

    public void preExecute(SearchContext searchContext) throws Exception;

    public void execute(SearchContext searchContext, Map<Component, Set<Property>> components, Map<Class<?>, Component> descriptors) throws Exception;
}
