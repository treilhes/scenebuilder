package com.oracle.javafx.scenebuilder.api.subjects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dock;
import com.oracle.javafx.scenebuilder.api.View;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SubjectManager;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

public interface ViewManager {
	
	Subject<DockRequest> dock();
	Subject<View> undock();
	Subject<View> close();

	@AllArgsConstructor
	@NoArgsConstructor
	@EqualsAndHashCode
	public static class DockRequest {
		private @Getter View source;
		private @Getter Dock target;
		
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
			dock = wrap(ViewSubjects.class, "dock", PublishSubject.create());
			undock = wrap(ViewSubjects.class, "undock", PublishSubject.create());
			close = wrap(ViewSubjects.class, "close", PublishSubject.create());
		}

	}
}
