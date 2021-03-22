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
package com.oracle.javafx.scenebuilder.api.subjects;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SubjectManager;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;

public interface DockManager {
	
	Subject<Dock> dockCreated();
	Subject<Dock> dockShow();
	Subject<Dock> dockHide();
		
	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	public class DockManagerImpl implements InitializingBean, DockManager {
		
		private DockSubjects subjects;
		
		private List<Dock> docks;
		
//		private List<Dock> visibleDocks;
//		
//		private Map<View, DockRequest> defaultDockRequests;
//		
//		private Map<View, DockRequest> dockRequests;
		
		public DockManagerImpl() {
			subjects = new DockSubjects();
			docks = new ArrayList<>();
//			visibleDocks = new ArrayList<>();
//			defaultDockRequests = new HashMap<>();
//			dockRequests = new HashMap<>();
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			dockCreated().subscribe(docks::add);
//			
//			dockShow().filter(dt -> !visibleDocks.contains(dt)).subscribe(dt -> {
//				visibleDocks.add(dt);
//				dockRequests.forEach((v,dr) -> {
//					if (dr.getTarget() == dt) {
//						v.getViewManager().dock().onNext(dr);
//					}
//				});
//			});
//			
//			dockHide().subscribe(dt -> {
//				visibleDocks.remove(dt);
//				dockRequests.forEach((v,dr) -> {
//					if (dr.getTarget() == dt) {
//						v.getViewManager().close().onNext(v);
//					}
//				});
//			});
			
		}
		
		@Override
		public Subject<Dock> dockCreated() {
			return subjects.getDockCreated();
		}

		@Override
		public Subject<Dock> dockShow() {
			return subjects.getDockShow();
		}
		
		@Override
		public Subject<Dock> dockHide() {
			return subjects.getDockHide();
		}

	}

	public class DockSubjects extends SubjectManager {
		
		private @Getter PublishSubject<Dock> dockCreated;
		private @Getter PublishSubject<Dock> dockShow;
		private @Getter PublishSubject<Dock> dockHide;
		
		public DockSubjects() {
			dockCreated = wrap(DockSubjects.class, "dockCreated", PublishSubject.create());
			dockShow = wrap(DockSubjects.class, "dockShow", PublishSubject.create());
			dockHide = wrap(DockSubjects.class, "dockHide", PublishSubject.create());
		}
	}
}
