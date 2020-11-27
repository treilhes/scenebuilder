package com.oracle.javafx.scenebuilder.api.subjects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SubjectManager;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;

public interface DocumentManager {
	Subject<Document> dirty();
	Subject<Document> saved();

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
	public class DocumentManagerImpl implements InitializingBean, DocumentManager {

		private DocumentSubjects subjects;

		public DocumentManagerImpl() {
			subjects = new DocumentSubjects();
		}

		@Override
		public void afterPropertiesSet() throws Exception {
		}

		@Override
		public Subject<Document> dirty() {
			return subjects.getDirty();
		}

		@Override
		public Subject<Document> saved() {
			return subjects.getSaved();
		}

	}

	public class DocumentSubjects extends SubjectManager {

		private @Getter PublishSubject<Document> dirty;
		private @Getter PublishSubject<Document> saved;

		public DocumentSubjects() {
			dirty = wrap(DocumentSubjects.class, "dirty", PublishSubject.create());
			saved = wrap(DocumentSubjects.class, "saved", PublishSubject.create());
		}

	}
}
