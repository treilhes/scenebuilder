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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.subjects.NetworkManager;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class ReloadableX509TrustManager extends X509ExtendedTrustManager implements X509TrustManager {
    
    private final TrustManagerFactory originalTrustManagerFactory;
    private X509ExtendedTrustManager clientCertsTrustManager;
    private X509ExtendedTrustManager serverCertsTrustManager;
    private static ArrayList<Certificate> certList;
    private NetworkManager networkManager;
    private static Logger logger = LoggerFactory.getLogger(ReloadableX509TrustManager.class);

    private static KeyStore physicalStore;
    private char[] physicalStorePassword;
    private File physicalStoreFile;
    private final CompositeDisposable disposables;
    public ReloadableX509TrustManager(TrustManagerFactory originalTrustManagerFactory, NetworkManager networkManager, File physicalStoreFile, char[] physicalStorePassword) throws Exception {

        try {
            this.originalTrustManagerFactory = originalTrustManagerFactory;
            this.networkManager = networkManager;
            this.physicalStoreFile = physicalStoreFile;
            this.physicalStorePassword = physicalStorePassword;
            this.disposables = new CompositeDisposable();
            
            if (certList == null) {
                certList = new ArrayList<>();
                certList.addAll(getDefaultCertificates());
            }
            
            if (physicalStore == null) {
                physicalStore = KeyStore.getInstance(KeyStore.getDefaultType());
                if (physicalStoreFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(physicalStoreFile)){
                        physicalStore.load(fis, physicalStorePassword);
                    } catch (Exception e) {
                        logger.error("Error while loading the physical store", e);
                    }
                } else {
                    physicalStore.load(null);
                    try (FileOutputStream fos = new FileOutputStream(physicalStoreFile)){
                        physicalStore.store(fos, physicalStorePassword);
                    } catch (Exception e) {
                        logger.error("Error while storing the physical store", e);
                    }
                }
                
                Iterator<String> it = physicalStore.aliases().asIterator();
                while (it.hasNext()) {
                    String alias = it.next();
                    certList.add(physicalStore.getCertificate(alias));
                }
            }
            
            reloadTrustManager();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw e;
        }
    }

    /**
     * Removes a certificate from the pending list. Automatically reloads the TrustManager
     *
     * @param cert is not null and was already added
     * @throws Exception if cannot be reloaded
     */
    public void removeCertificate(Certificate cert) throws Exception {
        certList.remove(cert);
        reloadTrustManager();
    }

    /**
     * Adds a list of certificates to the manager. Automatically reloads the TrustManager
     *
     * @param certs is not null
     * @throws Exception if cannot be reloaded
     */
    public void addCertificates(List<Certificate> certs) throws CertificateException {
        try {
            certList.addAll(certs);
            reloadTrustManager();
        } catch (Exception e) {
            throw new CertificateException("Unable to update in memory truststore", e);
        }
    }
    
    /**
     * Adds a list of certificates to the physical certificate store for backup.
     *
     * @param certs is not null
     * @throws Exception if cannot be saved
     */
    public void addCertificatesToPhysicalKeystore(List<Certificate> certs) throws CertificateException {
        try {
            for (Certificate cert : certs) {
                physicalStore.setCertificateEntry(UUID.randomUUID().toString(), cert);
            }
            try (FileOutputStream fos = new FileOutputStream(physicalStoreFile)){
                physicalStore.store(fos, physicalStorePassword);
            }
        } catch (Exception e) {
            throw new CertificateException("Unable to update physical truststore", e);
        }
    }
    
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            clientCertsTrustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException e) {
            handleNewCertificates(chain, e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
        try {
            clientCertsTrustManager.checkClientTrusted(x509Certificates, s, socket);
        } catch (CertificateException e) {
            handleNewCertificates(x509Certificates, e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
        try {
            clientCertsTrustManager.checkClientTrusted(x509Certificates, s, sslEngine);
        } catch (CertificateException e) {
            handleNewCertificates(x509Certificates, e);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            serverCertsTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            handleNewCertificates(chain, e);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
        try {
            serverCertsTrustManager.checkServerTrusted(x509Certificates, s, socket);
        } catch (CertificateException e) {
            handleNewCertificates(x509Certificates, e);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
        serverCertsTrustManager.checkServerTrusted(x509Certificates, s, sslEngine);
    }
    
    private void handleNewCertificates(X509Certificate[] x509Certificates, CertificateException originalException) throws CertificateException {
        final AtomicBoolean distrust = new AtomicBoolean(false);
        
        disposables.add(networkManager.trustedPermanently().filter(certs -> Arrays.equals(x509Certificates, certs))
                .subscribe(c -> {
            logger.info("Certificates trusted permanently" + this);
            addCertificatesToPhysicalKeystore(Arrays.asList(x509Certificates));
            addCertificates(Arrays.asList(x509Certificates));
            disposables.dispose();
        }));
        disposables.add(networkManager.trustedTemporarily().filter(certs -> Arrays.equals(x509Certificates, certs)).subscribe(c -> {
            logger.info("Certificates trusted temporarily");
            addCertificates(Arrays.asList(x509Certificates));
            disposables.dispose();
        }));
        disposables.add(networkManager.untrusted().filter(certs -> Arrays.equals(x509Certificates, certs)).subscribe(c -> {
            logger.info("Certificates untrusted");
            distrust.set(true);
            disposables.dispose();
        }));
        
        networkManager.trustRequest().set(x509Certificates);
        logger.info("Waiting for user action");
        try {
            Observable.merge(
                    networkManager.untrusted().filter(certs -> Arrays.equals(x509Certificates, certs)),
                    networkManager.trustedPermanently().filter(certs -> Arrays.equals(x509Certificates, certs)),
                    networkManager.trustedTemporarily().filter(certs -> Arrays.equals(x509Certificates, certs)))
            .timeout(30, TimeUnit.SECONDS)
            .doOnError(e -> logger.error("blocking for certificate answer failed due to timeout", e))
            .blockingFirst();
        } catch (Exception te) {
            throw originalException;
        }
        logger.info("User did choose distrust :" + distrust.get());
        if (distrust.get()) {
            throw originalException;
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        int length = serverCertsTrustManager.getAcceptedIssuers().length
                + clientCertsTrustManager.getAcceptedIssuers().length;
        List<X509Certificate> certificates = new ArrayList<>(length);
        certificates.addAll(Arrays.asList(serverCertsTrustManager.getAcceptedIssuers()));
        certificates.addAll(Arrays.asList(clientCertsTrustManager.getAcceptedIssuers()));
        return certificates.toArray(new X509Certificate[0]);
    }

    private void reloadTrustManager() throws Exception {
        KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
        ts.load(null);

        for (Certificate cert : certList) {
            ts.setCertificateEntry(UUID.randomUUID().toString(), cert);
        }
        
        if (certList.isEmpty()) {
            clientCertsTrustManager = getTrustManager(null);
            serverCertsTrustManager = getTrustManager(null);
        } else {
            clientCertsTrustManager = getTrustManager(ts);
            serverCertsTrustManager = getTrustManager(ts);
        }
        
    }
    
    private List<X509Certificate> getDefaultCertificates() throws KeyStoreException {
        originalTrustManagerFactory.init((KeyStore) null);

        List<TrustManager> trustManagers = Arrays.asList(originalTrustManagerFactory.getTrustManagers());
        return trustManagers.stream()
          .filter(X509TrustManager.class::isInstance)
          .map(X509TrustManager.class::cast)
          .map(trustManager -> Arrays.asList(trustManager.getAcceptedIssuers()))
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
    }

    private X509ExtendedTrustManager getTrustManager(KeyStore ts) throws NoSuchAlgorithmException, KeyStoreException {
        originalTrustManagerFactory.init(ts);
        TrustManager tms[] = originalTrustManagerFactory.getTrustManagers();
        for (int i = 0; i < tms.length; i++) {
            if (tms[i] instanceof X509ExtendedTrustManager) {
                return (X509ExtendedTrustManager) tms[i];
            }
        }
        throw new NoSuchAlgorithmException("No X509TrustManager in TrustManagerFactory");
    }
}
