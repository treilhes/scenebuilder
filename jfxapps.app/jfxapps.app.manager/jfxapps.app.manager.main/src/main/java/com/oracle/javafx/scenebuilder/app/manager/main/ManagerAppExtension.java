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
package com.oracle.javafx.scenebuilder.app.manager.main;

import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.extension.EditorExtension;
import com.oracle.javafx.scenebuilder.api.appmngr.annotation.EditorDescriptor;
import com.oracle.javafx.scenebuilder.app.manager.main.api.ApplicationCard;
import com.oracle.javafx.scenebuilder.app.manager.main.api.ExtensionCard;
import com.oracle.javafx.scenebuilder.app.manager.main.ui.ManagerUiTemplate;
import com.oracle.javafx.scenebuilder.app.manager.main.ui.WindowIconSettings;
import com.oracle.javafx.scenebuilder.app.manager.main.ui.cmp.ApplicationCardController;
import com.oracle.javafx.scenebuilder.app.manager.main.ui.cmp.ExtensionCardController;

@EditorDescriptor(
            label = "manager.app.label",
            licence = "BSD",
            licenceFile = "LICENSE",
            description = "manager.app.description",
            image = "manager.png",
            imageX2 = "manager@2x.png",
            extensions = {"jfxmngr"}
        )
public class ManagerAppExtension  implements EditorExtension  {

    public final static UUID ID = EditorExtension.MANAGER_APP_ID;

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public UUID getParentId() {
        return ROOT_ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of(
                WindowIconSettings.class,
                ManagerUiTemplate.class,
                ApplicationCard.Factory.class,
                ApplicationCardController.class,
                ExtensionCard.Factory.class,
                ExtensionCardController.class
                );
    }

}
