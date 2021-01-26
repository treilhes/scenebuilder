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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta
public class ExtendedAction<T extends AbstractAction> extends AbstractAction {

	private List<ActionExtension<T>> extensions;

	private boolean extended = false;
	private final AbstractAction action;

	@SuppressWarnings("unchecked")
	public ExtendedAction(T action) {
		super(action.getApi());

		this.action = action;

		ResolvableType resolvable = ResolvableType.forClassWithGenerics(ActionExtension.class, action.getClass());
		String[] beanNamesForType = getApi().getContext().getBeanNamesForType(resolvable);

		if (beanNamesForType.length > 0) {
			extensions = Arrays.asList(beanNamesForType).stream()
					.map(b -> (ActionExtension<T>)getApi().getContext().getBean(b)).collect(Collectors.toList());
		}

		if (extensions != null) {
			extensions.forEach(ext -> ext.setExtendedAction(action));
			extended = true;
		}

	}

	@Override
	public boolean canPerform() {
		return action.canPerform();
	}

	@Override
	public void perform() {
		if (extended) {
			extensions.stream().filter(ext -> ext.canPerform()).forEach(ext -> ext.prePerform());
		}

		action.perform();

		if (extended) {
			extensions.stream().filter(ext -> ext.canPerform()).forEach(ext -> ext.postPerform());
		}
	}

	public Action getExtendedAction() {
		return action;
	}



}
