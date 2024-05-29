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
package com.gluonhq.jfxapps.core.fs.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.application.InstanceWindow;
import com.gluonhq.jfxapps.core.api.di.SbPlatform;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.CombinedResourceBundle;
import com.gluonhq.jfxapps.core.api.i18n.I18nResourceProvider;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.fs.preference.document.PathPreference;
import com.gluonhq.jfxapps.core.fs.preference.global.InitialDirectoryPreference;
import com.gluonhq.jfxapps.core.fs.preference.global.RecentItemsPreference;
import com.gluonhq.jfxapps.core.fs.preference.global.WildcardImportsPreference;
import com.gluonhq.jfxapps.core.fs.util.FileWatcher;
import com.gluonhq.jfxapps.core.fxom.FXOMAssetIndex;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

import jakarta.inject.Provider;

@ApplicationInstanceSingleton
public class FileSystemController implements FileWatcher.Delegate, FileSystem {

    private final static Logger logger = LoggerFactory.getLogger(FileSystemController.class);

    private final InitialDirectoryPreference initialDirectoryPreference;
    private final RecentItemsPreference recentItemsPreference;
    private final Provider<PathPreference> pathPreference;
    private final WildcardImportsPreference wildcardImportsPreference;

    private final Map<InstanceWindow, List<Object>> documentWatchKeys = new HashMap<>();
    private final Map<Object, List<Path>> watchedFiles = new HashMap<>();
    private final Map<Path, List<WatchingCallback>> watchCallbacks = new HashMap<>();

    private final FileWatcher fileWatcher = new FileWatcher(2000 /* ms */, this,
            FileSystemController.class.getSimpleName());

    private final SceneBuilderManager sceneBuilderManager;
    private final DocumentManager documentManager;

    private FileTime loadFileTime;

    // @formatter:off
    public FileSystemController(
            SceneBuilderManager sceneBuilderManager,
            DocumentManager documentManager,
            RecentItemsPreference recentItemsPreference,
            Provider<PathPreference> pathPreference,
            InitialDirectoryPreference initialDirectoryPreference,
            WildcardImportsPreference wildcardImportsPreference) {
     // @formatter:on
        this.documentManager = documentManager;
        this.sceneBuilderManager = sceneBuilderManager;
        this.initialDirectoryPreference = initialDirectoryPreference;
        this.recentItemsPreference = recentItemsPreference;
        this.pathPreference = pathPreference;
        this.wildcardImportsPreference = wildcardImportsPreference;

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
            initialDirectoryPreference.setValue(chosenFolder.toFile()).writeToJavaPreferences();
        }
    }

    @Override
    public void watch(InstanceWindow document, Set<Path> files, WatchingCallback callback) {
        List<File> fileList = files.stream().map(p -> p.toFile()).collect(Collectors.toList());
        watch(document, fileList, callback);
    }

    @Override
    public void watch(InstanceWindow document, List<File> files, WatchingCallback callback) {
        Object key = callback.getOwnerKey();

        List<Object> documentKeys = documentWatchKeys.get(document);

        if (documentKeys == null) {
            documentKeys = new ArrayList<>();
            documentWatchKeys.put(document, documentKeys);
        }

        if (!documentKeys.contains(key)) {
            documentKeys.add(key);
        }

        if (files != null && !files.isEmpty()) {
            List<Path> paths = files.stream().filter(f -> f != null && f.exists()).map(f -> f.toPath())
                    .collect(Collectors.toList());

            watchedFiles.put(key, paths);

            paths.forEach(p -> {
                if (!fileWatcher.hasTarget(p)) {
                    fileWatcher.addTarget(p);
                }

                List<WatchingCallback> callbacks = watchCallbacks.get(p);

                if (callbacks == null) {
                    callbacks = new ArrayList<>();
                    watchCallbacks.put(p, callbacks);
                }

                if (!callbacks.contains(callback)) {
                    callbacks.add(callback);
                }

                logger.info("Watching file : {}", p.toAbsolutePath());
            });
        }
    }

    @Override
    public void unwatch(Object key) {
        if (watchedFiles.containsKey(key)) {
            watchedFiles.get(key).forEach(p -> {
                List<WatchingCallback> callbacks = watchCallbacks.get(p);
                List<WatchingCallback> ownedCallbacks = callbacks.stream().filter(c -> c.getOwnerKey() == key)
                        .collect(Collectors.toList());

                callbacks.removeAll(ownedCallbacks);

                if (callbacks.isEmpty()) {
                    watchCallbacks.remove(p);
                    fileWatcher.removeTarget(p);
                }
            });
            watchedFiles.remove(key);
        }
    }

    @Override
    public void unwatchDocument(InstanceWindow document) {
        List<Object> keys = documentWatchKeys.get(document);
        if (keys != null) {
            keys.forEach(this::unwatch);
        }
        documentWatchKeys.remove(document);
    }

    @Override
    public void startWatcher() {
        logger.info("Starting filewatcher !");
        fileWatcher.start();
    }

    @Override
    public void stopWatcher() {
        logger.info("Stoping filewatcher !");
        fileWatcher.stop();
    }

    /*
     * FileWatcher.Delegate
     */
    // FIXME SbPlatform.runForDocumentLater is misused here, what about watcher from
    // other documents ?
    @Override
    public void fileWatcherDidWatchTargetCreation(Path target) {
        logger.info("File Event : file created ({})", target.toFile().getName());
        if (watchCallbacks.containsKey(target)) {
            logger.info("File Event sent : file created ({})", target.toFile().getName());
            SbPlatform.runOnFxThreadWithActiveScope(() -> watchCallbacks.get(target).forEach(c -> c.created(target)));
        }
    }

    // FIXME SbPlatform.runForDocumentLater is misused here, what about watcher from
    // other documents ?
    @Override
    public void fileWatcherDidWatchTargetDeletion(Path target) {
        logger.info("File Event : file deleted ({})", target.toFile().getName());
        if (watchCallbacks.containsKey(target)) {
            logger.info("File Event sent : file deleted ({})", target.toFile().getName());
            SbPlatform.runOnFxThreadWithActiveScope(() -> watchCallbacks.get(target).forEach(c -> c.deleted(target)));
        }
    }

    // FIXME SbPlatform.runForDocumentLater is misused here, what about watcher from
    // other documents ?
    @Override
    public void fileWatcherDidWatchTargetModification(Path target) {
        logger.info("File Event : file modified ({})", target.toFile().getName());
        if (watchCallbacks.containsKey(target)) {
            logger.info("File Event sent : file modified ({})", target.toFile().getName());
            SbPlatform.runOnFxThreadWithActiveScope(() -> watchCallbacks.get(target).forEach(c -> c.modified(target)));
        }
    }

    @Override
    public File getMessageBoxFolder() {
        return JfxAppsPlatform.getMessageBoxFolder();
    }

    @Override
    public File getApplicationDataFolder() {
        return JfxAppsPlatform.getApplicationDataFolder();
    }


    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    @Override
    public void loadFromFile(File fxmlFile) throws IOException {
        final URL fxmlURL = fxmlFile.toURI().toURL();
        loadFromURL(fxmlURL, true);

        // TODO remove after checking the new watching system is operational in
        // EditorController or in filesystem
        // watchingController.update();

        // WarnThemeAlert.showAlertIfRequired(themePreference,
        // editorController.getFxomDocument(), documentWindow.getStage());
    }

    @Override
    public void loadFromURL(URL fxmlURL, boolean keepTrackOfLocation) {
        assert fxmlURL != null;
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            setFxmlTextAndLocation(fxmlText, keepTrackOfLocation ? fxmlURL : null, false);
            updateLoadFileTime();

            // TODO remove after checking the new watching system is operational in
            // EditorController or in filesystem
            // watchingController.update();
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
    }

    @Override
    public void loadDefaultContent() {
        try {
            setFxmlTextAndLocation("", null, true); // NOI18N
            updateLoadFileTime();
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
    }

    @Override
    public void reload() throws IOException{
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        assert (fxomDocument != null) && (fxomDocument.getLocation() != null);
        final URL fxmlURL = fxomDocument.getLocation();
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        setFxmlTextAndLocation(fxmlText, fxmlURL, true);
        updateLoadFileTime();
        // Here we do not invoke updateStageTitleAndPreferences() neither
        // watchingController.update()
    }

    @Override
    public FileTime getLoadFileTime() {
        // TODO Auto-generated method stub
        return loadFileTime;
    }

    private void updateLoadFileTime() {

        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        if (fxomDocument == null) {
            loadFileTime = null;
            return;
        }

        final URL fxmlURL = documentManager.fxomDocument().get().getLocation();
        if (fxmlURL == null) {
            loadFileTime = null;
        } else {
            try {
                final Path fxmlPath = Paths.get(fxmlURL.toURI());
                if (Files.exists(fxmlPath)) {
                    loadFileTime = Files.getLastModifiedTime(fxmlPath);
                } else {
                    loadFileTime = null;
                }
            } catch (URISyntaxException x) {
                throw new RuntimeException("Bug", x); // NOI18N
            } catch (IOException x) {
                loadFileTime = null;
            }
        }
    }

    @Override
    public boolean checkLoadFileTime() throws IOException {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        /*
         * loadFileTime == null => fxml file does not exist => TRUE
         *
         * loadFileTime != null => fxml file does/did exist
         *
         * currentFileTime == null => fxml file no longer exists => TRUE
         *
         * currentFileTime != null => fxml file still exists =>
         * loadFileTime.compare(currentFileTime) == 0
         */

        boolean result;
        if (loadFileTime == null) {
            // editorController.getFxmlLocation() does not exist yet
            result = true;
        } else {
            try {
                // editorController.getFxmlLocation() still exists
                // Check if its file time matches loadFileTime
                Path fxmlPath = Paths.get(fxomDocument.getLocation().toURI());
                FileTime currentFileTime = Files.getLastModifiedTime(fxmlPath);
                result = loadFileTime.compareTo(currentFileTime) == 0;
            } catch (NoSuchFileException x) {
                // editorController.getFxmlLocation() no longer exists
                result = true;
            } catch (URISyntaxException x) {
                throw new RuntimeException("Bug", x); // NOI18N
            }
        }

        return result;
    }

    /**
     * Sets both fxml text and location to be edited by this editor. Performs
     * setFxmlText() and setFxmlLocation() but in a optimized manner (it avoids an
     * extra scene graph refresh).
     *
     * @param fxmlText     null or the fxml text to be edited
     * @param fxmlLocation null or the location of the fxml text being edited
     * @param checkTheme   if set to true a check will be made if the fxml contains
     *                     Gluon controls and if so, the correct theme is set
     * @throws IOException if fxml text cannot be parsed and loaded correctly.
     */
    //@Override
    private void setFxmlTextAndLocation(String fxmlText, URL fxmlLocation, boolean checkTheme) throws IOException {

        I18nResourceProvider i18nResources = documentManager.i18nResourceConfig().get();

        updateFxomDocument(fxmlText, fxmlLocation,
                new CombinedResourceBundle(i18nResources == null ? new ArrayList<>() : i18nResources.getBundles(), false),
                checkTheme);

        if (fxmlLocation != null) {
            try {
                pathPreference.get().setValue(new File(fxmlLocation.toURI()).getPath());
            } catch (URISyntaxException e) {
                // TODO log something here
                e.printStackTrace();
            }

            // recentItems may not contain the current document
            // if the Open Recent -> Clear menu has been invoked
            if (!recentItemsPreference.containsRecentItem(fxmlLocation)) {
                recentItemsPreference.addRecentItem(fxmlLocation);
            }
        }

    }


    private void updateFxomDocument(String fxmlText, URL fxmlLocation, ResourceBundle resources, boolean checkTheme)
            throws IOException {
        final FXOMDocument newFxomDocument;

        if (fxmlText != null) {
            newFxomDocument = new FXOMDocument(fxmlText, fxmlLocation, sceneBuilderManager.classloader().get(),
                    resources);
        } else {
            newFxomDocument = null;
        }

        documentManager.fxomDocument().set(newFxomDocument);

        updateFileWatcher(newFxomDocument);

    }

    private void updateFileWatcher(FXOMDocument fxomDocument) {

        unwatch(this);

        if (fxomDocument != null && fxomDocument.getLocation() != null) {
            final FXOMAssetIndex assetIndex = new FXOMAssetIndex(fxomDocument);
            watch(null, assetIndex.getFileAssets().keySet(), new FileSystem.WatchingCallback() {

                @Override
                public void modified(Path path) {
                    documentManager.filesystemUpdate().set(Map.of(path, "file.watching.file.modified"));
                }

                @Override
                public void deleted(Path path) {
                    documentManager.filesystemUpdate().set(Map.of(path, "file.watching.file.deleted"));
                }

                @Override
                public void created(Path path) {
                    documentManager.filesystemUpdate().set(Map.of(path, "file.watching.file.created"));
                }

                @Override
                public Object getOwnerKey() {
                    return this;
                }
            });
        }
    }

    @Override
    public void save() throws IOException {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        final Path fxmlPath;
        try {
            fxmlPath = Paths.get(fxomDocument.getLocation().toURI());
        } catch (URISyntaxException x) {
            // Should not happen
            throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); // NOI18N
        }

        saveAs(fxmlPath.toFile());
    }

    @Override
    public void saveAs(File target) throws IOException {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;

        final Path fxmlPath = Paths.get(target.toURI());

        final byte[] fxmlBytes = fxomDocument.getFxmlText(wildcardImportsPreference.getValue())
                .getBytes(StandardCharsets.UTF_8); // NOI18N
        Files.write(fxmlPath, fxmlBytes);

        updateLoadFileTime();

        documentManager.dirty().set(false);
        documentManager.saved().set(true);
    }

}
