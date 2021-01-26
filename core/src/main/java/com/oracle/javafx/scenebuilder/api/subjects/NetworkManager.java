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
package com.oracle.javafx.scenebuilder.api.subjects;

import java.net.Proxy;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.util.SubjectManager;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;

public interface NetworkManager {
    
    SubjectItem<Proxy> proxy();
    SubjectItem<X509Certificate[]> trustRequest();
    SubjectItem<X509Certificate[]> trustedTemporarily();
    SubjectItem<X509Certificate[]> trustedPermanently();
    SubjectItem<X509Certificate[]> untrusted();

    @Component
    public class NetworkManagerImpl implements InitializingBean, NetworkManager {

        private NetworkSubjects subjects;

        private final SubjectItem<Proxy> proxy;
        private final SubjectItem<X509Certificate[]> trustRequest;
        private final SubjectItem<X509Certificate[]> untrusted;
        private final SubjectItem<X509Certificate[]> trustedTemporarily;
        private final SubjectItem<X509Certificate[]> trustedPermanently;

        public NetworkManagerImpl() {
            subjects = new NetworkSubjects();
            proxy = new SubjectItem<Proxy>(subjects.getProxy());
            trustRequest = new SubjectItem<X509Certificate[]>(subjects.getTrustRequest());
            trustedTemporarily = new SubjectItem<X509Certificate[]>(subjects.getTrustedTemporarily());
            trustedPermanently = new SubjectItem<X509Certificate[]>(subjects.getTrustedPermanently());
            untrusted = new SubjectItem<X509Certificate[]>(subjects.getUntrusted());
        }

        @Override
        public void afterPropertiesSet() throws Exception {
        }

        @Override
        public SubjectItem<Proxy> proxy() {
            return proxy;
        }

        @Override
        public SubjectItem<X509Certificate[]> trustRequest() {
            return trustRequest;
        }

        @Override
        public SubjectItem<X509Certificate[]> trustedTemporarily() {
            return trustedTemporarily;
        }

        @Override
        public SubjectItem<X509Certificate[]> trustedPermanently() {
            return trustedPermanently;
        }
        
        @Override
        public SubjectItem<X509Certificate[]> untrusted() {
            return untrusted;
        }
    }

    public class NetworkSubjects extends SubjectManager {

        private @Getter ReplaySubject<Proxy> proxy;
        private @Getter Subject<X509Certificate[]> trustRequest;
        private @Getter Subject<X509Certificate[]> trustedTemporarily;
        private @Getter Subject<X509Certificate[]> trustedPermanently;
        private @Getter Subject<X509Certificate[]> untrusted;

        public NetworkSubjects() {
            proxy = wrap(NetworkSubjects.class, "proxy", ReplaySubject.create(1));
            trustRequest = wrap(NetworkSubjects.class, "trustRequest", PublishSubject.create());
            trustedTemporarily = wrap(NetworkSubjects.class, "trustedTemporarily", PublishSubject.create());
            trustedPermanently = wrap(NetworkSubjects.class, "trustedPermanently", PublishSubject.create());
            untrusted = wrap(NetworkSubjects.class, "untrusted", PublishSubject.create());
        }

    }
}
