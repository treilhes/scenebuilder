package com.oracle.javafx.scenebuilder.api.preferences;

@FunctionalInterface
public interface KeyProvider<T> {
	String newKey(T object);
}
