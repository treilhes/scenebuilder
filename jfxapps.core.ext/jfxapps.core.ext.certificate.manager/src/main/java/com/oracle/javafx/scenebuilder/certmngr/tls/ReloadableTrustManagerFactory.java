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
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;

import com.oracle.javafx.scenebuilder.api.subjects.NetworkManager;

public class ReloadableTrustManagerFactory extends TrustManagerFactorySpi {

    private final TrustManagerFactory defaultTrustManagerFactory;
    private final NetworkManager networkManager;
    private final ReloadableTrustManagerProvider trustProvider;
    private File storeFile;
    private char[] storePassword;
    private long userResponseTimeout;
    
    public ReloadableTrustManagerFactory() throws NoSuchAlgorithmException {
         List<Provider> defaultProviders = Arrays.stream(Security.getProviders())
            .filter(p -> p.getClass() != ReloadableTrustManagerProvider.class)
            .collect(Collectors.toList());
         
         trustProvider = (ReloadableTrustManagerProvider)Arrays.stream(Security.getProviders())
                .filter(p -> p.getClass() == ReloadableTrustManagerProvider.class)
                .findFirst().get();
        this.networkManager = trustProvider.getNetworkManager();
        this.storeFile = trustProvider.getStoreFile();
        this.storePassword = trustProvider.getStorePassword();
        this.userResponseTimeout = trustProvider.getUserResponseTimeout();
        
        Provider.Service service = getService(defaultProviders, TrustManagerFactory.class.getSimpleName(),
                TrustManagerFactory.getDefaultAlgorithm());
        defaultTrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm(),
                service.getProvider());
    }
    
    private Provider.Service getService(List<Provider> providers, String type, String algorithm) {
        for (Provider p:providers) {
            Provider.Service service = p.getService(type, algorithm);
            if (service != null) {
                return service;
            }
        }
        return null;
    }

    @Override
    protected void engineInit(KeyStore keyStore) throws KeyStoreException {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        try {
            return new TrustManager[]{
                        new ReloadableX509TrustManager(defaultTrustManagerFactory, networkManager, storeFile, storePassword, userResponseTimeout)
                    };
        } catch (Exception e) {
            return new TrustManager[0];
        }
    }
}
