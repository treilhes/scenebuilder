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
package com.oracle.javafx.scenebuilder.document.hierarchy.display;

import java.net.URL;
import java.util.List;
import java.util.Set;

import org.scenebuilder.fxml.api.HierarchyMask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.collector.FxIdCollector;
import com.oracle.javafx.scenebuilder.api.Glossary;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.ui.misc.MessageLogger;
import com.oracle.javafx.scenebuilder.document.api.AbstractDisplayOption;
import com.oracle.javafx.scenebuilder.document.api.annotation.DisplayOptionName;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyFxIdJob;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@DisplayOptionName("hierarchy.displayoption.fxid")
public class FxIdDisplayOption extends AbstractDisplayOption {

    private static final Logger logger = LoggerFactory.getLogger(FxIdDisplayOption.class);

    private final JobManager jobManager;
    private final MessageLogger messageLogger;
    private final Glossary glossary;
    private final ModifyFxIdJob.Factory modifyFxIdJobFactory;

    public FxIdDisplayOption(
            JobManager jobManager,
            MessageLogger messageLogger,
            Glossary glossary,
            ModifyFxIdJob.Factory modifyFxIdJobFactory) {
        super();
        this.jobManager = jobManager;
        this.messageLogger = messageLogger;
        this.glossary = glossary;
        this.modifyFxIdJobFactory = modifyFxIdJobFactory;
    }

    /**
     * Returns the FX ID of the FX object represented by this item.
     *
     * @return the FX ID of the FX object represented by this item.
     */
    @Override
    public String getValue(HierarchyMask mask) {
        // Can be null for place holder items
        String id = mask == null ? null : mask.getFxId();

        if (logger.isDebugEnabled()) {
            logger.debug("Got fx:id '{}' from {}/{}", id, mask == null ? null : mask.getFxomObject(), // NOCHECK
                    mask == null ? null : mask.getFxomObject().hashCode());
        }

        return id;
    }

    @Override
    public String getResolvedValue(HierarchyMask mask) {
        return getValue(mask);
    }

    @Override
    public boolean isReadOnly(HierarchyMask mask) {
        return false;
    }

    @Override
    public boolean isMultiline(HierarchyMask mask) {
        return false;
    }

    @Override
    public boolean hasValue(HierarchyMask mask) {
        return mask != null;
    }

    @Override
    public void setValue(HierarchyMask mask, String newValue) {
        FXOMObject fxomObject = mask.getFxomObject();
        assert newValue != null;
        final String fxId = newValue.isEmpty() ? null : newValue;
        final String oldID = mask.getFxId();

        final AbstractJob job2 = modifyFxIdJobFactory.getJob(fxomObject, fxId);
        if (job2.isExecutable()) {

            if (logger.isDebugEnabled()) {
                logger.debug("Setting fx:id '{}' into {}/{}", fxId, fxomObject, fxomObject.hashCode()); // NOCHECK
            }

            // If a controller class has been defined,
            // check if the fx id is an injectable field
//            final String controllerClass
//                    = editorController.getFxomDocument().getFxomRoot().getFxController();
            final FXOMDocument fxomDocument = fxomObject.getFxomDocument();
            final String controllerClass = fxomDocument.getFxomRoot().getFxController();
            if (controllerClass != null && fxId != null) {
                //final URL location = editorController.getFxmlLocation();
                final URL location = fxomDocument.getLocation();
                final Class<?> clazz = fxomObject.getSceneGraphObject() == null ? null
                        : fxomObject.getSceneGraphObject().getClass();

                final List<String> fxIds1 = glossary.queryFxIds(location, controllerClass, clazz);
                if (fxIds1.contains(fxId) == false) {
                    messageLogger.logWarningMessage("log.warning.no.injectable.fxid", fxId);
                }
            }

            // Check duplicared fx ids
            //final FXOMDocument fxomDocument = editorController.getFxomDocument();
            final Set<String> fxIds2 = fxomDocument.collect(FxIdCollector.fxIdsMap()).keySet();
            if (fxIds2.contains(fxId)) {
                messageLogger.logWarningMessage("log.warning.duplicate.fxid", fxId);
            }

            jobManager.push(job2);

        } else if (fxId != null && !fxId.equals(oldID)) {
            messageLogger.logWarningMessage("log.warning.invalid.fxid", fxId);
        }
    }

}
