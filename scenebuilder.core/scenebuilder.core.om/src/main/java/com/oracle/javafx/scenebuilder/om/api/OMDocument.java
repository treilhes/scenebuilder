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
package com.oracle.javafx.scenebuilder.om.api;

import java.io.IOException;
import java.net.URL;

import com.oracle.javafx.scenebuilder.util.URLUtils;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class OMDocument {

    private ClassLoader classLoader;
    private int updateDepth;
    private URL location;
    private OMObject fxomRoot;

    private final SimpleIntegerProperty sceneGraphRevisionProperty = new SimpleIntegerProperty();

    public OMDocument() {
    }

    public OMDocument(AbstractBuilder<?, ?> builder) {
        this.location = builder.location;
        this.classLoader = builder.classloader;
        this.fxomRoot = builder.root;
    }

    public OMDocument(URL location, ClassLoader classLoader) {
        this.location = location;
        this.classLoader = classLoader;
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

    public ClassLoader getClassLoader() {
        return classLoader;
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
    protected abstract void refreshSceneGraph();

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

    public void setClassLoader(ClassLoader classLoader) {
        beginUpdate();
        this.classLoader = classLoader;
        endUpdate();
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

    protected static abstract class AbstractBuilder<SELF, TOBUILD> {

        protected OMObject root;
        protected URL location;
        protected ClassLoader classloader;

        @SuppressWarnings("unchecked")
        protected SELF self() {
            return (SELF)this;
        }

        protected SELF withRoot(OMObject root) {
            this.root = root;
            return self();
        }

        protected SELF withLocation(URL location) {
            this.location = location;
            return self();
        }

        protected SELF withClassLoader(ClassLoader classloader) {
            this.classloader = classloader;
            return self();
        }

        public abstract TOBUILD build() throws Exception;
    }
}
