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
package com.oracle.javafx.scenebuilder.core.loader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionReport {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionReport.class);

    private final UUID id;
    private final List<ReportItem> items = new ArrayList<>();

    public ExtensionReport(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void info(String message) {
        logger.info(message);
        items.add(new ReportItem(Type.Info, LocalDate.now(), message, null));
    }

    public void warn(String message) {
        logger.warn(message);
        items.add(new ReportItem(Type.Warn, LocalDate.now(), message, null));
    }

    public void warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
        items.add(new ReportItem(Type.Warn, LocalDate.now(), message, throwable));
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
        items.add(new ReportItem(Type.Error, LocalDate.now(), message, throwable));
    }

    public boolean hasError() {
        return items.stream().anyMatch(ReportItem::isError);
    }

    public record ReportItem(Type type, LocalDate date, String message, Throwable throwable) {
        public boolean isError() {
            return this.type == Type.Error;
        }
    }

    public enum Type {
        Warn,
        Error,
        Info;
    }

    public Optional<Throwable> getThrowable() {
        return items.stream().filter(ReportItem::isError).map(ReportItem::throwable).findFirst();
    }
}
