/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.subjects;

import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.dock.ViewAttachment;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface ViewManager {

	Subject<DockRequest> dock();
	Subject<View> undock();
	Subject<View> close();

	@AllArgsConstructor
	@RequiredArgsConstructor
	@EqualsAndHashCode
	public static class DockRequest {
	    private final @Getter ViewAttachment viewAttachment;
		private final @Getter View source;
		private final @Getter UUID target;
		private @Getter boolean select = true;;

	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	public class ViewManagerImpl implements InitializingBean, ViewManager {

		private ViewSubjects subjects;

		public ViewManagerImpl() {
			subjects = new ViewSubjects();
		}

		@Override
		public void afterPropertiesSet() throws Exception {
		}

		@Override
		public Subject<DockRequest> dock() {
			return subjects.getDock();
		}

		@Override
		public Subject<View> undock() {
			return subjects.getUndock();
		}

		@Override
		public Subject<View> close() {
			return subjects.getClose();
		}
	}

	public class ViewSubjects extends SubjectManager {

		private @Getter PublishSubject<DockRequest> dock;
		private @Getter PublishSubject<View> undock;
		private @Getter PublishSubject<View> close;

		public ViewSubjects() {
			dock = wrap(ViewSubjects.class, "dock", PublishSubject.create()); // NOI18N
			undock = wrap(ViewSubjects.class, "undock", PublishSubject.create()); // NOI18N
			close = wrap(ViewSubjects.class, "close", PublishSubject.create()); // NOI18N
		}

	}
}
