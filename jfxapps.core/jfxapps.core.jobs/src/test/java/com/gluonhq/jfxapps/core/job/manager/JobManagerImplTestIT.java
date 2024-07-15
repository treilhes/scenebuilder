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

package com.gluonhq.jfxapps.core.job.manager;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.base.BatchJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.job.manager.JobManagerImpl.JobPipelineFactory;

@ExtendWith(MockitoExtension.class)
class JobManagerImplTestIT {

    private DocumentManager documentManager = new DocumentManager.DocumentManagerImpl();

    @Mock
    private JobPipelineFactory jobPipelineFactory;

    @Mock
    FxomJobsFactory fxomJobsFactory;

    @Mock
    private BatchJob.Factory batchJobFactory;

    @Mock
    private Job job;

    private JobManagerImpl jobManager;

    @BeforeEach
    void setUp() {
        jobManager = new JobManagerImpl(
                documentManager,
                jobPipelineFactory,
                fxomJobsFactory,
                batchJobFactory);

        when(batchJobFactory.getJob()).thenAnswer(invocation -> new BatchJob(null, documentManager ,null));
    }

    @Test
    void must_forbid_nested_execution() {
        when(job.isExecutable()).thenReturn(true);

        // to allow testing assertions execute 3 jobs and undo last to enable undo AND
        // redo
        // the stack must be job > [job] > job
        jobManager.push(job);
        jobManager.push(job);
        jobManager.push(job);
        jobManager.undo();

        doAnswer(invocation -> {
            assertThrows(IllegalStateException.class, () -> jobManager.push(job));
            assertThrows(IllegalStateException.class, () -> jobManager.undo());
            assertThrows(IllegalStateException.class, () -> jobManager.redo());
            assertThrows(IllegalStateException.class, () -> jobManager.clear());
            return null;
        }).when(job).execute();

        jobManager.push(job);
    }

    @Test
    void document_must_be_dirty_after_job_execution() {
        when(job.isExecutable()).thenReturn(true);

        jobManager.push(job);

        assertTrue("document must be dirty", documentManager.dirty().get());
    }

    // This test is disabled because the document is not dirty after undo and , at
    // least for now, can't due to the implementation of the JobManagerImpl
    // which have a maximum stack size = even if we are a the beginning of the stack
    // it does not mean that the document has not been modified
//    @Test
//    void document_mustnt_be_dirty_after_job_execution_and_undo() {
//        when(job.isExecutable()).thenReturn(true);
//        when(jobPipeline.buildPipeline(any(Job.class))).then(invocation -> (Job)invocation.getArgument(0));
//
//        jobManager.push(job);
//        jobManager.undo();
//
//        assertTrue("document must'nt be dirty", documentManager.dirty().get());
//    }
}
