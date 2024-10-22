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
package com.gluonhq.jfxapps.core.fs.controller;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.fs.preference.RecentItemsPreference;
import com.gluonhq.jfxapps.core.fs.preference.RecentItemsSizePreference;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;

@ApplicationSingleton
public class RecentItemsController {

    private static final Logger logger = LoggerFactory.getLogger(RecentItemsController.class);

    private final RecentItemsPreference recentItemsPreference;
    private final RecentItemsSizePreference recentItemsSize;

    public RecentItemsController(
            RecentItemsPreference recentItemsPreference,
            RecentItemsSizePreference recentItemsSize) {
        this.recentItemsPreference = recentItemsPreference;
        this.recentItemsSize = recentItemsSize;

        this.recentItemsSize.getObservableValue().addListener(cleanRecentItems(recentItemsPreference));
    }


    private ChangeListener<? super Integer> cleanRecentItems(RecentItemsPreference recentItemsPreference) {
        return (c, o, n) -> {
            var list = recentItemsPreference.getValue();
            var max = n.intValue();
            while (list.size() > max) {
                list.remove(list.size() - 1);
            }
            recentItemsPreference.save();
        };
    }


    public boolean containsRecentItem(File file) {
        final String path = file.getPath();
        return recentItemsPreference.getValue().contains(path);
    }

    public boolean containsRecentItem(URL url) {
        final File fxmlFile;
        try {
            fxmlFile = new File(url.toURI());
            return containsRecentItem(fxmlFile);
        } catch (URISyntaxException ex) {
            logger.error("", ex);
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
            logger.error("", ex);
        }
    }

    public void addRecentItems(List<File> files) {
        var list = recentItemsPreference.getValue();
        for (File file : files) {
            final String path = file.getPath();

            if (list.contains(path)) {
                list.remove(path);
            }
            // Add the specified file to the recent items at first position
            list.add(0, path);
        }
        // Remove last items depending on the size
        while (list.size() > recentItemsSize.getValue()) {
            list.remove(list.size() - 1);
        }
        recentItemsPreference.save();
    }

    public void removeRecentItems(List<String> filePaths) {
        var list = recentItemsPreference.getValue();
        // Remove the specified files from the recent items
        for (String filePath : filePaths) {
            list.remove(filePath);
        }
        recentItemsPreference.save();
    }

    public void clearRecentItems() {
        recentItemsPreference.getValue().clear();
        recentItemsPreference.save();
    }


    public ObservableList<String> getRecentItems() {
        return recentItemsPreference.getValue();
    }
}
