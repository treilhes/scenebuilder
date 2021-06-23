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
package com.oracle.javafx.scenebuilder.fs.preference.global;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.ListOfStringPreference;

@Component
public class RecentItemsPreference extends ListOfStringPreference implements ManagedGlobalPreference {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "RECENT_ITEMS"; //NOI18N
    public static final List<String> PREFERENCE_DEFAULT_VALUE = new ArrayList<>();

	private final RecentItemsSizePreference recentItemsSize;

	public RecentItemsPreference(
			@Autowired PreferencesContext preferencesContext,
			@Lazy @Autowired RecentItemsSizePreference recentItemsSize
			) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
		this.recentItemsSize = recentItemsSize;
	}

    public boolean containsRecentItem(File file) {
        final String path = file.getPath();
        return getValue().contains(path);
    }

    public boolean containsRecentItem(URL url) {
        final File fxmlFile;
        try {
            fxmlFile = new File(url.toURI());
            return containsRecentItem(fxmlFile);
        } catch (URISyntaxException ex) {
            Logger.getLogger(RecentItemsPreference.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void addRecentItem(File file) {
        final List<File> files = new ArrayList<>();
        files.add(file);
        addRecentItems(files);
    }

    public void addRecentItem(URL url) {
        final File fxmlFile;
        try {
            fxmlFile = new File(url.toURI());
            addRecentItem(fxmlFile);
        } catch (URISyntaxException ex) {
            Logger.getLogger(RecentItemsPreference.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addRecentItems(List<File> files) {
        for (File file : files) {
            final String path = file.getPath();
            if (getValue().contains(path)) {
                getValue().remove(path);
            }
            // Add the specified file to the recent items at first position
            getValue().add(0, path);
        }
        // Remove last items depending on the size
        while (getValue().size() > recentItemsSize.getValue()) {
            getValue().remove(getValue().size() - 1);
        }
        writeToJavaPreferences();
    }

    public void removeRecentItems(List<String> filePaths) {
        // Remove the specified files from the recent items
        for (String filePath : filePaths) {
            getValue().remove(filePath);
        }
        writeToJavaPreferences();
    }

    public void clearRecentItems() {
        getValue().clear();
        writeToJavaPreferences();
        getPreferencesContext().getDocumentsNode().clearAllDocumentNodes();
    }
}
