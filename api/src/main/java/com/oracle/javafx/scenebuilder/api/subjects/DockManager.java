package com.oracle.javafx.scenebuilder.api.subjects;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dock;
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
