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
package com.gluonhq.jfxapps.boot.platform.internal;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.boot.platform.config.PlatformConfig;

@Component
public class JfxAppsPlatformImpl implements JfxAppsPlatform {

    private final PlatformConfig config;

    public JfxAppsPlatformImpl(PlatformConfig config) {
        super();
        this.config = config;
    }

    @Override
    public Path rootPath() {
        return config.getRootPath();
    }

    @Override
    public File rootFile() {
        return config.getRootPath().toFile();
    }

    @Override
    public File tempDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    @Override
    public File defaultUserM2Repository() {
        String m2Path = ".m2"; // NOCHECK

        assert m2Path != null;

        File target = new File(rootFile(), m2Path);
        if (!target.exists()) {
            target.mkdirs();
        }
        return target;
    }

    @Override
    public File defaultTempM2Repository() {
        String m2Path = "m2Tmp"; // NOCHECK

        assert m2Path != null;

        File target = new File(tempDir(), m2Path);
        if (!target.exists()) {
            target.mkdirs();
        }
        return target;
    }

    @Override
    public File getApplicationDataFolder() {
        return DefaultFolders.getApplicationDataFolder();
    }

    @Override
    public File getUserExtensionsFolder(UUID extensionId) {
        return DefaultFolders.getUserExtensionsFolder(extensionId);
    }

    @Override
    public File getMessageBoxFolder() {
        return DefaultFolders.getMessageBoxFolder();
    }
}
