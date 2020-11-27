package com.oracle.javafx.scenebuilder.api;

import java.util.Comparator;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public interface Library {

	ClassLoader getClassLoader();

	ObservableValue<ClassLoader> classLoaderProperty();

	ObservableList<LibraryItem> getItems();

	Comparator<String> getSectionComparator();

}
