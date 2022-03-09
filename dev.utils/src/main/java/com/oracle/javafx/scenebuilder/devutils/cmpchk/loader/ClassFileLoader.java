/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.devutils.cmpchk.loader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.oracle.javafx.scenebuilder.devutils.model.ClassFile;
import com.oracle.javafx.scenebuilder.devutils.model.ModuleFile;
import com.oracle.javafx.scenebuilder.devutils.model.StringOccurence;
import com.oracle.javafx.scenebuilder.devutils.strchk.utils.Patterns;

public class ClassFileLoader {

    public static List<ClassFile> loadClassFiles(File file) {
        List<ClassFile> result = new ArrayList<>();

        final String name = file.getName().replace(".java", "");
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            String content = String.join("\n", lines);
            String packageName = Patterns.PACKAGE.matcher(content).results().findFirst().get().group(1);

            final AtomicLong numComponents = new AtomicLong();
            final AtomicBoolean isComponent = new AtomicBoolean();
            final AtomicBoolean hasComponent = new AtomicBoolean();

            Patterns.COMPONENT.matcher(content).results().forEach(r -> {
                String cmpName = r.group(1);

                numComponents.incrementAndGet();
                if (cmpName.equals(name)) {
                    isComponent.set(true);
                } else {
                    ClassFile inner = new ClassFile(name + "." + cmpName, file, packageName, true, false, 1);
                    result.add(inner);
                    hasComponent.set(true);
                }
            });

            ClassFile cls = new ClassFile(file, packageName, isComponent.get(), hasComponent.get(), numComponents.get());

            if (Patterns.EXTENSION_CLASS_CHECK.matcher(content).results().count() > 0) {
                Patterns.EXTENSION_CLASS_REGISTER_CONTENT.matcher(content).results().forEach(r -> {

                    String registerContent = r.group(2);
                    Patterns.REGISTER_CONTENT_CLASSES.matcher(registerContent).results().forEach(c -> {

                        String className = c.group(1);
                        cls.getStringOccurences().add(new StringOccurence(className));
                    });
                });
            }


            result.add(cls);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static ModuleFile loadModuleFile(File file) {
        String name = file.getName().replace(".java", "");
        try {
//            String content = new String(Files.readAllBytes(file.toPath()));
//            String packageName = Patterns.PACKAGE.matcher(content).results().findFirst().get().group(1);
//            long numComponents = Patterns.COMPONENT.matcher(content).results().count();
//            boolean hasComponent = numComponents > 0;
//            boolean hasInnerComponent = Patterns.INNER_COMPONENT.matcher(content).results().findFirst().isPresent();
//
            ModuleFile cls = new ModuleFile(file);

//            Patterns.STRING.matcher(content).results().forEach(r -> {
//                cls.getStringOccurences().add(new StringOccurence(r.group(1)));
//            });

            return cls;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}