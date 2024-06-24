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
package com.gluonhq.jfxapps.core.api.css;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.util.Deprecation;

public interface StylesheetProvider {
    String getUserAgentStylesheet();
    List<String> getStylesheets();

    public default boolean isThemeClass(String styleClass) {
        return getThemeStyleClasses().contains(styleClass);
    }

//    public static List<String> getThemeStyleClasses(Theme theme) {
//        String themeStyleSheet = theme.getStylesheetURL();
//        Set<String> themeClasses = new HashSet<>();
//        // For Theme css, we need to get the text css (.css) to be able to parse it.
//        // (instead of the default binary format .bss)
//        themeClasses.addAll(getStyleClasses(Deprecation.getThemeTextStylesheet(themeStyleSheet)));
//        return new ArrayList<>(themeClasses);
//    }

    public default List<String> getThemeStyleClasses() {
        // TODO maybe some other css are needed here
        //TODO check updated function
//        String themeStyleSheet = stylesheetConfig.getUserAgentStylesheet();
        Set<String> themeClasses = new HashSet<>();
        // For Theme css, we need to get the text css (.css) to be able to parse it.
        // (instead of the default binary format .bss)
        if (this.getUserAgentStylesheet() != null) {
            themeClasses.addAll(CssInternal.getStyleClasses(Deprecation.getThemeTextStylesheet(this.getUserAgentStylesheet())));
        }
        this.getStylesheets().stream().filter(s -> s != null)
            .forEach(s -> themeClasses.addAll(CssInternal.getStyleClasses(Deprecation.getThemeTextStylesheet(s))));

        return new ArrayList<>(themeClasses);
    }

    public default Map<String, String> getStyleClassesMap(Set<FXOMElement> instances) {
        Map<String, String> classesMap = new TreeMap<>();
        Object fxRoot = null;
        for (FXOMElement instance : instances) {
            if (fxRoot == null) {
                fxRoot = instance.getFxomDocument().getSceneGraphRoot();
            }
            Object fxObject = instance.getSceneGraphObject().get();
            classesMap.putAll(CssInternal.getFxObjectClassesMap(fxObject, fxRoot));
        }

        List<File> sceneStyleSheets = this.getStylesheets().stream().map(s -> new File(s)).filter(f -> f.exists())
                .collect(Collectors.toList());

        if (sceneStyleSheets != null) {
            for (File stylesheet : sceneStyleSheets) {
                try {
                    URL stylesheetUrl = stylesheet.toURI().toURL();
                    for (String styleClass : CssInternal.getStyleClasses(stylesheetUrl)) {
                        classesMap.put(styleClass, stylesheetUrl.toExternalForm());
                    }
                } catch (MalformedURLException ex) {
                    return classesMap;
                }
            }
        }

        return classesMap;
    }
}