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

package com.gluonhq.jfxapps.core.job.editor.atomic;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;

/**
 * This Job updates the FXOM document at execution time.
 * It removes the provided {@link FXOMPropertyT} from his parent {@link FXOMElement} provided by {@link FXOMPropertyT#getParentInstance()}
 * by an {@link FXOMPropertyC} containing the new {@link FXOMObject} value.
 * This peculiar case happens mainly when replacing a reference by the referee scenegraph
 * Use the dedicated {@link Factory} to create an instance
 */
@Prototype
public final class ReplacePropertyValueJobT extends AbstractJob {

    private FXOMPropertyT hostProperty;
    private FXOMObject newValue;

    private FXOMElement hostInstance;
    private FXOMPropertyC newProperty;

    protected ReplacePropertyValueJobT(
            JobExtensionFactory extensionFactory) {
        super(extensionFactory);
    }

    public void setJobParameters(FXOMPropertyT hostProperty, FXOMObject newValue) {
        assert hostProperty != null;
        assert newValue != null;

        this.hostProperty = hostProperty;
        this.newValue = newValue;
    }
    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return (hostProperty.getParentInstance() != null);
    }

    @Override
    public void doExecute() {
        hostInstance = hostProperty.getParentInstance();
        newProperty = new FXOMPropertyC(hostProperty.getFxomDocument(), hostProperty.getName());

        // Now same as redo()
        doRedo();
    }

    @Override
    public void doUndo() {
        assert hostProperty.getParentInstance() == null;
        assert newProperty.getParentInstance() == hostInstance;

        newProperty.removeFromParentInstance();
        newValue.removeFromParentProperty();
        hostProperty.addToParentInstance(-1, hostInstance);
    }

    @Override
    public void doRedo() {
        assert hostProperty.getParentInstance() == hostInstance;
        assert newProperty.getParentInstance() == null;

        hostProperty.removeFromParentInstance();
        newValue.addToParentProperty(-1, newProperty);
        newProperty.addToParentInstance(-1, hostInstance);
    }

}
