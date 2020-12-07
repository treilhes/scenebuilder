package com.oracle.javafx.scenebuilder.api.subjects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider2;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SubjectManager;

import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;

public interface SceneBuilderManager {
	Subject<StylesheetProvider2> stylesheetConfig();

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
	public class SceneBuilderManagerImpl implements InitializingBean, SceneBuilderManager {

		private SceneBuilderSubjects subjects;

		public SceneBuilderManagerImpl() {
			subjects = new SceneBuilderSubjects();
		}

		@Override
		public void afterPropertiesSet() throws Exception {
		}

		@Override
		public Subject<StylesheetProvider2> stylesheetConfig() {
			return subjects.getStylesheetConfig();
		}

	}

	public class SceneBuilderSubjects extends SubjectManager {

		private @Getter ReplaySubject<StylesheetProvider2> stylesheetConfig;


		public SceneBuilderSubjects() {
		    stylesheetConfig = wrap(SceneBuilderSubjects.class, "stylesheetConfig", ReplaySubject.create(1));
		}

	}
}
