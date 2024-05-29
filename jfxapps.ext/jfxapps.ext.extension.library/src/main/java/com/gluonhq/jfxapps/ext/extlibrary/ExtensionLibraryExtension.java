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

package com.gluonhq.jfxapps.ext.extlibrary;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.ext.extlibrary.controller.ExtensionLibraryMenuController;
import com.gluonhq.jfxapps.ext.extlibrary.controller.ExtensionLibraryWindowController;
import com.gluonhq.jfxapps.ext.extlibrary.i18n.I18NExtensionLibrary;
import com.gluonhq.jfxapps.ext.extlibrary.importer.ExtensionImportWindowController;
import com.gluonhq.jfxapps.ext.extlibrary.library.ExtensionLibrary;
import com.gluonhq.jfxapps.ext.extlibrary.library.ExtensionLibraryDialogConfiguration;
import com.gluonhq.jfxapps.ext.extlibrary.library.builtin.ExtensionBuiltinLibrary;
import com.gluonhq.jfxapps.ext.extlibrary.library.builtin.ExtensionDefaultLibraryFilter;
import com.gluonhq.jfxapps.ext.extlibrary.library.explorer.ExtensionFileExplorer;
import com.gluonhq.jfxapps.ext.extlibrary.library.explorer.ExtensionFolderExplorer;
import com.gluonhq.jfxapps.ext.extlibrary.library.explorer.ExtensionMavenArtifactExplorer;
import com.gluonhq.jfxapps.ext.extlibrary.menu.ExtensionLibraryMenuProvider;

public class ExtensionLibraryExtension implements OpenExtension {

    public final static UUID ID = UUID.fromString("12337440-1d43-4cee-9a37-6b6b4aa8dca1");

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public UUID getParentId() {
        return OpenExtension.ROOT_ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
     // @formatter:off
        return Arrays.asList(
                ExtensionBuiltinLibrary.class,
                ExtensionDefaultLibraryFilter.class,
                ExtensionFileExplorer.class,
                ExtensionFolderExplorer.class,
                ExtensionImportWindowController.class,
                ExtensionLibrary.class,
                ExtensionLibraryDialogConfiguration.class,
                ExtensionLibraryMenuController.class,
                ExtensionLibraryMenuProvider.class,
                ExtensionLibraryWindowController.class,
                ExtensionMavenArtifactExplorer.class,
                I18NExtensionLibrary.class
            );
     // @formatter:on
    }
}
