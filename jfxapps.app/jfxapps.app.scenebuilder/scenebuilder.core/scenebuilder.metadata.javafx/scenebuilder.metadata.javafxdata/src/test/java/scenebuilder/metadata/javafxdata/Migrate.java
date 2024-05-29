/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package scenebuilder.metadata.javafxdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Migrate {

    public static void main(String[] args) {
        File originFolder = new File("./resources_backup");
        File targetFolder = new File("./src/main/resources");
        try {
            new Migrate(originFolder, targetFolder).migrate();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private File originFolder;
    private File targetFolder;

    private Migrate(File originFolder, File targetFolder) {
        this.originFolder = originFolder;
        this.targetFolder = targetFolder;
    }

    private void migrate() throws IOException {
        recursiveMigrate(originFolder, "");
    }


    private void recursiveMigrate(File currentFolder, String relativePath) throws IOException {

        List<String> componentCustoProps = List.of("resizeWhenTop", "category","labelMutation");
        List<String> componentPropCustoProps = List.of("image", "imagex2","order","freeChildPositioning", "childLabelMutation");
        List<String> valuePropCustoProps = List.of("section","subSection","order","nullEquivalent");



        File[] filesList = currentFolder.listFiles();
        if (filesList != null) {
            for (File file : filesList) {
                String newRelativePath = relativePath.isEmpty() ? file.getName() : relativePath + File.separator + file.getName();
                if (file.isDirectory()) {
                    // Create the same directory in the target folder
                    Path targetPath = Paths.get(targetFolder.getPath(), newRelativePath);
                    Files.createDirectories(targetPath);
                    recursiveMigrate(file, newRelativePath);
                } else {
                    Path targetPath = Paths.get(targetFolder.getPath(), newRelativePath);
                    if (file.getName().endsWith(".properties")) {
                        // Load the file into a Properties object
                        Properties properties = new Properties();
                        try (FileInputStream inStream = new FileInputStream(file)) {
                            properties.load(inStream);
                        }
                        // Create a copy of the Properties object
                        Properties propertiesCopy = new Properties();
                        //propertiesCopy.putAll(properties);

                        String cls = file.getName().substring(0, file.getName().lastIndexOf("."));

                        Set<String> components = new HashSet<>();
                        components.add("children");
                        Set<String> values = new HashSet<>();
                        Set<String> statics = new HashSet<>();
                        Set<String> qualifiers = new HashSet<>();

                        for (String k : properties.stringPropertyNames()) {
                            List<String> key = new ArrayList<>(List.of(k.split("\\.")));
                            String value = properties.getProperty(k);

                            if (!key.get(0).equals("static")
                                    && key.get(key.size()-1).equals("class")
                                    && value.endsWith(".ComponentPropertyMetadata")) {
                                components.add(key.get(0));

                            } else if (!key.get(0).equals("static")
                                    && key.get(key.size()-1).equals("class")
                                    && !value.endsWith(".ComponentPropertyMetadata")) {
                                values.add(key.get(0));

                            } else if (key.get(0).equals("static")
                                    && key.get(key.size()-1).equals("class")) {
                                statics.add(key.get(1));

                            }

                            if (key.get(0).equals(cls) && key.get(1).equals("qualifiers")) {
                                String defaults = properties.getProperty(cls + ".qualifiers");
                                String[] defaultValues = defaults.split(",");
                                qualifiers.addAll(List.of(defaultValues));
                            }
                        }

                        System.out.println("CLASS:" + cls);
                        System.out.println();

                        for (String k : properties.stringPropertyNames()) {

                            String value = properties.getProperty(k);
                            List<String> key = new ArrayList<>(List.of(k.split("\\.")));
                            String newValue = value;

                            if (
                                    (key.get(0).equals(cls) && key.get(1).equals("qualifiers"))
                                    || (key.get(key.size() - 1).equals("visibility"))
                                    || (key.get(key.size() - 1).equals("classKind"))) {
                                System.out.println("Discarded: " + key);
                                continue;
                            }

                            String firstItem = key.get(0);
                            String secondItem = key.get(1);
                            String lastItem = key.get(key.size() - 1);

                            if (firstItem.equals(cls)) {
                                key.set(0, "class");
                            }

                            if (qualifiers.contains(secondItem)) {
                                key.add(1, "custo");
                                key.add(2, "qualifiers");
                            }
                            if (componentCustoProps.contains(secondItem) && key.get(0).equals("class")) {
                                key.add(1, "custo");
                            }

                            if (values.contains(firstItem)) {
                                key.add(0, "property");
                            }

                            if (valuePropCustoProps.contains(secondItem) && key.get(0).equals("property")) {
                                key.add(2, "custo");
                            }

                            if (components.contains(firstItem)) {
                                key.add(0, "component");
                            }

                            if (componentPropCustoProps.contains(secondItem) && key.get(0).equals("component")) {
                                key.add(2, "custo");
                            }

                            if (firstItem.equals("static") && statics.contains(secondItem) && valuePropCustoProps.contains(key.get(2))) {
                                key.add(2, "custo");
                            }

                            if (key.get(key.size() - 1).equals("resizeWhenTop")) {
                                key.set(key.size() - 1, "resizeNeededWhenTop");
                            }

                            if (key.get(key.size() - 1).equals("class")) {
                                key.set(key.size() - 1, "metadataClass");

                                if ("TOBEDEFINED".equals(newValue)) {
                                    newValue = "";
                                }
                            }

                            if (key.get(key.size() - 1).equals("order")) {
                                if ("TOBEDEFINED".equals(newValue)) {
                                    newValue = "-1";
                                }
                            }

                            if (key.get(key.size() - 1).equals("shadows")) {
                                String[] items = newValue.split(",");
                                if (items.length == 1) {
                                    key.add("1");
                                }
                            }

                            String builtKey = String.join(".", key);
                            propertiesCopy.put(builtKey, newValue);
                            if (k.equals(builtKey)) {
                                System.out.println("Key not changed: " + k);
                            }
                        }
                        // Save the copy to the target folder
                        try (FileOutputStream outStream = new FileOutputStream(targetPath.toFile())) {
                            propertiesCopy.store(outStream, null);
                        }
                    } else {
                        // Copy the file to the target folder
                        Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }


}
