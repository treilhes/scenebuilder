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
package com.oracle.javafx.scenebuilder.certmngr.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.CertificateManager;
import com.oracle.javafx.scenebuilder.certmngr.tls.ReloadableTrustManagerProvider;
import com.oracle.javafx.scenebuilder.certmngr.tls.ReloadableX509TrustManager;

@Component
public class CertificateManagerController implements CertificateManager {
    
    private final static String KEYSTORE_PASSWORD = "scenebuilder";
    private final Api api;

    private KeyStore keystore;
    
    public CertificateManagerController(
            @Autowired Api api
            ) {
        this.api = api;
        Security.insertProviderAt(new ReloadableTrustManagerProvider(api.getNetworkManager(), keystoreFile(), KEYSTORE_PASSWORD.toCharArray()), 1);
    }
    
    private File keystoreFile() {
        return new File(api.getFileSystem().getApplicationDataFolder(), "truststore.jks");
    }
    private void load() throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
        File keystoreFile = keystoreFile();
        
        if (keystoreFile.exists()) {
            try(FileInputStream fis = new FileInputStream(keystoreFile)){
                keystore = KeyStore.getInstance(KeyStore.getDefaultType());
                keystore.load(fis, KEYSTORE_PASSWORD.toCharArray());
            }
        } else {
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null);
        }
    }
    
//    private SSLContext getSSLContext() throws Exception {
//        TrustManagerFactory trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        trustMgrFactory.init(keystore);
//        
//        TrustManager[] trustManagers = new TrustManager[] {
//                new ReloadableX509TrustManager(trustMgrFactory)
//        };
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, trustManagers, null);
//        SSLContext.setDefault(sslContext);
//        return sslContext;
//    }

    @Override
    public Certificate loadFromUrl(URL url) {
        String host = url.getHost();
        int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
        
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            ReloadableX509TrustManager defaultTrustManager = (ReloadableX509TrustManager)tmf.getTrustManagers()[0];
            System.out.println();
          context.init(null, new TrustManager[] {defaultTrustManager}, null);
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
        socket.setSoTimeout(10000);

        try {
        System.out.println("Starting SSL handshake...");
        socket.startHandshake();
        socket.close();
        System.out.println();
        System.out.println("No errors, certificate is already trusted");
        } catch (SSLException e) {
        System.out.println();
        e.printStackTrace(System.out);
        }

        X509Certificate[] chain = null;//defaultTrustManager.chain;
        if (chain == null) {
            System.out.println("Could not obtain server certificate chain");
            return null;
        }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void trustPermanently(Certificate certificate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void trustTemporarily(Certificate certificate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeTrust(Certificate certificate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Certificate> listTrustedCertificates() {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    
    

}
