package com.oracle.javafx.scenebuilder.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SceneBuilderFactory {

	@Autowired
	ApplicationContext context;
	
	public SceneBuilderFactory() {
	}
	
	public <C> C get(Class<C> cls) {
		return context.getBean(cls);
	}
}
