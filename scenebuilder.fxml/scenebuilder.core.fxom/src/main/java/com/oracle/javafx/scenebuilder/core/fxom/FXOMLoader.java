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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.core.fxom.ext.LoaderCapabilitiesManager;
import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueCursor;
import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueDocument;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.sun.javafx.fxml.FXMLLoaderHelper;

import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadListener;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

/**
 *
 *
 */
class FXOMLoader implements LoadListener {

    private static final Logger logger = LoggerFactory.getLogger(FXOMLoader.class);

    private static final Set<LoaderCapabilitiesManager> loaderCapabilitiesManagers;
    static {
        ServiceLoader<LoaderCapabilitiesManager> loaderCapabilitiesManagerLoader = ServiceLoader.load(LoaderCapabilitiesManager.class);
        loaderCapabilitiesManagers = new HashSet<>();
        loaderCapabilitiesManagerLoader.forEach(lcm -> loaderCapabilitiesManagers.add(lcm));
    }

    private final FXOMDocument document;
    private TransientNode currentTransientNode;
    private GlueCursor glueCursor;
    private long virtualElementIndex;
    private TransientClassLoader classloader;
    private FXMLLoader fxmlLoader;

    /*
     * FXOMLoader
     */

    public FXOMLoader(FXOMDocument document) {
        assert document != null;
        assert document.getGlue().getMainElement() != null;
        this.document = document;
    }

    public void load(String fxmlText) throws java.io.IOException {
        assert fxmlText != null;

        final ClassLoader classLoader;
        if (document.getClassLoader() != null) {
            classLoader = document.getClassLoader();
        } else {
            classLoader = FXMLLoader.getDefaultClassLoader();
        }

        classloader = new TransientClassLoader(classLoader);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(document.getLocation());
        fxmlLoader.setResources(new ResourceKeyCollector(document.getResources()));
        fxmlLoader.setClassLoader(classloader);
        fxmlLoader.setLoadListener(this);

        fxmlLoader.getNamespace().putAll(FXOMDocument.DEFAULT_NAMESPACE);

        if (loaderCapabilitiesManagers.size() == 0
                || loaderCapabilitiesManagers.stream().anyMatch(l -> l.isStaticLoadingEnabled())) {
            logger.info("Loading document with scripts/bindings/eventHandlers disabled");
            FXOMLoader.setStaticLoad(fxmlLoader, true);
        } else {
            logger.info("Loading document with scripts/bindings/eventHandlers enabled");
        }

        final Charset utf8 = Charset.forName("UTF-8");// NOCHECK
        try (final InputStream is = new ByteArrayInputStream(fxmlText.getBytes(utf8))) {
            glueCursor = document.getGlue().cursor();
            currentTransientNode = null;
            assert is.markSupported();
            is.reset();
            setSceneGraphRoot(fxmlLoader.load(is));
            setNamespaces(fxmlLoader.getNamespace());
        } catch (RuntimeException | IOException x) {
            if (x.getCause() != null && x.getCause().getClass() == XMLStreamException.class) {
                // TODO this exception was previously bubbled to the user
                // TODO present this exception to the user but without compromising the black
                // boxing of core
                throw new IOException("Unsupported charset", x); // NOCHECK
            } else
                throw new IOException("unable to load fxml", x);
        }
    }

    /**
     * @param namespace
     */
    private void setNamespaces(ObservableMap<String, Object> namespaces) {
        document.setNamespaces(namespaces);
    }

    private void setSceneGraphRoot(Object sceneGraphRoot) {
        document.setSceneGraphRoot(sceneGraphRoot);
        document.setDisplayNode(null);
        document.setDisplayStylesheets(Collections.emptyList());

        if (sceneGraphRoot instanceof Scene) {
            Scene scene = (Scene) sceneGraphRoot;
            document.setDisplayNode(scene.getRoot());
            document.setDisplayStylesheets(scene.getStylesheets());
            scene.setRoot(new Pane()); // ensure displayNode is only part of one scene
        } else if (sceneGraphRoot instanceof Window) {
            Window window = (Window) sceneGraphRoot;
            if (window.getScene() != null) {
                document.setDisplayNode(window.getScene().getRoot());
                document.setDisplayStylesheets(window.getScene().getStylesheets());
                window.getScene().setRoot(new Pane()); // ensure displayNode is only part of one scene
            }
        }
    }

    public FXOMDocument getDocument() {
        return document;
    }

    /*
     * LoadListener
     */

    @Override
    public void readImportProcessingInstruction(String data) {

    }

    @Override
    public void readLanguageProcessingInstruction(String data) {
        document.setScriptingLanguage(data);
    }

    @Override
    public void readComment(String string) {

        if (currentTransientNode == null) { //header
            //FIXME find a way to keep header comments
            //glueCursor.moveToNextElement();
            return;
        }

        final GlueDocument glueDocument = currentTransientNode.getGlueElement().getDocument();
        final TransientComment transientComment = new TransientComment(currentTransientNode, glueDocument,
                glueCursor.getCurrentElement(), ++virtualElementIndex, string);

        if (currentTransientNode instanceof TransientProperty) {
            final TransientProperty parentProperty = (TransientProperty) currentTransientNode;
            parentProperty.getValues().add(transientComment.makeFxomObject(document));
        } else if (currentTransientNode instanceof TransientObject) {
            final TransientObject parentObject = (TransientObject) currentTransientNode;
            parentObject.getProperties().add(transientComment.makeFxomProperty(document));
        } else if (currentTransientNode instanceof TransientIntrinsic) {
            final TransientIntrinsic transientIntrinsic = (TransientIntrinsic) currentTransientNode;
            transientIntrinsic.getProperties().add(transientComment.makeFxomProperty(document));
        }

        glueCursor.moveToNextElement();
    }

    @Override
    public void beginInstanceDeclarationElement(Class<?> declaredClass) {
        assert declaredClass != null;
        assert glueCursor.getCurrentElement().getTagName().equals(PropertyName.makeClassFullName(declaredClass))
                || glueCursor.getCurrentElement().getTagName().equals(declaredClass.getCanonicalName());

        final TransientObject transientInstance = new TransientObject(currentTransientNode, declaredClass,
                glueCursor.getCurrentElement());

        currentTransientNode = transientInstance;
        glueCursor.moveToNextElement();
    }

    @Override
    public void beginUnknownTypeElement(String unknownClassName) {
        assert unknownClassName != null;
        assert glueCursor.getCurrentElement().getTagName().equals(unknownClassName);

        final TransientObject transientInstance = new TransientObject(currentTransientNode, unknownClassName,
                glueCursor.getCurrentElement());

        currentTransientNode = transientInstance;
        glueCursor.moveToNextElement();
    }

    @Override
    public void beginIncludeElement() {
        assert glueCursor.getCurrentElement().getTagName().equals("fx:include"); // NOCHECK

        final TransientIntrinsic transientIntrinsic = new TransientInclude(currentTransientNode,
                glueCursor.getCurrentElement());

        currentTransientNode = transientIntrinsic;
        glueCursor.moveToNextElement();
    }

    @Override
    public void beginReferenceElement() {
        assert glueCursor.getCurrentElement().getTagName().equals("fx:reference"); // NOCHECK

        final TransientIntrinsic transientIntrinsic = new TransientReference(currentTransientNode,
                glueCursor.getCurrentElement());

        currentTransientNode = transientIntrinsic;
        glueCursor.moveToNextElement();
    }

    @Override
    public void beginCopyElement() {
        assert glueCursor.getCurrentElement().getTagName().equals("fx:copy"); // NOCHECK

        final TransientIntrinsic transientIntrinsic = new TransientCopy(currentTransientNode,
                glueCursor.getCurrentElement());

        currentTransientNode = transientIntrinsic;
        glueCursor.moveToNextElement();
    }

    @Override
    public void beginRootElement() {
        assert glueCursor.getCurrentElement().getTagName().equals("fx:root"); // NOCHECK

        final TransientObject transientInstance = new TransientObject(currentTransientNode,
                glueCursor.getCurrentElement());

        currentTransientNode = transientInstance;
        glueCursor.moveToNextElement();
    }

    @Override
    public void beginPropertyElement(String name, Class<?> staticClass) {
        assert name != null;

        final TransientProperty transientProperty = new TransientProperty(currentTransientNode,
                new PropertyName(name, staticClass), glueCursor.getCurrentElement());

        currentTransientNode = transientProperty;
        glueCursor.moveToNextElement();
    }

    @Override
    public void beginUnknownStaticPropertyElement(String string) {
        currentTransientNode = new TransientIgnored(currentTransientNode);
        glueCursor.moveToNextElement();
    }

    @Override
    public void beginScriptElement() {
//        currentTransientNode = new TransientIgnored(currentTransientNode);
//        glueCursor.moveToNextElement();
        assert glueCursor.getCurrentElement().getTagName().equals("fx:script"); // NOCHECK

        final TransientScript transientScript = new TransientScript(currentTransientNode,
                glueCursor.getCurrentElement(), ++virtualElementIndex);

        currentTransientNode = transientScript;
        glueCursor.moveToNextElement();
    }

    @Override
    public void beginDefineElement() {
        assert glueCursor.getCurrentElement().getTagName().equals("fx:define"); // NOCHECK

        final TransientDefine transientDefine = new TransientDefine(currentTransientNode,
                glueCursor.getCurrentElement(), ++virtualElementIndex);

        currentTransientNode = transientDefine;
        glueCursor.moveToNextElement();
    }

    @Override
    public void readInternalAttribute(String attrName, String attrValue) {
        assert currentTransientNode instanceof TransientObject || currentTransientNode instanceof TransientIntrinsic;

        if (attrName.equals("type")) {
            assert currentTransientNode instanceof TransientObject;
            final TransientObject transientObject = (TransientObject) currentTransientNode;
            transientObject.setFxRootType(attrValue);
        }
    }

    @Override
    public void readPropertyAttribute(String name, Class<?> staticClass, String fxmlValue) {
        assert currentTransientNode instanceof TransientObject || currentTransientNode instanceof TransientIntrinsic
                || currentTransientNode instanceof TransientProperty;

        assert name != null;

        final PropertyName pname = new PropertyName(name, staticClass);
        final FXOMPropertyT fxomProperty = new FXOMPropertyT(document, pname, null, null, fxmlValue);

        if (currentTransientNode instanceof TransientObject) {
            final TransientObject transientInstance = (TransientObject) currentTransientNode;
            transientInstance.getProperties().add(fxomProperty);
        } else if (currentTransientNode instanceof TransientProperty) {
            final TransientProperty transientProperty = (TransientProperty) currentTransientNode;
            transientProperty.getCollectedProperties().add(fxomProperty);
        } else if (currentTransientNode instanceof TransientIntrinsic) {
            final TransientIntrinsic transientIntrinsic = (TransientIntrinsic) currentTransientNode;
            transientIntrinsic.getProperties().add(fxomProperty);
        }
    }

    @Override
    public void readUnknownStaticPropertyAttribute(String string, String string1) {
        // TODO(elp) : implement FXOMLoader.readUnknownStaticPropertyAttribute.

    }

    @Override
    public void readEventHandlerAttribute(String name, String hashStatement) {
        // Same as readPropertyAttribute()
        readPropertyAttribute(name, null, hashStatement);
    }

    @Override
    public void endElement(Object sceneGraphObject) {

        currentTransientNode.setSceneGraphObject(sceneGraphObject);

        if (currentTransientNode instanceof TransientVirtual) {
            final TransientVirtual currentProperty = (TransientVirtual) currentTransientNode;
            final TransientNode currentParent = currentProperty.getParentNode();

            if (currentParent instanceof TransientProperty) {
                final TransientProperty parentProperty = (TransientProperty) currentParent;
                parentProperty.getValues().add(currentProperty.makeFxomObject(document));
            } else if (currentParent instanceof TransientObject) {
                final TransientObject parentObject = (TransientObject) currentParent;
                parentObject.getProperties().add(currentProperty.makeFxomProperty(document));
            } else if (currentParent instanceof TransientIntrinsic) {
                final TransientIntrinsic transientIntrinsic = (TransientIntrinsic) currentParent;
                transientIntrinsic.getProperties().add(currentProperty.makeFxomProperty(document));
            } else if (currentParent instanceof TransientIgnored) {
                // currentObject is an object inside an ignored object
                // Nothing to do for now
            } else {
                assert currentParent == null;
                final FXOMObject currentFxomObject = currentProperty.makeFxomObject(document);
                document.updateRoots(currentFxomObject, currentFxomObject.getSceneGraphObject());
            }

        } else if (currentTransientNode instanceof TransientObject) {
            final TransientObject currentInstance = (TransientObject) currentTransientNode;
            final FXOMObject currentFxomObject = currentInstance.makeFxomObject(document);
            final TransientNode currentParent = currentInstance.getParentNode();

            if (currentParent instanceof TransientProperty) {
                final TransientProperty parentProperty = (TransientProperty) currentParent;
                parentProperty.getValues().add(currentFxomObject);

            } else if (currentParent instanceof TransientObject) {
                final TransientObject parentInstance = (TransientObject) currentParent;
                parentInstance.getCollectedItems().add(currentFxomObject);

            } else if (currentParent instanceof TransientIntrinsic) {
                final TransientIntrinsic parentInstance = (TransientIntrinsic) currentParent;
                parentInstance.getCollectedItems().add(currentFxomObject);

            } else if (currentParent instanceof TransientIgnored) {
                // currentObject is an object inside an fx:define section
                // Nothing to do for now
            } else {
                assert currentParent == null;
                document.updateRoots(currentFxomObject, currentFxomObject.getSceneGraphObject());
            }

        } else if (currentTransientNode instanceof TransientIntrinsic) {
            final TransientIntrinsic currentIntrinsic = (TransientIntrinsic) currentTransientNode;
            final FXOMIntrinsic currentFxomIntrinsic = currentIntrinsic.makeFxomIntrinsic(document);
            final TransientNode currentParent = currentIntrinsic.getParentNode();

            if (currentParent instanceof TransientProperty) {
                final TransientProperty parentProperty = (TransientProperty) currentParent;
                parentProperty.getValues().add(currentFxomIntrinsic);
            } else if (currentParent instanceof TransientObject) {
                final TransientObject parentInstance = (TransientObject) currentParent;
                parentInstance.getCollectedItems().add(currentFxomIntrinsic);
            } else if (currentParent instanceof TransientIgnored) {
                // currentObject is an object inside an fx:define section
                // Nothing to do for now
            } else {
                assert currentParent == null;
                document.updateRoots(currentFxomIntrinsic, currentFxomIntrinsic.getSceneGraphObject());
            }
        } else if (currentTransientNode instanceof TransientProperty) {
            final TransientProperty currentProperty = (TransientProperty) currentTransientNode;
            final TransientNode currentParent = currentProperty.getParentNode();
            final FXOMProperty currentFxomProperty = currentProperty.makeFxomProperty(document);
            assert currentParent instanceof TransientObject || currentParent instanceof TransientIntrinsic;
            if (currentParent instanceof TransientObject) {
                final TransientObject parentObject = (TransientObject) currentParent;
                parentObject.getProperties().add(currentFxomProperty);
            } else if (currentParent instanceof TransientIntrinsic) {
                final TransientIntrinsic transientIntrinsic = (TransientIntrinsic) currentParent;
                transientIntrinsic.getProperties().add(currentFxomProperty);
            }

            // We ignore sceneGraphObject
        } else {
            assert currentTransientNode instanceof TransientIgnored;
            // Nothing to do in this case
        }

        currentTransientNode = currentTransientNode.getParentNode();
    }

    public ObservableMap<String, Object> getNamespace() {
        return fxmlLoader.getNamespace();
    }

    public Set<String> getNotFoundClasses() {
        return classloader.getNotFoundClasses();
    }

    // Deprecated stuff in FXMLLoader
    // RT-21226 : Promote setStaticLoad to public API
    private static void setStaticLoad(FXMLLoader loader, boolean staticLoad) {
        // See SB-266 and JDK-8186429
        // ReflectionUtils.setStaticLoad(loader, staticLoad);

        FXMLLoaderHelper.setStaticLoad(loader, staticLoad);
    }
}
