/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.css.CssParser;

public interface ErrorReport {

    /**
     * List all errors found relative to the provided {@link FXOMObject}
     * @param fxomObject the {@link FXOMObject}
     * @param recursive if true children errors are added into the returned list
     * @return the error list
     */
    List<ErrorReportEntry> query(FXOMObject fxomObject, boolean recursive);

    /**
     * get the string representation of the provided error
     * @param entry
     * @return
     */
    String getText(ErrorReportEntry entry);


    void forget();

    void cssFileDidChange(Path target);

    public interface ErrorReportEntry {

        public enum Type {
            UNRESOLVED_CLASS("sb.error.unresolved.class"),
            UNRESOLVED_LOCATION("sb.error.unresolved.location"),
            UNRESOLVED_RESOURCE("sb.error.unresolved.resource"),
            INVALID_CSS_CONTENT("sb.error.invalid.css"),
            UNSUPPORTED_EXPRESSION("sb.error.unsupported.expression"),
            UNRESOLVED_REFERENCE("sb.error.unresolved.reference");

            private String message;

            Type(String message) {
                this.message = message;
            }

            public String getMessage() {
                return I18N.getString(message, message);
            }
        }

        public FXOMNode getFxomNode();

        public Type getType();

        public CSSParsingReport getCssParsingReport();

        public interface CSSParsingReport {

            IOException getIOException();

            List<CssParser.ParseError> getParseErrors();

            public default String asString(int maxErrors, String separator, String elipsis) {

                final StringBuilder result = new StringBuilder();

                if (getIOException() != null) {
                    result.append(getIOException());
                } else {
                    assert getParseErrors().isEmpty() == false;
                    int errorCount = 0;
                    for (CssParser.ParseError e : getParseErrors()) {
                        result.append(e.getMessage());
                        errorCount++;
                        if (errorCount < maxErrors) {
                            result.append(separator);
                        } else {
                            result.append(elipsis);
                            break;
                        }
                    }
                }

                return result.toString();
            }

        }
    }

}
