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
package com.gluonhq.jfxapps.boot.platform;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public interface InternalRestClient {

    public static final String DEFAULT_PATH = "boot";

    public static final UUID BOOT_CONTEXT = null;

    public static String queryOf(String path, Map<String, String> parameters) {

        String params = parameters.entrySet().stream()
                .map(p -> URLEncoder.encode(p.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(p.getValue(), StandardCharsets.UTF_8))
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");

        if (params.isEmpty()) {
            return path;
        }
        if (path.contains("?")) {
            if (!path.endsWith("?")) {
                return path + "&" + params;
            } else {
                return path + params;
            }
        } else {
            return path + "?" + params;
        }

    }

    HttpResponse<String> get(UUID uuid, String path) throws URISyntaxException, IOException, InterruptedException;

    <RESP> HttpResponse<RESP> get(UUID uuid, String path, BodyHandler<RESP> handler)
            throws URISyntaxException, IOException, InterruptedException;

    <RESP> HttpResponse<RESP> get(UUID uuid, String path, Class<RESP> result)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> get(UUID uuid, String path, RequestConfig customization, Class<RESP> resultClass)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> get(UUID uuid, String path, RequestConfig customization, BodyHandler<RESP> handler)
            throws URISyntaxException, IOException, InterruptedException;

    HttpResponse<String> post(UUID uuid, String path) throws URISyntaxException, IOException, InterruptedException;

    <RESP> HttpResponse<RESP> post(UUID uuid, String path, BodyHandler<RESP> handler)
            throws URISyntaxException, IOException, InterruptedException;

    <RESP> HttpResponse<RESP> post(UUID uuid, String path, Class<RESP> result)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> post(UUID uuid, String path, DATA posted, Class<RESP> result)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA> HttpResponse<String> post(UUID uuid, String path, DATA posted)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> post(UUID uuid, String path, DATA posted, BodyHandler<RESP> handler)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA> HttpResponse<String> post(UUID uuid, String path, RequestConfig customization, DATA posted)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> post(UUID uuid, String path, RequestConfig customization, DATA posted,
            Class<RESP> resultClass) throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> post(UUID uuid, String path, RequestConfig customization, DATA posted,
            BodyHandler<RESP> handler) throws URISyntaxException, IOException, InterruptedException;

    HttpResponse<String> put(UUID uuid, String path) throws URISyntaxException, IOException, InterruptedException;

    <RESP> HttpResponse<RESP> put(UUID uuid, String path, BodyHandler<RESP> handler)
            throws URISyntaxException, IOException, InterruptedException;

    <RESP> HttpResponse<RESP> put(UUID uuid, String path, Class<RESP> result)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> put(UUID uuid, String path, DATA posted, Class<RESP> result)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA> HttpResponse<String> put(UUID uuid, String path, DATA posted)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> put(UUID uuid, String path, DATA posted, BodyHandler<RESP> handler)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA> HttpResponse<String> put(UUID uuid, String path, RequestConfig customization, DATA posted)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> put(UUID uuid, String path, RequestConfig customization, DATA posted,
            Class<RESP> resultClass) throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> put(UUID uuid, String path, RequestConfig customization, DATA posted,
            BodyHandler<RESP> handler) throws URISyntaxException, IOException, InterruptedException;

    HttpResponse<String> delete(UUID uuid, String path) throws URISyntaxException, IOException, InterruptedException;

    <RESP> HttpResponse<RESP> delete(UUID uuid, String path, BodyHandler<RESP> handler)
            throws URISyntaxException, IOException, InterruptedException;

    <RESP> HttpResponse<RESP> delete(UUID uuid, String path, Class<RESP> result)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> delete(UUID uuid, String path, RequestConfig customization, Class<RESP> resultClass)
            throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> delete(UUID uuid, String path, RequestConfig customization,
            BodyHandler<RESP> handler) throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> request(UUID uuid, String path, RequestConfig customization,
            Class<RESP> resultClass) throws URISyntaxException, IOException, InterruptedException;

    <DATA, RESP> HttpResponse<RESP> request(UUID uuid, String path, RequestConfig customization,
            BodyHandler<RESP> handler) throws URISyntaxException, IOException, InterruptedException;

    public static class JsonResponseException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private final ResponseInfo response;
        private final String body;

        public JsonResponseException(ResponseInfo response, String body, Throwable cause) {
            super(cause);
            this.response = response;
            this.body = body;
        }

        public ResponseInfo getResponse() {
            return response;
        }

        public String getBody() {
            return body;
        }

    }

    public interface RequestConfig extends Function<HttpRequest.Builder, HttpRequest.Builder> {
        default RequestConfig andThen(RequestConfig after) {
            Objects.requireNonNull(after);
            return (t) -> after.apply(apply(t));
        }
    }

    public class RequestException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private final HttpResponse<String> response;

        public RequestException(HttpResponse<String> response, Throwable cause) {
            super(cause);
            this.response = response;
        }

        public HttpResponse<String> getResponse() {
            return response;
        }

    }
}