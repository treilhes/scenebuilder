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
package com.gluonhq.jfxapps.core.extstore.fs;

import java.io.File;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.loader.extension.Extension;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;

@ApplicationSingleton
public class ExtensionFileSystemFactory {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionFileSystemFactory.class);

    private final FileSystem fileSystem;

    private final Dialog dialog;

    public ExtensionFileSystemFactory(
            FileSystem fileSystem,
            Dialog dialog) {
        super();
        this.fileSystem = fileSystem;
        this.dialog = dialog;
    }

    public ExtensionFileSystem get(Class<? extends Extension> extensionClass) {

        Optional<Extension> optionalExtension = ServiceLoader.load(Extension.class).stream()
                .filter(p -> p.type() == extensionClass)
                .map(Provider::get)
                .findFirst();

        if (optionalExtension.isEmpty()) {
            logger.error("Unable to load the required extension filesystem, extension not found {}", extensionClass.getName());
            return null;
        }

        Extension instance = optionalExtension.get();

        String folderName = instance.getId().toString();

        File target = new File(fileSystem.getApplicationDataFolder(), folderName);

        return new ExtensionFileSystemImpl(target.toPath(), dialog);
    }




}
