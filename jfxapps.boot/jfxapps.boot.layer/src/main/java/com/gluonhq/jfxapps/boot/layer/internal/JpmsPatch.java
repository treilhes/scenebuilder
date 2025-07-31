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

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

public class JpmsPatch {

    private static Pattern DEPENDENCY_PATCH_PATTERN = Pattern.compile(".*=.*");

    public static Optional<JpmsPatch> tryGetPatchJpms(Path path) {
        try (ZipFile zf = new ZipFile(path.toFile())) {
            InputStream in = zf.getInputStream(zf.getEntry("patch.jpms"));
            String content = new String(in.readAllBytes()).trim();
            return parse(content);
        } catch (Exception e) {}

        return Optional.empty();
    }

    private static Optional<JpmsPatch> parse(String content) {
        //process content line by line
        JpmsPatch jpmsPatch = new JpmsPatch();
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue; //skip empty lines and comments
            }
            if (DEPENDENCY_PATCH_PATTERN.matcher(line).matches()) {
                // This line is a patch request
                // Example: "java.base=patch-file"

                String[] parts = line.split("=");
                jpmsPatch.patchRequests.put(parts[0].trim(), parts[1].trim());
                continue;
            }

            if (jpmsPatch.patchTarget != null) {
                throw new IllegalArgumentException("Multiple patch targets found: " + jpmsPatch.patchTarget + " and " + line);
            }
            jpmsPatch.patchTarget = line.trim();
            // process parts[0] as patch target and parts[1] as patch request
        }

        if (jpmsPatch.patchTarget != null || !jpmsPatch.patchRequests.isEmpty()) {
            return Optional.of(jpmsPatch);
        }

        return Optional.empty();
    }

    private String patchTarget;
    private Map<String, String> patchRequests = new HashMap<>();

    public String getPatchTarget() {
        return patchTarget;
    }

    public Map<String, String> getPatchRequests() {
        return patchRequests;
    }
}
