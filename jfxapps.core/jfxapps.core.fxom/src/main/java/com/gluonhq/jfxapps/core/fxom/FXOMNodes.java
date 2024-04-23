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
package com.gluonhq.jfxapps.core.fxom;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.collector.PropertyCollector;
import com.gluonhq.jfxapps.core.fxom.ext.FileLoader;
import com.gluonhq.jfxapps.core.fxom.ext.WeakProperty;
import com.gluonhq.jfxapps.core.fxom.glue.GlueComment;
import com.gluonhq.jfxapps.core.fxom.glue.GlueDocument;
import com.gluonhq.jfxapps.core.fxom.glue.GlueElement;
import com.gluonhq.jfxapps.core.fxom.glue.GlueInstruction;
import com.gluonhq.jfxapps.core.fxom.util.JavaLanguage;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleGroup;

/**
 * This class groups static utility methods which operate on FXOMNode and
 * subclasses (a bit like Collection and Collections).
 *
 *
 */
public class FXOMNodes {

    private static final Set<WeakProperty> weakProperties;
    private static final Set<FileLoader> fileLoaders;

    static {
        ServiceLoader<WeakProperty> weakPropertyLoaders = ServiceLoader.load(WeakProperty.class);
        ServiceLoader<FileLoader> fileLoaderLoaders = ServiceLoader.load(FileLoader.class);
        weakProperties = new HashSet<>();
        fileLoaders = new HashSet<>();
        weakPropertyLoaders.forEach(wkp -> weakProperties.add(wkp));
        fileLoaderLoaders.forEach(fl -> fileLoaders.add(fl));
    }



    //TODO the content is set in stone, how to expand it? Who may want to expand it?
    // anyway this is not the right place for this method
    // UPDATE: now it is extensible but legacy values still need to come from metadata
    public static Set<WeakProperty> getWeakProperties() {

//            weakPropertyNames.add("labelFor");
//            weakPropertyNames.add("expandedPane");
//            weakPropertyNames.add("clip");
        return weakProperties;
    }
    /**
     * Sorts the specified set of objects according their location in
     * the fxom document. Objets are sorted according depth first order.
     * In particular, if objects all have the same parent, then the resulting
     * list will be sorted by indexes.
     *
     * @param objects a set of fxom objects (never null but possibly empty)
     * @return the list of objets sorted by position in the fxom document
     */
    public static List<FXOMObject> sort(Set<FXOMObject> objects) {
        final List<FXOMObject> result;

        assert objects != null;

        if (objects.isEmpty()) {
            result = Collections.emptyList();
        } else if (objects.size() == 1) {
            result = Collections.singletonList(objects.iterator().next());
        } else {
            final FXOMObject object0 = objects.iterator().next();
            final FXOMDocument fxomDocument = object0.getFxomDocument();
            assert fxomDocument != null;
            result = new ArrayList<>();
            sort(fxomDocument.getFxomRoot(), objects, result);
        }

        return result;
    }


    /**
     * Flattens a set of fxom objects.
     * A set of fxom objects is declared "flat" if each object member
     * of the set has no ancestor member of the set.
     *
     * @param objects a set of fxom objects (never null)
     * @return the flat set of objects.
     */
    public static Set<FXOMObject> flatten(Set<FXOMObject> objects) {
        final Set<FXOMObject> result = new HashSet<>();

        assert objects != null;

        for (FXOMObject o : objects) {
            if (lookupAncestor(o, objects) == null) {
                result.add(o);
            }
        }

        return result;
    }


    /**
     * Returns null or the first ancestor of "obj" which belongs to "candidates".
     * @param obj an fxom object (never null)
     * @param candidates a set of fxom object (not null and not empty)
     * @return null or the first ancestor of "obj" which belongs to "candidates".
     */
    public static FXOMObject lookupAncestor(FXOMObject obj, Set<FXOMObject> candidates) {
        assert obj != null;
        assert candidates != null;
        assert candidates.isEmpty() == false;

        FXOMObject result = obj.getParentObject();
        while ((result != null) && (candidates.contains(result) == false)) {
            result = result.getParentObject();
        }

        return result;
    }


    //TODO this method must move. It is too high level for this module
    public static FXOMObject newObject(FXOMDocument targetDocument, File file)
            throws IOException {
        assert targetDocument != null;
        assert file != null;
        FXOMObject result = null;
        if (file.getAbsolutePath().endsWith(".fxml")) { //NOCHECK
            final String fxmlText
                    = FXOMDocument.readContentFromURL(file.toURI().toURL());
            final FXOMDocument transientDoc = new FXOMDocument(
                    fxmlText,
                    targetDocument.getLocation(),
                    targetDocument.getClassLoader(),
                    targetDocument.getResources());
            result = transientDoc.getFxomRoot();
            if (result != null) {
                result.moveToFxomDocument(targetDocument);
            }
        } else {

            Optional<FileLoader> fileLoader = fileLoaders.stream().filter(fl -> fl.canLoad(file)).findFirst();
            if (fileLoader.isPresent()) {
                result = fileLoader.get().loadInto(targetDocument, file);
            }

        }

        return result;
    }

    public static FXOMIntrinsic newInclude(FXOMDocument targetDocument, File file)
            throws IOException {
        assert targetDocument != null;
        assert targetDocument.getLocation() != null;
        assert file != null;
        FXOMIntrinsic result = null;
        if (file.getAbsolutePath().endsWith(".fxml")) { //NOCHECK
            final URL fxmlURL = file.toURI().toURL();
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            final FXOMDocument transientDoc = new FXOMDocument(
                    fxmlText,
                    fxmlURL,
                    targetDocument.getClassLoader(),
                    targetDocument.getResources());
            if (transientDoc.getFxomRoot() != null) {
                final PrefixedValue pv
                        = PrefixedValue.makePrefixedValue(fxmlURL, targetDocument.getLocation());
                assert pv.isDocumentRelativePath();
                assert pv.toString().startsWith(FXMLLoader.RELATIVE_PATH_PREFIX);
                final String includeRef
                        = pv.toString().substring(FXMLLoader.RELATIVE_PATH_PREFIX.length());
                result = new FXOMInclude(targetDocument, includeRef);
                result.setSourceSceneGraphObject(transientDoc.getFxomRoot().getSceneGraphObject());
            }
        }

        return result;
    }

    public static FXOMDocument newDocument(FXOMObject source) {
        assert source != null;

        final FXOMDocument result = new FXOMDocument();

        /*
         * If source's document contains unresolved objects,
         * then clones import instructions from the source document
         * to the new document.
         */
        final FXOMDocument sourceDocument = source.getFxomDocument();
        assert sourceDocument.getFxomRoot() != null; // contains at least source

        final boolean cloneHeaderFooter = source.getFxomDocument().getFxomRoot() == source;

        final List<FXOMObject> unresolvedObjects
                = collectUnresolvedObjects(sourceDocument.getFxomRoot());

        final GlueDocument sourceGlue = sourceDocument.getGlue();
        final GlueDocument resultGlue = result.getGlue();

        if (unresolvedObjects.isEmpty() == false) {
            // Copy all the imports from the source document to the new document
            for (GlueInstruction i : sourceGlue.collectInstructions("import")) {
                final GlueInstruction ci = new GlueInstruction(resultGlue, i.getTarget(), i.getData());
                resultGlue.addHeader(ci);
            }
        }

        if (cloneHeaderFooter) {
            //if we clone top level element, we also clone headers (virtual) elements
            for (GlueElement i : sourceGlue.collectHeaderElements()) {
                if (GlueComment.class.isInstance(i)) {
                    // only comments can be in header/footer
                    GlueComment comment = (GlueComment)i;
                    final GlueComment ci = new GlueComment(resultGlue, comment);
                    ci.setContentText(comment.getContentText());
                    resultGlue.addHeader(ci);
                }
            }
        }
        /*
         * Clones source to the new document
         */
        final FXOMCloner cloner = new FXOMCloner(result);
        final FXOMObject sourceClone = cloner.clone(source);

        /*
         * Setup new document : sourceClone is the root,
         * same location, same class loader.
         */
        result.beginUpdate();
        result.setLocation(sourceDocument.getLocation());
        result.setClassLoader(sourceDocument.getClassLoader());
        result.setFxomRoot(sourceClone);
        if (result.getFxomRoot() instanceof FXOMInstance) {
            trimStaticProperties((FXOMInstance) result.getFxomRoot());
        }
        result.endUpdate();

        return result;
    }


    public static void updateProperty(FXOMElement fxomInstance, FXOMProperty sourceProperty) {
        assert fxomInstance != null;
        assert sourceProperty != null;
        assert sourceProperty.getFxomDocument() == fxomInstance.getFxomDocument();

        final FXOMProperty currentProperty = fxomInstance.getProperties().get(sourceProperty.getName());
        if (currentProperty == null) {
            sourceProperty.addToParentInstance(-1, fxomInstance);
        } else if ((currentProperty instanceof FXOMPropertyT)
                && (sourceProperty instanceof FXOMPropertyT)) {
            final FXOMPropertyT currentPropertyT = (FXOMPropertyT) currentProperty;
            final FXOMPropertyT newPropertyT = (FXOMPropertyT) sourceProperty;
            updateProperty(currentPropertyT, newPropertyT);
        } else if ((currentProperty instanceof FXOMPropertyC)
                && (sourceProperty instanceof FXOMPropertyC)) {
            final FXOMPropertyC currentPropertyC = (FXOMPropertyC) currentProperty;
            final FXOMPropertyC newPropertyC = (FXOMPropertyC) sourceProperty;
            updateProperty(currentPropertyC, newPropertyC);
        } else {
            final int index = currentProperty.getIndexInParentInstance();
            currentProperty.removeFromParentInstance();
            sourceProperty.addToParentInstance(index, fxomInstance);
        }
    }


    public static void updateProperty(FXOMPropertyT fxomProperty, FXOMPropertyT sourceProperty) {
        assert fxomProperty != null;
        assert sourceProperty != null;
        assert fxomProperty.getName().equals(sourceProperty.getName());
        fxomProperty.setValue(sourceProperty.getValue());
    }


    public static void updateProperty(FXOMPropertyC fxomProperty, FXOMPropertyC sourceProperty) {
        assert fxomProperty != null;
        assert sourceProperty != null;
        assert fxomProperty.getName().equals(sourceProperty.getName());

        final List<FXOMObject> currentValues = new ArrayList<>();
        currentValues.addAll(fxomProperty.getChildren());
        final List<FXOMObject> sourceValues = new ArrayList<>();
        sourceValues.addAll(sourceProperty.getChildren());

        final int currentCount = currentValues.size();
        final int newCount = sourceValues.size();
        final int updateCount = Math.min(currentCount, newCount);

        // Update items
        for (int i = 0; i < updateCount; i++) {
            final FXOMObject currentValue = currentValues.get(i);
            final FXOMObject newValue = sourceValues.get(i);
            if ((currentValue instanceof FXOMInstance) &&
                    (newValue instanceof FXOMInstance)) {
                final FXOMInstance currentInstance = (FXOMInstance) currentValue;
                final FXOMInstance newInstance = (FXOMInstance) newValue;
                if (currentInstance.getDeclaredClass() == newInstance.getDeclaredClass()) {
                    updateInstance(currentInstance, newInstance);
                } else {
                    replacePropertyValue(currentValue, newValue);
                }
            } else if ((currentValue instanceof FXOMCollection) &&
                    (newValue instanceof FXOMCollection)) {
                final FXOMCollection currentCollection = (FXOMCollection) currentValue;
                final FXOMCollection newCollection = (FXOMCollection) newValue;
                updateCollection(currentCollection, newCollection);
            } else if ((currentValue instanceof FXOMIntrinsic) &&
                    (newValue instanceof FXOMIntrinsic)) {
                final FXOMIntrinsic currentIntrinsic = (FXOMIntrinsic) currentValue;
                final FXOMIntrinsic newIntrinsic = (FXOMIntrinsic) newValue;
                updateIntrinsic(currentIntrinsic, newIntrinsic);
            } else {
                replacePropertyValue(currentValue, newValue);
           }
        }

        if (currentCount < newCount) {
            // Add new items
            for (int i = currentCount; i < newCount; i++) {
                final FXOMObject newValue = sourceValues.get(i);
                newValue.addToParentProperty(-1, fxomProperty);
            }
        } else {
            // Delete old items
            for (int i = newCount; i < currentCount; i++) {
                final FXOMObject currentValue = currentValues.get(i);
                currentValue.removeFromParentProperty();
            }
        }
    }


    public static void updateInstance(FXOMInstance fxomInstance, FXOMInstance sourceInstance) {
        assert fxomInstance != null;
        assert sourceInstance != null;
        assert fxomInstance.getFxomDocument() == sourceInstance.getFxomDocument();
        assert fxomInstance.getDeclaredClass() == sourceInstance.getDeclaredClass();

        // Compute obsolete properties.
        // It must be done here because sourceInstance is going to mutate.
        final Set<PropertyName> obsoleteNames = new HashSet<>();
        obsoleteNames.addAll(fxomInstance.getProperties().keySet());
        obsoleteNames.removeAll(sourceInstance.getProperties().keySet());

        // Update properties
        final Set<FXOMProperty> sourceProperties = new HashSet<>(sourceInstance.getProperties().values());
        for (FXOMProperty sourceProperty : sourceProperties) {
            updateProperty(fxomInstance, sourceProperty);
        }
        // Remove obsolete properties
        for (PropertyName pn : obsoleteNames) {
            final FXOMProperty fxomProperty = fxomInstance.getProperties().get(pn);
            assert fxomProperty != null;
            assert fxomProperty.getParentInstance() == fxomInstance;
            fxomProperty.removeFromParentInstance();
        }

        fxomInstance.setFxConstant(sourceInstance.getFxConstant());
        fxomInstance.setFxValue(sourceInstance.getFxValue());
        fxomInstance.setFxFactory(sourceInstance.getFxFactory());
    }


    public static void updateCollection(FXOMCollection fxomCollection, FXOMCollection sourceCollection) {
        assert fxomCollection != null;
        assert sourceCollection != null;
        assert fxomCollection.getFxomDocument() == sourceCollection.getFxomDocument();

        final int currentCount = fxomCollection.getItems().size();
        final int sourceCount = sourceCollection.getItems().size();
        final int updateCount = Math.min(currentCount, sourceCount);

        // Update items
        for (int i = 0; i < updateCount; i++) {
            final FXOMObject currentValue = fxomCollection.getItems().get(i);
            final FXOMObject newValue = sourceCollection.getItems().get(i);
            if ((currentValue instanceof FXOMInstance) &&
                    (newValue instanceof FXOMInstance)) {
                final FXOMInstance currentInstance = (FXOMInstance) currentValue;
                final FXOMInstance newInstance = (FXOMInstance) newValue;
                updateInstance(currentInstance, newInstance);
            } else if ((currentValue instanceof FXOMCollection) &&
                    (newValue instanceof FXOMCollection)) {
                final FXOMCollection currentCollection = (FXOMCollection) currentValue;
                final FXOMCollection newCollection = (FXOMCollection) newValue;
                updateCollection(currentCollection, newCollection);
            } else if ((currentValue instanceof FXOMIntrinsic) &&
                    (newValue instanceof FXOMIntrinsic)) {
                final FXOMIntrinsic currentIntrinsic = (FXOMIntrinsic) currentValue;
                final FXOMIntrinsic newIntrinsic = (FXOMIntrinsic) newValue;
                updateIntrinsic(currentIntrinsic, newIntrinsic);
            } else {
                final int index = currentValue.getIndexInParentProperty();
                assert index != -1;
                currentValue.removeFromParentCollection();
                newValue.addToParentCollection(index, fxomCollection);
            }
        }

        if (currentCount < sourceCount) {
            // Add new items
            final int addCount = sourceCount - currentCount;
            for (int i = 0; i < addCount; i++) {
                final FXOMObject newValue = sourceCollection.getItems().get(i);
                newValue.addToParentCollection(-1, fxomCollection);
            }
        } else {
            // Delete old items
            final int removeCount = currentCount - sourceCount;
            for (int i = 0; i < removeCount; i++) {
                final FXOMObject currentValue = fxomCollection.getItems().get(sourceCount);
                currentValue.removeFromParentProperty();
            }
        }

        fxomCollection.setFxConstant(sourceCollection.getFxConstant());
        fxomCollection.setFxValue(sourceCollection.getFxValue());
        fxomCollection.setFxFactory(sourceCollection.getFxFactory());
    }


    public static void updateIntrinsic(FXOMIntrinsic fxomIntrinsic, FXOMIntrinsic sourceIntrinsic) {
        assert fxomIntrinsic != null;
        assert sourceIntrinsic != null;
        assert fxomIntrinsic.getFxomDocument() != sourceIntrinsic.getFxomDocument();
        assert fxomIntrinsic.getType() == sourceIntrinsic.getType();

        fxomIntrinsic.setSource(sourceIntrinsic.getSource());
        fxomIntrinsic.setFxConstant(sourceIntrinsic.getFxConstant());
        fxomIntrinsic.setFxValue(sourceIntrinsic.getFxValue());
        fxomIntrinsic.setFxFactory(sourceIntrinsic.getFxFactory());
    }


    public static List<FXOMPropertyT> collectReferenceExpression(FXOMObject fxomRoot, String fxId) {
        assert fxomRoot != null;
        assert fxId != null;

        final List<FXOMPropertyT> result = new ArrayList<>();

        for (FXOMPropertyT p : fxomRoot.collect(PropertyCollector.allSimpleProperties())) {
            final PrefixedValue pv = new PrefixedValue(p.getValue());
            if (pv.isExpression()) {
                /*
                 * p is an FXOMPropertyT like this:
                 *
                 * <.... property="$id" .... />
                 */
                final String id = pv.getSuffix();
                if (id.equals(fxId)) {
                    result.add(p);
                }
            }
        }

        return result;
    }


    public static List<FXOMObject> collectUnresolvedObjects(FXOMObject fxomObject) {
        final List<FXOMObject> result = new ArrayList<>();

        for (FXOMObject o : serializeObjects(fxomObject)) {
            if (o.getSceneGraphObject() == null) {
                result.add(o);
            }
        }

        return result;
    }


    public static List<FXOMObject> serializeObjects(FXOMObject fxomObject) {
        final List<FXOMObject> result = new ArrayList<>();

        serializeObjects(fxomObject, result);
        assert result.isEmpty() == false;
        assert result.get(0) == fxomObject;

        return result;
    }

    public static void removeToggleGroups(Map<String, FXOMObject> fxIdMap) {
        assert fxIdMap != null;

        for (String fxId : new HashSet<>(fxIdMap.keySet())) {
            final FXOMObject fxomObject = fxIdMap.get(fxId);
            if (fxomObject.getSceneGraphObject().get() instanceof ToggleGroup) {
                fxIdMap.remove(fxId);
            }
        }
    }


    /**
     * Extract the reference id from the node <br/>
     * Case node is a fx:reference/fx:copy then return 'source' property value<br/>
     * Case node is an single valued property {@link FXOMPropertyT} then return the expression with a removed suffix<br/>
     * or null
     * @param node
     * @return
     */
    public static String extractReferenceSource(FXOMNode node) {
        final String result;

        if (node instanceof FXOMIntrinsic) {
            final FXOMIntrinsic intrinsic = (FXOMIntrinsic) node;
            switch(intrinsic.getType()) {
                case FX_REFERENCE:
                case FX_COPY:
                    result = intrinsic.getSource();
                    break;
                default:
                    result = null;
            }
        } else if (node instanceof FXOMPropertyT) {
            final FXOMPropertyT property = (FXOMPropertyT) node;
            final PrefixedValue pv = new PrefixedValue(property.getValue());
            if (pv.isExpression() && JavaLanguage.isIdentifier(pv.getSuffix())) {
                result = pv.getSuffix();
            } else {
                result = null;
            }
        } else {
            result = null;
        }

        return result;
    }


    private static final PropertyName toggleGroupName = new PropertyName("toggleGroup");

    public static boolean isToggleGroupReference(FXOMNode node) {
        final boolean result;

        if (extractReferenceSource(node) == null) {
            result = false;
        } else {
            if (node instanceof FXOMIntrinsic) {
                final FXOMIntrinsic intrinsic = (FXOMIntrinsic) node;
                final FXOMProperty parentProperty = intrinsic.getParentProperty();
                if (parentProperty == null) {
                    result = false;
                } else {
                    result = parentProperty.getName().equals(toggleGroupName);
                }
            } else if (node instanceof FXOMPropertyT) {
                final FXOMPropertyT property = (FXOMPropertyT) node;
                result = property.getName().equals(toggleGroupName);
            } else {
                result = false;
            }
        }

        return result;
    }


    public static FXOMPropertyC makeToggleGroup(FXOMDocument fxomDocument, String fxId) {
        final FXOMInstance toggleGroup = new FXOMInstance(fxomDocument, ToggleGroup.class);
        toggleGroup.setFxId(fxId);
        return new FXOMPropertyC(fxomDocument, toggleGroupName, toggleGroup);
    }


    public static boolean isWeakReference(FXOMNode node) {
        final boolean result;

        if (node instanceof FXOMIntrinsic) {
            final FXOMIntrinsic intrinsic = (FXOMIntrinsic) node;
            switch(intrinsic.getType()) {
                case FX_REFERENCE:
                case FX_COPY:
                    if (intrinsic.getParentProperty() != null) {
                        final PropertyName propertyName = intrinsic.getParentProperty().getName();
                        if (propertyName.getResidenceClass() == null) {
                            //TODO test me
                            FXOMElement parent = intrinsic.getParentProperty().getParentInstance();
                            if (parent.getSceneGraphObject() != null) {
                                Class<?> parentClass = parent.getSceneGraphObject().getClass();
                                result = getWeakProperties().stream().anyMatch(wp -> {
                                    return wp.getPropertyName().equals(propertyName.getName())
                                            && wp.getPropertyOwnerType().isAssignableFrom(parentClass);
                                });
                            } else {
                                result = false;
                            }
                        } else {
                            result = false;
                        }
                    } else {
                        result = false;
                    }
                    break;
                default:
                    result = false;
            }
        } else if (node instanceof FXOMPropertyT) {
            final FXOMPropertyT property = (FXOMPropertyT) node;
            final PrefixedValue pv = new PrefixedValue(property.getValue());
            if (pv.isExpression() && JavaLanguage.isIdentifier(pv.getSuffix())) {
                final PropertyName propertyName = property.getName();
                if (propertyName.getResidenceClass() == null) {
                    //Before
                    //result = getWeakPropertyNames().contains(propertyName.getName());

                    //TODO test me
                    FXOMElement parent = property.getParentInstance();
                    if (parent.getSceneGraphObject() != null) {
                        Class<?> parentClass = parent.getSceneGraphObject().getClass();
                        result = getWeakProperties().stream().anyMatch(wp -> {
                            return wp.getPropertyName().equals(propertyName.getName())
                                    && wp.getPropertyOwnerType().isAssignableFrom(parentClass);
                        });
                    } else {
                        result = false;
                    }
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
        } else {
            result = false;
        }

        return result;
    }





    /*
     * Private
     */

    private static void sort(FXOMObject from,
            Set<FXOMObject> objects, List<FXOMObject> result) {

        if (objects.contains(from)) {
            result.add(from);
        }

        if (from instanceof FXOMCollection) {
            final FXOMCollection collection = (FXOMCollection) from;
            for (FXOMObject item : collection.getItems()) {
                sort(item, objects, result);
            }
        } else if (from instanceof FXOMInstance) {
            final FXOMInstance instance = (FXOMInstance) from;
            final List<PropertyName> propertyNames
                    = new ArrayList<>(instance.getProperties().keySet());
            Collections.sort(propertyNames);
            for (PropertyName name : propertyNames) {
                final FXOMProperty property = instance.getProperties().get(name);
                assert property != null;
                if (property instanceof FXOMPropertyC) {
                    final FXOMPropertyC propertyC = (FXOMPropertyC) property;
                    for (FXOMObject v : propertyC.getChildren()) {
                        sort(v, objects, result);
                    }
                }
            }
        } else {
            assert from instanceof FXOMIntrinsic
                    : "Unexpected FXOMObject subclass " + from.getClass();
        }
    }


    private static void trimStaticProperties(FXOMInstance fxomInstance) {
        final List<FXOMProperty> properties =
                new ArrayList<>(fxomInstance.getProperties().values());
        for (FXOMProperty p : properties) {
            if (p.getName().getResidenceClass() != null) {
                // This is a static property : we remove it.
                p.removeFromParentInstance();
            }
        }
    }


    private static void replacePropertyValue(FXOMObject replacee, FXOMObject replacement) {
        assert replacee.getIndexInParentProperty() != -1;

        final int replaceeIndex = replacee.getIndexInParentProperty();
        assert replaceeIndex != -1;
        replacement.addToParentProperty(replaceeIndex, replacee.getParentProperty());
        replacee.removeFromParentProperty();
    }

    private static void serializeObjects(FXOMObject fxomObject, List<FXOMObject> result) {
        assert fxomObject != null;
        assert result != null;

        result.add(fxomObject);

        if (fxomObject instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            for (FXOMProperty p : fxomInstance.getProperties().values()) {
                if (p instanceof FXOMPropertyC) {
                    final FXOMPropertyC pc = (FXOMPropertyC) p;
                    for (FXOMObject v : pc.getChildren()) {
                        serializeObjects(v, result);
                    }
                }
            }
        } else if (fxomObject instanceof FXOMCollection) {
            final FXOMCollection fxomCollection = (FXOMCollection) fxomObject;
            for (FXOMObject i : fxomCollection.getItems()) {
                serializeObjects(i, result);
            }
        }
    }
}
