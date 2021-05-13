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
package com.oracle.javafx.scenebuilder.library.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.library.api.ExplorerInspector;

public class FolderExplorer {
    
    private final static Logger logger = LoggerFactory.getLogger(FolderExplorer.class);

    private FolderExplorer(Path folderPath) {}

    public static <T> List<T> explore(Path folderPath, ExplorerInspector<Path, T> entryInspector) throws IOException {
        assert folderPath != null;
        assert folderPath.isAbsolute();
        
        final List<T> result = new ArrayList<>();
        final Predicate<? super Path> folderFilter = p -> !p.toFile().isDirectory();
        
        try (Stream<Path> streamCount = Files.walk(folderPath).filter(folderFilter);
                Stream<Path> stream = Files.walk(folderPath).filter(folderFilter)) {
            
            long count = streamCount.count();
            AtomicLong index = new AtomicLong();
            
            stream.forEach(p -> {
                long current = index.incrementAndGet();
                double progress = (double)current / (double)count;
                try {
                    T explored = exploreEntry(p, progress, entryInspector);
                    if (explored != null) {
                        result.add(explored);
                    }
                } catch (ExplorationCancelledException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch(RuntimeException e) {
            if (!(e.getCause() instanceof ExplorationCancelledException)) {
                throw e;
            } else {
                logger.info("Exploration cancelled");
            }
        }

        return result;
    }
    
    /*
     * Private
     */

    private static <T> T exploreEntry(Path path, double progress, ExplorerInspector<Path, T> entryInspector) throws ExplorationCancelledException {
        return entryInspector.explore(path, progress);
    }
}
