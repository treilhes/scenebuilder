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
package com.oracle.javafx.scenebuilder.api.action;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;

import javafx.scene.input.KeyCombination;

public abstract class AbstractAction implements Action {

	private final String nameI18nKey;
	private final String descriptionI18nKey;
	private final String rawAccelerator;
	private ExtendedAction<?> extendedAction;
    private final Api api;

	public AbstractAction(Api api) {
	    this.api = api;

		ActionMeta actionMeta = this.getClass().getAnnotation(ActionMeta.class);

		if (actionMeta == null) {
			throw new RuntimeException("Class inheriting AbstractAction class must be annotated with @ActionMeta");
		}

		nameI18nKey = actionMeta.nameKey();
		descriptionI18nKey = actionMeta.descriptionKey();
		rawAccelerator = actionMeta.accelerator();
	}

	@Override
	public String getUniqueId() {
		return this.getClass().getName();
	}

	@Override
	public String getName() {
		return nameI18nKey == null ? null : I18N.getString(nameI18nKey);
	}

	@Override
	public String getDescription() {
		return descriptionI18nKey == null ? null : I18N.getString(descriptionI18nKey);
	}

	@Override
	public KeyCombination getWishedAccelerator() {
		if (rawAccelerator == null) {
			return null;
		}
		return KeyCombination.valueOf(rawAccelerator);
	}

	@Override
	public void checkAndPerform() {
		if (canPerform()) {
			perform();
		}
	}

	@Override
	public ExtendedAction<?> extend() {
		if (this.getClass().isAssignableFrom(ExtendedAction.class)) {
			return (ExtendedAction<?>) this;
		}
		if (extendedAction == null) {
			extendedAction = api.getContext().getBean(ExtendedAction.class, this);
		}
		return extendedAction;
	}

	public Api getApi() {
		return api;
	}
}
