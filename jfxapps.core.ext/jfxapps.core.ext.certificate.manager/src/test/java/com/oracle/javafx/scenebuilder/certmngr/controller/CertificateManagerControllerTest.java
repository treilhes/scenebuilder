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
package com.oracle.javafx.scenebuilder.certmngr.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.socket.PortFactory;

import com.gluonhq.jfxapps.core.api.subjects.NetworkManager;
import com.oracle.javafx.scenebuilder.certmngr.tls.ReloadableTrustManagerProvider;
import com.oracle.javafx.scenebuilder.certmngr.tls.ReloadableX509TrustManager;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;


@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {8887})
public class CertificateManagerControllerTest {

    private static Logger logger = LoggerFactory.getLogger(CertificateManagerControllerTest.class);

    @TempDir
    static Path sharedTempDir;

    @TempDir
    static Path keystoreTempDir;

    static String storePassword = "password";

    private static NetworkManager nm = new NetworkManager.NetworkManagerImpl();
    private static ClientAndServer mockServer;
    private static String TEST_URL = "https://localhost:8887/test";
    protected final static long USER_TIMEOUT = 2;//seconds

    @BeforeAll
    public static void startMockServer() {

        ConfigurationProperties.dynamicallyCreateCertificateAuthorityCertificate(true);
        ConfigurationProperties.directoryToSaveDynamicSSLCertificate(sharedTempDir.toString());

        // ensure all connection using HTTPS will use the SSL context defined by
        // MockServer to allow dynamically generated certificates to be accepted
        //HttpsURLConnection.setDefaultSSLSocketFactory(new KeyStoreFactory(new MockServerLogger()).sslContext().getSocketFactory());
        mockServer = ClientAndServer.startClientAndServer(PortFactory.findFreePort());
        mockServer
            //.secure(true)
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/test")
            )
            .respond(
                response()
                    .withBody("some_response_body")
            );
    }

    @AfterAll
    public static void stopMockServer() {
        mockServer.stop();
    }

    private static HttpClient newClient() {
        return HttpClientBuilder.create().setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
    }

    private static int keystoreCertificateCount(File store) {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            if (store.exists()) {
                logger.info(() -> String.format("Counting certificates in store : %s", store.getAbsolutePath()));
                try (FileInputStream fis = new FileInputStream(store)){
                    ks.load(fis, storePassword.toCharArray());
                }
                return ks.size();
            }
        } catch (Exception e) {
            logger.error(e, () -> "Error while loading the store");
        }
        return 0;
    }

    private static File newTrustManager(TestInfo testInfo) {
        ReloadableX509TrustManager.reset();

        File truststore = new File(keystoreTempDir.toFile(), testInfo.getDisplayName() + ".jks");
        Provider p = new ReloadableTrustManagerProvider(nm, truststore, storePassword.toCharArray(), USER_TIMEOUT);
        Security.removeProvider(p.getName());
        Security.insertProviderAt(p, 1);
        return truststore;
    }

    @Test
    void shouldThrowSslValidationException(TestInfo testInfo) {
        newTrustManager(testInfo);

        HttpGet request = new HttpGet(TEST_URL);

        // the handshake has failed
        Assertions.assertThrows(SSLHandshakeException.class, () -> {
            newClient().execute(request);
        });
    }

    @Test
    void shouldThrowSslValidationExceptionAndNotSaveCertificateInStore(TestInfo testInfo) throws Exception {
        File store = newTrustManager(testInfo);
        TestObserver<?> trustRequestObserver = nm.trustRequest().test();
        AtomicInteger requestedCertificatesCount = new AtomicInteger(-1);
        Disposable disposable = nm.trustRequest()
            .observeOn(Schedulers.io())
            .subscribe(tr -> {
                requestedCertificatesCount.set(tr.length);
                nm.untrusted().set(tr);
            });

        HttpGet request = new HttpGet(TEST_URL);

        Assertions.assertThrows(SSLHandshakeException.class, () -> {
            newClient().execute(request);
        });

        trustRequestObserver.assertValueCount(1);
        disposable.dispose();

        assertTrue(store.exists());
        assertEquals(0, keystoreCertificateCount(store));
        assertTrue(disposable.isDisposed());
    }

    @Test
    void shouldSuccessAndSaveCertificateInStore(TestInfo testInfo) throws Exception {
        File store = newTrustManager(testInfo);
        TestObserver<?> trustRequestObserver = nm.trustRequest().test();
        AtomicInteger requestedCertificatesCount = new AtomicInteger(-1);
        Disposable disposable = nm.trustRequest()
            .observeOn(Schedulers.io())
            .subscribe(tr -> {
                requestedCertificatesCount.set(tr.length);
                nm.trustedPermanently().set(tr);
            });

        HttpGet request = new HttpGet(TEST_URL);

        Assertions.assertDoesNotThrow(() -> {
            newClient().execute(request);
        });

        trustRequestObserver.assertValueCount(1);
        disposable.dispose();

        assertTrue(store.exists());
        assertEquals(requestedCertificatesCount.get(), keystoreCertificateCount(store));
        assertTrue(disposable.isDisposed());
    }

    @Test
    void shouldSuccessAndNotSaveCertificateInStore(TestInfo testInfo) throws Exception {
        File store = newTrustManager(testInfo);
        TestObserver<?> trustRequestObserver = nm.trustRequest().test();

        AtomicInteger requestedCertificatesCount = new AtomicInteger(-1);
        Disposable disposable = nm.trustRequest()
            .observeOn(Schedulers.io())
            .subscribe(tr -> {
                requestedCertificatesCount.set(tr.length);
                nm.trustedTemporarily().set(tr);
            });

        HttpGet request = new HttpGet(TEST_URL);

        // the handshake was successfull
        Assertions.assertDoesNotThrow(() -> {
            newClient().execute(request);
        });

        // a trust request has been sent
        trustRequestObserver.assertValueCount(1);
        disposable.dispose();
        // the store was created
        assertTrue(store.exists());
        // the trust request contained certificates
        assertTrue(requestedCertificatesCount.get() > 0);
        // the truststore is empty
        assertEquals(0, keystoreCertificateCount(store));
        assertTrue(disposable.isDisposed());
    }


    @Test
    void shouldThrowSslValidationExceptionThenSuccessAndSaveCertificateInStore(TestInfo testInfo) throws Exception {
        File store = newTrustManager(testInfo);
        TestObserver<?> trustRequestObserver = nm.trustRequest().test();
        AtomicInteger requestedCertificatesCount = new AtomicInteger(-1);

        Disposable disposable = nm.trustRequest()
            .observeOn(Schedulers.io())
            .subscribe(tr -> {
                nm.untrusted().set(tr);
            });

        HttpGet request = new HttpGet(TEST_URL);

        Assertions.assertThrows(SSLHandshakeException.class, () -> {
            newClient().execute(request);
        });
        disposable.dispose();

        disposable = nm.trustRequest()
            .observeOn(Schedulers.io())
            .subscribe(tr -> {
                requestedCertificatesCount.set(tr.length);
                nm.trustedPermanently().set(tr);
            });

        Assertions.assertDoesNotThrow(() -> {
            newClient().execute(request);
        });



        trustRequestObserver.assertValueCount(2);
        disposable.dispose();

        assertTrue(store.exists());
        assertEquals(requestedCertificatesCount.get(), keystoreCertificateCount(store));
        assertTrue(disposable.isDisposed());
    }
}
