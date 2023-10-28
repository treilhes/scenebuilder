package com.gluonhq.jfxapps.boot.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Scope("prototype")
@Component
public @interface Prototype {}
