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
package com.oracle.javafx.scenebuilder.selection;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.extension.AbstractExtension;
import com.oracle.javafx.scenebuilder.selection.i18n.I18NSelection;
import com.oracle.javafx.scenebuilder.selection.job.AddContextMenuToSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.AddTooltipToSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.BackupSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.BringForwardJob;
import com.oracle.javafx.scenebuilder.selection.job.BringToFrontJob;
import com.oracle.javafx.scenebuilder.selection.job.ClearSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.CutSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.DeleteObjectJob;
import com.oracle.javafx.scenebuilder.selection.job.DeleteObjectSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.DeleteSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.DuplicateSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.FitToParentSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.InsertAsAccessoryJob;
import com.oracle.javafx.scenebuilder.selection.job.InsertAsSubComponentJob;
import com.oracle.javafx.scenebuilder.selection.job.ModifyCacheHintJob;
import com.oracle.javafx.scenebuilder.selection.job.ModifySelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.PasteIntoJob;
import com.oracle.javafx.scenebuilder.selection.job.PasteJob;
import com.oracle.javafx.scenebuilder.selection.job.SendBackwardJob;
import com.oracle.javafx.scenebuilder.selection.job.SendToBackJob;
import com.oracle.javafx.scenebuilder.selection.job.SetDocumentRootJob;
import com.oracle.javafx.scenebuilder.selection.job.TrimSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.UpdateSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.UseComputedSizesSelectionJob;

public class SelectionExtension extends AbstractExtension {
    @Override
    public UUID getId() {
        return UUID.fromString("a112d6e9-4079-4733-96d1-d29b3fef675d");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                AddContextMenuToSelectionJob.class,
                AddContextMenuToSelectionJob.Factory.class,
                AddTooltipToSelectionJob.class,
                AddTooltipToSelectionJob.Factory.class,
                BackupSelectionJob.class,
                BackupSelectionJob.Factory.class,
                BringForwardJob.class,
                BringForwardJob.Factory.class,
                BringToFrontJob.class,
                BringToFrontJob.Factory.class,
                ClearSelectionJob.class,
                ClearSelectionJob.Factory.class,
                CutSelectionJob.class,
                CutSelectionJob.Factory.class,
                DeleteObjectJob.class,
                DeleteObjectJob.Factory.class,
                DeleteObjectSelectionJob.class,
                DeleteObjectSelectionJob.Factory.class,
                DeleteSelectionJob.class,
                DeleteSelectionJob.Factory.class,
                DuplicateSelectionJob.class,
                DuplicateSelectionJob.Factory.class,
                FitToParentSelectionJob.class,
                FitToParentSelectionJob.Factory.class,
                I18NSelection.class,
                InsertAsAccessoryJob.class,
                InsertAsAccessoryJob.Factory.class,
                InsertAsSubComponentJob.class,
                InsertAsSubComponentJob.Factory.class,
                ModifyCacheHintJob.class,
                ModifyCacheHintJob.Factory.class,
                ModifySelectionJob.class,
                ModifySelectionJob.Factory.class,
                ObjectSelectionGroup.class,
                ObjectSelectionGroup.Factory.class,
                PasteIntoJob.class,
                PasteIntoJob.Factory.class,
                PasteJob.class,
                PasteJob.Factory.class,
                SelectionImpl.class,
                SendBackwardJob.class,
                SendBackwardJob.Factory.class,
                SendToBackJob.class,
                SendToBackJob.Factory.class,
                SetDocumentRootJob.class,
                SetDocumentRootJob.Factory.class,
                TrimSelectionJob.class,
                TrimSelectionJob.Factory.class,
                UpdateSelectionJob.class,
                UpdateSelectionJob.Factory.class,
                UseComputedSizesSelectionJob.class,
                UseComputedSizesSelectionJob.Factory.class

            );
     // @formatter:on
    }
}
