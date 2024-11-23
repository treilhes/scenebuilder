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
package com.gluonhq.jfxapps.core.selection;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.core.selection.i18n.I18NSelection;
import com.gluonhq.jfxapps.core.selection.job.BackupSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.BringForwardJob;
import com.gluonhq.jfxapps.core.selection.job.BringToFrontJob;
import com.gluonhq.jfxapps.core.selection.job.ClearSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.CutSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.DeleteObjectJob;
import com.gluonhq.jfxapps.core.selection.job.DeleteObjectSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.DeleteSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.DuplicateSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.InsertAsAccessoryJob;
import com.gluonhq.jfxapps.core.selection.job.ModifySelectionJob;
import com.gluonhq.jfxapps.core.selection.job.PasteIntoJob;
import com.gluonhq.jfxapps.core.selection.job.SendBackwardJob;
import com.gluonhq.jfxapps.core.selection.job.SendToBackJob;
import com.gluonhq.jfxapps.core.selection.job.SetDocumentRootJob;
import com.gluonhq.jfxapps.core.selection.job.TrimSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.UpdateSelectionJob;

public class SelectionExtension implements OpenExtension {
    @Override
    public UUID getId() {
        return UUID.fromString("a112d6e9-4079-4733-96d1-d29b3fef675d");
    }

    @Override
    public UUID getParentId() {
        return OpenExtension.ROOT_ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
     // @formatter:off
        return Arrays.asList(
                BackupSelectionJob.class,
                BringForwardJob.class,
                BringToFrontJob.class,
                ClearSelectionJob.class,
                CutSelectionJob.class,
                DeleteObjectJob.class,
                DeleteObjectSelectionJob.class,
                DeleteSelectionJob.class,
                DuplicateSelectionJob.class,
                InsertAsAccessoryJob.class,
                ModifySelectionJob.class,
                PasteIntoJob.class,
                SendBackwardJob.class,
                SendToBackJob.class,
                SetDocumentRootJob.class,
                TrimSelectionJob.class,
                UpdateSelectionJob.class,

                SelectionJobsFactoryImpl.class,
                SelectionImpl.class,
                TargetSelectionImpl.class,
                I18NSelection.class
            );
     // @formatter:on
    }
}
