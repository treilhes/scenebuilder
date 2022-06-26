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
package com.oracle.javafx.scenebuilder.core.fxom;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueDocument;
import com.oracle.javafx.scenebuilder.core.fxom.sampledata.SampleDataGenerator;
import com.oracle.javafx.scenebuilder.core.fxom.util.Deprecation;
import com.oracle.javafx.scenebuilder.om.api.OMDocument;
import com.oracle.javafx.scenebuilder.om.api.OMObject;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 *
 *
 */
public class FXOMDocument extends OMDocument {

    public static Map<String, Object> DEFAULT_NAMESPACE = new HashMap<>();

    private final GlueDocument glue;
    private ResourceBundle resources;
    private SampleDataGenerator sampleDataGenerator;
    private Object sceneGraphRoot;
    private Node displayNode;
    private ArrayList<String> displayStylesheets = new ArrayList<>();
    //private final SimpleIntegerProperty sceneGraphRevision = new SimpleIntegerProperty();
    private final SimpleIntegerProperty cssRevision = new SimpleIntegerProperty();
    private SceneGraphHolder sceneGraphHolder;

    private ObservableMap<String, Object> namespaces = FXCollections.observableHashMap();
    private String scriptingLanguage;
    //private boolean hasGluonControls;

    private List<Class<?>> initialDeclaredClasses;


    public FXOMDocument(AbstractBuilder<?, ?> builder) throws IOException {
        super(builder);
        this.glue = builder.fxmlText == null ? new GlueDocument(): new GlueDocument(builder.fxmlText);
        this.resources = builder.resources;

        initialDeclaredClasses = new ArrayList<>();

        if (this.glue.getMainElement() != null) {
            final FXOMLoader loader = new FXOMLoader(this);
            loader.load(builder.fxmlText);
            if (builder.normalize) {
                final FXOMNormalizer normalizer = new FXOMNormalizer(this);
                normalizer.normalize();
            }
        } else {
            // Document is empty
            assert GlueDocument.isEmptyXmlText(builder.fxmlText);
            // Keeps this.fxomRoot == null
            // Keeps this.sceneGraphRoot == null
        }
    }

    public FXOMDocument(String fxmlText, URL location, ClassLoader classLoader, ResourceBundle resources, boolean normalize) throws IOException {
        super(location, classLoader);
        this.glue = new GlueDocument(fxmlText);
        this.resources = resources;
        initialDeclaredClasses = new ArrayList<>();
        if (this.glue.getMainElement() != null) {
            final FXOMLoader loader = new FXOMLoader(this);
            loader.load(fxmlText);
            if (normalize) {
                final FXOMNormalizer normalizer = new FXOMNormalizer(this);
                normalizer.normalize();
            }
        } else {
            // Document is empty
            assert GlueDocument.isEmptyXmlText(fxmlText);
            // Keeps this.fxomRoot == null
            // Keeps this.sceneGraphRoot == null
        }
    }


    public FXOMDocument(String fxmlText, URL location, ClassLoader classLoader, ResourceBundle resources) throws IOException {
        this(fxmlText, location, classLoader, resources, true /* normalize */);
    }


    public FXOMDocument() {
        this.glue = new GlueDocument();
    }

    public GlueDocument getGlue() {
        return glue;
    }

    public List<Class<?>> getInitialDeclaredClasses() {
        return initialDeclaredClasses;
    }

    public ResourceBundle getResources() {
        return resources;
    }

    public void setResources(ResourceBundle resources) {
        beginUpdate();
        this.resources = resources;
        endUpdate();
    }

    public boolean isSampleDataEnabled() {
        return sampleDataGenerator != null;
    }

    public void setSampleDataEnabled(boolean sampleDataEnabled) {
        assert isUpdateOnGoing() == false;

        final SampleDataGenerator newSampleDataGenerator;
        if (sampleDataEnabled) {
            if (sampleDataGenerator != null) {
                newSampleDataGenerator = sampleDataGenerator;
            } else {
                newSampleDataGenerator = new SampleDataGenerator();
            }
        } else {
            newSampleDataGenerator = null;
        }

        if (newSampleDataGenerator != sampleDataGenerator) {
            if (sampleDataGenerator != null) {
                sampleDataGenerator.removeSampleData(getFxomRoot());
            }
            sampleDataGenerator = newSampleDataGenerator;
            if (sampleDataGenerator != null) {
                sampleDataGenerator.assignSampleData(getFxomRoot());
            }
        }
    }

    @Override
    protected void notifyRootUpdated(OMObject fxomRoot) {
        assert fxomRoot == null || fxomRoot.getFxomDocument() == this;

        if (this.getFxomRoot() == null) {
            this.glue.setMainElement(null);
        } else {
            this.glue.setMainElement(this.getFxomRoot().getGlueElement());
        }

        this.displayNode = null;
        this.displayStylesheets.clear();
    }



    @Override
    public FXOMObject getFxomRoot() {
        return (FXOMObject)super.getFxomRoot();
    }

    void updateRoots(FXOMObject fxomRoot, Object sceneGraphRoot) {
        setFxomRoot(fxomRoot, false);
        setSceneGraphRoot(sceneGraphRoot);
    }

    public Object getSceneGraphRoot() {
        return sceneGraphRoot;
    }

    void setSceneGraphRoot(Object sceneGraphRoot) {
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

    void setDisplayStylesheets(List<String> displayStylesheets) {
        this.displayStylesheets.clear();
        this.displayStylesheets.addAll(displayStylesheets);
    }

    /**
     * Sets the Node that should be displayed in the editor instead of the scene graph root.
     */
    void setDisplayNode(Node displayNode) {
        this.displayNode = displayNode;
    }

    /**
     * Returns the display node if one is set, otherwise returns the scene graph root.
     */
    public Object getDisplayNodeOrSceneGraphRoot() {
        return displayNode != null ? displayNode : sceneGraphRoot;
    }

    /**
     * Returns the FXML string representation of the FXOMDocument.
     * @param wildcardImports If the FXML should have wildcards in its imports.
     * @return The FXML string representation. This can be empty if current root is null.
     */
    public String getFxmlText(boolean wildcardImports) {
        final String result;
        if (getFxomRoot() == null) {
            assert glue.getMainElement() == null;
            assert sceneGraphRoot == null;
            result = "";
        } else {
            assert glue.getMainElement() != null;
            // Note that sceneGraphRoot might be null if fxomRoot is unresolved
            glue.updateIndent();
            final FXOMSaver saver = new FXOMSaver(wildcardImports);
            result = saver.save(this);
        }
        return result;
    }

    @Override
    public byte[] getBytes() {
        return getFxmlText(false).getBytes();
    }

    public FXOMObject searchWithSceneGraphObject(Object sceneGraphObject) {
        final FXOMObject result;

        if (getFxomRoot() == null) {
            result = null;
        } else {
            result = getFxomRoot().searchWithSceneGraphObject(sceneGraphObject);
        }

        return result;
    }

    public FXOMObject searchWithFxId(String fxId) {
        final FXOMObject result;

        if (getFxomRoot() == null) {
            result = null;
        } else {
            result = getFxomRoot().searchWithFxId(fxId);
        }

        return result;
    }

    public boolean isNamespaceFxId(String fxId) {
        return getNamespaces().containsKey(fxId);
    }

    public Map<String, FXOMObject> collectFxIds() {
        final Map<String, FXOMObject> result;

        if (getFxomRoot() == null) {
            result = Collections.emptyMap();
        } else {
            result = getFxomRoot().collectFxIds();
        }

        return result;
    }

    @Override
    public void refreshSceneGraph() {
        if (sceneGraphHolder != null) {
            sceneGraphHolder.fxomDocumentWillRefreshSceneGraph(this);
        }
        final FXOMRefresher fxomRefresher = new FXOMRefresher();
        fxomRefresher.refresh(this);
        if ((sampleDataGenerator != null) && (getFxomRoot() != null)) {
            sampleDataGenerator.assignSampleData(getFxomRoot());
        }
        if (sceneGraphHolder != null) {
            sceneGraphHolder.fxomDocumentDidRefreshSceneGraph(this);
        }

        setSceneGraphRevision(sceneGraphRevisionProperty().get()+1);
    }

//    /**
//     * Returns the property holding the revision number of the scene graph.
//     * refreshSceneGraph() method increments the revision by one each time it
//     * refreshes the scene graph.
//     *
//     * @return the property holding the revision number of scene graph.
//     */
//    public ReadOnlyIntegerProperty sceneGraphRevisionProperty() {
//        return sceneGraphRevision;
//    }

    /**
     * Forces this document to reload the specified css stylesheet file.
     *
     * @param stylesheetPath path of the stylesheet to be reloaded.
     */
    public void reapplyCSS(Path stylesheetPath) {
        if (sceneGraphRoot instanceof Node) {

            /*
             * Normally we should scan for all stylesheets properties which
             * include stylesheetPath and update them.
             * Right now, we use a workaround solution because of bug RT-34863.
             */
            final Parent contentGroup = ((Node) sceneGraphRoot).getParent();
            if ((contentGroup != null) && (contentGroup.getScene() != null) && stylesheetPath != null) {
                Deprecation.reapplyCSS(contentGroup, stylesheetPath.toUri());
                cssRevision.set(cssRevision.get()+1);
            }
        }
    }

    /**
     * Returns the property holding the css revision number.
     * reapplyCSS() method increments the revision by one each time it
     * is invoked.
     *
     * @return the property holding the css revision number.
     */
    public ReadOnlyIntegerProperty cssRevisionProperty() {
        return cssRevision;
    }

    /**
     * Utility method that fetches the text content from a URL.
     *
     * @param url a URL
     * @return  the text content read from the URL.
     * @throws IOException if something goes wrong
     */
    public static String readContentFromURL(URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

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

    public static interface SceneGraphHolder {
        public void fxomDocumentWillRefreshSceneGraph(FXOMDocument fxomDocument);
        public void fxomDocumentDidRefreshSceneGraph(FXOMDocument fxomDocument);
    }

    /**
     *
     * @return true if the current FXOM document represents a 3D layout, false
     *         otherwise.
     */
    public boolean is3D() {
        boolean res = false;
        Object sgroot = getSceneGraphRoot();

        if (sgroot instanceof Node) {
            final Bounds rootBounds = ((Node)sgroot).getLayoutBounds();
            res = (rootBounds.getDepth() > 0);
        }
        return res;
    }


    /**
     *
     * @return true if the current FXOM document is an instance of a Node, false
     * otherwise.
     */
    public boolean isNode() {
        boolean res = false;
        Object sgroot = getSceneGraphRoot();

        if (sgroot instanceof Node) {
            res = true;
        }
        return res;
    }


    public String getScriptingLanguage() {
        return scriptingLanguage;
    }


    protected void setScriptingLanguage(String scriptingLanguage) {
        this.scriptingLanguage = scriptingLanguage;
    }


    /**
     * @param namespaces
     */
    protected void setNamespaces(ObservableMap<String, Object> namespaces) {
        this.namespaces.putAll(namespaces);
    }


    public ObservableMap<String, Object> getNamespaces() {
        return namespaces;
    }


    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends OMDocument.AbstractBuilder<SELF, TOBUILD> {

        private ResourceBundle resources;
        private boolean normalize;
        private String fxmlText;

        protected SELF withFxmlText(String fxmlText) {
            this.fxmlText = fxmlText;
            return self();
        }

        public SELF withResourceBundle(ResourceBundle resources) {
            this.resources = resources;
            return self();
        }

        public SELF withNormalization(boolean normalize) {
            this.normalize = normalize;
            return self();
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, FXOMDocument> {
        @Override
        public FXOMDocument build() throws IOException {
            return new FXOMDocument(this);
        }
    }
}
