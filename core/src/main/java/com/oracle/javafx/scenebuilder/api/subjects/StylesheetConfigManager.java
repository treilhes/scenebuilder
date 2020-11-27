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

public interface StylesheetConfigManager {

	Subject<StylesheetProvider2> configUpdated();

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	public class StylesheetConfigManagerImpl implements InitializingBean, StylesheetConfigManager {

		private StylesheetConfigSubjects subjects;

		public StylesheetConfigManagerImpl() {
			subjects = new StylesheetConfigSubjects();
		}

		@Override
		public void afterPropertiesSet() throws Exception {
		}

		@Override
		public Subject<StylesheetProvider2> configUpdated() {
			return subjects.getConfigUpdated();
		}

	}

	public class StylesheetConfigSubjects extends SubjectManager {

		private @Getter ReplaySubject<StylesheetProvider2> configUpdated;

		public StylesheetConfigSubjects() {
			configUpdated = wrap(StylesheetConfigSubjects.class, "configUpdated", ReplaySubject.create(1));
		}
	}
}
