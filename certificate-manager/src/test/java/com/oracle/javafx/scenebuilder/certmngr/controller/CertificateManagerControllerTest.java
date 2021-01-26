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

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.security.Security;
import java.security.cert.Certificate;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.socket.PortFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.subjects.NetworkManager;
import com.oracle.javafx.scenebuilder.certmngr.tls.ReloadableTrustManagerProvider;

import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;


@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {8887})
public class CertificateManagerControllerTest {
    
    private static Logger logger = LoggerFactory.getLogger(CertificateManagerControllerTest.class);
    
    @TempDir
    static Path sharedTempDir;
    
    private static NetworkManager nm = new NetworkManager.NetworkManagerImpl();
    private static ClientAndServer mockServer;
    private static String TEST_URL = "https://localhost:8887/test";
    
    static {
        Security.insertProviderAt(new ReloadableTrustManagerProvider(nm), 1);
    }
    
    Api api = Mockito.mock(Api.class);
    FileSystem fs = Mockito.mock(FileSystem.class);
    
    
    private void tmp() {
        
       
    }
    
    
    @BeforeAll
    public static void startMockServer() {
        ConfigurationProperties.dynamicallyCreateCertificateAuthorityCertificate(true);
        ConfigurationProperties.directoryToSaveDynamicSSLCertificate(sharedTempDir.toString());
        
        // ensure all connection using HTTPS will use the SSL context defined by
        // MockServer to allow dynamically generated certificates to be accepted
        //HttpsURLConnection.setDefaultSSLSocketFactory(new KeyStoreFactory(new MockServerLogger()).sslContext().getSocketFactory());
        mockServer = ClientAndServer.startClientAndServer(PortFactory.findFreePort());
        mockServer
            //.withSecure(true)
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
    
    @Test
    void shouldThrowSslValidationEception() {
        HttpGet request = new HttpGet(TEST_URL);
        CloseableHttpClient client = HttpClients.createDefault();
        
        Assertions.assertThrows(SSLHandshakeException.class, () -> {
            client.execute(request);
        });
    }
    
    @Test
    void shouldLoadCertificate() throws Exception {
        Mockito.when(fs.getApplicationDataFolder()).thenReturn(sharedTempDir.toFile());
        Mockito.when(api.getFileSystem()).thenReturn(fs);
        Mockito.when(api.getNetworkManager()).thenReturn(nm);
        URL url = new URL(TEST_URL);
        TestObserver<?> to = nm.trustRequest().test();
        nm.trustRequest()
        //.subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(tr -> {
        //nm.trustRequest().subscribe(tr -> {
            LoggerFactory.getLogger(CertificateManagerControllerTest.class).info(Thread.currentThread().getName());
            LoggerFactory.getLogger(CertificateManagerControllerTest.class).info("RECEIVEDDDDDDD RRRRRREEEEEQQQQUUUUEEESSTSTT " + tr);
           try {
               Thread.sleep(5000) ;
            }  catch (InterruptedException e) {
                // gestion de l'erreur
            }
           LoggerFactory.getLogger(CertificateManagerControllerTest.class).info("AFTER RECEIVEDDDDDDD RRRRRREEEEEQQQQUUUUEEESSTSTT " + tr);
           nm.trustedPermanently().set(tr);
        });
        
        nm.trustedPermanently().subscribe(c -> {
            LoggerFactory.getLogger(CertificateManagerControllerTest.class).info("SETVAL");
        });
        
        CertificateManagerController cmc = new CertificateManagerController(api);
        Certificate certificate = cmc.loadFromUrl(new URL(TEST_URL));
        LoggerFactory.getLogger(CertificateManagerControllerTest.class).info("XXXXXXXXXXXXXXXXXXXXX");
        
        
        to.assertValueCount(1);
        //to.assertComplete();
        LoggerFactory.getLogger(CertificateManagerControllerTest.class).info("END");
    }
    
	@Test
	void shouldCreateValue(TestInfo testInfo) throws Exception {
	    System.out.println("XXXXXXXXXXXx");
	    HttpGet request = new HttpGet(TEST_URL);
	    CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(request);
        InputStream dataStream = response.getEntity().getContent();
        byte[] bytes = new byte[100];
        IOUtils.readFully(dataStream, bytes);
        System.out.println(new String(bytes));
	}
}
