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
package com.oracle.javafx.scenebuilder.maven.client.search;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.eclipse.aether.artifact.DefaultArtifact;

import com.oracle.javafx.scenebuilder.maven.client.api.Repository;
import com.oracle.javafx.scenebuilder.maven.client.api.RepositoryType;

public class NexusSearch implements RepositoryType {

    // nexus
    private static final String URL_PREFIX = "/service/local/data_index?q="; //NOCHECK
    private static final String URL_SUFFIX = "&from=";

    private static boolean first;
    private static int iteration;
    private int totalCount = 0;
    private static final int ITEMS_ITERATION = 200;
    private static final int MAX_RESULTS = 2000;

    public NexusSearch() {
        iteration = 0;
        first = true;
    }

    @Override
    public List<DefaultArtifact> getCoordinates(Repository repository, String query) {

        String name = repository.getId();
        String url = repository.getURL();

        final HttpClient httpClient = newClientBuilder(repository).build();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url + URL_PREFIX + query + (first ? "" : URL_SUFFIX + iteration * ITEMS_ITERATION)))//NOCHECK
                    .setHeader("Accept", "application/json") // NOCHECK
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            try (JsonReader rdr = Json.createReader(response.body())) {
                JsonObject obj = rdr.readObject();
                if (first && obj != null && !obj.isEmpty() && obj.containsKey("totalCount")) { //NOCHECK
                    first = false;
                    totalCount = Math.min(obj.getInt("totalCount", 0), MAX_RESULTS); //NOCHECK
                    if (totalCount > ITEMS_ITERATION) {
                        List<DefaultArtifact> coordinates = new ArrayList<>(processRequest(obj, name));
                        while (totalCount > ITEMS_ITERATION) {
                            iteration += 1;
                            coordinates.addAll(getCoordinates(repository, query)
                                    .stream()
                                    .filter(ga -> coordinates.stream()
                                            .noneMatch(ar -> ar.getGroupId().equals(ga.getGroupId()) &&
                                                             ar.getArtifactId().equals(ga.getArtifactId())))
                                    .collect(Collectors.toList()));

                            totalCount -= ITEMS_ITERATION;
                        }
                        return coordinates;
                    }
                }
                return processRequest(obj, name);
            }
        } catch (Exception ex) {
            Logger.getLogger(NexusSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<DefaultArtifact> processRequest(JsonObject obj, String name) {
        if (obj != null && !obj.isEmpty() && obj.containsKey("data")) { //NOCHECK
            JsonArray docResults = obj.getJsonArray("data"); //NOCHECK
            return docResults.getValuesAs(JsonObject.class)
                    .stream()
                    .map(doc -> {
                        final Map<String, String> map = new HashMap<>();
                        map.put("Repository", name + " (" + doc.getString("repoId", "") + ")"); //NOCHECK
                        return new DefaultArtifact(doc.getString("groupId", "") + ":" + //NOCHECK
                                doc.getString("artifactId", "") + ":" + MIN_VERSION, map); //NOCHECK
                    })
                    .distinct()
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean validate(Repository repository) {
        // TODO Auto-generated method stub
        return false;
    }
}
