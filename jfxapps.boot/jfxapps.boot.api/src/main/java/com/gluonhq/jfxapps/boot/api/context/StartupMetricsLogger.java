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
package com.gluonhq.jfxapps.boot.api.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.boot.context.metrics.buffering.StartupTimeline.TimelineEvent;
import org.springframework.context.event.EventListener;

/**
 * Logs startup metrics to the logger.
 */
public class StartupMetricsLogger {

    private static final Logger logger = LoggerFactory.getLogger(StartupMetricsLogger.class);
    private final BufferingApplicationStartup startup;

    public StartupMetricsLogger(BufferingApplicationStartup startup) {
        this.startup = startup;
    }

    @EventListener({ApplicationReadyEvent.class, ExtensionReadyEvent.class})
    public void logStartupMetrics() {
        StartupTimeline timeline = startup.getBufferedTimeline();
        List<TimelineEvent> events = timeline.getEvents();

        logOrderedEvents(events);
    }

    public static void logOrderedEvents(List<TimelineEvent> events) {
        // Step 1: Organize events into a tree structure
        Map<String, List<TimelineEvent>> parentToChildrenMap = new HashMap<>();
        Map<String, TimelineEvent> idToEventMap = new HashMap<>();

        for (TimelineEvent event : events) {
            String id = String.valueOf(event.getStartupStep().getId());
            String parentId = event.getStartupStep().getParentId() != null
                    ? String.valueOf(event.getStartupStep().getParentId())
                    : "null";
            idToEventMap.put(id, event);

            parentToChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(event);
        }

        // Step 2: Find root events (those with null or missing parentId)
        List<TimelineEvent> roots = parentToChildrenMap.getOrDefault("null", new ArrayList<>());

        // Step 3: Recursively log events
        for (TimelineEvent root : roots) {
            logEventRecursively(root, parentToChildrenMap, 0);
        }
    }

    private static void logEventRecursively(TimelineEvent event, Map<String, List<TimelineEvent>> parentToChildrenMap, int indentationLevel) {
        // Log the current event with proper indentation
        String indentation = "  ".repeat(indentationLevel);
        String id = String.valueOf(event.getStartupStep().getId());
        String parentId = event.getStartupStep().getParentId() != null
                ? String.valueOf(event.getStartupStep().getParentId())
                : "null";
        String name = event.getStartupStep().getName();
        long duration = event.getDuration().toMillis();

        logger.error("{} {} > {} - {} - {} ms",
            indentation, parentId, id, name, duration);

        event.getStartupStep().getTags().forEach((tag) -> {
            logger.error("{}   - tag {} : {}", indentation, tag.getKey(), tag.getValue());
        });

        // Log child events
        List<TimelineEvent> children = parentToChildrenMap.getOrDefault(id, Collections.emptyList());
        for (TimelineEvent child : children) {
            logEventRecursively(child, parentToChildrenMap, indentationLevel + 1);
        }
    }
}
