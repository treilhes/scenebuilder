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
package com.oracle.javafx.scenebuilder.devutils.strchk;

import java.util.List;
import java.util.regex.Pattern;

public class Config {

    public static boolean DISABLE_ALL_FILTERS = false;

    public static final List<String> EXCLUDE_LINES_WITH_PREFIX = List.of();
    public static final List<String> EXCLUDE_LINES_WITH_SUFFIX = List.of(
            "//NOCHECK", "//NOCHECK", "// NOI18N", "// NOCHECK");

    public static final List<Pattern> EXCLUDE_LINES_WITH_PATTERN = List.of(
                Pattern.compile(".*@SuppressWarnings\\(\".*?\"\\).*"),
                Pattern.compile(".*Exception\\(\".*?\"\\);.*"),
                Pattern.compile(".*logger\\..*?\\(\".*?\".*"),
                Pattern.compile(".*\\*.*")
            );

    public static final List<Pattern> INCLUDE_LINES_WITH_PATTERN = List.of(
            Pattern.compile(".*\\*.*src=\"doc-files/.*")
        );
    public static final List<String> EXCLUDED_VALUES = List.of("AS IS", "UNSET", "true", "false", "null", "\\n", "\\n\\n");
    public static final List<String> EXCLUDE_VALUES_STARTING_WITH = List.of("http://", "https://","-");
    public static final List<String> EXCLUDE_VALUES_CONTAINING = List.of("*"," ");
    public static final List<Pattern> EXCLUDE_VALUES_WITH_PATTERN = List.of(
            Pattern.compile("fx:.*"),
            Pattern.compile("[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}")
            );

}
