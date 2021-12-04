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
package com.oracle.javafx.scenebuilder.extension;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarFile;

public class Agent {

    private static Instrumentation inst;

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        throw new RuntimeException("Extension agent can't be attached dynamically");
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) throws Exception {
        inst = instrumentation;
        populateClasspath();
    }

    private static void populateClasspath() throws Exception {
        File extensionFolder = DefaultFolders.getUserExtensionsFolder(ExtensionLibrary.ID);
        System.out.println("EXTFOLDER=" + extensionFolder.getAbsolutePath());
        
        ExtensionStates extensionStates = ExtensionStates.load(extensionFolder);
        extensionStates.cleanDeletedExtensions();
        List<UUID> loadable = extensionStates.getLoadableExtensions();
        List<File> jars = listAllRequiredJars(extensionFolder, loadable);
        
        for (File jar:jars) {
            addToClassPath(jar);
        }
    }
    
    private static List<File> listAllRequiredJars(File userExtensionsFolder, List<UUID> loadableExtensions) {
        ExtensionRegistry registry = new ExtensionRegistry(userExtensionsFolder, loadableExtensions);
        
        Dependency tmpMain = new Dependency("tmp", "tmp", "0.0.0");
        tmpMain.setLocalFile(new File("C:/SSDDrive/git/scenebuilder/scenebuilder.ext.help.features/target/scenebuilder.ext.help.features-17.0.0-SNAPSHOT.jar"));
        
        ExtensionMetadata tmp = new ExtensionMetadata();
        tmp.setMain(tmpMain);
        registry.getExtensions().add(tmp);
        
        return registry.listRequiredJars();
    }

    public static void addToClassPath(File jarFile) throws IOException {
        inst.appendToSystemClassLoaderSearch(new JarFile(jarFile));
    }

}