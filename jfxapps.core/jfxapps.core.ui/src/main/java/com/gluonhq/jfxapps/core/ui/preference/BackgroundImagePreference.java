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
package com.gluonhq.jfxapps.core.ui.preference;

import java.net.URL;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.ui.preference.BackgroundImagePreference.BackgroundImage;
import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;

import javafx.scene.Parent;
import javafx.scene.image.Image;

//@formatter:off
@ApplicationSingleton
@PreferenceContext(id = "59fc7fe2-70d4-4d90-beee-ae57703d6e9f",
      name = BackgroundImagePreference.PREFERENCE_KEY,
      defaultValueProvider = BackgroundImagePreference.DefaultProvider.class)
//@formatter:on
public interface BackgroundImagePreference
        extends Preference<BackgroundImage>, ManagedGlobalPreference, UserPreference<BackgroundImage> {

    /***************************************************************************
     * * Support Classes * *
     **************************************************************************/

    // FIXME enums keys need to be resolved be i18n bean
    public enum BackgroundImage {

        BACKGROUND_01 {

            @Override
            public String toString() {
                // return I18N.getString("prefs.background.value1");
                return "prefs.background.value1";
            }
        },
        BACKGROUND_02 {

            @Override
            public String toString() {
                // return I18N.getString("prefs.background.value2");
                return "prefs.background.value2";
            }
        },
        BACKGROUND_03 {

            @Override
            public String toString() {
                // return I18N.getString("prefs.background.value3");
                return "prefs.background.value3";
            }
        }
    }

    /***************************************************************************
     * * Static fields * *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "prefs.background"; // NOCHECK
    public static final BackgroundImage PREFERENCE_DEFAULT_VALUE = BackgroundImage.BACKGROUND_03;

    public static Image getImage(BackgroundImage bgi) {
        final URL url;
        switch (bgi) {
        case BACKGROUND_01:
            url = BackgroundImagePreference.class.getResource("Background-Blue-Grid.png");
            break;
        case BACKGROUND_02:
            url = BackgroundImagePreference.class.getResource("Background-Neutral-Grid.png");
            break;
        case BACKGROUND_03:
            url = BackgroundImagePreference.class.getResource("Background-Neutral-Uniform.png");
            break;
        default:
            url = null;
            assert false;
            break;
        }
        assert url != null;
        return new Image(url.toExternalForm());
    }

    default Image getBackgroundImageImage() {
        return getImage(getValue());
    }

    @Override
    default String getLabelI18NKey() {
        return PREFERENCE_KEY;
    }

    @Override
    default Parent getEditor() {
        return getPreferenceEditorFactory().newEnumFieldEditor(this);
    }

    @Override
    default PreferenceGroup getGroup() {
        return DefaultPreferenceGroups.GLOBAL_GROUP_B;
    }

    @Override
    default String getOrderKey() {
        return getGroup().getOrderKey() + "_A";
    }

    public static class DefaultProvider implements DefaultValueProvider<BackgroundImage> {
        @Override
        public BackgroundImage get() {
            return PREFERENCE_DEFAULT_VALUE;
        }
    }
}
