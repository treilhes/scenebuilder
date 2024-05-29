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
package com.oracle.javafx.scenebuilder.gluon;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.core.extension.AbstractExtension;
import com.oracle.javafx.scenebuilder.gluon.controller.AlertController;
import com.oracle.javafx.scenebuilder.gluon.controller.GluonJarImportController;
import com.oracle.javafx.scenebuilder.gluon.controller.RegistrationController;
import com.oracle.javafx.scenebuilder.gluon.controller.TrackingController;
import com.oracle.javafx.scenebuilder.gluon.controller.UpdateController;
import com.oracle.javafx.scenebuilder.gluon.dialog.UpdateSceneBuilderDialog;
import com.oracle.javafx.scenebuilder.gluon.editor.job.AddPropertyValueJobExtension;
import com.oracle.javafx.scenebuilder.gluon.editor.job.SetFxomRootJobExtension;
import com.oracle.javafx.scenebuilder.gluon.i18n.I18NGluon;
import com.oracle.javafx.scenebuilder.gluon.menu.GluonMenuProvider;
import com.oracle.javafx.scenebuilder.gluon.metadata.GluonDocumentationUrlBuilder;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonSwatchDocumentPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.IgnoreVersionPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.ImportedGluonJarsPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.LastSentTrackingInfoDatePreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.RegistrationEmailPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.RegistrationHashPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.RegistrationOptInPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.ShowUpdateDialogDatePreference;
import com.oracle.javafx.scenebuilder.gluon.registration.RegistrationWindowController;
import com.oracle.javafx.scenebuilder.gluon.setting.VersionSetting;
import com.oracle.javafx.scenebuilder.gluon.template.GluonTemplateList;
import com.oracle.javafx.scenebuilder.gluon.theme.GluonThemesList;

public class GluonExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("1528d4d1-1518-4d34-9fc9-ec4d5b73292e");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                AddPropertyValueJobExtension.class,
                AlertController.class,
                GluonControlLibraryFilter.class,
                GluonDocumentationUrlBuilder.class,
                GluonInitializer.class,
                GluonJarImportController.class,
                GluonMenuProvider.class,
                GluonSwatchDocumentPreference.class,
                GluonSwatchPreference.class,
                GluonTemplateList.BasicPhoneTemplate.class,
                GluonTemplateList.EmptyPhoneTemplate.class,
                GluonThemesList.class,
                GluonThemesList.GluonMobileDark.class,
                GluonThemesList.GluonMobileLight.class,
                I18NGluon.class,
                IgnoreVersionPreference.class,
                ImportedGluonJarsPreference.class,
                LastSentTrackingInfoDatePreference.class,
                RegistrationController.class,
                RegistrationEmailPreference.class,
                RegistrationHashPreference.class,
                RegistrationOptInPreference.class,
                RegistrationWindowController.class,
                SetFxomRootJobExtension.class,
                ShowUpdateDialogDatePreference.class,
                TrackingController.class,
                UpdateController.class,
                UpdateSceneBuilderDialog.class,
                VersionSetting.class
            );
     // @formatter:on
    }
}
