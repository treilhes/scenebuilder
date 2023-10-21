package com.oracle.javafx.scenebuilder.core.di;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.oracle.javafx.scenebuilder.api.di.DocumentScope;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstance;
import com.oracle.javafx.scenebuilder.core.di.processor.SceneBuilderBeanFactoryPostProcessor;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
// TODO maybe add some multithreaded tests
public class InjectionTmpTest {

    private ApplicationContext context;
    
    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        
    }
    
    @BeforeEach
    void setupContext() {
        this.context = new SpringApplicationBuilder()
                .sources(new Class[]{
                        SceneBuilderBeanFactoryPostProcessor.class,
                        FakeDocument.class,
                        DocumentScopedObject.class
                }).build().run(new String[0]);
    }
    
    @Test
    void documentScopeMustChange() {
        FakeDocument f1 = context.getBean(FakeDocument.class);
        assertEquals(f1, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        DocumentScope.setCurrentScope(null);
        
        FakeDocument f2 = context.getBean(FakeDocument.class);
        assertEquals(f2, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        DocumentScope.setCurrentScope(null);
        
        FakeDocument f3 = context.getBean(FakeDocument.class);
        assertEquals(f3, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        DocumentScope.setCurrentScope(null);
        
        
    }
    
    @Test
    void documentMustBeTheSameInstance() {
        FakeDocument f1 = context.getBean(FakeDocument.class);
        FakeDocument f2 = context.getBean(FakeDocument.class);
        assertEquals(f1, f2, "Document must be the same instance");
        assertEquals(f1.getDocumentScopedObject(), f2.getDocumentScopedObject(), "DocumentScopedObject must be the same instance");
    }
    
    @Test
    void documentNestedObjectMustBeTheSameInstance() {
        FakeDocument f1 = context.getBean(FakeDocument.class);
        DocumentScopedObject scoped = context.getBean(DocumentScopedObject.class);
        assertEquals(f1.getDocumentScopedObject(), scoped, "DocumentScopedObject must be the same instance");
    }
    
    @Test
    void documentMustBeAnotherInstance() {
        FakeDocument f1 = context.getBean(FakeDocument.class);
        DocumentScope.setCurrentScope(null);// will create a new document scope
        FakeDocument f2 = context.getBean(FakeDocument.class);
        assertNotEquals(f1, f2, "Document must be another instance");
        assertNotEquals(f1.getDocumentScopedObject(), f2.getDocumentScopedObject(), "DocumentScopedObject must be another instance");
    }
    
    @Test
    void documentScopeMustChangeOnlyInFxThread() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        
        final FakeDocument f1 = context.getBean(FakeDocument.class);
        assertEquals(f1, DocumentScope.getActiveScope(), "Scope must be the current document scope");


        DocumentScope.setCurrentScope(null);
        final FakeDocument f2 = context.getBean(FakeDocument.class);
        assertEquals(f2, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        
        FutureTask<Runnable> futureTask = new FutureTask<>(() -> {
            try {
                final EditorInstance activeScope = DocumentScope.getActiveScope();
                final EditorInstance currentScope = DocumentScope.getCurrentScope();
                return () -> {
                    assertNotEquals(f1, f2);
                    assertEquals(f1, activeScope);
                    assertEquals(f2, currentScope);
                };
            } finally {
                latch.countDown();
            }
        });
                
        SbPlatform.runForDocumentLater(f1, futureTask::run);
        
        latch.await();
        
        futureTask.get().run();
        
        assertEquals(f2, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        assertEquals(f2, DocumentScope.getCurrentScope(), "Scope must be the current document scope");
    }
    
    @Test
    void documentScopeNotCreatedThrowException() {
        final FakeDocument f1 = new FakeDocument(null); // no scope created
        Assertions.assertThrows(RuntimeException.class, () -> {
            SbPlatform.runForDocumentLater(f1, () -> {});
        });
    }
    
    @Test
    void documentScopeMustLoadTheRightObjectInFxThread() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        
        final FakeDocument f1 = context.getBean(FakeDocument.class);
        assertEquals(f1, DocumentScope.getActiveScope(), "Scope must be the current document scope");

        DocumentScope.setCurrentScope(null);
        final FakeDocument f2 = context.getBean(FakeDocument.class);
        assertEquals(f2, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        
        FutureTask<Runnable> futureTask = new FutureTask<>(() -> {
            try {
                final DocumentScopedObject scoped = context.getBean(DocumentScopedObject.class);
                return () -> {
                    assertEquals(f1.getDocumentScopedObject(), scoped);
                };
            } finally {
                latch.countDown();
            }
        });
                
        SbPlatform.runForDocumentLater(f1, futureTask::run);
        
        latch.await();
        
        futureTask.get().run();
        
        assertEquals(f2.getDocumentScopedObject(), context.getBean(DocumentScopedObject.class));
    }
    
    @Test
    void nestedScopeMustLoadTheRightObjectInFxThread() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        
        final FakeDocument f1 = context.getBean(FakeDocument.class);
        assertEquals(f1, DocumentScope.getActiveScope(), "Scope must be the current document scope");

        DocumentScope.setCurrentScope(null);
        final FakeDocument f2 = context.getBean(FakeDocument.class);
        assertEquals(f2, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        
        DocumentScope.setCurrentScope(null);
        final FakeDocument f3 = context.getBean(FakeDocument.class);
        assertEquals(f3, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        
        final FutureTask<Runnable> futureTask2 = new FutureTask<>(() -> {
            try {
                final DocumentScopedObject scoped = context.getBean(DocumentScopedObject.class);
                
                return () -> {
                    assertEquals(f2.getDocumentScopedObject(), scoped);
                };
            } finally {
                latch.countDown();
            }
        });
        
        FutureTask<Runnable> futureTask1 = new FutureTask<>(() -> {
            try {
                final DocumentScopedObject scoped = context.getBean(DocumentScopedObject.class);
                SbPlatform.runForDocumentLater(f2, futureTask2::run);
                final DocumentScopedObject scoped2 = context.getBean(DocumentScopedObject.class);
                return () -> {
                    assertEquals(f1.getDocumentScopedObject(), scoped);
                    assertEquals(f1.getDocumentScopedObject(), scoped2);
                };
            } finally {
                latch.countDown();
            }
        });
                
        SbPlatform.runForDocumentLater(f1, futureTask1::run);
        
        latch.await();
        
        futureTask1.get().run();
        futureTask2.get().run();
        
        assertEquals(f3.getDocumentScopedObject(), context.getBean(DocumentScopedObject.class));
    }
}
