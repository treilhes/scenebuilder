package com.oracle.javafx.scenebuilder.core.loader.extension;

import java.util.List;

public non-sealed interface OpenExtension extends Extension {

    List<Class<?>> exportedContextClasses();

}
