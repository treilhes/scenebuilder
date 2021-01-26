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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExtensionStates {
    
    protected static String STATE_FILE_NAME = "extensions.properties";
    
    public static ExtensionStates load(File folder) {
        ExtensionStates states = new ExtensionStates(folder);
        states.load();
        return states;
    }
    
    private final Map<UUID, ExtensionState> states = new HashMap<>();
    private final File rootFolder;
    
    private ExtensionStates(File rootFolder) {
        this.rootFolder = rootFolder;
        
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
    }
    
    private void load() {
        try (FileInputStream fis = new FileInputStream(new File(rootFolder, STATE_FILE_NAME))){
            Properties properties = new Properties();
            properties.load(fis);
            
            properties.entrySet().forEach((e)->{
                UUID extensionId = UUID.fromString(e.getKey().toString());
                ExtensionState state = ExtensionState.valueOf(e.getValue().toString());
                states.put(extensionId, state);
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected Map<UUID, ExtensionState> getStates() {
        return states;
    }
    
    public void markForDeletion(UUID extensionId) {
        states.put(extensionId, ExtensionState.DELETE);
    }
    
    public void markAsActive(UUID extensionId) {
        states.put(extensionId, ExtensionState.ACTIVE);
    }
    
    public void markAsInactive(UUID extensionId) {
        states.put(extensionId, ExtensionState.DISABLED);
    }
    public void cleanDeletedExtensions() {
        List<UUID> toDelete = states.entrySet().stream()
                .filter(e -> e.getValue() == ExtensionState.DELETE)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
        
        for (UUID id:toDelete) {
            File extFolder = new File(rootFolder, id.toString());
            if (extFolder.exists()) {
                extFolder.delete();
            }
            if (!extFolder.exists()) {
                states.remove(id);
                save();
            }
        }
    }
    
    private void save(String comment) {
        try (FileOutputStream fos = new FileOutputStream(new File(rootFolder, STATE_FILE_NAME))){
            Properties properties = new Properties();
            states.entrySet().forEach(e -> properties.put(e.getKey().toString(), e.getValue().toString()));
            properties.store(fos, comment);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void save() {
        save("Scenebuilder extension state file");
    }

    public List<UUID> getLoadableExtensions() {
        return states.entrySet().stream()
                .filter(e -> e.getValue() == ExtensionState.ACTIVE)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }
}
