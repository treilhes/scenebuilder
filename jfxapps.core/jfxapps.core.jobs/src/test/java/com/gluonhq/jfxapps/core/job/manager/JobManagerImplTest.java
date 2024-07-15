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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
class JobManagerImplTest {


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
        when(fxomJobsFactory.fixUndeclaredExpressionReference(any(), any())).thenAnswer(invocation -> new BatchJob(null, documentManager ,null));
        when(fxomJobsFactory.fixUndeclaredIntrinsic(any(), any())).thenAnswer(invocation -> new BatchJob(null, documentManager ,null));
    }

    @Test
    void testPush() {
        when(job.isExecutable()).thenReturn(true);

        var executedJob = jobManager.push(job);

        verify(job).execute();
        assertEquals(1, jobManager.getUndoStack().size());
        assertEquals(executedJob, jobManager.getUndoStack().get(0));
    }

    @Test
    void testClear() {
        when(job.isExecutable()).thenReturn(true);

        jobManager.push(job);
        jobManager.clear();

        assertTrue(jobManager.getUndoStack().isEmpty());
        assertTrue(jobManager.getRedoStack().isEmpty());
    }

    @Test
    void testCanUndo() {
        assertFalse(jobManager.canUndo());

        when(job.isExecutable()).thenReturn(true);

        jobManager.push(job);

        assertTrue(jobManager.canUndo());
    }

    @Test
    void testUndo() {
        when(job.isExecutable()).thenReturn(true);

        var executedJob = jobManager.push(job);
        jobManager.undo();

        assertTrue(jobManager.getRedoStack().contains(executedJob));
        assertFalse(jobManager.getUndoStack().contains(executedJob));
    }

    @Test
    void testCanRedo() {
        assertFalse(jobManager.canRedo());

        when(job.isExecutable()).thenReturn(true);

        jobManager.push(job);
        jobManager.undo();

        assertTrue(jobManager.canRedo());
    }

    @Test
    void testRedo() {
        when(job.isExecutable()).thenReturn(true);

        var executedJob = jobManager.push(job);
        jobManager.undo();
        jobManager.redo();

        assertTrue(jobManager.getUndoStack().contains(executedJob));
        assertFalse(jobManager.getRedoStack().contains(executedJob));
    }

    @Test
    void testGetRedoDescription() {
        assertNull(jobManager.getRedoDescription());

        when(job.isExecutable()).thenReturn(true);

        when(job.getDescription()).thenReturn("Test Job");

        jobManager.push(job);
        jobManager.undo();

        assertEquals("Test Job", jobManager.getRedoDescription());
    }

    @Test
    void testGetUndoDescription() {
        assertNull(jobManager.getUndoDescription());

        when(job.isExecutable()).thenReturn(true);

        when(job.getDescription()).thenReturn("Test Job");

        jobManager.push(job);

        assertEquals("Test Job", jobManager.getUndoDescription());
    }

    @Test
    void testGetCurrentJob() {
        assertNull(jobManager.getCurrentJob());

        when(job.isExecutable()).thenReturn(true);


        var executedJob = jobManager.push(job);

        assertEquals(executedJob, jobManager.getCurrentJob());
    }

    @Test
    void testRevisionProperty() {
        assertEquals(0, jobManager.revisionProperty().get());

        when(job.isExecutable()).thenReturn(true);


        jobManager.push(job);

        assertEquals(1, jobManager.revisionProperty().get());
    }


}
