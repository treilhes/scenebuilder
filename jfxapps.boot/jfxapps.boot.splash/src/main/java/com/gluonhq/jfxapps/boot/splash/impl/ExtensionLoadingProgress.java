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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.ProgressListener;

public class ExtensionLoadingProgress extends LoadingProgress {

	private static final Logger log = LoggerFactory.getLogger(ExtensionLoadingProgress.class);

	static final Float INIT_CONTEXT_PROGRESS = -1f;
	static final Float INIT_UI_PROGRESS = 0.95f;

	private final ContextLoadingMonitor contextMonitor = new ContextLoadingMonitor(this);

	private final ExtensionLoadingProgress parent;
	private final Set<ExtensionLoadingProgress> children = new HashSet<>();
	private final UUID id;
	private final URL imageUrl;

	boolean childrenLoaded = false;

	public static ExtensionLoadingProgress rootInstance(UUID id, URL imageUrl) {
		return new ExtensionLoadingProgress(null, id, imageUrl);
	}

	private ExtensionLoadingProgress(ExtensionLoadingProgress parent, UUID id, URL imageUrl) {
		super();
		this.parent = parent;
		this.id = id;
		this.imageUrl = imageUrl;
	}

	public void startLoadingLayer() {
		step(INIT_CONTEXT_PROGRESS, "Scanning classpath");
	}

	public void startLoadingContext() {
		step(INIT_CONTEXT_PROGRESS, "Scanning classpath");
	}

	public void initializingApplication() {
		log.info("Initial loading of singletons beans done");
		step(INIT_UI_PROGRESS, "Init UI");
	}

	public ContextLoadingMonitor getContextMonitor() {
		return contextMonitor;
	}

	public ExtensionLoadingProgress createChild(UUID id, URL imageUrl) {
		ExtensionLoadingProgress child = new ExtensionLoadingProgress(this, id, imageUrl);
		children.add(child);
		return child;
	}

	public ExtensionLoadingProgress getParent() {
		return parent;
	}

	public Set<ExtensionLoadingProgress> getChildren() {
		return children;
	}

	public UUID getId() {
		return id;
	}

	public URL getImageUrl() {
		return imageUrl;
	}

	public boolean isChildrenLoaded() {
		return childrenLoaded;
	}

	private void notifyChildLoaded() {
		childrenLoaded = children.stream().allMatch(ExtensionLoadingProgress::isDone);
	}

}
