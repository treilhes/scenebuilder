package com.oracle.javafx.scenebuilder.api;

public interface Dialog {

	void showErrorAndWait(String title, String message, String detail, Throwable cause);

}
