/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.fxom.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class JavaLanguage {
    
    /**
     * Returns true if value is a valid identifier (as specified 
     * in Java Language Specification, section 3.8).
     * 
     * @param value string to test (can be null or empty)
     * @return true if value is a valid java identifier.
     */
    public static boolean isIdentifier(String value) {
        /*
         * See Java JavaLanguage Specification, section 3.8: Identifiers
         * https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8
         */
        return isIdentifierChars(value)
                && ! isKeyword(value)
                && ! isBooleanLiteral(value)
                && ! isNullLiteral(value);
    }
    
    
    /**
     * Returns true if value is a valid class name (fully qualified or not).
     * 
     * @param value string to test (can be null or empty)
     * @return  true if value is a valid class name
     */
    public static boolean isClassName(String value) {
        boolean result;
        
        if (value == null) {
            result = false;
        } else {
            result = true;
            for (String item : value.split("\\.")) { //NOCHECK
                if (isIdentifier(item) == false) {
                    result = false;
                    break;
                }
            }
        }
        
        return result;
    }
    
    /*
     * Private
     */
    
    private static boolean isIdentifierChars(String value) {
        if (value == null || value.isEmpty()
                || !Character.isJavaIdentifierStart(value.codePointAt(0))) {
            return false;
        }
        for (int i = 0; i < value.length();) {
            int codePoint = value.codePointAt(i);
            if (!Character.isJavaIdentifierPart(codePoint)) {
                return false;
            }
            i += Character.charCount(codePoint);
        }
        return true;
    }
    
    private static Set<String> keywords;
    private static synchronized boolean isKeyword(String value) {
        if (keywords == null) {
            keywords = new HashSet<>();
            Collections.addAll(
                    keywords,
                    "abstract", "continue", "for", "new", "switch", //NOCHECK
                    "assert", "default", "if", "package", "synchronized", //NOCHECK
                    "boolean", "do", "goto", "private", "this", //NOCHECK
                    "break", "double", "implements", "protected", "throw", //NOCHECK
                    "byte", "else", "import", "public", "throws", //NOCHECK
                    "case", "enum", "instanceof", "return", "transient", //NOCHECK
                    "catch", "extends", "int", "short", "try", //NOCHECK
                    "char", "final", "interface", "static", "void", //NOCHECK
                    "class", "finally", "long", "strictfp", "volatile",  //NOCHECK
                    "const", "float", "native", "super", "while"); //NOCHECK
        }
        return keywords.contains(value);
    }
    
    private static boolean isBooleanLiteral(String value) {
        return value.equals("true") || value.equals("false"); //NOCHECK
    }
    
    private static boolean isNullLiteral(String value) {
        return value.equals("null"); //NOCHECK
    }
}
