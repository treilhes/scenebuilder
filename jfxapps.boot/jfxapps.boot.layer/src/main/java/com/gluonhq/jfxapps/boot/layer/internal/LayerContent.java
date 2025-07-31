/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.layer.internal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LayerContent {

    private final Map<String, List<String>> patches = new HashMap<>();
    private final Set<Path> paths = new HashSet<>();
    private final Map<String, List<String>> patchRequests = new HashMap<>();

    public void addPatch(String module, String path) {
        patches.computeIfAbsent(module, k -> new ArrayList<>()).add(path);
    }

    public void addPatchRequest(String module, String filename) {
        patchRequests.computeIfAbsent(module, k -> new ArrayList<>()).add(filename);
    }

    public void addPath(Path path) {
        paths.add(path);
    }

    public Map<String, List<String>> getPatches() {
        return patches;
    }

    public Set<Path> getPaths() {
        return paths;
    }

    /**
     * Resolves the patch requests.
     * Patch requests are entries composed of a module name and a list of jar file names.
     * The method iterates through the patch requests, for each file name it searches for a matching path
     * in the provided paths. If a match is found, it adds a patch in patches.
     */
    public void resolvePatchRequests() {
        for (Map.Entry<String, List<String>> entry : patchRequests.entrySet()) {
            String module = entry.getKey();
            List<String> patchFiles = entry.getValue();
            if (patchFiles.isEmpty()) {
                continue; // No patch files for this module
            }

            for (String patchFile : patchFiles) {
                // Search for the patch file in the paths
                Path foundPath = null;
                for (Path path : paths) {
                    if (path.getFileName().toString().equals(patchFile)) {
                        // Found a matching path for the patch file
                        addPatch(module, path.toString());
                        foundPath = path;
                        break; // No need to search further for this patch file
                    }
                }

                if (foundPath != null) {
                    paths.remove(foundPath); // Remove the path if it was used for a patch
                }
            }
        }
    }
    @Override
    public String toString() {
        return "LayerContent [patches=" + patches + ", paths=" + paths + "]";
    }

}
