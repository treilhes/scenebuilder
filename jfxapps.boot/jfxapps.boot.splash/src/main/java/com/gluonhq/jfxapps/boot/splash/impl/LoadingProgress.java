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

import com.gluonhq.jfxapps.boot.api.context.ProgressListener;

public class LoadingProgress implements ProgressListener {

	//private static final Logger log = LoggerFactory.getLogger(LoadingProgress.class);

	static final Float START_PROGRESS = 0f;
	static final Float DONE_PROGRESS = 1.0f;

	private TextChange onTextChange;
	private ProgressChange onProgressChange;
	private LoadingDone onLoadingDone;

	float currentProgress;
	boolean started = false;
	boolean done = false;

	protected LoadingProgress() {
		super();
	}

	void step(Float value, String text) {
		currentProgress = value;

		if (onProgressChange != null && value != null) {
			onProgressChange.onProgressChange(value);
		}
		if (onTextChange != null && text != null) {
			onTextChange.onTextChange(text);
		}
		if (currentProgress >= DONE_PROGRESS && !done) {
			end();
		}
	}

	public void start() {
		done = false;
		started = true;
		step(START_PROGRESS, "Start loading");
	}

	public void end() {
		done = true;
		step(DONE_PROGRESS, "");
		if (onLoadingDone != null) {
			onLoadingDone.loadingDone();
		}
	}

	public void setOnTextChange(TextChange onTextChange) {
		this.onTextChange = onTextChange;
	}

	public void setOnProgressChange(ProgressChange onProgressChange) {
		this.onProgressChange = onProgressChange;
	}

	public void setOnLoadingDone(LoadingDone onLoadingDone) {
		this.onLoadingDone = onLoadingDone;
	}

	@FunctionalInterface
	public interface TextChange {
		void onTextChange(String text);
	}

	@FunctionalInterface
	public interface ProgressChange {
		void onProgressChange(float progress);
	}

	@FunctionalInterface
	public interface LoadingDone {
		void loadingDone();
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isDone() {
		return done;
	}

	@Override
	public void notifyProgress(float progress) {
		step(progress, null);
	}

	public float getCurrentProgress() {
		return currentProgress;
	}


}
