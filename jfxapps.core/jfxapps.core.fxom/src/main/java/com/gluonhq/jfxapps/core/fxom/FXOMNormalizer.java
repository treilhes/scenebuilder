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
package com.gluonhq.jfxapps.core.fxom;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * When loading an fxml document from an unknown source we may encounter some syntactics elements
 * that are hard or simply fail to be translated to this object model.<br/>
 * In that case the loaded {@link FXOMDocument} may contains duplicates or misplaced items that need to be cleaned/handled
 * The goal of FXOMNormalizer is to handle those special cases in the best way possible.<br/>
 * Take a look at {@link com.gluonhq.jfxapps.core.fxom.ext.FXOMNormalizer} to provide 
 * a custom extension to this process using the {@link ServiceLoader} mechanism  
 */
class FXOMNormalizer {
    
    private static ServiceLoader<com.gluonhq.jfxapps.core.fxom.ext.FXOMNormalizer> extensions;
    
    static {
        extensions = ServiceLoader.load(com.gluonhq.jfxapps.core.fxom.ext.FXOMNormalizer.class);
    }
    
    private final FXOMDocument fxomDocument;
    private final AtomicInteger changeCount;
    
    public FXOMNormalizer(FXOMDocument fxomDocument) {
        this.fxomDocument = fxomDocument;
        this.changeCount = new AtomicInteger();
    }
    
    public void normalize() {
        this.changeCount.set(0);
        extensions.forEach(e -> {
            this.changeCount.addAndGet(e.normalize(fxomDocument));
        });
        if (changeCount.get() >= 1) {
            fxomDocument.refreshSceneGraph();
        }
    }
}
