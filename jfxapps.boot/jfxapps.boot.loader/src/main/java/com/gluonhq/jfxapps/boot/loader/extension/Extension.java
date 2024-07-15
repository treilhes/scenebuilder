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
package com.gluonhq.jfxapps.boot.loader.extension;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.layer.Layer;

/**
 * Some rules about extensions <br>
 * - Only one extension in the jar <br>
 * - No extensions in dependencies<br>
 * - The extended component library must have a provided scope<br>
 *
 * @author ptreilhes
 *
 */
public sealed interface Extension permits OpenExtension, SealedExtension, RootExtension {

    final static Logger logger = LoggerFactory.getLogger(Extension.class);

    final static UUID ROOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    UUID getId();

    UUID getParentId();

    List<Class<?>> localContextClasses();

    public default void initializeModule(Layer layer) {
        var module = this.getClass().getModule();

        logger.info("Add read to spring.core for {}", module.getName());

        com.gluonhq.jfxapps.spring.core.patch.PatchLink.addRead(module);
        com.gluonhq.jfxapps.hibernate.core.patch.PatchLink.addRead(module);
    }
//    InputStream getLicense();
//
//    InputStream getDescription();
//
//    InputStream getLoadingImage();
//
//    InputStream getIcon();
//
//    InputStream getIconX2();
}
