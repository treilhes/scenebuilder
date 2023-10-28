package com.gluonhq.jfxapps.boot.loader.extension;

import java.util.List;

public non-sealed interface OpenExtension extends Extension {

    List<Class<?>> exportedContextClasses();

}
