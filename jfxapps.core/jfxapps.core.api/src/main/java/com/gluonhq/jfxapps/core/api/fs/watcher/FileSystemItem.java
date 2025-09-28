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
package com.gluonhq.jfxapps.core.api.fs.watcher;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public abstract class FileSystemItem<I extends FileSystemItem, F extends Feature, H extends FeatureHandler<F, I>> {
    /**
     * Flag to enable or disable virtual threading for refresh operations.
     * If true, refresh operations will run on virtual threads; otherwise, they will run on the current thread.
     */
    private static final boolean ENABLE_VIRTUAL_THREADING = true;
    /**
     * The parent folder of this file.
     */
    private final Folder parent;

    /**
     * The location of the file in the filesystem.
     */
    private final Path path;
    /**
     * Indicates whether the file is currently being refreshed.
     * This prevents multiple refresh requests from being processed simultaneously.
     */
    private boolean refreshing;

    private final ObservableList<H> featureHandlers = FXCollections.observableArrayList();

    private final ObservableMap<Class<? extends F>, F> features = FXCollections.observableHashMap();

    private ListChangeListener<H> handlerListener = change -> {
        if (change.wasRemoved()) {
            var removedFeature = change.getRemoved();
            removedFeature.forEach(f -> features.remove(f.getFeatureClass()));
        }
        if (change.wasAdded()) {
            var addedFeature = change.getAddedSubList();
            addedFeature.stream()
                    .filter(h -> h.isApplicable((I) this))
                    .map(h -> h.createFeature((I) this))
                    .forEach(f -> features.put((Class<? extends F>) f.getClass(), f));
        }
    };

    private MapChangeListener<Class<? extends F>, F> listener = change -> {
        if (change.wasRemoved()) {
            F removedFeature = change.getValueRemoved();
            if (removedFeature != null) {
                removedFeature.onRemove();
            }
        }
        if (change.wasAdded()) {
            F addedFeature = change.getValueAdded();
            if (addedFeature != null) {
                addedFeature.refresh(this);
            }
        }
    };

    public FileSystemItem(Folder parent, Path location, List<H> initialFeatureHandlers) {
        super();
        Objects.requireNonNull(location, "location can't be null");
        this.parent = parent;
        this.path = location;
        this.featureHandlers.addAll(initialFeatureHandlers);
        this.featureHandlers.addListener(handlerListener);
        this.features.addListener(listener);
    }


    /**
     * Returns the parent folder of this file.
     *
     * @return the parent folder
     */
    public Path getPath() {
        return path;
    }

    public final void requestRefresh() {
        requestRefresh(ENABLE_VIRTUAL_THREADING);
    }
    /**
     * Request the refresh of the internal state of this file.
     *
     * @param multithreaded if true, the refresh will be done on a virtual thread; otherwise, it will run on the current thread.
     */
    public final void requestRefresh(boolean multithreaded) {

        if (refreshing) {
            return; // already refreshing
        }
        refreshing = true;

        Runnable refreshTask = () -> {
            try {
                this.refreshInternal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                refreshing = false;
            }
        };

        if (multithreaded) {
            Thread.startVirtualThread(refreshTask);
        } else {
            refreshTask.run();
        }
    }

    protected void refreshInternal() {
        refresh();
    }

    /**
     * This method should be overridden to implement the actual internal state refresh logic.
     */
    public abstract void refresh();

    protected void onRemoveInternal() {
        features.removeListener(listener);
        onRemove();
    }

    /**
     * This method should be overridden to implement the actual removal logic.
     */
    public abstract void onRemove();

    /**
     * Adds a feature to this FsItem.
     *
     * @param featureClass the class of the feature
     * @param feature the feature instance
     */

    public void addFeature(Class<? extends F> featureClass, F feature) {
        Objects.requireNonNull(featureClass, "featureClass can't be null");
        Objects.requireNonNull(feature, "feature can't be null");
        features.put(featureClass, feature);
    }


    /**
     * Retrieves a feature of the specified class from this FsItem.
     *
     * @param featureClass the class of the feature to retrieve
     * @param <T> the type of the feature
     * @return the feature instance, or null if not found
     */
    public <V extends F> V getFeature(Class<V> featureClass) {
        Objects.requireNonNull(featureClass, "featureClass can't be null");
        return featureClass.cast(features.get(featureClass));
    }

    /**
     * removes a feature of the specified class from this FsItem.
     * @param featureClass the class of the feature to remove
     * @param <T> the type of the feature
     * @return the feature instance, or null if not found
     */
    public <T extends F> T removeFeature(Class<T> featureClass) {
        Objects.requireNonNull(featureClass, "featureClass can't be null");
        return featureClass.cast(features.remove(featureClass));
    }

    /**
     * Check if this FsItem has a feature of the specified class.
     * @param featureClass the class of the feature to check
     * @param <T> the type of the feature
     * @return true if the feature exists, false otherwise
     */
    public <T extends F> boolean hasFeature(Class<T> featureClass) {
        return features.containsKey(featureClass);
    }

    public void addFeatureHandler(H handler) {
        Objects.requireNonNull(handler, "handler can't be null");
        featureHandlers.add(handler);
    }

    public boolean removeFeatureHandler(H handler) {
        return featureHandlers.remove(handler);
    }

    public void applyFeatures() {
        featureHandlers.stream()
                .filter(h -> h.isApplicable((I) this))
                .map(h -> h.createFeature((I) this))
                .peek(f -> f.refresh(this))
                .forEach(f -> addFeature((Class<F>) f.getClass(), f));
    }

    public Folder getParent() {
        return parent;
    }

    public boolean isRefreshing() {
        return refreshing;
    }
}
