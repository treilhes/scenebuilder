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
package com.oracle.javafx.scenebuilder.fxml.job;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.model.AbstractExtension;
import com.gluonhq.jfxapps.core.job.editor.atomic.AddPropertyJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.AddPropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyFxControllerJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyFxIdJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ReIndexObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemoveCollectionItemJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemoveFxControllerJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemoveNodeJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemoveObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ReplaceObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ReplacePropertyValueJobT;
import com.gluonhq.jfxapps.core.job.editor.atomic.ToggleFxRootJob;
import com.gluonhq.jfxapps.core.job.editor.misc.PrunePropertiesJob;
import com.gluonhq.jfxapps.core.job.editor.reference.CombineExpressionReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.CombineIntrinsicReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.CombineReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.DeleteRefereeObjectJob;
import com.gluonhq.jfxapps.core.job.editor.reference.ExpandExpressionReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.ExpandIntrinsicReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.ExpandReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.ReferencesUpdaterJob;
import com.gluonhq.jfxapps.core.job.editor.reference.UpdateReferencesJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.FitToParentObjectJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.RelocateSelectionJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.UseComputedSizesObjectJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.UsePredefinedSizeJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.UseSizeJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RelocateNodeJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.reference.FixToggleGroupExpressionReferenceJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.reference.FixToggleGroupIntrinsicReferenceJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.reference.FixToggleGroupReferenceJob;
import com.oracle.javafx.scenebuilder.fxml.job.preferences.global.RootContainerHeightPreference;
import com.oracle.javafx.scenebuilder.fxml.job.preferences.global.RootContainerWidthPreference;

public class SbJobsExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("9ba90ad0-95d1-4872-b13a-daa8c1289655");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                AddPropertyJob.class,
                AddPropertyJob.Factory.class,
                AddPropertyValueJob.class,
                AddPropertyValueJob.Factory.class,
                CombineExpressionReferenceJob.class,
                CombineExpressionReferenceJob.Factory.class,
                CombineIntrinsicReferenceJob.class,
                CombineIntrinsicReferenceJob.Factory.class,
                CombineReferenceJob.class,
                CombineReferenceJob.Factory.class,
                DeleteRefereeObjectJob.class,
                DeleteRefereeObjectJob.Factory.class,
                ExpandExpressionReferenceJob.class,
                ExpandExpressionReferenceJob.Factory.class,
                ExpandIntrinsicReferenceJob.class,
                ExpandIntrinsicReferenceJob.Factory.class,
                ExpandReferenceJob.class,
                ExpandReferenceJob.Factory.class,
                FitToParentObjectJob.class,
                FitToParentObjectJob.Factory.class,
                FixToggleGroupExpressionReferenceJob.class,
                FixToggleGroupExpressionReferenceJob.Factory.class,
                FixToggleGroupIntrinsicReferenceJob.class,
                FixToggleGroupIntrinsicReferenceJob.Factory.class,
                FixToggleGroupReferenceJob.class,
                FixToggleGroupReferenceJob.Factory.class,
                ModifyFxControllerJob.class,
                ModifyFxControllerJob.Factory.class,
                ModifyFxIdJob.class,
                ModifyFxIdJob.Factory.class,
                ModifyObjectJob.class,
                ModifyObjectJob.Factory.class,
                PrunePropertiesJob.class,
                PrunePropertiesJob.Factory.class,
                ReIndexObjectJob.class,
                ReIndexObjectJob.Factory.class,
                ReferencesUpdaterJob.class,
                ReferencesUpdaterJob.Factory.class,
                RelocateNodeJob.class,
                RelocateNodeJob.Factory.class,
                RelocateSelectionJob.class,
                RelocateSelectionJob.Factory.class,
                RemoveCollectionItemJob.class,
                RemoveCollectionItemJob.Factory.class,
                RemoveFxControllerJob.class,
                RemoveFxControllerJob.Factory.class,
                RemoveNodeJob.class,
                RemoveNodeJob.Factory.class,
                RemoveObjectJob.class,
                RemoveObjectJob.Factory.class,
                RemovePropertyJob.class,
                RemovePropertyJob.Factory.class,
                RemovePropertyValueJob.class,
                RemovePropertyValueJob.Factory.class,
                ReplaceObjectJob.class,
                ReplaceObjectJob.Factory.class,
                ReplacePropertyValueJobT.class,
                ReplacePropertyValueJobT.Factory.class,
                RootContainerHeightPreference.class,
                RootContainerWidthPreference.class,

                ToggleFxRootJob.class,
                ToggleFxRootJob.Factory.class,
                UpdateReferencesJob.class,
                UpdateReferencesJob.Factory.class,
                UseComputedSizesObjectJob.class,
                UseComputedSizesObjectJob.Factory.class,
                UsePredefinedSizeJob.class,
                UsePredefinedSizeJob.Factory.class,
                UseSizeJob.class,
                UseSizeJob.Factory.class
            );
     // @formatter:on
    }
}
