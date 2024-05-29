/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package org.scenebuilder.fxml.api;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.scenebuilder.fxml.api.i18n.I18NFxmlApi;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager.FxmlDocumentManagerImpl;

import com.gluonhq.jfxapps.boot.loader.extension.ApplicationExtension;
import com.gluonhq.jfxapps.core.api.application.annotation.EditorDescriptor;
import com.gluonhq.jfxapps.core.extension.AbstractExtension;
import com.gluonhq.jfxapps.core.ui.controller.ModeManagerController;
import com.oracle.javafx.scenebuilder.api.control.driver.DriverExtensionRegistry;
import com.oracle.javafx.scenebuilder.api.control.driver.GenericDriver;
import com.oracle.javafx.scenebuilder.api.control.inlineedit.SimilarInlineEditorBounds;
import com.oracle.javafx.scenebuilder.api.control.pickrefiner.NoPickRefiner;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.fxml.preferences.global.ParentRingColorPreference;

@EditorDescriptor(
            label = "scenebuilder.app.label",
            licence = "BSD",
            licenceFile = "LICENSE",
            description = "scenebuilder.app.description",
            image = "scenebuilder.png",
            imageX2 = "scenebuilder@2x.png",
            extensions = {"?????"}
        )

public class FxmlApiExtension implements ApplicationExtension  {

    public static final UUID ID = UUID.fromString("06ae1f67-a8aa-49e3-abb5-4f108534578c");

    @Override
    public UUID getId() {
        return ID;
    }


    @Override
    public UUID getParentId() {
        return ApplicationExtension.ROOT_ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
     // @formatter:off
        return Arrays.asList(
                ParentRingColorPreference.class,
                DesignHierarchyMask.class,
                DesignHierarchyMask.Factory.class,

                DriverExtensionRegistry.class,
                GenericDriver.class,

                ModeManagerController.class,

                NoPickRefiner.class,

                SimilarInlineEditorBounds.class,

                FxmlDocumentManagerImpl.class,
                I18NFxmlApi.class
            );
     // @formatter:on
    }

}
