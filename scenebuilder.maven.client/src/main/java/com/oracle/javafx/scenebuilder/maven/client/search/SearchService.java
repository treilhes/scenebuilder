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

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;

import com.oracle.javafx.scenebuilder.maven.client.api.Repository;

public class SearchService {

    private final ExecutorService exec = Executors.newFixedThreadPool(5, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t ;
    });

    private Deque<DefaultArtifact> result = new ConcurrentLinkedDeque<>();
    private String query;
    private boolean searching;

    private final String userM2Repository;

    public SearchService(String userM2Repository) {
        this.userM2Repository = userM2Repository;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void cancelSearch() {
//        if (tasks != null) {
//            tasks.forEach(Task::cancel);
//        }
//        searching.set(false);
    }

    protected void search(List<Repository> repositories) {
        List<Future<List<DefaultArtifact>>> futures = repositories.stream()
            .map(r -> createSearchTask(r))
            .map(c -> exec.submit(c))
            .collect(Collectors.toList());

        futures.forEach(f -> {
            try {
                var list = f.get();
                result.addAll(getLatestVersions(
                        list.stream()
                            .distinct()
                            .collect(Collectors.groupingBy(a -> a.getGroupId() + ":" + a.getArtifactId()))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private Callable<List<DefaultArtifact>> createSearchTask(Repository repo) {
        return () -> repo.getType().getConstructor().newInstance().getCoordinates(repo, query);
    }

    private List<DefaultArtifact> getLatestVersions(Map<String, List<DefaultArtifact>> mapArtifacts) {
        List<DefaultArtifact> list = new ArrayList<>();
        mapArtifacts.forEach((s, l) -> {
            DefaultArtifact da = l.stream()
                    // TODO: Include snapshots
                    .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("snapshot"))
                    .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("javadoc"))
                    .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("source"))
                    .reduce((a1, a2) -> {
                        Version v1 = getVersion(a1.getVersion());
                        Version v2 = getVersion(a2.getVersion());
                        if (v1 != null && v2 != null && v1.compareTo(v2) > 0) {
                            return a1;
                        } else {
                            return a2;
                        }
                    })
                    .get();
            list.add(da);
        });
        return list;
    }

    // TODO: Return all versions, including snapshots
    private List<DefaultArtifact> getAllVersions(Map<String, List<DefaultArtifact>> mapArtifacts) {
        List<DefaultArtifact> list = new ArrayList<>();
        mapArtifacts.forEach((s, l) -> {
            l.stream()
                .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("javadoc"))
                .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("source"))
                .forEach(list::add);
        });
        return list;
    }

    private Version getVersion(String version) {
        Version v1 = null;
        try {
            v1 = new GenericVersionScheme().parseVersion(version);
        } catch (InvalidVersionSpecificationException ivse) { }
        return v1;
    }

}
