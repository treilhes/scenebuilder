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
package com.oracle.javafx.scenebuilder.certmngr.tls;

import java.io.File;
import java.security.Provider;

import javax.net.ssl.TrustManagerFactory;

import com.oracle.javafx.scenebuilder.api.subjects.NetworkManager;

public class ReloadableTrustManagerProvider extends Provider {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant PROVIDER_VERSION. */
    private final static String PROVIDER_VERSION = "1.0.0";

    private final NetworkManager networkManager;

    private final File storeFile;

    private final char[] storePassword;
    
    public ReloadableTrustManagerProvider(
            NetworkManager networkManager,
            File storeFile,
            char[] storePassword
            ) {
        
        super(ReloadableX509TrustManager.class.getSimpleName(), PROVIDER_VERSION, "Provider to manage SceneBuilder certificate at runtime");
        this.networkManager = networkManager;
        this.storeFile = storeFile;
        this.storePassword = storePassword;
        put(TrustManagerFactory.class.getSimpleName() + "." + TrustManagerFactory.getDefaultAlgorithm(), ReloadableTrustManagerFactory.class.getName());
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public File getStoreFile() {
        return storeFile;
    }

    public char[] getStorePassword() {
        return storePassword;
    }

}
