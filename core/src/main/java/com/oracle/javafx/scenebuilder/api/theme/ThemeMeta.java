package com.oracle.javafx.scenebuilder.api.theme;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThemeMeta {
	public String name();
	public Class<? extends AbstractGroup> group();
}