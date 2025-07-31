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
package com.gluonhq.jfxapps.core.document.controller;

import java.io.ByteArrayInputStream;
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
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.action.Action.ActionStatus;
import com.gluonhq.jfxapps.core.api.document.DocumentController;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.fs.FileSystemActionFactory;
import com.gluonhq.jfxapps.core.api.fxom.subjects.FxomEvents;
import com.gluonhq.jfxapps.core.api.i18n.CombinedResourceBundle;
import com.gluonhq.jfxapps.core.api.i18n.I18nResourceProvider;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMAssetIndex;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMDocumentFactory;
import com.gluonhq.jfxapps.core.fxom.transform.FXOMSerializer;

@ApplicationInstanceSingleton
public class DocumentControllerImpl implements DocumentController  {

    private final static Logger logger = LoggerFactory.getLogger(DocumentControllerImpl.class);

    private final JfxAppPlatform jfxAppPlatform;
    private final ApplicationEvents applicationEvents;
    private final FxomEvents fxomEvents;
    private final FXOMDocumentFactory fxomDocumentFactory;
    private final FXOMSerializer serializer;
    private final FileSystem fileSystem;
    private final FileSystemActionFactory fileSystemActionFactory;

    private FileTime loadFileTime;



    // @formatter:off
    public DocumentControllerImpl(
            JfxAppPlatform jfxAppPlatform,
            ApplicationEvents applicationEvents,
            FxomEvents fxomEvents,
            FXOMDocumentFactory fxomDocumentFactory,
            FXOMSerializer serializer,
            FileSystem fileSystem,
            FileSystemActionFactory fileSystemActionFactory) {
     // @formatter:on
        this.jfxAppPlatform = jfxAppPlatform;
        this.fxomEvents = fxomEvents;
        this.applicationEvents = applicationEvents;
        this.fxomDocumentFactory = fxomDocumentFactory;
        this.serializer = serializer;
        this.fileSystem = fileSystem;
        this.fileSystemActionFactory = fileSystemActionFactory;
    }


    @Override
    public void loadFromFile(File fxmlFile, boolean keepTrackOfLocation) throws IOException {
        final URL fxmlURL = fxmlFile.toURI().toURL();
        loadFromURL(fxmlURL, true);

        // TODO remove after checking the new watching system is operational in
        // EditorController or in filesystem
        // watchingController.update();

        // WarnThemeAlert.showAlertIfRequired(themePreference,
        // editorController.getFxomDocument(), documentWindow.getStage());
    }

    @Override
    public void loadFromURL(URL fxmlURL, boolean keepTrackOfLocation) throws IOException {
        assert fxmlURL != null;
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        setFxmlTextAndLocation(fxmlText, keepTrackOfLocation ? fxmlURL : null);
        updateLoadFileTime();
    }

    @Override
    public void save() throws IOException {
        final FXOMDocument fxomDocument = fxomEvents.fxomDocument().get();
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
    public void saveAs(File target) {
        final FXOMDocument fxomDocument = fxomEvents.fxomDocument().get();
        assert fxomDocument != null;

        final Path fxmlPath = Paths.get(target.toURI());

        final byte[] fxmlBytes = serializer.serialize(fxomDocument).getBytes(StandardCharsets.UTF_8); // NOI18N

        var action = fileSystemActionFactory.save(new ByteArrayInputStream(fxmlBytes), fxmlPath.toFile());

        if (action.checkAndPerform() == ActionStatus.DONE) {
            updateLoadFileTime();
            fxomEvents.dirty().set(false);
            fxomEvents.saved().set(true);
        }
    }

    @Override
    public void loadDefaultContent() {
        try {
            fileSystem.unwatchDocument(null);
            setFxmlTextAndLocation("", null); // NOI18N
            updateLoadFileTime();
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
    }


    /**
     * Sets both fxml text and location to be edited by this editor. Performs
     * setFxmlText() and setFxmlLocation() but in a optimized manner (it avoids an
     * extra scene graph refresh).
     *
     * @param fxmlText     null or the fxml text to be edited
     * @param fxmlLocation null or the location of the fxml text being edited
     * @throws IOException if fxml text cannot be parsed and loaded correctly.
     */
    //@Override
    private void setFxmlTextAndLocation(String fxmlText, URL fxmlLocation) throws IOException {

        I18nResourceProvider i18nResources = fxomEvents.i18nResourceConfig().get();

        updateFxomDocument(fxmlText, fxmlLocation,
                new CombinedResourceBundle(i18nResources == null ? new ArrayList<>() : i18nResources.getBundles(), false));
    }


    private void updateFxomDocument(String fxmlText, URL fxmlLocation, ResourceBundle resources)
            throws IOException {
        final FXOMDocument newFxomDocument;

        if (fxmlText != null) {
            newFxomDocument = fxomDocumentFactory.newDocument(fxmlText, fxmlLocation, applicationEvents.classloader().get(),
                    resources);
        } else {
            newFxomDocument = null;
        }

        fxomEvents.fxomDocument().set(newFxomDocument);

        updateFileWatcher(newFxomDocument);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileTime getLoadFileTime() {
        // TODO Auto-generated method stub
        return loadFileTime;
    }

    private void updateLoadFileTime() {

        final FXOMDocument fxomDocument = fxomEvents.fxomDocument().get();
        if (fxomDocument == null) {
            loadFileTime = null;
            return;
        }

        final URL fxmlURL = fxomEvents.fxomDocument().get().getLocation();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkLoadFileTime() throws IOException {
        final FXOMDocument fxomDocument = fxomEvents.fxomDocument().get();

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

    @Override
    public void reload() throws IOException{

        final FXOMDocument fxomDocument = fxomEvents.fxomDocument().get();

        assert (fxomDocument != null) && (fxomDocument.getLocation() != null);

        final URL fxmlURL = fxomDocument.getLocation();
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);

        setFxmlTextAndLocation(fxmlText, fxmlURL);

        updateLoadFileTime();
        // Here we do not invoke updateStageTitleAndPreferences() neither
        // watchingController.update()
    }

    private void updateFileWatcher(FXOMDocument fxomDocument) {

        fileSystem.unwatch(this);

        if (fxomDocument != null && fxomDocument.getLocation() != null) {
            final FXOMAssetIndex assetIndex = new FXOMAssetIndex(fxomDocument);
            fileSystem.watch(null, assetIndex.getFileAssets().keySet(), new FileSystem.WatchingCallback() {

                @Override
                public void modified(Path path) {
                    fxomEvents.filesystemUpdate().set(Map.of(path, "file.watching.file.modified"));
                }

                @Override
                public void deleted(Path path) {
                    fxomEvents.filesystemUpdate().set(Map.of(path, "file.watching.file.deleted"));
                }

                @Override
                public void created(Path path) {
                    fxomEvents.filesystemUpdate().set(Map.of(path, "file.watching.file.created"));
                }

                @Override
                public Object getOwnerKey() {
                    return this;
                }
            });
        }
    }

}
