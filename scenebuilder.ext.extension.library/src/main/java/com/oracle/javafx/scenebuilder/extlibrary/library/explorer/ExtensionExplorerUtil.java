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
package com.oracle.javafx.scenebuilder.extlibrary.library.explorer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionLibraryFilter;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionReportEntry;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionReportEntry.SubStatus;

public class ExtensionExplorerUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionExplorerUtil.class);
    
    public final static String EXTENSION_SERVICE_FILE = "META-INF/services/com.oracle.javafx.scenebuilder.extension.Extension";
    
    private ExtensionExplorerUtil() {}

    public static ExtensionReportEntry exploreEntry(ClassLoader classLoader, String className, List<ExtensionLibraryFilter> filters) {
        if (className == null) {
            return null;
        }
        filters = filters == null ? new ArrayList<>() : filters;
        
        ExtensionReportEntry.Status status;
        ExtensionReportEntry.SubStatus subStatus = SubStatus.NONE;
        Throwable entryException;
        Class<?> entryClass = null;

        // Filtering out what starts with com.javafx. is bound to DTL-6378.
        if (filters.stream().anyMatch(f -> f.isFiltered(className))) { //NOCHECK
            status = ExtensionReportEntry.Status.IGNORED;
            entryClass = null;
            entryException = null;
        } else {
            try {
                // Some reading explaining why using Class.forName is not appropriate:
                // http://blog.osgi.org/2011/05/what-you-should-know-about-class.html
                // http://blog.bjhargrave.com/2007/09/classforname-caches-defined-class-in.html
                // http://stackoverflow.com/questions/8100376/class-forname-vs-classloader-loadclass-which-to-use-for-dynamic-loading
                entryClass = classLoader.loadClass(className); // Note: static intializers of entryClass are not run, this doesn't seem to be an issue

                if (!Extension.class.isAssignableFrom(entryClass)) {
                    status = ExtensionReportEntry.Status.IGNORED;
                    entryClass = null;
                    entryException = null;
                } else {

                    status = ExtensionReportEntry.Status.OK;
                    entryException = null;
                }
            } catch (RuntimeException x) {
                status = ExtensionReportEntry.Status.KO;
                subStatus = ExtensionReportEntry.SubStatus.CANNOT_INSTANTIATE;
                entryException = x;
            } catch (Error | ClassNotFoundException x) {
                status = ExtensionReportEntry.Status.KO;
                subStatus = ExtensionReportEntry.SubStatus.CANNOT_LOAD;
                entryClass = null;
                entryException = x;
            }
        }

        if (entryException != null) {
            logger.warn("Exception while exploring class {}", className, entryException);
        }
        
        return new ExtensionReportEntry(className, status, subStatus, entryException, entryClass, className);
    }
        
}
