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
package com.gluonhq.jfxapps.app.devtools.ext.strchk.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import com.gluonhq.jfxapps.app.devtools.ext.strchk.config.CommonConfig;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.model.CssFile;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.model.FxmlFile;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.model.I18nFile;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.model.ResourceFile;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.model.StringOccurence;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.utils.Patterns;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.utils.StringValue;

public class ResourceLoader {

    public static I18nFile loadI18nFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        boolean localized = file.getName().contains("_");
        String fileName = file.getName();
        String packageName = pathToPackage(path);

        String locale = localized ? fileName.substring(fileName.indexOf("_"), fileName.indexOf(".")) : "";

        Properties properties = new Properties();
        try(FileInputStream fis = new FileInputStream(file)){
            properties.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
            return new I18nFile(file, packageName, locale, null);
        }

        return new I18nFile(file, packageName, locale, properties);
    }

    private static String pathToPackage(String path) {
        String packageName = path.replace("\\", "/");
        if (packageName.contains(CommonConfig.PROJECT_RESOURCE_FOLDER)) {
            packageName = packageName.substring(packageName.indexOf(CommonConfig.PROJECT_RESOURCE_FOLDER) + CommonConfig.PROJECT_RESOURCE_FOLDER.length());
        } else {
            packageName = packageName.substring(packageName.indexOf(CommonConfig.PROJECT_JAVA_FOLDER) + CommonConfig.PROJECT_JAVA_FOLDER.length());
        }

        if (packageName.startsWith("/")) {
            packageName = packageName.substring(1);
        }

        packageName = packageName.replace("/", ".");
        return packageName;
    }
    public static FxmlFile loadFxmlFile(File file) {
        try {
            String path = file.getParentFile().getAbsolutePath();

            String packageName = pathToPackage(path);

            FxmlFile fxml = new FxmlFile(file, packageName);

            String content = new String(Files.readAllBytes(file.toPath()));

            Patterns.STRING_IN_FXML.matcher(content).results().forEach(r -> {
                if (StringValue.isValidCandidate(r.group(1))) {
                    fxml.getStringOccurences().add(new StringOccurence(r.group(1)));
                }
            });

            Patterns.I18N_STRING_IN_FXML.matcher(content).results().forEach(r -> {
                if (StringValue.isValidCandidate(r.group(1))) {
                    fxml.getStringOccurences().add(new StringOccurence(r.group(1)));
                }
            });

            return fxml;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CssFile loadCssFile(File file) {
        try {
            String path = file.getParentFile().getAbsolutePath();
            String packageName = pathToPackage(path);

            CssFile css = new CssFile(file, packageName);

            String content = new String(Files.readAllBytes(file.toPath()));

            Patterns.STRING.matcher(content).results().forEach(r -> {
                if (StringValue.isValidCandidate(r.group(1))) {
                    css.getStringOccurences().add(new StringOccurence(r.group(1)));
                }
            });

            return css;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResourceFile loadResourceFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        String packageName = pathToPackage(path);

        return new ResourceFile(file, packageName);
    }

}
