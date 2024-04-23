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
package com.oracle.javafx.scenebuilder.api.om;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.oracle.javafx.scenebuilder.util.URLUtils;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;

public abstract class FXOMDocument {

    private SceneGraphHolder sceneGraphHolder;
    private int updateDepth;
    private URL location;
    private OMObject fxomRoot;

    private Object sceneGraphRoot;
    private Node displayNode;
    private ArrayList<String> displayStylesheets = new ArrayList<>();

    private final SimpleIntegerProperty sceneGraphRevisionProperty = new SimpleIntegerProperty();

    public FXOMDocument() {
    }

    public FXOMDocument(AbstractBuilder<?, ?> builder) {
        this.location = builder.location;
        this.fxomRoot = builder.root;
    }

    public FXOMDocument(URL location) {
        this.location = location;
    }

    public void beginUpdate() {
        updateDepth++;
    }

    public void endUpdate() {
        assert updateDepth >= 1;
        updateDepth--;
        if (updateDepth == 0) {
            refreshSceneGraph();
        }
    }

    public OMObject getFxomRoot() {
        return fxomRoot;
    }

    public URL getLocation() {
        return location;
    }

    protected int getUpdateDepth() {
        return updateDepth;
    }

    public boolean isUpdateOnGoing() {
        return updateDepth >= 1;
    }

    /**
     * Returns the property holding the revision number of the scene graph.
     * refreshSceneGraph() method increments the revision by one each time it
     * refreshes the scene graph.
     *
     * @return the property holding the revision number of scene graph.
     */
    public ReadOnlyIntegerProperty sceneGraphRevisionProperty() {
        return sceneGraphRevisionProperty;
    }

    public void setFxomRoot(OMObject root) {
        setFxomRoot(root, true);
    }

    protected void setFxomRoot(OMObject root, boolean incrementRevision) {
        if (incrementRevision) {
            beginUpdate();
        }
        this.fxomRoot = root;
        notifyRootUpdated(root);
        if (incrementRevision) {
            endUpdate();
        }
    }


    public void setLocation(URL location) {
        if (URLUtils.equals(this.location, location) == false) {
            beginUpdate();
            if (fxomRoot != null) {
                fxomRoot.documentLocationWillChange(location);
            }
            this.location = location;
            endUpdate();
        }
    }

    protected void setSceneGraphRevision(int newRevision) {
        sceneGraphRevisionProperty.set(newRevision);
    }

    protected abstract void notifyRootUpdated(OMObject rootObject);

    public abstract byte[] getBytes();


    /**
     * Informs this fxom document that its scene graph is hold by the specified
     * scene graph holder.
     *
     * @param holder an scene graph holder (should not be null)
     */
    public void beginHoldingSceneGraph(SceneGraphHolder holder) {
        assert holder != null;
        assert sceneGraphHolder == null;
        sceneGraphHolder = holder;
    }

    /**
     * Informs this fxom document that its scene graph i no longer hold.
     */
    public void endHoldingSceneGraph() {
        assert sceneGraphHolder != null;
        sceneGraphHolder = null;
    }

    /**
     * Returns null or the object holding the scene graph of this fxom document.
     *
     * @return  null or the object holding the scene graph of this fxom document.
     */
    public SceneGraphHolder getSceneGraphHolder() {
        return sceneGraphHolder;
    }

    public void refreshSceneGraph() {
        if (sceneGraphHolder != null) {
            sceneGraphHolder.fxomDocumentWillRefreshSceneGraph(this);
        }

        onRefreshSceneGraph();

        if (sceneGraphHolder != null) {
            sceneGraphHolder.fxomDocumentDidRefreshSceneGraph(this);
        }

        setSceneGraphRevision(sceneGraphRevisionProperty().get()+1);
    }

    protected abstract void onRefreshSceneGraph();

    public void updateRoots(OMObject fxomRoot, Object sceneGraphRoot) {
        setFxomRoot(fxomRoot, false);
        setSceneGraphRoot(sceneGraphRoot);
    }

    public Object getSceneGraphRoot() {
        return sceneGraphRoot;
    }

    public void setSceneGraphRoot(Object sceneGraphRoot) {
        if (getFxomRoot() != null && getFxomRoot().isVirtual()) {
            // FXMLLoader.load return the first node element if fx:define is root
            // so prevent editing this first element
            this.sceneGraphRoot = null;
        } else {
            this.sceneGraphRoot = sceneGraphRoot;
        }
    }

    /**
     * Returns the Node that should be displayed in the editor instead of the scene graph root.
     */
    public Node getDisplayNode() {
        return displayNode;
    }

    public List<String> getDisplayStylesheets() {
        return Collections.unmodifiableList(displayStylesheets);
    }

    protected void clearDisplayStylesheets() {
        displayStylesheets.clear();
    }

    public void setDisplayStylesheets(List<String> displayStylesheets) {
        this.displayStylesheets.clear();
        this.displayStylesheets.addAll(displayStylesheets);
    }

    /**
     * Sets the Node that should be displayed in the editor instead of the scene graph root.
     */
    public void setDisplayNode(Node displayNode) {
        this.displayNode = displayNode;
    }

    /**
     * Returns the display node if one is set, otherwise returns the scene graph root.
     */
    public Object getDisplayNodeOrSceneGraphRoot() {
        return displayNode != null ? displayNode : sceneGraphRoot;
    }

    public static interface SceneGraphHolder {
        public void fxomDocumentWillRefreshSceneGraph(FXOMDocument fxomDocument);
        public void fxomDocumentDidRefreshSceneGraph(FXOMDocument fxomDocument);
    }


    protected static abstract class AbstractBuilder<SELF, TOBUILD> {

        protected OMObject root;
        protected URL location;

        @SuppressWarnings("unchecked")
        protected SELF self() {
            return (SELF)this;
        }

        protected SELF root(OMObject root) {
            this.root = root;
            return self();
        }

        protected SELF location(URL location) {
            this.location = location;
            return self();
        }

        public abstract TOBUILD build() throws Exception;
    }
}
