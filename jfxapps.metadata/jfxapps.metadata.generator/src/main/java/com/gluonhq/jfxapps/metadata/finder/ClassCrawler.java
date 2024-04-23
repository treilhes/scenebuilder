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
package com.gluonhq.jfxapps.metadata.finder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.metadata.util.Report;

public class ClassCrawler {

    private final static Logger logger = LoggerFactory.getLogger(ClassCrawler.class);

    public Set<Class<?>> crawl(Set<Path> jars, SearchContext context) {
        Set<Class<?>> result = new HashSet<>();

        jars.forEach(j -> {
            try {
                Set<String> classes = Jar.listClasses(j);

                for(String cls:classes) {
                    // if provided only included package are allowed
                    if (context.getIncludedPackages() != null && !context.getIncludedPackages().isEmpty()
                            && !context.getIncludedPackages().stream().anyMatch(p -> cls.startsWith(p))) {
                        logger.debug("Class rejected, package not included : {}", cls);
                        continue;
                    }

                    // if provided excluded package removed
                    if (context.getExcludedPackages() != null && !context.getExcludedPackages().isEmpty()
                            && context.getExcludedPackages().stream().anyMatch(p -> cls.startsWith(p))) {
                        logger.debug("Class rejected, package excluded : {}", cls);
                        continue;
                    }

                    try {
                        logger.debug("Class did pass package checks, continue : {}", cls);
                        Class<?> clazz = Class.forName(cls);
                        processClass(clazz, context, result);

                        for (Class<?> innerClass:clazz.getDeclaredClasses()) {
                            if (Modifier.isStatic(innerClass.getModifiers())
                                    && Modifier.isPublic(innerClass.getModifiers())) {
                                logger.debug("Inner Class did pass package checks, continue : {}", innerClass);
                                processClass(innerClass, context, result);
                            }
                        }
                    } catch (Throwable e) {
                    	logger.error("Unable to process class", e);
                        Report.error(cls, "Unable to process class", e);
                    }
                }
            } catch (IOException e) {
                Report.error("Unexpected exception occured while processing classes", e);
            }
        });

        return result;
    }

    private void processClass(Class<?> cls, SearchContext context, Set<Class<?>> result) {

        boolean accepted = context.getRootClasses().isEmpty() || context.getRootClasses().stream().anyMatch(rc -> rc.isAssignableFrom(cls));
        //boolean accepted = context.getRootClasses().stream().anyMatch(rc -> rc.isAssignableFrom(cls));

        if (accepted) {
            logger.debug("Class has a root class into her hierarchy : {}", cls);
        }

        boolean excluded = context.getExcludeClasses().stream()
        		.anyMatch(rc -> rc.isAssignableFrom(cls));

        if (excluded) {
            logger.debug("Class does not have an excluded class into her hierarchy : {}", cls);
        }

        if (accepted && !excluded) {
            logger.info("Class did pass all checks, processing : {}", cls);

            result.add(cls);
        }

    }

}
