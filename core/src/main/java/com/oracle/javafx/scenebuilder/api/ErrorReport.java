package com.oracle.javafx.scenebuilder.api;

import java.io.IOException;
import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.css.CssParser;

public interface ErrorReport {

	List<ErrorReportEntry> query(FXOMObject fxomObject, boolean b);

	public interface ErrorReportEntry {

		public enum Type {
	        UNRESOLVED_CLASS,
	        UNRESOLVED_LOCATION,
	        UNRESOLVED_RESOURCE,
	        INVALID_CSS_CONTENT,
	        UNSUPPORTED_EXPRESSION
	    }

		public FXOMNode getFxomNode();

		public Type getType();

		public CSSParsingReport getCssParsingReport();

		public interface CSSParsingReport {

			IOException getIOException();

			List<CssParser.ParseError> getParseErrors();

		}
	}


}
