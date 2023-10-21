/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.maven.client.type;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.oracle.javafx.scenebuilder.maven.client.api.MavenArtifactId;
import com.oracle.javafx.scenebuilder.maven.client.api.Repository;
import com.oracle.javafx.scenebuilder.maven.client.api.RepositoryType;

public class Maven implements RepositoryType {

    // maven
    private static final String URL_PREFIX = "https://search.maven.org/solrsearch/select?q=";
    private static final String URL_SUFFIX = "&rows=200&wt=json";

    public Maven() {
    }

    @Override
    public Set<MavenArtifactId> getCoordinates(Repository repository, String query) {
        final HttpClient httpClient = newClientBuilder(repository).build();

        String searchUrl = toApiUrl(repository.getURL());

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(searchUrl + query + URL_SUFFIX))
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            try (JsonReader rdr = Json.createReader(response.body())) {
                JsonObject obj = rdr.readObject();
                if (obj != null && !obj.isEmpty() && obj.containsKey("response")) {
                    JsonObject jsonResponse = obj.getJsonObject("response");
                    if (jsonResponse != null && !jsonResponse.isEmpty() && jsonResponse.containsKey("docs")) {
                        JsonArray docResults = jsonResponse.getJsonArray("docs");
                        return docResults.getValuesAs(JsonObject.class)
                                .stream()
                                .map(doc -> doc.getString("id", ""))
                                .distinct()
                                .map(gav -> gav.split(":"))
                                .map(gav -> MavenArtifactId.builder().withGroupId(gav[0]).withArtifactId(gav[1]).build())
                                .collect(Collectors.toSet());
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Maven.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static String toApiUrl(String repositoryUrl) {
        try {
            URL url = new URL(repositoryUrl);
            String host = url.getHost();
            String newHost = host;
            if (newHost.indexOf(".") != host.lastIndexOf(".")) { // the host is a composed domain a.b.c so use api for 1rst part api.b.c
                newHost = "search." + newHost.substring(host.indexOf("."));
            } else { // add api at the start a.b  to api.a.b
                newHost = "search." + newHost;
            }
            String newUrl = url.toString().replace(host, newHost);
            return String.format(URL_PREFIX, newUrl);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean validate(Repository repository) {
        // TODO Auto-generated method stub
        return false;
    }
}
