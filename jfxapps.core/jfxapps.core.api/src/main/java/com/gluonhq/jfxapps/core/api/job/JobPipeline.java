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
package com.gluonhq.jfxapps.core.api.job;

import java.util.List;
import java.util.Map;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.collector.FXOMCollector;

/**
 * Pipeline of jobs and collectors that will be executed on a {@link FXOMDocument} before and after
 * the main job is executed. The purpose of this class is to provide a way to execute a set of
 * jobs/collectors each time a job is executed on a {@link FXOMDocument}. This is useful to execute
 * a set of jobs that are always executed together. For example, if a job is executed that modifies
 * the {@link FXOMDocument}, it is possible to execute a set of collectors that will collect data
 * before the job is executed and after the job is executed. This is useful to collect data that
 * will be used by the job or that has been modified by the job.</br>
 * </br>
 * Collectors won't be executed a second time on undo/redo operations</br>
 * </br>
 * ONLY ONE OR ZERO instance of this bean is expected in the context</br>
 */
public interface JobPipeline {
    /**
     * Collectors that will be executed on the current {@link FXOMDocument} before the job is executed
     * @return a map of collectors
     */
    Map<String, FXOMCollector<?>> preExecutionCollectors();
    /**
     * Collectors that will be executed on the current {@link FXOMDocument} after the job is executed
     * @return a map of collectors
     */
    Map<String, FXOMCollector<?>> postExecutionCollectors();
    /**
     * Job that will be executed after execution of collectors provided by {@link #preExecutionCollectors()}
     * but before the main job is executed.
     *
     * The job must be executed before returning.
     *
     * @param preIdMap an read/write map of id/objects that have been collected by the default collector
     * @return a job
     */
    Job preExecutionJob(Map<String, List<FXOMObject>> preIdMap);
    /**
     * Job that will be executed after execution of collectors provided by {@link #postExecutionCollectors()}
     * and after the main job is executed
     *
     * The job must be executed before returning.
     *
     * @param preIdMap an read/write map of id/objects that have been collected by the default collector
     * @return a job
     */
    Job postExecutionJob(Map<String, List<FXOMObject>> postIdMap);

}
