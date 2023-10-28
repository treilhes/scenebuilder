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
package com.oracle.javafx.scenebuilder.library.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.maven.client.api.MavenArtifact;
import com.oracle.javafx.scenebuilder.api.preferences.ListPreferences;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

@Component
public class MavenArtifactsPreferences extends ListPreferences<MavenArtifactPreferences, MavenArtifact> {

	// NODE
	protected static final String NODE_NAME = "ARTIFACTS"; //NOCHECK

	@Autowired
    public MavenArtifactsPreferences(PreferencesContext preferencesContext) {
    	super(preferencesContext, NODE_NAME, MavenArtifactPreferences.keyProvider(), MavenArtifactPreferences.defaultProvider());
    }

    public MavenArtifactsPreferences(PreferencesContext preferencesContext, String name) {
        super(preferencesContext, name, MavenArtifactPreferences.keyProvider(), MavenArtifactPreferences.defaultProvider());
    }

//    /*
//     * Single Artifact
//     */
//
//    private String getArtifactJarPath(MavenArtifact artifact) {
//        var recordArtefact = getRecord(artifact).getValue();
//        return mavenClient.resolveWithDependencies(recordArtefact).g;
//    }
//
//    private List<String> getArtifactJarDependencies(MavenArtifact artifact) {
//        String dep = getRecord(artifact).getDependencies();
//        if (dep != null && !dep.isEmpty()) {
//                return Stream.of(dep.split(File.pathSeparator)).collect(Collectors.toList());
//        }
//        return new ArrayList<>();
//    }
//
//    public File getArtifactFile(MavenArtifact artifact) {
//        String path = getArtifactJarPath(artifact);
//        if (path != null && !path.isEmpty()) {
//            File file = new File(path);
//            if (file.exists()) {
//                return file;
//            }
//        }
//        return null;
//    }
//
//    public List<File> getArtifactFileWithDependencies(MavenArtifact artifact) {
//        String path = getArtifactJarPath(artifact);
//        if (path != null && !path.isEmpty()) {
//
//            List<File> jarPaths = new ArrayList<>();
//            jarPaths.add(getArtifactFile(artifact));
//
//            jarPaths.addAll(getArtifactJarDependencies(artifact)
//                    .stream()
//                    .filter(d -> d != null && !d.isEmpty())
//                    .map(File::new)
//                    .filter(File::exists)
//                    .collect(Collectors.toList()));
//            return jarPaths;
//        }
//        return null;
//    }
//
//    /*
//     * All Artifacts
//     */
//
//    private List<String> getArtifactsJarsPaths() {
//        return getRecords().values()
//                .stream()
//                .map(MavenArtifactPreferences::getPath)
//                .distinct()
//                .collect(Collectors.toList());
//    }
//
//    private List<String> getArtifactsJarsDependencies() {
//        return getRecords().values()
//                .stream()
//                .map(p -> p.getDependencies())
//                .filter(d -> d != null && !d.isEmpty())
//                .flatMap(d -> Stream.of(d.split(File.pathSeparator)))
//                .distinct()
//                .collect(Collectors.toList());
//    }
//
//    public List<File> getArtifactsFiles() {
//        return getArtifactsJarsPaths().stream()
//                .filter(s -> s != null && !s.isEmpty())
//                .map(File::new)
//                .filter(File::exists)
//                .collect(Collectors.toList());
//    }
//
//    public List<File> getArtifactsFilesWithDependencies() {
//        List<String> jarsPaths = getArtifactsJarsPaths();
//        jarsPaths.addAll(getArtifactsJarsDependencies());
//        return jarsPaths.stream()
//                .filter(s -> s != null && !s.isEmpty())
//                .map(File::new)
//                .filter(File::exists)
//                .collect(Collectors.toList());
//    }
//
//    public List<Path> getArtifactsPaths() {
//        return getArtifactsFiles()
//                .stream()
//                .map(File::toPath)
//                .collect(Collectors.toList());
//    }
//    public List<Path> getArtifactsPathsWithDependencies() {
//        return getArtifactsFilesWithDependencies()
//                .stream()
//                .map(File::toPath)
//                .collect(Collectors.toList());
//    }
//
//    public List<String> getArtifactsCoordinates() {
//        return getRecords().entrySet()
//                .stream()
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//    }


    public MavenArtifactPreferences getRecordArtifact(MavenArtifact mavenArtifact) {
    	return getRecord(mavenArtifact);
    }

    public MavenArtifactPreferences getRecordArtifact(String key) {
        return getRecord(key);
    }

    public void removeArtifact(String coordinates) {
        removeRecord(coordinates);
    }


}
