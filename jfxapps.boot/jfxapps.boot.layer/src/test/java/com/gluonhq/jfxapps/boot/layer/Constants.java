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
package com.gluonhq.jfxapps.boot.layer;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Constants {

    static Path createJarPath(String name) {
        return Paths.get(String.format(IT_JAR_ROOT, name, String.format(JAR_FORMAT, name, IT_VERSION)));
    }
    static Path createTargetPath(String name) {
        return Paths.get(String.format(IT_CLASSES_ROOT, name));
    }

    final static String IT_JAR_ROOT = "./src/test/resources-its/common/%s/target/%s";
    final static String IT_CLASSES_ROOT = "./src/test/resources-its/common/%s/target/classes";
    final static String IT_VERSION = "1.0.0-SNAPSHOT";
    final static String JAR_FORMAT = "%s-%s.jar";

    final static Path IT_MODULE_JAR = createJarPath("module");
    final static Path IT_MODULE_CLASSES = createTargetPath("module");
    final static String IT_MODULE_NAME = "it.modul";
    final static String IT_MODULE_CLASS = "it.modul.ModuleClass";
    final static String IT_MODULE_RESOURCE = "module.txt";

    final static Path IT_AUTOMATIC_MODULE_JAR = createJarPath("automatic-modul");
    final static Path IT_AUTOMATIC_MODULE_CLASSES = createTargetPath("automatic-modul");
    final static String IT_AUTOMATIC_MODULE_NAME = "automatic.modul";
    final static String IT_AUTOMATIC_MODULE_CLASS = "it.automatic.modul.AutoModuleClass";
    final static String IT_AUTOMATIC_MODULE_RESOURCE = "automatic-modul.txt";

    final static Path IT_CLASSPATH_JAR = createJarPath("classpath");
    final static Path IT_CLASSPATH_CLASSES = createTargetPath("classpath");
    final static String IT_CLASSPATH_MODULE_NAME = "classpath";
    final static String IT_CLASSPATH_CLASS = "classpath.ClasspathClass";
    final static String IT_CLASSPATH_RESOURCE = "classpath.txt";

    final static Path IT_MODULE_WITH_DEPENDENCY_JAR = createJarPath("module-with-dependency");
    final static Path IT_MODULE_WITH_DEPENDENCY_CLASSES = createTargetPath("module-with-dependency");
    final static String IT_MODULE_WITH_DEPENDENCY_NAME = "it.modul.wiz.dependency";
    final static String IT_MODULE_WITH_DEPENDENCY_CLASS = "it.modul.wiz.dependency.ModuleWithDependencyClass";
    final static String IT_MODULE_WITH_DEPENDENCY_RESOURCE = "module-with-dependency.txt";

    final static Path IT_MODULE_WITH_SERVICE_JAR = createJarPath("module-with-service");

}
