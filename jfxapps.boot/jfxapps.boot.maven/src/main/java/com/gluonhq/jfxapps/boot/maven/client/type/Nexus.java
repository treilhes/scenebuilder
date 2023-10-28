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
package com.gluonhq.jfxapps.boot.maven.client.type;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.maven.client.api.MavenArtifactId;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryType;

public class Nexus implements RepositoryType {

    private static final Logger logger = LoggerFactory.getLogger(Nexus.class);

    // nexus
    private static final String URL_PREFIX = "/service/local/data_index?q="; //NOCHECK
    private static final String URL_SUFFIX = "&from=";

    private boolean first;
    private int iteration;
    private int totalCount = 0;
    private static final int ITEMS_ITERATION = 200;
    private static final int MAX_RESULTS = 2000;

    public Nexus() {
        iteration = 0;
        first = true;
    }

    @Override
    public Set<MavenArtifactId> getCoordinates(Repository repository, String query) {

        String url = repository.getURL();
        String base = url.substring(0, url.indexOf("/content"));

        final HttpClient httpClient = newClientBuilder(repository).build();
        String s = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(base + URL_PREFIX + query + (first ? "" : URL_SUFFIX + iteration * ITEMS_ITERATION)))//NOCHECK
                    .setHeader("Accept", "application/json") // NOCHECK
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            s = new String(response.body().readAllBytes());
            StringReader sr = new StringReader(s);

            try (JsonReader rdr = Json.createReader(sr)) {
                JsonObject obj = rdr.readObject();
                if (first && obj != null && !obj.isEmpty() && obj.containsKey("totalCount")) { //NOCHECK
                    first = false;
                    totalCount = Math.min(obj.getInt("totalCount", 0), MAX_RESULTS); //NOCHECK
                    if (totalCount > ITEMS_ITERATION) {
                        Set<MavenArtifactId> coordinates = new HashSet<>(processRequest(obj));
                        while (totalCount > ITEMS_ITERATION) {
                            iteration += 1;
                            coordinates.addAll(getCoordinates(repository, query)
                                    .stream()
                                    .distinct()
                                    .collect(Collectors.toSet()));

                            totalCount -= ITEMS_ITERATION;
                        }
                        return coordinates;
                    }
                }
                return processRequest(obj);
            }
        } catch (Exception ex) {
            logger.error("error during search", ex);
        }
        return null;
    }

    private Set<MavenArtifactId> processRequest(JsonObject obj) {
        if (obj != null && !obj.isEmpty() && obj.containsKey("data")) { //NOCHECK
            JsonArray docResults = obj.getJsonArray("data"); //NOCHECK
            return docResults.getValuesAs(JsonObject.class)
                    .stream()
                    .map(this::toMavenArtifactId)
                    .filter(ma -> ma.getGroupId() != null)
                    .filter(ma -> ma.getArtifactId() != null)
                    .distinct()
                    .collect(Collectors.toSet());
        }
        return null;
    }

    private MavenArtifactId toMavenArtifactId(JsonObject doc) {
        String groupId = doc.getString("groupId", "");
        String artifactId = doc.getString("artifactId", "");

        if (groupId == null) {
            System.out.println();
        }
        if (artifactId == null) {
            System.out.println();
        }
        return MavenArtifactId.builder()
                .withGroupId(groupId)
                .withArtifactId(artifactId)
                .build();
    }

    @Override
    public boolean validate(Repository repository) {
        // TODO Auto-generated method stub
        return false;
    }
}
