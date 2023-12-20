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
package com.oracle.javafx.scenebuilder.library.store;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;
import com.oracle.javafx.scenebuilder.extstore.fs.ExtensionFileSystem;
import com.oracle.javafx.scenebuilder.library.api.LibraryStore;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenArtifactsPreferences;
import com.oracle.javafx.scenebuilder.library.util.LibraryUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

public class LibraryStoreController implements LibraryStore, Runnable {

    private final static Logger logger = LoggerFactory.getLogger(LibraryStoreController.class);

    public enum State {
        READY, WATCHING
    }

    private final MavenArtifactsPreferences mavenArtifactsPreferences;
    private final ExtensionFileSystem extensionFileSystem;

    private final ObservableList<UniqueArtifact> artifacts = FXCollections.observableArrayList();
    private final ObservableList<Path> filesOrFolders = FXCollections.observableArrayList();
    private final Properties configuration = new Properties();

    private final Path libraryRoot;
    private final Path libraryFilesRoot;
    private final Path libraryFoldersFile;
    private final Path libraryThumbnailsRoot;
    private final Path libraryConfigFile;

    private State state = State.READY;
    private Thread watcherThread;
    private Exception exception;
    private Consumer<LibraryStore> updateConsumer;
    private WatchService watchService;
    private Boolean storeReady;

    public LibraryStoreController(
            String storeId,
            ExtensionFileSystem extensionFileSystem,
            MavenArtifactsPreferences mavenArtifactsPreferences) {
        this.mavenArtifactsPreferences = mavenArtifactsPreferences;
        this.extensionFileSystem = extensionFileSystem;

        this.libraryRoot = extensionFileSystem.get(storeId);
        this.libraryFilesRoot = libraryRoot.resolve(Paths.get(LibraryUtil.FOLDERS_FOR_FILES));
        this.libraryFoldersFile = libraryFilesRoot.resolve(Paths.get(LibraryUtil.FOLDERS_LIBRARY_FILENAME));
        this.libraryConfigFile = libraryRoot.resolve(Paths.get(LibraryUtil.CONFIG_LIBRARY_FILENAME));
        this.libraryThumbnailsRoot = libraryRoot.resolve(Paths.get(LibraryUtil.FOLDERS_FOR_THUMBNAILS));
        mavenArtifactsPreferences.readFromJavaPreferences();

        if (Files.exists(this.libraryConfigFile)) {
            try (FileInputStream input = new FileInputStream(this.libraryConfigFile.toFile())){
                configuration.load(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean create() {
        if (!extensionFileSystem.isCreated()) {
            extensionFileSystem.create();
            extensionFileSystem.createDirectoryIfNotExists(libraryRoot);
            extensionFileSystem.createDirectoryIfNotExists(libraryFilesRoot);
            extensionFileSystem.createDirectoryIfNotExists(libraryThumbnailsRoot);
        }
        return true;
    }
    @Override
    public boolean isReady() {
        if (storeReady == null) {
            storeReady = mavenArtifactsPreferences != null && extensionFileSystem != null
                    && extensionFileSystem.isCreated()
                    && extensionFileSystem.existsDirectory(libraryRoot)
                    && extensionFileSystem.existsDirectory(libraryFilesRoot);
        }
        return storeReady;
    }

    /*
     * Runnable
     */

    @Override
    public void run() {
        try {
            runWatching();
        } catch (InterruptedException x) {
            // Let's stop
            try {
                watchService.close();
            } catch (IOException e) {
                logger.warn("A WatchService is leaking, performance will be more and more degraded", e);
            } finally {
                watchService = null;
            }
        }
    }

    @Override
    public boolean load() throws IOException {
System.out.println("LOADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
        List<Path> localFilesAndFolders = new ArrayList<>();
        List<UniqueArtifact> localArtifacts = new ArrayList<>();

        localArtifacts.addAll(mavenArtifactsPreferences.getRecords().values().stream().map(ma -> ma.getValue())
                .collect(Collectors.toList()));

        List<Path> folders = LibraryUtil.getFolderPaths(libraryFoldersFile);
        localFilesAndFolders.addAll(folders);

        // TODO is it realy worth it to handle retry on this listing ?
        if (Files.exists(libraryFilesRoot) && Files.isDirectory(libraryFilesRoot)) {
            localFilesAndFolders.addAll(Files.list(libraryFilesRoot)
                    .filter(f -> !LibraryUtil.FOLDERS_LIBRARY_FILENAME.equals(f.getFileName().toString()))
                    .collect(Collectors.toList()));
        }

        artifacts.setAll(localArtifacts);
        filesOrFolders.setAll(localFilesAndFolders);

        if (updateConsumer != null) {
            updateConsumer.accept(this);
        }
        return true;
    }

    @Override
    public boolean save() throws IOException {
        mavenArtifactsPreferences.reset();
        artifacts.forEach(a -> mavenArtifactsPreferences.getRecord(a));
        mavenArtifactsPreferences.writeToJavaPreferences();

        // collect directories from importFiles and add to library.folders file
        // for other filex (jar, fxml) copy them directly
        List<Path> folders = new ArrayList<>(filesOrFolders.size());
        List<Path> files = new ArrayList<>(filesOrFolders.size());

        for (Path file : filesOrFolders) {
            if (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS))
                folders.add(file);
            else
                files.add(file);
        }

        if (!files.isEmpty()) {
            extensionFileSystem.copy(files, "");
        }

        if (!Files.exists(libraryFoldersFile)) {
            Files.createFile(libraryFoldersFile);
        }

        Set<String> lines = new TreeSet<>(Files.readAllLines(libraryFoldersFile));
        lines.addAll(folders.stream().map(f -> f.toAbsolutePath().toString()).collect(Collectors.toList()));

        Files.write(libraryFoldersFile, lines);

        return true;
    }

    @Override
    public void saveConfiguration() {
        try (FileOutputStream output = new FileOutputStream(this.libraryConfigFile.toFile())) {
            configuration.store(output, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized State getState() {
        return state;
    }

    // TODO another watching code. may be replaced by filsesystem watcher
    @Override
    public synchronized void startWatching() {
        assert state == State.READY;

        if (state == State.READY) {
            assert watcherThread == null;

            watcherThread = new Thread(this);
            watcherThread.setName(this.getClass().getSimpleName() + "(" + libraryFilesRoot + ")"); // NOI18N
            watcherThread.setDaemon(true);
            watcherThread.start();
            state = State.WATCHING;
        }
    }

    // TODO another watching code. may be replaced by filsesystem watcher
    @Override
    public synchronized void stopWatching() {
        assert state == State.WATCHING;

        if (state == State.WATCHING) {
            assert watcherThread != null;
            assert exception == null;

            watcherThread.interrupt();

            try {
                watcherThread.join();
            } catch (InterruptedException x) {
                x.printStackTrace();
            } finally {
                watcherThread = null;
                state = State.READY;
            }
        }
    }

    @Override
    public ObservableList<UniqueArtifact> getArtifacts() {
        return artifacts;
    }

    @Override
    public ObservableList<Path> getFilesOrFolders() {
        return filesOrFolders;
    }

    private void runWatching() throws InterruptedException {
        while (true) {

            watchService = null;
            while (watchService == null) {
                try {
                    watchService = libraryFilesRoot.getFileSystem().newWatchService();
                } catch (IOException x) {
                    System.out.println("FileSystem.newWatchService() failed"); // NOI18N
                    System.out.println("Sleeping..."); // NOI18N
                    Thread.sleep(1000 /* ms */);
                }
            }
            WatchKey watchKey = null;
            while ((watchKey == null) || (watchKey.isValid() == false)) {
                try {
                    watchKey = libraryFilesRoot.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

                    WatchKey wk;
                    do {
                        wk = watchService.take();
                        assert wk == watchKey;

                        // sleep a bit to allow successive events to be handled as one
                        Thread.sleep( 500 );

                        boolean isDirty = false;
                        for (WatchEvent<?> e : wk.pollEvents()) {
                            final WatchEvent.Kind<?> kind = e.kind();
                            final Object context = e.context();

                            if (kind == StandardWatchEventKinds.ENTRY_CREATE
                                    || kind == StandardWatchEventKinds.ENTRY_DELETE
                                    || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                assert context instanceof Path;
                                isDirty = true;
                            } else {
                                assert kind == StandardWatchEventKinds.OVERFLOW;
                            }
                        }

                        // We reconstruct a full set from scratch as soon as the
                        // dirty flag is set.
                        if (isDirty) {
                            System.out.println("DIRTYLOADDDDDDDDD");
                            load();
                        }
                    } while (wk.reset());
                } catch (IOException x) {
                    Thread.sleep(1000 /* ms */);
                }
            }

        }
    }



    @Override
    public Path getRoot() {
        return libraryRoot;
    }

    @Override
    public void onStoreUpdated(Consumer<LibraryStore> updateConsumer) {
        this.updateConsumer = updateConsumer;
    }
//    @Override
//    public SceneBuilderWindow getDialog() {
//        return libraryDialogController;
//    }

    @Override
    public boolean add(UniqueArtifact artifact) {
        try {
            mavenArtifactsPreferences.getRecord(artifact);
            mavenArtifactsPreferences.writeToJavaPreferences();
            return true;
        } catch (Exception e) {
            logger.error("Unable to add artifact", e);
            return false;
        }
    }

    @Override
    public boolean addAll(List<Path> pathes) {
        try {

            Map<Boolean, List<Path>> partioned = pathes.stream()
                    .collect(Collectors.partitioningBy(path -> Files.isRegularFile(path)));

            List<Path> files = partioned.get(true);
            List<Path> folders = partioned.get(false);

            if (files.size() > 0) {
                extensionFileSystem.copy(files, libraryFilesRoot);
            }
            if (folders.size() > 0) {
                String content = Stream.concat(folders.stream(), filesOrFolders.stream())
                    .filter(p -> Files.isDirectory(p))
                    .filter(p -> Files.exists(p))
                    .map(p -> p.toAbsolutePath().toString())
                    .collect(Collectors.joining("\n"));
                Files.write(libraryFoldersFile, content.getBytes(), StandardOpenOption.CREATE);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error("Unable to add all pathes {}", pathes, e);
            return false;
        }
    }

    @Override
    public boolean remove(UniqueArtifact artifact) {
        try {
            mavenArtifactsPreferences.removeArtifact(artifact.getCoordinates());
            mavenArtifactsPreferences.writeToJavaPreferences();
            return true;
        } catch (Exception e) {
            logger.error("Unable to remove artifact", e);
            return false;
        }
    }

    @Override
    public boolean remove(Path path) {
        try {
            if (Files.isRegularFile(path)) {
                Files.deleteIfExists(libraryFilesRoot.resolve(path.getFileName()));
                return true;
            } else if (Files.isDirectory(path)) {
                String content = filesOrFolders.stream()
                    .filter(p -> Files.isDirectory(p))
                    .filter(p -> Files.exists(p))
                    .filter(p -> !p.toAbsolutePath().toString().equals(path.toAbsolutePath().toString()))
                    .map(p -> p.toAbsolutePath().toString())
                    .collect(Collectors.joining("\n"));
                Files.write(libraryFoldersFile, content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error("Unable to remove path {}", path, e);
            return false;
        }
    }

    @Override
    public URL getThumbnail(String name, int width, int height) {
        String thumbnailName = name.replace("/", "_") + "_" + width + "x" + height + ".png";
        Path thumbnail = libraryThumbnailsRoot.resolve(thumbnailName);
        Path result = extensionFileSystem.get(thumbnail);

        if (result == null || !Files.exists(result)) {
            return null;
        } else {
            try {
                return result.toUri().toURL();
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }

    @Override
    public boolean saveThumbnail(String name, int width, int height, WritableImage snapshot) {
        String thumbnailName = name.replace("/", "_") + "_" + width + "x" + height + ".png";
        Path thumbnail = libraryThumbnailsRoot.resolve(thumbnailName);
        Path target = extensionFileSystem.get(thumbnail);

        if (target != null && !Files.exists(target)) {
            BufferedImage tempImg = SwingFXUtils.fromFXImage(snapshot, null);
            try (FileOutputStream fos = new FileOutputStream(target.toFile())){
                ImageIO.write(tempImg, "png", fos);
                return true;
            } catch(Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Path getFilesFolder() {
        return libraryFilesRoot;
    }

    @Override
    public Properties getConfiguration() {
        return configuration;
    }



}
