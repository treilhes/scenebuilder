package com.oracle.javafx.scenebuilder.api.subjects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18nResourceProvider;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider2;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SubjectManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;

public interface DocumentManager {
	Subject<Boolean> dirty();
	Subject<Boolean> saved();
	Subject<StylesheetProvider2> stylesheetConfig();
	Subject<I18nResourceProvider> i18nResourceConfig();
	Subject<FXOMDocument> fxomDocument();

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	public class DocumentManagerImpl implements InitializingBean, DocumentManager {

		private DocumentSubjects subjects;

		public DocumentManagerImpl() {
			subjects = new DocumentSubjects();
		}

		@Override
		public void afterPropertiesSet() throws Exception {
		}

		@Override
		public Subject<Boolean> dirty() {
			return subjects.getDirty();
		}

		@Override
		public Subject<Boolean> saved() {
			return subjects.getSaved();
		}

		@Override
		public Subject<StylesheetProvider2> stylesheetConfig() {
			return subjects.getStylesheetConfig();
		}

		@Override
		public Subject<I18nResourceProvider> i18nResourceConfig() {
			return subjects.getI18nResourceConfig();
		}

		@Override
		public Subject<FXOMDocument> fxomDocument() {
			return subjects.getFxomDocument();
		}

	}

	public class DocumentSubjects extends SubjectManager {

		private @Getter ReplaySubject<Boolean> dirty;
		private @Getter ReplaySubject<Boolean> saved;
		private @Getter ReplaySubject<StylesheetProvider2> stylesheetConfig;
		private @Getter ReplaySubject<I18nResourceProvider> i18nResourceConfig;
		private @Getter ReplaySubject<FXOMDocument> fxomDocument;


		public DocumentSubjects() {
			dirty = wrap(DocumentSubjects.class, "dirty", ReplaySubject.create(1));
			saved = wrap(DocumentSubjects.class, "saved", ReplaySubject.create(1));
			stylesheetConfig = wrap(DocumentSubjects.class, "stylesheetConfig", ReplaySubject.create(1));
			i18nResourceConfig = wrap(DocumentSubjects.class, "i18nResourceConfig", ReplaySubject.create(1));
			fxomDocument = wrap(DocumentSubjects.class, "fxomDocument", ReplaySubject.create(1));
		}

	}
}
