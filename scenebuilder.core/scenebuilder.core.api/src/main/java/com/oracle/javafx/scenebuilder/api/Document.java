/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;

public interface Document {
    //API validated
    boolean isInited();
    boolean isUnused();
    boolean isDocumentDirty();
    boolean isEditing();
    boolean hasContent();
    boolean hasName();
    String getName();

    void logInfoMessage(String key);
    void logInfoMessage(String key, Object... args);

    //API to be validated

    /**
     * Load an fxml document from a local file and track his location.
     *
     * @param fxmlFile the fxml file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void loadFromFile(File fxmlFile) throws IOException;

    /**
     * Load an fxml document from an URL with or without tracking his location.
     * Tracking the document's location allow to define a default location for saving
     * and enable watching the location for external modifications.
     * Not tracking the location of an fxml file is mainly done for templates which prevent
     * overwriting them
     * @param url the url
     * @param keepTrackOfLocation keep track of the document location
     */
    void loadFromURL(URL url, boolean keepTrackOfLocation);

    void openWindow();
    void updatePreferences();

    void updateWithDefaultContent();

    void close();
    void onFocus();
    DocumentWindow getDocumentWindow();
    URL getFxmlLocation();
    void closeWindow();
    Editor getEditorController();
    FileTime getLoadFileTime();
    void updateLoadFileTime();

    public static class TitleComparator implements Comparator<Document> {

        @Override
        public int compare(Document d1, Document d2) {
            final int result;

            assert d1 != null;
            assert d2 != null;

            if (d1 == d2) {
                result = 0;
            } else {
                final String t1 = d1.getDocumentWindow().getStage().getTitle();
                final String t2 = d2.getDocumentWindow().getStage().getTitle();
                assert t1 != null;
                assert t2 != null;
                result = t1.compareTo(t2);
            }

            return result;
        }

    }

}
