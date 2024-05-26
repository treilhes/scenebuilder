/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.metadata.util;

public class StringUtils {
    /**
     * Utility method to take a string and convert it to normal Java variable
     * name capitalization.  This normally means converting the first
     * character from upper case to lower case, but in the (unusual) special
     * case when there is more than one character and both the first and
     * second characters are upper case, we leave it alone.
     * <p>
     * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays
     * as "URL".
     *
     * @param name The string to be decapitalized. If null, then null is
     *        returned.
     * @return The decapitalized version of the string.
     */
    public static String decapitalize(String name) {
        if (name == null) {
            return name;
        }
        name = name.trim();
        if (name.length() == 0) {
            return name;
        }

        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * Takes the given name and formats it for display. Given a camel-case
     * name, it will split the name at each of the upper-case letters, unless
     * multiple uppercase letters are in a series, in which case it treats them
     * as a single name. The initial lower-case letter is upper cased. So a
     * name like "translateX" becomes "Translate X" and a name like "halign"
     * becomes "Halign".
     * <p>
     * Numbers are treated the same as if they were capital letters, such that
     * "MyClass3" would become "My Class 3" and "MyClass23" would become
     * "My Class 23".
     * <p>
     * Underscores are converted to spaces, with the first letter following
     * the underscore converted to upper case. Multiple underscores in a row
     * are treated only as a single space, and any leading or trailing
     * underscores are skipped.
     *
     * @param name
     * @return
     */
    public static String toDisplayName(String name) {
    if (name == null) return name;
        // Replace all underscores with empty spaces
        name = name.replace("_", " ");
        // Trim out any leading or trailing space (which also effectively
        // removes any underscores that were leading or trailing, since the
        // above line had converted them all to spaces).
        name = name.trim();
        // If the resulting name is empty, return an empty string
        if (name.length() == 0) return name;
        // There are now potentially spaces already in the name. If, while
        // iterating over all of the characters in the name we encounter a
        // space, then we will simply step past the space and capitalize the
        // following character.
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        StringBuilder builder = new StringBuilder();
        char ch = name.charAt(0);
        builder.append(ch);
        boolean previousWasDigit = Character.isDigit(ch);
        boolean previousWasCapital = !previousWasDigit;
        for (int i=1; i<name.length(); i++) {
            ch = name.charAt(i);
            if ((Character.isUpperCase(ch) && !previousWasCapital) ||
                    (Character.isUpperCase(ch) && previousWasDigit)) {
                builder.append(" ");
                builder.append(ch);
                previousWasCapital = true;
                previousWasDigit = false;
            } else if ((Character.isDigit(ch) && !previousWasDigit) ||
                    (Character.isDigit(ch) && previousWasCapital)) {
                builder.append(" ");
                builder.append(ch);
                previousWasCapital = false;
                previousWasDigit = true;
            } else if (Character.isUpperCase(ch) || Character.isDigit(ch)) {
                builder.append(ch);
            } else if (Character.isWhitespace(ch)) {
                builder.append(" ");
                // There might have been multiple underscores in a row, so
                // we might now have multiple whitespace in a row. Search ahead
                // to the first non-whitespace character.
                ch = name.charAt(++i);
                while (Character.isWhitespace(ch)) {
                    // Note that because we trim the String, it should be
                    // impossible to have trailing whitespace, and thus we
                    // don't have to worry about the ArrayIndexOutOfBounds
                    // condition here.
                    ch = name.charAt(++i);
                }
                builder.append(Character.toUpperCase(ch));
                previousWasDigit = Character.isDigit(ch);
                previousWasCapital = !previousWasDigit;
            } else {
                previousWasCapital = false;
                previousWasDigit = false;
                builder.append(ch);
            }
        }
        return builder.toString();
    }
}
