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

package com.oracle.javafx.scenebuilder.fxml.selection.job;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.HierarchyMask;
import com.gluonhq.jfxapps.core.api.HierarchyMask.Accessory;
import com.gluonhq.jfxapps.core.api.editor.selection.AbstractSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata.Qualifier;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

import javafx.scene.control.Tooltip;

/**
 * Add a default tooltip into the currently selected objects if those objects have a tooltip property
 * @deprecated the new ui allow to insert a tooltip using standard drag and drop jobs
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Deprecated
public final class AddTooltipToSelectionJob extends BatchSelectionJob {

    private Map<FXOMObject, FXOMObject> tooltipMap; // Initialized lazily

    private final FXOMDocument fxomDocument;
    private final SceneBuilderManager sceneBuilderManager;
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final IMetadata metadata;
    private final InsertAsAccessoryJob.Factory insertAsAccessoryJobFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    protected AddTooltipToSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            SceneBuilderManager sceneBuilderManager,
            Selection selection,
            DesignHierarchyMask.Factory designMaskFactory,
            IMetadata metadata,
            InsertAsAccessoryJob.Factory insertAsAccessoryJobFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.sceneBuilderManager = sceneBuilderManager;
        this.designMaskFactory = designMaskFactory;
        this.metadata = metadata;
        this.insertAsAccessoryJobFactory = insertAsAccessoryJobFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters() {
    }

    public Collection<FXOMObject> getTooltips() {
        constructTooltipMap();
        return tooltipMap.values();
    }

    /*
     * BatchSelectionJob
     */

    @Override
    protected List<AbstractJob> makeSubJobs() {

        constructTooltipMap();

        final List<AbstractJob> result = new LinkedList<>();
        for (Map.Entry<FXOMObject, FXOMObject> e : tooltipMap.entrySet()) {
            final FXOMObject fxomObject = e.getKey();
            HierarchyMask designHierarchyMask = designMaskFactory.getMask(fxomObject);
            Accessory tooltipAccessory = designHierarchyMask
                    .getAccessory(DesignHierarchyMask.AccessoryProperty.TOOLTIP);

            if (tooltipAccessory == null) {
                continue;
            }
            final FXOMObject tooltipObject = e.getValue();
            final AbstractJob insertJob = insertAsAccessoryJobFactory.getJob(tooltipObject, fxomObject,tooltipAccessory);
            result.add(insertJob);
        }

        return result;
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        final Collection<FXOMObject> contextMenus = tooltipMap.values();
        assert contextMenus.isEmpty() == false;
        final FXOMObject hitMenu = contextMenus.iterator().next();

        return objectSelectionGroupFactory.getGroup(contextMenus, hitMenu, null);
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
            ComponentClassMetadata<?> ccm = metadata.queryComponentMetadata(Tooltip.class);
            final URL tooltipFxmlURL = ccm.getQualifiers().values().stream().findFirst().orElse(Qualifier.UNKNOWN).getFxmlUrl();
            assert tooltipFxmlURL != null;

            final AbstractSelectionGroup asg = getSelection().getGroup();
            assert asg instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;

            try {
                final String contextMenuFxmlText
                        = FXOMDocument.readContentFromURL(tooltipFxmlURL);

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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<AddTooltipToSelectionJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  AddTooltipToSelectionJob} job
         * @return the job to execute
         */
        public AddTooltipToSelectionJob getJob() {
            return create(AddTooltipToSelectionJob.class, j -> j.setJobParameters());
        }
    }
}
