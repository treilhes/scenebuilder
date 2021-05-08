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
package com.oracle.javafx.scenebuilder.controllibrary.tmp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.library.LibraryFilter;
import com.oracle.javafx.scenebuilder.controllibrary.tmp.ControlReportEntryImpl.SubStatus;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.LibraryUtil;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ControlExplorerUtil {

    private static final Logger logger = LoggerFactory.getLogger(ControlExplorerUtil.class);
    
    private ControlExplorerUtil() {}
    
    public static Object instantiateWithFXMLLoader(Class<?> klass, ClassLoader classLoader) throws IOException {
        final String fxmlText = LibraryUtil.makeFxmlText(klass);
        return instantiateWithFXMLLoader(fxmlText, classLoader);
    }
    
    public static Object instantiateWithFXMLLoader(String fxmlText, ClassLoader classLoader) throws IOException {
        Object result;

        final byte[] fxmlBytes = fxmlText.getBytes(Charset.forName("UTF-8")); //NOI18N

        final FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.setClassLoader(classLoader);
            result = fxmlLoader.load(new ByteArrayInputStream(fxmlBytes));
        } catch(IOException x) {
            throw x;
        } catch(RuntimeException|Error x) {
            throw new IOException(x);
        }

        return result;
    }

    public static String makeClassName(String entryName, String separator) {
        final String result;

        if (! entryName.endsWith(".class")) { //NOI18N
            result = null;
        } else if (entryName.contains("$")) { //NOI18N
            // We skip inner classes for now
            result = null;
        } else {
            final int endIndex = entryName.length()-6; // ".class" -> 6 //NOI18N
            result = entryName.substring(0, endIndex).replace(separator, "."); //NOI18N
        }

        return result;
    }

    public static ControlReportEntryImpl exploreEntry(String entryName, ClassLoader classLoader, String className, List<LibraryFilter> filters) {
        if (entryName == null || className == null) {
            return null;
        }
        filters = filters == null ? new ArrayList<>() : filters;
        
        ControlReportEntryImpl.Status status;
        ControlReportEntryImpl.SubStatus subStatus = SubStatus.NONE;
        Throwable entryException;
        Class<?> entryClass = null;

        // Filtering out what starts with com.javafx. is bound to DTL-6378.
        if (filters.stream().anyMatch(f -> f.isFiltered(className))) { //NOI18N
            status = ControlReportEntryImpl.Status.IGNORED;
            entryClass = null;
            entryException = null;
        } else {
            try {
                // Some reading explaining why using Class.forName is not appropriate:
                // http://blog.osgi.org/2011/05/what-you-should-know-about-class.html
                // http://blog.bjhargrave.com/2007/09/classforname-caches-defined-class-in.html
                // http://stackoverflow.com/questions/8100376/class-forname-vs-classloader-loadclass-which-to-use-for-dynamic-loading
                entryClass = classLoader.loadClass(className); // Note: static intializers of entryClass are not run, this doesn't seem to be an issue

                if (Modifier.isAbstract(entryClass.getModifiers())
                        || !Node.class.isAssignableFrom(entryClass)) {
                    status = ControlReportEntryImpl.Status.IGNORED;
                    entryClass = null;
                    entryException = null;
                } else {
                    instantiateWithFXMLLoader(entryClass, classLoader);
                    status = ControlReportEntryImpl.Status.OK;
                    entryException = null;
                }
            } catch (RuntimeException | IOException x) {
                status = ControlReportEntryImpl.Status.KO;
                subStatus = ControlReportEntryImpl.SubStatus.CANNOT_INSTANTIATE;
                entryException = x;
            } catch (Error | ClassNotFoundException x) {
                status = ControlReportEntryImpl.Status.KO;
                subStatus = ControlReportEntryImpl.SubStatus.CANNOT_LOAD;
                entryClass = null;
                entryException = x;
            }
        }

        if (entryException != null) {
            logger.warn("Exception while exploring entry {}", entryName, entryException);
        }
        
        return new ControlReportEntryImpl(entryName, status, subStatus, entryException, entryClass, className);
    }
    
    public static ControlReportEntryImpl exploreFxml(Path fxmlFile, ClassLoader classLoader) {
        ControlReportEntryImpl.Status status;
        ControlReportEntryImpl.SubStatus subStatus = SubStatus.NONE;
        Throwable entryException;
        Class<?> entryClass = null;

        try {
            String content = Files.readString(fxmlFile, StandardCharsets.UTF_8);
            FXOMDocument result= new FXOMDocument(content, null, classLoader, null);
            entryClass = result.getFxomRoot().getSceneGraphObject().getClass();
            status = ControlReportEntryImpl.Status.OK;
            entryException = null;
        } catch (RuntimeException | IOException x) {
            status = ControlReportEntryImpl.Status.KO;
            subStatus = ControlReportEntryImpl.SubStatus.CANNOT_INSTANTIATE;
            entryException = x;
        } catch (Error x) {
            status = ControlReportEntryImpl.Status.KO;
            subStatus = ControlReportEntryImpl.SubStatus.CANNOT_LOAD;
            entryClass = null;
            entryException = x;
        }

        if (entryException != null) {
            logger.warn("Exception while exploring entry {}", fxmlFile, entryException);
        }
        
        try {
//            return new ControlReportEntryImpl(fxmlFile.getFileName().toString(), status, 
//                    entryException, entryClass, entryClass == null ? null : entryClass.getName());
            return new ControlReportEntryImpl(fxmlFile.getFileName().toString(), status, subStatus,
                    entryException, entryClass, entryClass.getName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
