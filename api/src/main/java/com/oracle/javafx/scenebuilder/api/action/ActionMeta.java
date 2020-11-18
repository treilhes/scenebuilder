package com.oracle.javafx.scenebuilder.api.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActionMeta {
	public String nameKey() default "";
	public String descriptionKey() default "";
	public String accelerator() default "";
	public Class<?>[] focus() default {};
}
