/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.fs.RecentItems;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.fs.preference.InitialDirectoryPreference;

import javafx.collections.ObservableList;

@ApplicationInstanceSingleton
public class FileSystemController implements FileSystem {

    private final static Logger logger = LoggerFactory.getLogger(FileSystemController.class);

    //private final JfxAppPlatform jfxAppPlatform;
    //private final ApplicationEvents applicationEvents;
    //private final FxomEvents fxomEvents;
    //private final FXOMDocumentFactory fxomDocumentFactory;
    private final RecentItems recentItems;
    private final InitialDirectoryPreference initialDirectoryPreference;
    //private final FXOMSerializer serializer;
    private final FileWatchController fileWatchController;

    // @formatter:off
    public FileSystemController(
            //JfxAppPlatform jfxAppPlatform,
            //ApplicationEvents applicationEvents,
            //FxomEvents fxomEvents,
            //FXOMDocumentFactory fxomDocumentFactory,
            RecentItems recentItems,
            InitialDirectoryPreference initialDirectoryPreference,
            //FXOMSerializer serializer,
            FileWatchController fileWatchController) {
     // @formatter:on
        //this.jfxAppPlatform = jfxAppPlatform;
        //this.fxomEvents = fxomEvents;
        //this.applicationEvents = applicationEvents;
        //this.fxomDocumentFactory = fxomDocumentFactory;
        this.recentItems = recentItems;
        this.initialDirectoryPreference = initialDirectoryPreference;
        //this.serializer = serializer;
        this.fileWatchController = fileWatchController;
    }

    @Override
    public File getNextInitialDirectory() {
        return initialDirectoryPreference.getValue();
    }

    @Override
    public void updateNextInitialDirectory(File chosenFile) {
        assert chosenFile != null;

        final Path chosenFolder = chosenFile.toPath().getParent();
        if (chosenFolder != null) {
            initialDirectoryPreference.setValue(chosenFolder.toFile()).save();
        }
    }

    @Override
    public void watch(MainInstanceWindow document, Set<Path> files, WatchingCallback callback) {
        List<File> fileList = files.stream().map(p -> p.toFile()).collect(Collectors.toList());
        watch(document, fileList, callback);
    }

    @Override
    public void watch(MainInstanceWindow document, List<File> files, WatchingCallback callback) {
        fileWatchController.watch(document, files, callback);
    }

    @Override
    public void unwatch(Object key) {
        fileWatchController.unwatch(key);
    }

    @Override
    public void unwatchDocument(MainInstanceWindow document) {
        fileWatchController.unwatchDocument(document);
    }

    @Override
    public void startWatcher() {
        fileWatchController.startWatcher();
    }

    @Override
    public void stopWatcher() {
        fileWatchController.stopWatcher();
    }


//    @Override
//    public File getMessageBoxFolder() {
//        return JfxAppsPlatform.getMessageBoxFolder();
//    }
//
//    @Override
//    public File getApplicationDataFolder() {
//        return JfxAppsPlatform.getApplicationDataFolder();
//    }


    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

//    @Override
//    public void loadFromFile(File fxmlFile, boolean keepTrackOfLocation) throws IOException {
//        final URL fxmlURL = fxmlFile.toURI().toURL();
//        loadFromURL(fxmlURL, true);
//
//        // TODO remove after checking the new watching system is operational in
//        // EditorController or in filesystem
//        // watchingController.update();
//
//        // WarnThemeAlert.showAlertIfRequired(themePreference,
//        // editorController.getFxomDocument(), documentWindow.getStage());
//    }

//    @Override
//    public void loadFromURL(URL fxmlURL, boolean keepTrackOfLocation) {
//        assert fxmlURL != null;
//        try {
//            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
//            setFxmlTextAndLocation(fxmlText, keepTrackOfLocation ? fxmlURL : null, false);
//            updateLoadFileTime();
//
//            // TODO remove after checking the new watching system is operational in
//            // EditorController or in filesystem
//            // watchingController.update();
//        } catch (IOException x) {
//            throw new IllegalStateException(x);
//        }
//    }

//    @Override
//    public void loadDefaultContent() {
//        try {
//            setFxmlTextAndLocation("", null, true); // NOI18N
//            updateLoadFileTime();
//        } catch (IOException x) {
//            throw new IllegalStateException(x);
//        }
//    }
//
//    @Override
//    public void reload() throws IOException{
//        final FXOMDocument fxomDocument = fxomEvents.fxomDocument().get();
//        assert (fxomDocument != null) && (fxomDocument.getLocation() != null);
//        final URL fxmlURL = fxomDocument.getLocation();
//        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
//        setFxmlTextAndLocation(fxmlText, fxmlURL, true);
//        updateLoadFileTime();
//        // Here we do not invoke updateStageTitleAndPreferences() neither
//        // watchingController.update()
//    }


//    /**
//     * Sets both fxml text and location to be edited by this editor. Performs
//     * setFxmlText() and setFxmlLocation() but in a optimized manner (it avoids an
//     * extra scene graph refresh).
//     *
//     * @param fxmlText     null or the fxml text to be edited
//     * @param fxmlLocation null or the location of the fxml text being edited
//     * @param checkTheme   if set to true a check will be made if the fxml contains
//     *                     Gluon controls and if so, the correct theme is set
//     * @throws IOException if fxml text cannot be parsed and loaded correctly.
//     */
//    //@Override
//    private void setFxmlTextAndLocation(String fxmlText, URL fxmlLocation, boolean checkTheme) throws IOException {
//
//        I18nResourceProvider i18nResources = fxomEvents.i18nResourceConfig().get();
//
//        updateFxomDocument(fxmlText, fxmlLocation,
//                new CombinedResourceBundle(i18nResources == null ? new ArrayList<>() : i18nResources.getBundles(), false),
//                checkTheme);
//
//        if (fxmlLocation != null) {
//            // recentItems may not contain the current document
//            // if the Open Recent -> Clear menu has been invoked
//            if (!recentItems.containsRecentItem(fxmlLocation)) {
//                recentItems.addRecentItem(fxmlLocation);
//            }
//        }
//
//    }


//    private void updateFxomDocument(String fxmlText, URL fxmlLocation, ResourceBundle resources, boolean checkTheme)
//            throws IOException {
//        final FXOMDocument newFxomDocument;
//
//        if (fxmlText != null) {
//            newFxomDocument = fxomDocumentFactory.newDocument(fxmlText, fxmlLocation, applicationEvents.classloader().get(),
//                    resources);
//        } else {
//            newFxomDocument = null;
//        }
//
//        fxomEvents.fxomDocument().set(newFxomDocument);
//
//        updateFileWatcher(newFxomDocument);
//
//    }


//    @Override
//    public void save() throws IOException {
//        final FXOMDocument fxomDocument = fxomEvents.fxomDocument().get();
//        assert fxomDocument != null;
//        assert fxomDocument.getLocation() != null;
//
//        final Path fxmlPath;
//        try {
//            fxmlPath = Paths.get(fxomDocument.getLocation().toURI());
//        } catch (URISyntaxException x) {
//            // Should not happen
//            throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); // NOI18N
//        }
//
//        saveAs(fxmlPath.toFile());
//    }
//
//    @Override
//    public void saveAs(File target) throws IOException {
//        final FXOMDocument fxomDocument = fxomEvents.fxomDocument().get();
//        assert fxomDocument != null;
//
//        final Path fxmlPath = Paths.get(target.toURI());
//
//        final byte[] fxmlBytes = serializer.serialize(fxomDocument).getBytes(StandardCharsets.UTF_8); // NOI18N
//        Files.write(fxmlPath, fxmlBytes);
//
//        updateLoadFileTime();
//
//        fxomEvents.dirty().set(false);
//        fxomEvents.saved().set(true);
//    }

    /**
     * {@inheritDoc}
     * @deprecated use {@link RecentItems#getRecentItems} instead
     */
    @Deprecated
    @Override
    public ObservableList<String> getRecentItems() {
        return recentItems.getRecentItems();
    }

    /**
     * {@inheritDoc}
     * @deprecated use {@link RecentItems#getRecentItems} instead
     */
    @Deprecated
    @Override
    public void cleanupRecentItems() {
        recentItems.cleanupRecentItems();
    }

    @Override
    public void save(InputStream input, File target) throws IOException {
        Files.copy(input, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        recentItems.addRecentItem(target);
    }

}
