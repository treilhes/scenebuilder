package com.oracle.javafx.scenebuilder.core.di;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class DocumentScopedObject{
    
}
