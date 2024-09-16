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
package com.gluonhq.jfxapps.boot.platform.internal;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gluonhq.jfxapps.boot.api.platform.InternalRestClient;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.boot.api.platform.ResponseBuilder;

@Component
public class InternalRestClientImpl implements InternalRestClient {

    private static final Logger logger = LoggerFactory.getLogger(InternalRestClientImpl.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    private final ServerProperties serverProperties;
    private final String basePath;

    protected InternalRestClientImpl(ServerProperties serverProperties,
            @Value(InternalRestClient.SERVLET_PATH_PROP) String servletPath,
            @Value(InternalRestClient.CONTEXT_PATH_PROP) String contextPath) {
        this.serverProperties = serverProperties;
        this.basePath = ((StringUtils.hasText(contextPath) ? "/" + contextPath : "")
                + (StringUtils.hasText(servletPath) ? "/" + servletPath : "")).replaceAll("/+", "/");
    }

    private String createUri(UUID uuid, String path) {
        int serverPort = serverProperties.getPort();
        return String.format("http://localhost:%s%s/%s/%s", serverPort, basePath,
                uuid == null ? DEFAULT_PATH : JfxAppsPlatform.EXTENSION_REST_PATH_PREFIX + "/" + uuid.toString(), path);
    }

    private HttpRequest.Builder newRequest(URI uri) {
        return HttpRequest.newBuilder().uri(uri);
    }

    private HttpClient.Builder newClient() {
        return HttpClient.newBuilder().authenticator(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("username", "password".toCharArray());
            }
        }).proxy(ProxySelector.getDefault());
    }

    @Override
    public ResponseBuilder get(UUID uuid, String path) throws URISyntaxException, IOException, InterruptedException {
        return request(uuid, path, null);
    }

    @Override
    public ResponseBuilder get(UUID uuid, String path, RequestConfig customization)
            throws URISyntaxException, IOException, InterruptedException {

        RequestConfig finalCustomization = getCustomization(customization);

        return request(uuid, path, finalCustomization);
    }

    private RequestConfig getCustomization(RequestConfig customization) throws JsonProcessingException {
        RequestConfig localCustomization = customization == null ? r -> r : customization;
        RequestConfig getCustomization = r -> r.GET();
        RequestConfig finalCustomization = localCustomization.andThen(getCustomization);
        return finalCustomization;
    }

    @Override
    public ResponseBuilder post(UUID uuid, String path) throws URISyntaxException, IOException, InterruptedException {
        return post(uuid, path, null, null);
    }

    @Override
    public ResponseBuilder post(UUID uuid, String path, Object posted)
            throws URISyntaxException, IOException, InterruptedException {
        return post(uuid, path, null, posted);
    }

    @Override
    public ResponseBuilder post(UUID uuid, String path, RequestConfig customization, Object posted)
            throws URISyntaxException, IOException, InterruptedException {

        RequestConfig finalCustomization = postCustomization(customization, posted);

        return request(uuid, path, finalCustomization);
    }

    private RequestConfig postCustomization(RequestConfig customization, Object posted) throws JsonProcessingException {
        RequestConfig localCustomization = customization == null ? r -> r : customization;

        RequestConfig postCustomization = switch (posted) {

        case null -> (r) -> r.POST(HttpRequest.BodyPublishers.noBody());

        case String s -> r -> r.POST(HttpRequest.BodyPublishers.ofString(s));

        default -> {
            String json = mapper.writeValueAsString(posted);
            yield r -> r.POST(HttpRequest.BodyPublishers.ofString(json));
        }
        };
        RequestConfig finalCustomization = localCustomization.andThen(postCustomization);
        return finalCustomization;
    }

    @Override
    public ResponseBuilder put(UUID uuid, String path) throws URISyntaxException, IOException, InterruptedException {
        return put(uuid, path, null, null);
    }

    @Override
    public ResponseBuilder put(UUID uuid, String path, Object posted)
            throws URISyntaxException, IOException, InterruptedException {
        return put(uuid, path, null, posted);
    }

    @Override
    public ResponseBuilder put(UUID uuid, String path, RequestConfig customization, Object posted)
            throws URISyntaxException, IOException, InterruptedException {

        RequestConfig finalCustomization = putCustomization(customization, posted);

        return request(uuid, path, finalCustomization);
    }

    private RequestConfig putCustomization(RequestConfig customization, Object posted) throws JsonProcessingException {
        RequestConfig localCustomization = customization == null ? r -> r : customization;

        RequestConfig putCustomization = switch (posted) {

        case null -> (r) -> r.PUT(HttpRequest.BodyPublishers.noBody());

        case String s -> r -> r.PUT(HttpRequest.BodyPublishers.ofString(s));

        default -> {
            String json = mapper.writeValueAsString(posted);
            yield r -> r.PUT(HttpRequest.BodyPublishers.ofString(json));
        }
        };

        RequestConfig finalCustomization = localCustomization.andThen(putCustomization);
        return finalCustomization;
    }

    @Override
    public ResponseBuilder delete(UUID uuid, String path) throws URISyntaxException, IOException, InterruptedException {
        return delete(uuid, path, null);
    }

    @Override
    public ResponseBuilder delete(UUID uuid, String path, RequestConfig customization)
            throws URISyntaxException, IOException, InterruptedException {

        RequestConfig finalCustomization = deleteCustomization(customization);

        return request(uuid, path, finalCustomization);
    }

    private RequestConfig deleteCustomization(RequestConfig customization) throws JsonProcessingException {
        RequestConfig localCustomization = customization == null ? r -> r : customization;
        RequestConfig getCustomization = r -> r.DELETE();
        RequestConfig finalCustomization = localCustomization.andThen(getCustomization);
        return finalCustomization;
    }

//    @Override
//    public <DATA, RESP> HttpResponse<RESP> request(UUID uuid, String path, RequestConfig customization,
//            Class<RESP> resultClass) throws URISyntaxException, IOException, InterruptedException {
//
//        @SuppressWarnings("unchecked")
//        BodyHandler<RESP> handler = switch (resultClass.getName()) {
//        case null -> null;
//        case "java.lang.String" -> (BodyHandler<RESP>) BodyHandlers.ofString();
//        default -> createJsonBodyHandler(resultClass);
//        };
//
//        return request(uuid, path, customization, handler);
//
//    }

    @Override
    public ResponseBuilder request(UUID uuid, String path, RequestConfig customization)
            throws URISyntaxException, IOException, InterruptedException {

        String uri = createUri(uuid, path);

        logger.info("Requesting {} with customization {}", uri, customization);

        customization = customization == null ? r -> r : customization;

        HttpRequest request = customization.apply(newRequest(new URI(uri))).build();

        return new ExpectedResponseBuilder(request);
    }

    /*
     * Describe the different and ordered expected response. Builder class to create
     * the list of expected response. Initialized by two possible constructors
     * taking the status code and the expected class or only the expected class (the
     * status code is by default 200 in this case) Then the method 'or' to add a new
     * expected response taking the status code and the expected class or only the
     * expected class (the status code is by default 200 in this case) Then the
     * method else to add a "catch all" expected response for any status code and
     * expecting a String.class response content
     */
    public class ExpectedResponseBuilder implements ResponseBuilder {

        private static class Expected<T> {
            private int status;
            private BodyHandler<T> expected;
            private ThrowableConsumer<HttpResponse<T>> consumer;

            private Expected(int status, BodyHandler<T> expected, ThrowableConsumer<HttpResponse<T>> consumer) {
                this.status = status;
                this.expected = expected;
                this.consumer = consumer;
            }
        }

        private static class ResponseContext {
            private int status;
            private boolean matched;
            private Expected expect;
            private Exception exception;

        }

        private final HttpRequest request;
        // private final List<Expected<?>> expecteds;
        private final Map<Integer, Expected<?>> expectedsx;
        private ThrowableConsumer<HttpResponse<String>> noneMatch;
        private Consumer<Throwable> onException;

        protected ExpectedResponseBuilder(HttpRequest request) {
            this.expectedsx = new HashMap<>();
            this.request = request;
        }

        @Override
        public <T> ResponseBuilder on(int status, BodyHandler<T> expected, ThrowableConsumer<HttpResponse<T>> consumer) {
            expectedsx.put(status, new Expected(status, expected, consumer));
            return this;
        }

        @Override
        public <T> ResponseBuilder on(int status, ThrowableConsumer<HttpResponse<String>> consumer) {
            expectedsx.put(status, new Expected(status, null, consumer));
            return this;
        }

        @Override
        public <T> ResponseBuilder ifNoneMatch(BodyHandler<T> expected, ThrowableConsumer<HttpResponse<T>> consumer) {
            expectedsx.put(0, new Expected(0, expected, consumer));
            return this;
        }

        @Override
        public <T> ResponseBuilder ifNoneMatch(ThrowableConsumer<HttpResponse<String>> consumer) {
            noneMatch = consumer;
            return this;
        }

        @Override
        public <T> ResponseBuilder onThrow(Consumer<Throwable> consumer) {
            onException = consumer;
            return this;
        }

        @Override
        public <T> T execute() {
            ResponseContext context = new ResponseContext();

            try {
                HttpResponse<?> response = newClient().build().send(request, respInfo -> {
                    int status = respInfo.statusCode();
                    context.status = status;

                    Expected<?> handler = expectedsx.get(status);

                    if (handler == null) {
                        handler = expectedsx.get(0);
                    }

                    if (handler != null) {

                        context.matched = true;
                        context.expect = handler;

                        if (handler.expected == null) {
                            return BodyHandlers.ofString().apply(respInfo);
                        }

                        return context.expect.expected.apply(respInfo);
                    }
                    return BodyHandlers.ofString().apply(respInfo);
                });

                if (context.matched) {
                    context.expect.consumer.accept(response);
                } else if (noneMatch != null) {
                    noneMatch.accept((HttpResponse<String>) response);
                }

                return (T)response.body();
            } catch (Exception e) {
                if (onException != null) {
                    onException.accept(e);
                    return null;
                } else {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }
}