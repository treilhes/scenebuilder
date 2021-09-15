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
package com.oracle.javafx.scenebuilder.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.core.editors.CssPropAuthorInfo;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.Deprecation;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.sun.javafx.scene.NodeHelper;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.css.CompoundSelector;
import javafx.css.CssMetaData;
import javafx.css.CssParser;
import javafx.css.Rule;
import javafx.css.Selector;
import javafx.css.SimpleSelector;
import javafx.css.Style;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.Stylesheet;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 *
 * Utility classes using css internal classes (from com.sun package). Note that
 * the CSS Analyzer is also using extensively com.sun classes.
 *
 */

//TODO this class need a complete rewrite 
public class CssInternal {

//    private final static String[] themeUrls = {
//        Theme.CASPIAN_EMBEDDED_HIGH_CONTRAST.getStylesheetURL(),
//        Theme.CASPIAN_EMBEDDED_QVGA_HIGH_CONTRAST.getStylesheetURL(),
//        Theme.CASPIAN_EMBEDDED_QVGA.getStylesheetURL(),
//        Theme.CASPIAN_EMBEDDED.getStylesheetURL(),
//        Theme.CASPIAN_HIGH_CONTRAST.getStylesheetURL(),
//        Theme.CASPIAN.getStylesheetURL(),
//        Theme.MODENA_HIGH_CONTRAST_BLACK_ON_WHITE.getStylesheetURL(),
//        Theme.MODENA_HIGH_CONTRAST_WHITE_ON_BLACK.getStylesheetURL(),
//        Theme.MODENA_HIGH_CONTRAST_YELLOW_ON_BLACK.getStylesheetURL(),
//        Theme.MODENA.getStylesheetURL(),
//        Theme.MODENA_TOUCH_HIGH_CONTRAST_BLACK_ON_WHITE.getStylesheetURL(),
//        Theme.MODENA_TOUCH_HIGH_CONTRAST_WHITE_ON_BLACK.getStylesheetURL(),
//        Theme.MODENA_TOUCH_HIGH_CONTRAST_YELLOW_ON_BLACK.getStylesheetURL(),
//        Theme.MODENA_TOUCH.getStylesheetURL()
//    };

    /**
     * Check if the input style is from a theme stylesheet (caspian or modena).
     *
     * @param style style to be checked
     * @return true if the style is from a theme css.
     */
    public static boolean isThemeStyle(Style style) {
        return isThemeRule(style.getDeclaration().getRule());
    }

//    public static boolean isCaspianTheme(Style style) {
//        return style.getDeclaration().getRule().getStylesheet().getUrl()
//                .endsWith(Theme.CASPIAN.getStylesheetURL());
//    }
//
//    public static boolean isModenaTheme(Style style) {
//        return style.getDeclaration().getRule().getStylesheet().getUrl()
//                .endsWith(Theme.MODENA.getStylesheetURL());
//    }

//  TODO why those methods, they are breaking theme encapsulation
    public static boolean isCaspianTheme(Style style) {
//        return style.getDeclaration().getRule().getStylesheet().getUrl()
//                .endsWith(new DefaultThemesList.Caspian().getUserAgentStylesheet());
        return false;
    }

    public static boolean isModenaTheme(Style style) {
//        return style.getDeclaration().getRule().getStylesheet().getUrl()
//                .endsWith(new DefaultThemesList.Modena().getUserAgentStylesheet());
        return false;
    }

    public static String getThemeDisplayName(Style style) {
        String themeName = ""; // NOI18N
        String url = style.getDeclaration().getRule().getStylesheet().getUrl();
        if (url.contains("modena")) {// NOI18N
            themeName += "modena/"; // NOI18N
        } else if (url.contains("caspian")) {// NOI18N
            themeName += "caspian/"; // NOI18N
        }
        File file = new File(url);
        themeName += file.getName().replace(".bss", ".css");// NOI18N
        if (themeName.endsWith("modena.css")) {// NOI18N
            themeName = "modena.css";// NOI18N
        } else if (themeName.endsWith("caspian.css")) {// NOI18N
            themeName = "caspian.css";// NOI18N
        }
        return themeName;
    }

//  TODO why those methods, they are breaking theme encapsulation
    public static boolean isThemeRule(Rule rule) {
        String stylePath = rule.getStylesheet().getUrl();
        assert stylePath != null;

        // FIXME temp fix to remove, worst: gluon is not event present

        List<String> themeUrls = new ArrayList<>();
        
        //TODO uncomment when solution provided
//        new DefaultThemesList().themes().forEach(t -> {
//            try {
//                themeUrls.add(t.newInstance().getUserAgentStylesheet());
//            } catch (InstantiationException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        });

        for (String themeUrl : themeUrls) {
            if (stylePath.endsWith(themeUrl)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isThemeClass(Theme theme, String styleClass) {
        return getThemeStyleClasses(theme).contains(styleClass);
    }

//    public static List<String> getThemeStyleClasses(Theme theme) {
//        String themeStyleSheet = theme.getStylesheetURL();
//        Set<String> themeClasses = new HashSet<>();
//        // For Theme css, we need to get the text css (.css) to be able to parse it.
//        // (instead of the default binary format .bss)
//        themeClasses.addAll(getStyleClasses(Deprecation.getThemeTextStylesheet(themeStyleSheet)));
//        return new ArrayList<>(themeClasses);
//    }

    public static List<String> getThemeStyleClasses(StylesheetProvider stylesheetConfig) {
        // TODO maybe some other css are needed here
        //TODO check updated function
//        String themeStyleSheet = stylesheetConfig.getUserAgentStylesheet();
        Set<String> themeClasses = new HashSet<>();
        // For Theme css, we need to get the text css (.css) to be able to parse it.
        // (instead of the default binary format .bss)
        if (stylesheetConfig.getUserAgentStylesheet() != null) {
            themeClasses.addAll(getStyleClasses(Deprecation.getThemeTextStylesheet(stylesheetConfig.getUserAgentStylesheet())));
        }
        stylesheetConfig.getStylesheets().stream().filter(s -> s != null)
            .forEach(s -> themeClasses.addAll(getStyleClasses(Deprecation.getThemeTextStylesheet(s))));
        
        return new ArrayList<>(themeClasses);
    }

    // Return the stylesheet corresponding to a style class.
    // (input parameter: a map returned by getStyleClassesMap(), styleClass)
    public static String getStyleSheet(Map<String, String> styleClassMap, String styleClass) {
        return styleClassMap.get(styleClass);
    }

//    public static List<String> getStyleClasses(EditorController editorController, Set<FXOMInstance> instances) {
//        return new ArrayList<>(getStyleClassesMap(editorController, instances).keySet());
//    }

    public static Map<String, String> getStyleClassesMap(StylesheetProvider stylesheetConfig, Editor editorController,
            Set<FXOMInstance> instances) {
        Map<String, String> classesMap = new TreeMap<>();
        Object fxRoot = null;
        for (FXOMInstance instance : instances) {
            if (fxRoot == null) {
                fxRoot = instance.getFxomDocument().getSceneGraphRoot();
            }
            Object fxObject = instance.getSceneGraphObject();
            classesMap.putAll(getFxObjectClassesMap(fxObject, fxRoot));
        }

        StylesheetProvider sp = stylesheetConfig;

        if (sp != null) {
            List<File> sceneStyleSheets = sp.getStylesheets().stream().map(s -> new File(s)).filter(f -> f.exists())
                    .collect(Collectors.toList());

            if (sceneStyleSheets != null) {
                for (File stylesheet : sceneStyleSheets) {
                    try {
                        URL stylesheetUrl = stylesheet.toURI().toURL();
                        for (String styleClass : getStyleClasses(stylesheetUrl)) {
                            classesMap.put(styleClass, stylesheetUrl.toExternalForm());
                        }
                    } catch (MalformedURLException ex) {
                        return classesMap;
                    }
                }
            }
        }

        return classesMap;
    }

    // Retrieve the styClasses in the fx object scene graph
    private static Map<String, String> getFxObjectClassesMap(Object fxObject, Object fxRoot) {
        Map<String, String> classesMap = new HashMap<>();
        classesMap.putAll(getSingleFxObjectClassesMap(fxObject));
        if (!(fxObject instanceof Node)) {
            return classesMap;
        }
        Node node = (Node) fxObject;
        if (node == fxRoot) {
            return classesMap;
        }
        // Loop on scene graph tree, and stop at root node (to avoid to handle SB nodes)
        while (node.getParent() != null) {
            node = node.getParent();
            classesMap.putAll(getSingleFxObjectClassesMap(node));
            if (node == fxRoot) {
                break;
            }
        }
        return classesMap;
    }

    // Retrieve the styleClasses in the fx object only (not inherited ones)
    private static Map<String, String> getSingleFxObjectClassesMap(Object fxObject) {
        Map<String, String> classesMap = new HashMap<>();

        if (fxObject instanceof Parent) {
            List<String> stylesheets = ((Parent) fxObject).getStylesheets();
            for (String stylesheet : stylesheets) {
                try {
                    for (String styleClass : getStyleClasses(new URL(stylesheet))) {
                        classesMap.put(styleClass, stylesheet);
                    }
                } catch (MalformedURLException ex) {
                    return classesMap;
                }
            }
        }
        return classesMap;
    }

    private static Set<String> getStyleClasses(final URL url) {
        Set<String> styleClasses = new HashSet<>();
        Stylesheet s;
        try {
            s = new CssParser().parse(url);
        } catch (IOException ex) {
            System.out.println("Warning: Invalid Stylesheet " + url); // NOI18N
            return styleClasses;
        }
        if (s == null) {
            // The parsed CSS file was empty. No parsing occured.
            return styleClasses;
        }
        for (Rule r : s.getRules()) {
            for (Selector ss : r.getSelectors()) {
                if (ss instanceof SimpleSelector) {
                    SimpleSelector simple = (SimpleSelector) ss;
                    styleClasses.addAll(simple.getStyleClasses());
                } else {
                    if (ss instanceof CompoundSelector) {
                        CompoundSelector cs = (CompoundSelector) ss;
                        for (Selector selector : cs.getSelectors()) {
                            if (selector instanceof SimpleSelector) {
                                SimpleSelector simple = (SimpleSelector) selector;
                                styleClasses.addAll(simple.getStyleClasses());
                            }
                        }
                    }
                }
            }
        }
        return styleClasses;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getCssProperties(Set<Class<?>> classes) {
        TreeSet<String> cssProperties = new TreeSet<>();
        for (Class<?> clazz : classes) {
            if (Node.class.isAssignableFrom(clazz)) {
                Object metadatas = null;
                try {
                    metadatas = clazz.getMethod("getClassCssMetaData").invoke(null, (Object[]) null); // NOI18N
                } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException ex) {
                    assert false;
                }
                for (CssMetaData<? extends Styleable, ?> metadata : ((List<CssMetaData<? extends Styleable, ?>>) metadatas)) {
                    cssProperties.add(metadata.getProperty());
                    if (metadata.getSubProperties() != null) {
                        for (CssMetaData<? extends Styleable, ?> subMetadata : metadata.getSubProperties()) {
                            cssProperties.add(subMetadata.getProperty());
                        }
                    }
                }
            }
        }
        return new ArrayList<>(cssProperties);
    }

    // If this property is ruled by CSS, return a CssPropAuthorInfo. Otherwise
    // returns null.
    public static CssPropAuthorInfo getCssInfo(Map<StyleableProperty, List<Style>> cssState, ValuePropertyMetadata prop) {
        CssPropAuthorInfo info = null;
        if (cssState != null) {
            info = getCssInfoFromState(cssState, prop);
        }
        return info;
    }
    
     public static Map<StyleableProperty, List<Style>> getCssState(Object fxObject) {

        Node node = null;

        if (fxObject instanceof Node) {
            node = (Node) fxObject;
        } else {
            Styleable styleable = fxObject instanceof Styleable ? (Styleable) fxObject : null;
            if (styleable != null) {
                node = styleable.getStyleableNode();
            }
        }
        if (node != null) {
            return collectCssState(node);
        }
        return null;
    }

    private static CssPropAuthorInfo getCssInfoFromState(Map<StyleableProperty, List<Style>> cssState, ValuePropertyMetadata prop) {
        @SuppressWarnings("rawtypes")
        Map<StyleableProperty, List<Style>> map = cssState;
        for (@SuppressWarnings("rawtypes")
        Map.Entry<StyleableProperty, List<Style>> entry : map.entrySet()) {// NOI18N
            StyleableProperty<?> beanProp = entry.getKey();
            List<Style> styles = new ArrayList<>(entry.getValue());
            String name = getBeanPropertyName(beanProp);
            if (!name.equals(prop.getName().getName())) {
                continue;
            }
            if (name.equals(prop.getName().getName())) {
                // If the value has an origin of Author or Inline
                // then we have a property ruled by CSS, otherwise return null
                // This is in sync because the map is not empty
                StyleOrigin origin = beanProp.getStyleOrigin();
                if (origin == null || origin.equals(StyleOrigin.USER) || origin.equals(StyleOrigin.USER_AGENT)) {
                    return null;
                }
                CssMetaData<?, ?> styleable = beanProp.getCssMetaData();
                // Lookup the Author style
                CssPropAuthorInfo info = null;
                for (Style style : styles) {
                    Rule rule = style.getDeclaration().getRule();
                    assert rule != null;
                    // StyleOrigin can be null when the value is set to its initial value.
                    StyleOrigin o = rule.getOrigin();
                    if (o == null) {
                        return null;
                    }
                    if ((o.equals(StyleOrigin.AUTHOR) && (!CssInternal.isThemeStyle(style)))
                            || o.equals(StyleOrigin.INLINE)) {
                        if (info == null) {
                            info = new CssPropAuthorInfo(prop, beanProp, styleable);
                        }
                        info.getStyles().add(style);
                    }
                }
                return info;
            }
        }
        return null;
    }

    // TODO this method was not used
//    public static boolean isCssRuled(Object fxObject, ValuePropertyMetadata prop) {
//        return getCssInfo(fxObject, prop) != null;
//    }

    

    public static String getBeanPropertyName(StyleableProperty<?> val) {
        String property = null;
        if (val instanceof ReadOnlyProperty) {
            property = ((ReadOnlyProperty<?>) val).getName();
        }
        return property;
    }

    public static void attachMapToNode(Node node) {
        Map<StyleableProperty<?>, List<Style>> smap = new HashMap<>();
        Deprecation.setStyleMap(node, FXCollections.observableMap(smap));
    }

    public static void detachMapToNode(Node node) {
        Deprecation.setStyleMap(node, null);
    }

    @SuppressWarnings("rawtypes")
    public static Map<StyleableProperty, List<Style>> collectCssState(Node node) {
        attachMapToNode(node);
        // Force CSS to apply
        node.applyCss();

        Map<StyleableProperty, List<Style>> ret = new HashMap<>();
//        ret.putAll(Deprecation.getStyleMap(node));

        Map<StyleableProperty<?>, List<Style>> map = NodeHelper.findStyles(node, null);
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<StyleableProperty<?>, List<Style>> entry : map.entrySet()) {
                StyleableProperty<?> key = entry.getKey();
                List<Style> value = entry.getValue();
                if (((javafx.beans.property.Property<?>) key).getBean() == node) {
                    ret.put(key, value);
                }
            }
        }

        // Attached map may impact css performance, so remove it.
        detachMapToNode(node);
        // DEBUG
//        System.out.println("collectCssState() for " + node);
//        for (StyleableProperty s : ret.keySet()) {
//            List<Style> styles = ret.get(s);
//            for (Style style : styles) {
//                System.out.println(style.getDeclaration().getRule().getOrigin() + " ==> STYLE " + style.getDeclaration());
//                System.out.println("--> css url = " + style.getDeclaration().getRule().getStylesheet().getUrl());
//            }
//        }
        return ret;
    }

    public static StyleOrigin getOrigin(Style style) {
        if (style == null || style.getDeclaration() == null) {
            return null;
        }
        return style.getDeclaration().getRule().getOrigin();
    }

    // From an css url, returns the theme display name
    public static String getThemeDisplayName(String url) {
        String themeName = ""; // NOI18N
        if (url.contains("modena")) {// NOI18N
            themeName += "modena/"; // NOI18N
        } else if (url.contains("caspian")) {// NOI18N
            themeName += "caspian/"; // NOI18N
        }
        File file = new File(url);
        themeName += file.getName().replace(".bss", ".css");// NOI18N
        if (themeName.endsWith("modena.css")) {// NOI18N
            themeName = "modena.css";// NOI18N
        } else if (themeName.endsWith("caspian.css")) {// NOI18N
            themeName = "caspian.css";// NOI18N
        }
        return themeName;
    }

}
