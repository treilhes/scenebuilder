/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.devutils.loader;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.oracle.javafx.scenebuilder.devutils.model.FxmlFile;
import com.oracle.javafx.scenebuilder.devutils.model.I18nFile;
import com.oracle.javafx.scenebuilder.devutils.model.ResourceFile;

public class ResourceLoader {
    
    private static final String RESOURCE_FOLDER = "/resources/";

    public static I18nFile loadI18nFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        boolean localized = file.getName().contains("_");
        String fileName = file.getName();
        String packageName = path.substring(path.indexOf(RESOURCE_FOLDER) + RESOURCE_FOLDER.length())
                .replace(File.pathSeparator, ".");
        String baseName = fileName.substring(localized ? fileName.indexOf("_") : fileName.indexOf("."));
        String locale = localized ? fileName.substring(fileName.indexOf("_"), fileName.indexOf(".")) : "";
        
        Properties properties = new Properties();
        try(FileInputStream fis = new FileInputStream(file)){
            properties.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
            return new I18nFile(baseName, packageName, locale, null);
        }
        
        return new I18nFile(baseName, packageName, locale, properties);
    }

    public static FxmlFile loadFxmlFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        String fileName = file.getName();
        String packageName = path.substring(path.indexOf(RESOURCE_FOLDER) + RESOURCE_FOLDER.length())
                .replace(File.pathSeparator, ".");
        
        return new FxmlFile(fileName, packageName);
    }

    public static ResourceFile loadResourceFile(File file) {
        String path = file.getParentFile().getAbsolutePath();
        String fileName = file.getName();
        String packageName = path.substring(path.indexOf(RESOURCE_FOLDER) + RESOURCE_FOLDER.length())
                .replace(File.pathSeparator, ".");
        
        return new ResourceFile(fileName, packageName);
    }

}
