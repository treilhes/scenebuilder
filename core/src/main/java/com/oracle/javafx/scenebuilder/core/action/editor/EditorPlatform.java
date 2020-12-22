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
package com.oracle.javafx.scenebuilder.core.action.editor;

import java.util.Locale;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javafx.scene.input.MouseEvent;

/**
 * This class contains static methods that depends on the platform.
 *
 * @treatAsPrivate
 */
public class EditorPlatform {

	// TODO remove from here, added only to allow core compilation
	// TODO must be removed after gluon extension extraction
	public static final String GLUON_FILE_PREFIX  = "Gluon_";

    private static final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT); //NOI18N

    /**
     * True if current platform is running Linux.
     */
    public static final boolean IS_LINUX = osName.contains("linux"); //NOI18N

    /**
     * Spring bean condition, True if current platform is running Linux.
     */
    public static final class IS_LINUX_CONDITION implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return EditorPlatform.IS_LINUX;
		}
   }

    /**
     * True if current platform is running Mac OS X.
     */
    public static final boolean IS_MAC = osName.contains("mac"); //NOI18N

    /**
     * Spring bean condition, True if current platform is running Mac.
     */
    public static final class IS_MAC_CONDITION implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return EditorPlatform.IS_MAC;
		}
   }

    /**
     * True if current platform is running Windows.
     */
    public static final boolean IS_WINDOWS = osName.contains("windows"); //NOI18N

    /**
     * Spring bean condition, True if current platform is running Windows.
     */
    public static final class IS_WINDOWS_CONDITION implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return EditorPlatform.IS_MAC;
		}
   }

    /**
     * Gluon Glisten package
     */
    public static final String GLUON_PACKAGE = "com.gluonhq.charm.glisten";

    /**
     * This URL is where you go when the user takes Scene Builder Help action (shortcut F1)
     */
    public static final String DOCUMENTATION_URL = "https://docs.oracle.com/javafx/index.html"; //NOI18N

    /**
     * Javadoc home (for Inspector and CSS Analyzer properties)
     */
    public final static String JAVADOC_HOME = "https://openjfx.io/javadoc/11/"; //NOI18N

    /**
     * Gluon javadoc home (for Inspector and CSS Analyzer properties)
     */
    public final static String GLUON_JAVADOC_HOME = "http://docs.gluonhq.com/charm/javadoc/" + "latest" +"/"; //NOI18N

    /**
     * scene builder specific tweaks to Gluon theme
     */
    public static final String GLUON_DOCUMENT_STYLESHEET = "com/oracle/javafx/scenebuilder/app/css/GluonDocument.css";


    /**
     * Returns true if the modifier key for continuous selection is down.
     *
     * @param e mouse event to check (never null)
     * @return true if the modifier key for continuous selection is down.
     */
    public static boolean isContinuousSelectKeyDown(MouseEvent e) {
        return e.isShiftDown();
    }

    /**
     * Returns true if the modifier key for non-continuous selection is down.
     *
     * @param e mouse event to check (never null).
     * @return true if the modifier key for non-continuous selection is down.
     */
    public static boolean isNonContinousSelectKeyDown(MouseEvent e) {
        return IS_MAC ? e.isMetaDown(): e.isControlDown();
    }

    /**
     * Returns true if the jvm is running with assertions enabled.
     *
     * @return true if the jvm is running with assertions enabled.
     */
    public static boolean isAssertionEnabled() {
        return EditorPlatform.class.desiredAssertionStatus();
    }



}
