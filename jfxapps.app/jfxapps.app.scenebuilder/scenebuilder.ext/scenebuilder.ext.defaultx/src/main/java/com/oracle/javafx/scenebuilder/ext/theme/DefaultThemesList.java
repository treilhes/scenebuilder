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
package com.oracle.javafx.scenebuilder.ext.theme;

import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeGroup;

public class DefaultThemesList {

    @ApplicationSingleton
    public class CaspianGroup extends InnerGroup {
        public CaspianGroup() {
            super("theme.group.caspian");
        }
    }

    @ApplicationSingleton
    public class ModenaGroup extends InnerGroup {
        public ModenaGroup() {
            super("theme.group.modena");
        }
    }

    @ApplicationSingleton
    public static class Modena extends InnerTheme {
        public Modena(ModenaGroup group) {
            super("title.theme.modena", "com/sun/javafx/scene/control/skin/modena/modena.bss", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class ModenaTouch extends InnerTheme {
        public ModenaTouch(ModenaGroup group) {
            super("title.theme.modena_touch", "com/oracle/javafx/scenebuilder/ext/theme/modena/modena-touch.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class ModenaHighContrastBlackOnWhite extends InnerTheme {
        public ModenaHighContrastBlackOnWhite(ModenaGroup group) {
            super("title.theme.modena_high_contrast_black_on_white","com/oracle/javafx/scenebuilder/ext/theme/modena/modena-highContrast-blackOnWhite.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class ModenaHighContrastWhiteOnBlack extends InnerTheme {
        public ModenaHighContrastWhiteOnBlack(ModenaGroup group) {
            super("title.theme.modena_high_contrast_white_on_black","com/oracle/javafx/scenebuilder/ext/theme/modena/modena-highContrast-whiteOnBlack.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class ModenaHighContrastYellowOnBlack extends InnerTheme {
        public ModenaHighContrastYellowOnBlack(ModenaGroup group) {
            super("title.theme.modena_high_contrast_yellow_on_black","com/oracle/javafx/scenebuilder/ext/theme/modena/modena-highContrast-yellowOnBlack.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class ModenaTouchHighContrastBlackOnWhite extends InnerTheme {
        public ModenaTouchHighContrastBlackOnWhite(ModenaGroup group) {
            super("title.theme.modena_touch_high_contrast_black_on_white","com/oracle/javafx/scenebuilder/ext/theme/modena/modena-touch-highContrast-blackOnWhite.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class ModenaTouchHighContrastWhiteOnBlack extends InnerTheme {
        public ModenaTouchHighContrastWhiteOnBlack(ModenaGroup group) {
            super("title.theme.modena_touch_high_contrast_white_on_black","com/oracle/javafx/scenebuilder/ext/theme/modena/modena-touch-highContrast-whiteOnBlack.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class ModenaTouchHighContrastYellowOnBlack extends InnerTheme {
        public ModenaTouchHighContrastYellowOnBlack(ModenaGroup group) {
            super("title.theme.modena_touch_high_contrast_yellow_on_black","com/oracle/javafx/scenebuilder/ext/theme/modena/modena-touch-highContrast-yellowOnBlack.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class Caspian extends InnerTheme {
        public Caspian(CaspianGroup group) {
            super("title.theme.caspian","com/sun/javafx/scene/control/skin/caspian/caspian.bss", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class CaspianHighContrast extends InnerTheme {
        public CaspianHighContrast(CaspianGroup group) {
            super("title.theme.caspian_high_contrast","com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-highContrast.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class CaspianEmbedded extends InnerTheme {
        public CaspianEmbedded(CaspianGroup group) {
            super("title.theme.caspian_embedded","com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-embedded.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class CaspianEmbeddedHighContrast extends InnerTheme {
        public CaspianEmbeddedHighContrast(CaspianGroup group) {
            super("title.theme.caspian_embedded_high_contrast","com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-embedded-highContrast.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class CaspianEmbeddedQvga extends InnerTheme {
        public CaspianEmbeddedQvga(CaspianGroup group) {
            super("title.theme.caspian_embedded_qvga","com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-embedded-qvga.css", List.of(), group);
        }
    }

    @ApplicationSingleton
    public static class CaspianEmbeddedQvgaHighContrast extends InnerTheme {
        public CaspianEmbeddedQvgaHighContrast(CaspianGroup group) {
            super("title.theme.caspian_embedded_qvga_high_contrast","com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-embedded-qvga-highContrast.css", List.of(), group);
        }
    }

    private static class InnerTheme implements Theme {
        private final String name;
        private final String userAgentStylesheet;
        private final List<String> stylesheets;
        private final ThemeGroup group;

        public InnerTheme(String name, String userAgentStylesheet, List<String> stylesheets, ThemeGroup group) {
            this.name = name;
            this.userAgentStylesheet = userAgentStylesheet;
            this.stylesheets = stylesheets;
            this.group = group;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getUserAgentStylesheet() {
            return userAgentStylesheet;
        }

        @Override
        public List<String> getStylesheets() {
            return stylesheets;
        }

        @Override
        public ThemeGroup getGroup() {
            return group;
        }

    }

    private static class InnerGroup implements ThemeGroup {
        private final String name;

        public InnerGroup(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

    }

}
