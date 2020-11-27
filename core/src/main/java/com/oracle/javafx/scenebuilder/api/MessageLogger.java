package com.oracle.javafx.scenebuilder.api;

import java.util.List;

import javafx.beans.property.IntegerProperty;

public interface MessageLogger {

	void logWarningMessage(String warningKey, Object... arguments);

	void clear();

	IntegerProperty revisionProperty();

	IntegerProperty numOfWarningMessagesProperty();

	List<MessageEntry> getEntries();

	void clearEntry(MessageEntry mle);

	public interface MessageEntry {

	    public enum Type {
	        INFO,
	        WARNING
	    };

	    public Type getType();
	    public String getText();
	    public String getTimestamp();
	}

	MessageEntry getYoungestEntry();

	int getWarningEntryCount();

	int getEntryCount();









}
