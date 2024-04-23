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
package com.gluonhq.jfxapps.metadata.util;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Report {

    private static Logger logger = LoggerFactory.getLogger(Report.class);

    private static List<Log> logs = new ArrayList<>();

    public static boolean enableGlobalReport = true;

    private enum InternalLevel {
        Error, Info, Warn
    }

    private static class Log {
        private InternalLevel level;
        private String cls;
        private String message;
        private Throwable throwable;

        public Log(InternalLevel level, String cls, String message, Throwable throwable) {
            super();
            this.level = level;
            this.cls = cls;
            this.message = message;
            this.throwable = throwable;
        }
    }

    public static void error(Class<?> cls, String message) {
        logs.add(new Log(InternalLevel.Error, cls.getName(), message, null));

        if (!enableGlobalReport) {
            throw new RuntimeException(cls.getName() + " : " + message);
        }
    }

    public static void error(String message, Throwable throwable) {
        logs.add(new Log(InternalLevel.Error, (String) null, message, null));

        if (!enableGlobalReport) {
            throw new RuntimeException(message, throwable);
        }
    }

    public static void error(Class<?> cls, String message, Throwable throwable) {
        logs.add(new Log(InternalLevel.Error, cls.getName(), message, throwable));

        if (!enableGlobalReport) {
            throw new RuntimeException(cls.getName() + " : " + message, throwable);
        }
    }

    public static void error(String cls, String message, Throwable throwable) {
        logs.add(new Log(InternalLevel.Error, cls, message, throwable));

        if (!enableGlobalReport) {
            throw new RuntimeException(cls + " : " + message, throwable);
        }
    }

    public static void info(Class<?> cls, String message) {
        logs.add(new Log(InternalLevel.Info, cls.getName(), message, null));
    }

    public static void warn(Class<?> cls, String message) {
        logs.add(new Log(InternalLevel.Warn, cls.getName(), message, null));
    }

    public static void warn(Class<?> cls, String message, Throwable throwable) {
        logs.add(new Log(InternalLevel.Warn, cls.getName(), message, throwable));
    }

    public static boolean flush(boolean debug) {
        boolean hasError = false;
        List<Log> l = new ArrayList<>(logs);
        logs.clear();
        for (Log log : l) {
            String cls = log.cls == null ? "NONE" : log.cls;
            switch (log.level) {
            case Error:
                if (debug) {
                    logger.error("{} : {}" ,cls, log.message, log.throwable);
                } else {
                    String error = log.throwable != null
                            ? log.throwable.getMessage() != null ? log.throwable.getMessage()
                                    : log.throwable.getCause().getMessage()
                            : null;
                    logger.error("{} : {}{}", cls, log.message, (error != null ? " : " + error : ""));
                }

                hasError = true;
                break;
            case Warn:
                if (debug) {
                    logger.warn("{} : {}" ,cls, log.message, log.throwable);
                } else {
                    String error = log.throwable != null
                            ? log.throwable.getMessage() != null ? log.throwable.getMessage()
                                    : log.throwable.getCause().getMessage()
                            : null;
                    logger.warn("{} : {}{}", cls, log.message, (error != null ? " : " + error : ""));
                }
                break;
            default:
                logger.info("{} : {}" ,cls, log.message);
                break;
            }
        }

        return hasError;
    }
}
