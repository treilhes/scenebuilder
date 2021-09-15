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

package com.oracle.javafx.scenebuilder.job.editor;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata.Qualifier;

import javafx.scene.control.Tooltip;

/**
 *
 */
public class AddTooltipToSelectionJob extends BatchSelectionJob {
        
    private Map<FXOMObject, FXOMObject> tooltipMap; // Initialized lazily
    private final ApplicationContext context;
    private final SceneBuilderManager sceneBuilderManager;

    public AddTooltipToSelectionJob(ApplicationContext context, Editor editor) {
        super(context, editor);
        this.context = context;
        this.sceneBuilderManager = context.getBean(SceneBuilderManager.class);
    }

    public Collection<FXOMObject> getTooltips() {
        constructTooltipMap();
        return tooltipMap.values();
    }

    /*
     * BatchSelectionJob
     */

    @Override
    protected List<Job> makeSubJobs() {

        constructTooltipMap();

        final List<Job> result = new LinkedList<>();
        for (Map.Entry<FXOMObject, FXOMObject> e : tooltipMap.entrySet()) {
            final FXOMObject fxomObject = e.getKey();
            DesignHierarchyMask designHierarchyMask = new DesignHierarchyMask(fxomObject);
            Accessory tooltipAccessory = designHierarchyMask
                    .getAccessoryForPropertyName(DesignHierarchyMask.AccessoryProperty.TOOLTIP);
            
            if (tooltipAccessory == null) {
                continue;
            }
            final FXOMObject tooltipObject = e.getValue();
            final Job insertJob = new InsertAsAccessoryJob(getContext(),
                    tooltipObject, fxomObject,
                    tooltipAccessory,
                    getEditorController()).extend();
            result.add(insertJob);
        }

        return result;
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        final Collection<FXOMObject> contextMenus = tooltipMap.values();
        assert contextMenus.isEmpty() == false;
        final FXOMObject hitMenu = contextMenus.iterator().next();

        return new ObjectSelectionGroup(contextMenus, hitMenu, null);
    }

    /*
     * CompositeJob
     */

    @Override
    protected String makeDescription() {
        return I18N.getString("label.action.edit.add.tooltip");
    }


    /*
     * Private
     */

    private void constructTooltipMap() {
        if (tooltipMap == null) {
            tooltipMap = new LinkedHashMap<>();

            // Build the ContextMenu item from the library builtin items
            Metadata metadata = context.getBean(Metadata.class);
            ComponentClassMetadata<?> ccm = metadata.queryComponentMetadata(Tooltip.class);
            final URL tooltipFxmlURL = ccm.getQualifiers().values().stream().findFirst().orElse(Qualifier.UNKNOWN).getFxmlUrl();
            assert tooltipFxmlURL != null;

            final AbstractSelectionGroup asg = getEditorController().getSelection().getGroup();
            assert asg instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;

            try {
                final String contextMenuFxmlText
                        = FXOMDocument.readContentFromURL(tooltipFxmlURL);

                final FXOMDocument fxomDocument = context.getBean(DocumentManager.class).fxomDocument().get();

                for (FXOMObject fxomObject : osg.getItems()) {
                    final FXOMDocument contextMenuDocument = new FXOMDocument(
                            contextMenuFxmlText,
                            tooltipFxmlURL, sceneBuilderManager.classloader().get(), null);

                    assert contextMenuDocument != null;
                    final FXOMObject contextMenuObject = contextMenuDocument.getFxomRoot();
                    assert contextMenuObject != null;
                    contextMenuObject.moveToFxomDocument(fxomDocument);
                    assert contextMenuDocument.getFxomRoot() == null;

                    tooltipMap.put(fxomObject, contextMenuObject);
                }
            } catch(IOException x) {
                throw new IllegalStateException("Bug in " + getClass().getSimpleName(), x); //NOCHECK
            }
        }
    }
}
