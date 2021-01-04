/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.editor.selection;

import java.util.HashSet;
import java.util.Set;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

/*
 *   This class represents the selection state:
 *   - the selected instances,
 *   - the selected classes,
 *   - the common parent for the selected instances (if any),
 *   - the unresolved selected instances (if any),
 *      in case of an instance is missing its corresponding object (for instance a png file)
 */
public class SelectionState {

    private final Selection selection;
    private final Set<FXOMInstance> selectedInstances = new HashSet<>();
    private final Set<FXOMIntrinsic> selectedIntrinsics = new HashSet<>();
    private final Set<Class<?>> selectedClasses = new HashSet<>();
    private Class<?> commonParentClass;
    private FXOMObject commonParentObject;
    private final Set<FXOMInstance> unresolvedInstances = new HashSet<>();

    public SelectionState(Editor editorController) {
        this.selection = editorController.getSelection();
        initialize();
    }

    public void initialize() {
        // New selection: initializePopupContent all the selection variables
        selectedInstances.clear();
        selectedIntrinsics.clear();
        if (selection.getGroup() instanceof ObjectSelectionGroup) {
            handleObjectSelectionGroup(selection.getGroup());
        } else if (selection.getGroup() instanceof GridSelectionGroup) {
            handleGridSelectionGroup(selection.getGroup());
        }

        selectedClasses.clear();
        for (FXOMInstance instance : selectedInstances) {
            if (instance.getDeclaredClass() != null) { // null means unresolved instance
                selectedClasses.add(instance.getDeclaredClass());
            }
        }

        commonParentClass = null;
        for (FXOMInstance instance : selectedInstances) {
            if (commonParentClass == null) {
                // first instance
                commonParentClass = getParentClass(instance);
            } else {
                if (getParentClass(instance) != commonParentClass) {
                    commonParentClass = null;
                    break;
                }
            }
        }

        for (FXOMIntrinsic intrinsic : selectedIntrinsics) {
            if (commonParentClass == null) {
                // first instance
                commonParentClass = getParentClass(intrinsic);
            } else {
                if (getParentClass(intrinsic) != commonParentClass) {
                    commonParentClass = null;
                    break;
                }
            }
        }

        commonParentObject = null;
        for (FXOMInstance instance : selectedInstances) {
            if (commonParentObject == null) {
                // first instance
                commonParentObject = instance.getParentObject();
            } else {
                if (instance.getParentObject() != commonParentObject) {
                    commonParentObject = null;
                    break;
                }
            }
        }

        for (FXOMIntrinsic intrinsic : selectedIntrinsics) {
            if (commonParentObject == null) {
                // first instance
                commonParentObject = intrinsic.getParentObject();
            } else {
                if (intrinsic.getParentObject() != commonParentObject) {
                    commonParentObject = null;
                    break;
                }
            }
        }

        unresolvedInstances.clear();
        for (FXOMInstance instance : selectedInstances) {
            if (instance.getSceneGraphObject() == null) {
                unresolvedInstances.add(instance);
            }
        }
    }

    private void handleGridSelectionGroup(AbstractSelectionGroup group) {
        GridSelectionGroup gsg = (GridSelectionGroup) group;
        for (FXOMInstance inst : gsg.collectConstraintInstances()) {
            selectedInstances.add(inst);
//            // Open the Layout section, since all the row/columns properties are there.
//            if (getExpandedSectionId() != SectionId.LAYOUT) {
//                setExpandedSection(SectionId.LAYOUT);
//            }
        }
    }

    private void handleObjectSelectionGroup(AbstractSelectionGroup group) {
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) group;
        for (FXOMObject obj : osg.getItems()) {
            handleFxomInstance(obj);
            handleFxomIntrincis(obj);
        }
    }

    private void handleFxomInstance(FXOMObject obj) {
        if (obj instanceof FXOMInstance) {
            selectedInstances.add((FXOMInstance) obj);
        }
    }

    private void handleFxomIntrincis(FXOMObject obj) {
        if(obj instanceof  FXOMIntrinsic) {
            FXOMIntrinsic intrinsic = (FXOMIntrinsic) obj;
            selectedIntrinsics.add(intrinsic);
            FXOMInstance fxomInstance = intrinsic.createFxomInstanceFromIntrinsic();
            selectedInstances.add(fxomInstance);
        }
    }

    public boolean isSelectionEmpty() {
        return selection.isEmpty();
    }

    public Set<Class<?>> getSelectedClasses() {
        return selectedClasses;
    }

    public Class<?> getCommonParentClass() {
        return commonParentClass;
    }

    public FXOMObject getCommonParentObject() {
        return commonParentObject;
    }

    public Set<FXOMInstance> getSelectedInstances() {
        return selectedInstances;
    }
    
    public Set<FXOMIntrinsic> getSelectedIntrinsics() {
        return selectedIntrinsics;
    }

    public Set<FXOMInstance> getUnresolvedInstances() {
        return unresolvedInstances;
    }

    public Selection getSelection() {
        return selection;
    }

    public static Class<?> getParentClass(FXOMObject instance) {
        FXOMObject parent = instance.getParentObject();
        if (parent == null) {
            // root
            return null;
        }
        // A parent is always a FXOMInstance
        assert parent instanceof FXOMInstance;
        return ((FXOMInstance) parent).getDeclaredClass();
    }

}