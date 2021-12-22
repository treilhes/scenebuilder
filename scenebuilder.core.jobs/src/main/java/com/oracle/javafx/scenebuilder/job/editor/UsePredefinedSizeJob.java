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

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.preferences.global.RootContainerHeightPreference;
import com.oracle.javafx.scenebuilder.job.preferences.global.RootContainerWidthPreference;

/**
 * Job to use for setting the size of the given FXOMObject; when not provided
 * deal with the top level item of the layout. The job will set the preferred
 * width and height to the given value while min and max width and height are
 * set to Region.USE_PREF_SIZE.
 * No action is taken unless the FXOMObject is an instance of Region or WebView.
 */
public class UsePredefinedSizeJob extends UseSizeJob {

    public UsePredefinedSizeJob(SceneBuilderBeanFactory context, Editor editor, Size size, FXOMObject fxomObject) {
        super(context, editor, getWidthFromSize(context, size), getHeightFromSize(context, size), fxomObject);
    }

    public UsePredefinedSizeJob(SceneBuilderBeanFactory context, Editor editor, Size size) {
        super(context, editor, getWidthFromSize(context, size), getHeightFromSize(context, size));
    }

    private static int getWidthFromSize(SceneBuilderBeanFactory context, Size size) {
        assert size != Size.SIZE_PREFERRED;

        if (size == Size.SIZE_DEFAULT) {
            return context.getBean(RootContainerWidthPreference.class).getValue().intValue();
        }
        return size.getWidth();
    }

    private static int getHeightFromSize(SceneBuilderBeanFactory context, Size size) {
        assert size != Size.SIZE_PREFERRED;

        if (size == Size.SIZE_DEFAULT) {
            return context.getBean(RootContainerHeightPreference.class).getValue().intValue();
        }

        return size.getHeight();
    }
}
