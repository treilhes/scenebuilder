/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.splash.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.utils.ProgressListener;

public class LoadingProgress {

	private static final Logger log = LoggerFactory.getLogger(LoadingProgress.class);

	static final Float START_PROGRESS = 0f;
	static final Float DONE_PROGRESS = 1.0f;

	private final URL imageUrl;
	private Map<LoadingProgressItem, Float> subSteps = null;
	private List<ProgressListener> subStepsList = null;

	boolean started = false;
	boolean done = false;



	protected LoadingProgress(URL imageUrl) {
		super();
		this.imageUrl = imageUrl;
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isDone() {
		return done;
	}

	public float computeCurrentProgress() {
		if (subSteps == null) {
			return START_PROGRESS;
		}

		var currentProgress = START_PROGRESS;
		var allDone = true;
		for (var entry : subSteps.entrySet()) {
			var item = entry.getKey();
			var part = entry.getValue();

			if (item.isDone()) {
				currentProgress += part;
			} else if (item.isStarted()) {
				currentProgress += part * item.getCurrentProgress();
				allDone = false;
			} else {
				allDone = false;
			}
		}

		if (allDone) {
			done = true;
			return DONE_PROGRESS;
		} else {
			return currentProgress;
		}

	}

	public URL getImageUrl() {
		return imageUrl;
	}

	public List<ProgressListener> asSubSteps(int stepNumber) {

		if (started) {
            throw new IllegalStateException("loading has started already, you can't split it anymore");
		}

		subSteps = new HashMap<>();

		float part = 1f / stepNumber;
		subStepsList = new ArrayList<>();

		for (int i = 0; i < stepNumber; i++) {
			LoadingProgressItem subStep = new LoadingProgressItem();
            subStepsList.add(subStep);
            subSteps.put(subStep, part);
		}
		return subStepsList;
	}

	public List<ProgressListener> getSubSteps() {
		return subStepsList;
	}

}
