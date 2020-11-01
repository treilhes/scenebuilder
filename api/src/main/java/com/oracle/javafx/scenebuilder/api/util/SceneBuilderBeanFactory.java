package com.oracle.javafx.scenebuilder.api.util;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Document;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

@Component
public class SceneBuilderBeanFactory {

	/**
	 * Scope identifier for the standard singleton scope: {@value}.
	 * <p>
	 * Custom scopes can be added via {@code registerScope}.
	 * 
	 * @see #registerScope
	 */
	public static final String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

	/**
	 * Scope identifier for the standard prototype scope: {@value}.
	 * <p>
	 * Custom scopes can be added via {@code registerScope}.
	 * 
	 * @see #registerScope
	 */
	public static final String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

	/**
	 * Scope identifier for the custom document scope: {@value}.
	 * <p>
	 * Custom scopes can be added via {@code registerScope}.
	 * 
	 * @see #registerScope
	 */
	public static final String SCOPE_DOCUMENT = "document";
	
	/**
	 * Scope identifier for the custom thread scope: {@value}.
	 * <p>
	 * Custom scopes can be added via {@code registerScope}.
	 * 
	 * @see #registerScope
	 */
	public static final String SCOPE_THREAD = "thread";

	@Autowired
	ApplicationContext context;

	public SceneBuilderBeanFactory() {
	}

	public <C> C get(Class<C> cls) {
		return context.getBean(cls);
	}
	
	@Component
	public static class SceneBuilderBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

		public SceneBuilderBeanFactoryPostProcessor() {
			super();
		}
		
	    @Override
	    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
	        factory.registerScope(SCOPE_DOCUMENT, new DocumentScope());
	        factory.registerScope(SCOPE_THREAD, new ThreadScope());
	        factory.addBeanPostProcessor(new FxmlControllerBeanPostProcessor());
	    }
	}
	
	public static class FxmlControllerBeanPostProcessor implements BeanPostProcessor {

		public FxmlControllerBeanPostProcessor() {
			super();
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			bean = BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

			if (FxmlController.class.isAssignableFrom(bean.getClass())) {
				FxmlController controller = (FxmlController)bean; 
				FXMLLoader loader = new FXMLLoader();
				loader.setController(controller);
				loader.setLocation(controller.getFxmlURL());
		        loader.setResources(controller.getResources());
				loader.setClassLoader(bean.getClass().getClassLoader());
		        try {
		        	controller.setPanelRoot((Parent)loader.load());
		        	controller.controllerDidLoadFxml();
		        } catch (RuntimeException | IOException x) {
		            System.out.println("loader.getController()=" + loader.getController());
		            System.out.println("loader.getLocation()=" + loader.getLocation());
		            throw new RuntimeException("Failed to load " + controller.getFxmlURL().getFile(), x); //NOI18N
		        }
			}
			
			return bean;
		}
	}

	public static class DocumentScope implements Scope {
		
		private static final String SCOPE_OBJECT_NAME = "documentWindowController";
		
		private static Document currentScope;
		
		private static Map<Document, Map<String, Object>> scopes = new ConcurrentHashMap<>();
	
		public static synchronized void setCurrentScope(Document scopedDocument) {
			if (scopedDocument == null) {
				if (currentScope != null) {
					currentScope = null;
					System.out.println("DOCUMENT SCOPE TO NULL");
				}
				return;
			}
			if (!scopes.containsKey(scopedDocument)) {
				scopes.put(scopedDocument, new ConcurrentHashMap<>());
			}
			if (DocumentScope.currentScope != scopedDocument) {
				DocumentScope.currentScope = scopedDocument;
				String msg = "SCOPE CHANGE TO : %s (unused: %s, dirty: %s, content: %s, name %s)";
				if (DocumentScope.currentScope.isInited()) {
					System.out.println(String.format(msg, 
							DocumentScope.currentScope,
							DocumentScope.currentScope.isUnused(),
							DocumentScope.currentScope.isDocumentDirty(),
							DocumentScope.currentScope.hasName(),
							DocumentScope.currentScope.getName()
							));
				} else {
					System.out.println(String.format(msg, 
							DocumentScope.currentScope,
							"",
							"",
							"",
							""
							));
				}
				
			}
			
		}

		public static void removeScope(Document document) {
			System.out.println("REMOVING SCOPE " + document);
			if (currentScope == document) {
				currentScope = null;
			}
			scopes.remove(document);
		}

		public DocumentScope() {
			super();
		}
		
		@Override
		public synchronized Object get(String name, ObjectFactory<?> objectFactory) {

			if (SCOPE_OBJECT_NAME.equals(name) && (
					(currentScope == null) || 
					(currentScope != null && !scopes.get(currentScope).containsKey(name)))
				) { // new prototype bean as scope
				
				
				Document scopeDocument = (Document)objectFactory.getObject();
				setCurrentScope((Document)scopeDocument);
				Map<String, Object> scopedObjects = scopes.get(currentScope);
				scopedObjects.put(name, scopeDocument);
				return scopeDocument;
				
			} else {
				Map<String, Object> scopedObjects = scopes.get(currentScope);
				if(!scopedObjects.containsKey(name)) {
			        scopedObjects.put(name, objectFactory.getObject());
			    }
				return scopedObjects.get(name);
			}
		}
		
		@Override
		public String getConversationId() {
			return Thread.currentThread().getName();
		}

		@Override
		public void registerDestructionCallback(String name, Runnable callback) {
			
		}

		@Override
		public Object remove(String name) {
			return scopes.get(currentScope).remove(name);
		}

		@Override
		public Object resolveContextualObject(String arg0) {
			return null;
		}

		
	}
	
	public static class ThreadScope implements Scope {
		ScopedObjectsThreadLocal scopedObjectsThreadLocal = new ScopedObjectsThreadLocal();

		public ThreadScope() {
			super();
		}
		
		@Override
		public Object get(String name, ObjectFactory<?> objectFactory) {
			Map<String, Object> scopedObjects = scopedObjectsThreadLocal.get();
			if(!scopedObjects.containsKey(name)) {
		        scopedObjects.put(name, objectFactory.getObject());
		    }
		    return scopedObjects.get(name);	
		}

		@Override
		public String getConversationId() {
			return Thread.currentThread().getName();
		}

		@Override
		public void registerDestructionCallback(String name, Runnable callback) {
			
		}

		@Override
		public Object remove(String str) {
			return scopedObjectsThreadLocal.get().remove(str);
		}

		@Override
		public Object resolveContextualObject(String arg0) {
			return null;
		}

		class ScopedObjectsThreadLocal extends ThreadLocal<Map<String, Object>> {
			protected Map<String, Object> initialValue() {
				return Collections.synchronizedMap(new HashMap<String, Object>());
			}
		}

	}

	public RadioMenuItem createViewRadioMenuItem(String label, ToggleGroup toggleGroup) {
		RadioMenuItem r = new RadioMenuItem(label);
		if (toggleGroup != null) {
			r.setToggleGroup(toggleGroup);
		}
		return r;
	}

	public SeparatorMenuItem createSeparatorMenuItem() {
		return new SeparatorMenuItem();
	}

	public MenuItem createViewMenuItem(String label) {
		return new MenuItem(label);
	}

	public Menu createViewMenu(String label) {
		return new Menu(label);
	}
}
