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
package com.gluonhq.jfxapps.boot.maven.client.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.maven.Artifact;
import com.gluonhq.jfxapps.boot.api.maven.Repository;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryType;

public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    public SearchService() {}

    public void cancelSearch() {
//        if (tasks != null) {
//            tasks.forEach(Task::cancel);
//        }
//        searching.set(false);
    }

    public Set<Artifact> search(String query, List<Repository> repositories) {

        logger.info("Searching '{}' on {} repositories [{}]", query, repositories.size(), repositories);

        final ExecutorService exec = Executors.newFixedThreadPool(5, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t ;
        });

        Set<Artifact> result = new ConcurrentSkipListSet<>();

        Set<Future<Set<Artifact>>> futures = repositories.stream()
            .map(r -> createSearchTask(query, r))
            .map(c -> exec.submit(c))
            .collect(Collectors.toSet());

        futures.forEach(f -> {
            try {
                var list = f.get();
                result.addAll(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        exec.shutdownNow();

        return result;
    }

    private Callable<Set<Artifact>> createSearchTask(String query, Repository repo) {
        return () -> {
            logger.info("Searching on repository {} with type {}", repo.getName(), repo.getType().getSimpleName());

            RepositoryType search = repo.getType().getConstructor().newInstance();
            Set<Artifact> result = search.getCoordinates(repo, query);

            logger.info("Search on repository {} with type {} returned {} items", repo.getName(), repo.getType().getSimpleName(), result.size());
            return result;
        };
    }


}
