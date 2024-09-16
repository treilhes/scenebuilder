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
package com.gluonhq.jfxapps.core.api.application.todelete;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.loader.extension.ApplicationExtension;

public interface EditorDescriptor {
    ApplicationExtension getExtension();
    String getLabel();
    String getDescription();
    String getLicence();
    URL getLicenceFile();
    URL getImage();
    URL getImageX2();
    List<String> handledFileExtensions();

    public static EditorDescriptor fromExtension(ApplicationExtension extension) {

        final com.gluonhq.jfxapps.core.api.application.annotation.EditorDescriptor annotation = extension.getClass()
                .getAnnotation(com.gluonhq.jfxapps.core.api.application.annotation.EditorDescriptor.class);

        assert annotation != null;

        EditorDescriptor editorDescriptor = EditorDescriptor.create(
                extension,
                annotation.label(),
                annotation.description(),
                annotation.licence(),
                extension.getClass().getResource(annotation.licenceFile()),
                extension.getClass().getResource(annotation.image()),
                extension.getClass().getResource(annotation.imageX2()),
                Arrays.asList(annotation.extensions()));

        return editorDescriptor;

    }

    static EditorDescriptor create(ApplicationExtension extension, String label, String description, String licence, URL licenceFile, URL image,
            URL imageX2, List<String> fileExtensions) {
        return new EditorDescriptor() {

            @Override
            public ApplicationExtension getExtension() {
                return extension;
            }

            @Override
            public String getLabel() {
                return label;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getLicence() {
                return licence;
            }

            @Override
            public URL getLicenceFile() {
                return licenceFile;
            }

            @Override
            public URL getImage() {
                return image;
            }

            @Override
            public URL getImageX2() {
                return imageX2;
            }

            @Override
            public List<String> handledFileExtensions() {
                return fileExtensions;
            }

        };
    }
}
