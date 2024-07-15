/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.core.fxom.collector.FxCollector;
import com.gluonhq.jfxapps.core.fxom.glue.GlueElement;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

/**
 *
 */
public class FXOMCloner {

    private final FXOMDocument targetDocument;
    private final FxIdCollector fxIdCollector;
    private FXOMObject clonee;
    private final Set<String> addedFxIds = new HashSet<>();

    public FXOMCloner(FXOMDocument targetDocument) {
        assert targetDocument != null;
        this.targetDocument = targetDocument;
        this.fxIdCollector = new FxIdCollector(targetDocument);
    }

    public FXOMDocument getTargetDocument() {
        return targetDocument;
    }

    public FXOMObject clone(FXOMObject clonee) {
        return clone(clonee, false /* preserveCloneFxId */);
    }

    public FXOMObject clone(FXOMObject clonee, boolean preserveCloneeFxId) {
        assert clonee != null;
        assert addedFxIds.isEmpty();

        this.clonee = clonee;

        // Creates a deep clone of 'clonee'
        final FXOMObject result = cloneObject(this.clonee);
        addedFxIds.clear();

        // Renames fxid in the clone so that there is no naming
        // conflict when the clone is hooked to its target document.
        renameFxIds(result, preserveCloneeFxId);

        return result;
    }


    /*
     * Private
     */

    private FXOMObject cloneObject(FXOMObject fxomObject) {
        final FXOMObject result;

        if (fxomObject instanceof FXOMCollection) {
            result = cloneCollection((FXOMCollection) fxomObject);
        } else if (fxomObject instanceof FXOMInstance) {
            result = cloneInstance((FXOMInstance) fxomObject);
        } else if (fxomObject instanceof FXOMIntrinsic) {
            result = cloneIntrinsic((FXOMIntrinsic) fxomObject);
        } else if (fxomObject instanceof FXOMDefine) {
            result = cloneDefine((FXOMDefine) fxomObject);
        } else if (fxomObject instanceof FXOMScript) {
            result = cloneScript((FXOMScript) fxomObject);
        } else if (fxomObject instanceof FXOMComment) {
            result = cloneComment((FXOMComment) fxomObject);
        } else {
            throw new RuntimeException(getClass().getSimpleName()
                    + " needs some additional implementation"); //NOCHECK
        }

        return result;
    }

    private FXOMCollection cloneCollection(FXOMCollection source) {
        assert source != null;

        final FXOMCollection result = new FXOMCollection(
                targetDocument,
                source.getDeclaredClass());

        for (FXOMObject sourceItem : source.getItems()) {
            final FXOMObject newItem = cloneObject(sourceItem);
            newItem.addToParentCollection(-1, result);
        }

        result.setFxConstant(source.getFxConstant());
        result.setFxController(source.getFxController());
        result.setFxFactory(source.getFxFactory());
        result.setFxId(source.getFxId());
        result.setFxValue(source.getFxValue());

        return result;
    }


    private FXOMInstance cloneInstance(FXOMInstance source) {

        assert source != null;

        final FXOMInstance result;
        if (source.getDeclaredClass() == null) {
            assert source.getSceneGraphObject().isEmpty(); // source is unresolved
            result = new FXOMInstance(
                    targetDocument,
                    source.getGlueElement().getTagName());
        } else {
            result = new FXOMInstance(
                    targetDocument,
                    source.getDeclaredClass());
        }

        for (Map.Entry<PropertyName, FXOMProperty> e : source.getProperties().entrySet()) {
            final FXOMProperty newProperty = cloneProperty(e.getValue());
            // Note: cloneProperty() may
            if (newProperty != null) {
                newProperty.addToParentInstance(-1, result);
            }
        }

        result.setFxConstant(source.getFxConstant());
        result.setFxController(source.getFxController());
        result.setFxFactory(source.getFxFactory());
        result.setFxId(source.getFxId());
        result.setFxValue(source.getFxValue());

        return result;
    }


    private FXOMObject cloneIntrinsic(FXOMIntrinsic source) {
        assert source != null;

        final boolean shallowClone;
        final FXOMObject sourceObject;
        switch(source.getType()) {
            case FX_REFERENCE:
            case FX_COPY:
                final String sourceFxId = source.getSource();
                assert sourceFxId != null;
                sourceObject = clonee.getFxomDocument().collect(
                        FxCollector.fxIdFindFirst(sourceFxId))
                        .get();
                if (isInsideClonee(sourceObject) || addedFxIds.contains(sourceFxId)) {
                    shallowClone = true;
                } else {
                    shallowClone = false;
                    addedFxIds.add(sourceFxId);
                }
                break;
            default:
                sourceObject = null;
                shallowClone = true;
                break;
        }

        final FXOMObject result;
        if (shallowClone) {
            // We clone the intrinsic itself
            switch (source.getType()) {
            case FX_COPY:
                result = new FXOMCopy(targetDocument);
                break;
            case FX_REFERENCE:
                result = new FXOMReference(targetDocument);
                break;
            case FX_INCLUDE:
                result = new FXOMInclude(targetDocument);
                break;
            default:
                result = null;
                break;
            }

            result.setFxConstant(source.getFxConstant());
            result.setFxController(source.getFxController());
            result.setFxFactory(source.getFxFactory());
            result.setFxId(source.getFxId());
            result.setFxValue(source.getFxValue());

        }
        else {
            assert sourceObject != null;

            // We clone the target of the intrinsic
            result = cloneObject(sourceObject);
        }

        if (result instanceof FXOMElement fe) {
            for (Map.Entry<PropertyName, FXOMProperty> e : source.getProperties().entrySet()) {
                final FXOMProperty newProperty = cloneProperty(e.getValue());
                // Note: cloneProperty() may
                if (newProperty != null) {
                    newProperty.addToParentInstance(-1, fe);
                }
            }
        }

        return result;
    }

    private FXOMObject cloneComment(FXOMComment source) {
        assert source != null;
        assert source.getSceneGraphObject().isEmpty(); // source is unresolved

        final FXOMComment result = new FXOMComment(targetDocument, source.getComment());

        return result;
    }

    private FXOMObject cloneScript(FXOMScript source) {
        assert source != null;
        assert source.getSceneGraphObject().isEmpty(); // source is unresolved

        final FXOMScript result = new FXOMScript(targetDocument);
        result.setScript(source.getScript());
        result.setSource(source.getSource());
        return result;
    }

    private FXOMObject cloneDefine(FXOMDefine source) {
        assert source != null;

        final FXOMDefine result = new FXOMDefine(targetDocument);

        for (Map.Entry<PropertyName, FXOMProperty> e : source.getProperties().entrySet()) {
            final FXOMProperty newProperty = cloneProperty(e.getValue());
            // Note: cloneProperty() may
            if (newProperty != null) {
                newProperty.addToParentInstance(-1, result);
            }
        }

        return result;
    }

    private FXOMProperty cloneProperty(FXOMProperty source) {
        final FXOMProperty result;

        if (source instanceof FXOMPropertyV) {
            result = clonePropertyV((FXOMPropertyV) source);
        } else if (source instanceof FXOMPropertyC) {
            result = clonePropertyC((FXOMPropertyC) source);
        } else if (source instanceof FXOMPropertyT) {
            result = clonePropertyT((FXOMPropertyT) source);
        } else {
            throw new RuntimeException(getClass().getSimpleName()
                    + " needs some additional implementation"); //NOCHECK
        }

        return result;
    }

    public FXOMPropertyC clonePropertyC(FXOMPropertyC source) {
        assert source != null;

        final FXOMPropertyC result = new FXOMPropertyC(
                targetDocument,
                source.getName());

        for (FXOMObject sourceValue : source.getChildren()) {
            final FXOMObject newValue = cloneObject(sourceValue);
            newValue.addToParentProperty(-1, result);
        }

        return result;
    }

    public FXOMProperty clonePropertyT(FXOMPropertyT source) {
        assert source != null;

        final boolean shallowClone;
        final FXOMObject sourceObject;
        final PrefixedValue pv = new PrefixedValue(source.getValue());
        if (pv.isExpression()) {
            final String sourceFxId = pv.getSuffix();
            assert sourceFxId != null;
            sourceObject = clonee.getFxomDocument()
                    .collect(FxCollector.fxIdFindFirst(sourceFxId))
                    .get();
            assert sourceObject != null : "sourceFxId=" + sourceFxId;
            if (isInsideClonee(sourceObject) || addedFxIds.contains(sourceFxId)) {
                shallowClone = true;
            } else {
                shallowClone = false;
                addedFxIds.add(sourceFxId);
            }
        } else {
            shallowClone = true;
            sourceObject = null;
        }

        final FXOMProperty result;
        if (shallowClone) {


            if (source.getChildren().size() > 0) {
                GlueElement propertyElement = new GlueElement(targetDocument.getGlue(), source.getName().getName());

                List<FXOMObject> newChildren = source.getChildren().stream()
                        .map(c -> cloneObject(c))
                        .peek(c -> c.getGlueElement().addToParent(propertyElement))
                        .collect(Collectors.toList());

                result = new FXOMPropertyT(
                        targetDocument,
                        source.getName(),
                        propertyElement,
                        newChildren,
                        source.getValue());
            } else {
                result = new FXOMPropertyT(
                        targetDocument,
                        source.getName(),
                        source.getValue());
            }

        } else {
            assert sourceObject != null;
            if (FXOMNodes.isWeakReference(source)) {
                result = null;
            } else {
                result = new FXOMPropertyC(
                        targetDocument,
                        source.getName(),
                        cloneObject(sourceObject));
            }
        }

        return result;
    }

    public FXOMPropertyV clonePropertyV(FXOMPropertyV source) {
        assert source != null;

        final FXOMPropertyV result = new FXOMPropertyV(
                targetDocument,
                source.getName());

        for (FXOMObject sourceValue : source.getChildren()) {
            final FXOMObject newValue = cloneObject(sourceValue);
            newValue.addToParentProperty(-1, result);
        }

        return result;
    }

    private boolean isInsideClonee(FXOMObject object) {
        assert object != null;
        return (object == clonee) || object.isDescendantOf(clonee);
    }

    private void renameFxIds(FXOMObject clone, boolean preserveCloneeFxId) {

        final Map<String, FXOMObject> fxIds = clone.collect(FxCollector.fxIdsUniqueMap());

        if (preserveCloneeFxId && (clonee.getFxId() != null)) {
            // We don't apply renaming to the fx:id of the clonee
            assert clonee.getFxId().equals(clone.getFxId());
            assert fxIds.get(clonee.getFxId()) == clone;
            fxIds.remove(clonee.getFxId());
        }

        for (Map.Entry<String, FXOMObject> e : fxIds.entrySet()) {
            final String candidateFxId = e.getKey();
            final FXOMObject declarer = e.getValue();

            final String renamedFxId = fxIdCollector.importFxId(candidateFxId);

            if (renamedFxId.equals(candidateFxId) == false) {

                /*
                 * We renamed candidateFxId as renamedFxId
                 *  1) on the declarer object
                 *  2) in each fx:reference/fx:copy object
                 *  3) in each xxx="$candidateFxId" expression property //NOCHECK
                 */

                // 1)
                declarer.setFxId(renamedFxId);

                // 2)
                for (FXOMIntrinsic reference : clone.collect(FxCollector.fxReferenceBySource(candidateFxId))) {
                    assert reference.getSource().equals(candidateFxId);
                    reference.setSource(renamedFxId);
                }

                // 3)
                final PrefixedValue pv = new PrefixedValue(PrefixedValue.Type.EXPRESSION, renamedFxId);
                final String newValue = pv.toString();
                for (FXOMPropertyT reference : FXOMNodes.collectReferenceExpression(clone, candidateFxId)) {
                    reference.setValue(newValue);
                }
            }
        }

    }
}
