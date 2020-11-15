package com.oracle.javafx.scenebuilder.api.preferences;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

@Component
public class PreferencesContext {

	private static final String HASH_PATH_SEPARATOR = "-"; //NOI18N
	public static final String DEFAULT_DOCUMENT_NODE = "<<<empty>>>"; //NOI18N
	
	private final GenericApplicationContext context;
		
	private final RootPreferencesNode rootNode;
	
	private DocumentPreferencesNode documentsNode;
	
	public PreferencesContext(
			@Autowired GenericApplicationContext context, 
			@Autowired RootPreferencesNode rootNode, 
			@Autowired DocumentPreferencesNode documentsNode) {
		this.context = context;
		this.rootNode = rootNode;
		this.documentsNode = documentsNode;
	}

	public boolean isDocumentScope(Class<?> cls) {
		String[] names = context.getBeanNamesForType(cls);
		BeanDefinition definition = context.getBeanDefinition(names[0]);
		return SceneBuilderBeanFactory.SCOPE_DOCUMENT.equals(definition.getScope());
	}
	
	public boolean isDocumentAlreadyInPathScope() {
		return this.documentsNode.getNode().absolutePath().contains(computeDocumentNodeName());
	}
	
	public String getCurrentFilePath() {
		final Editor editor = getEditor();
		final URL fxmlLocation = editor.getFxmlLocation();
        
        try {
        	if (fxmlLocation != null) {
        		final File fxmlFile = new File(fxmlLocation.toURI());
				return fxmlFile.getPath();
	        }
        	return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isDocumentNameDefined() {
		return getCurrentFilePath() != null;
	}
	
	public String computeDocumentNodeName() {
		String filePath = getCurrentFilePath();
		if (filePath != null) {
    		return generateKey(filePath);
        }
    	return DEFAULT_DOCUMENT_NODE;
	}
	
	private void handleMovedNode(String newDocumentKey) throws IOException, BackingStoreException, InvalidPreferencesFormatException {
		// find the document node
		Preferences previousDocumentNode = this.documentsNode.getNode();
		
		String oldPath = previousDocumentNode.absolutePath();
		String newPath = oldPath.replace(DEFAULT_DOCUMENT_NODE, newDocumentKey);
		
		if (oldPath.equals(newPath)) {
			return;
		}
		
		final Preferences newDocumentNode = this.documentsNode.getNode().node(newPath);
		final DocumentPreferencesNode oldNode = this.documentsNode;
		this.documentsNode = new DocumentPreferencesNode() {
			
			@Override
			public Preferences getNode() {
				return newDocumentNode;
			}
			
			@Override
			public void clearAllDocumentNodes() {
				oldNode.clearAllDocumentNodes();
			}
			
			@Override
			public void cleanupCorruptedNodes() {
				oldNode.cleanupCorruptedNodes();
			}
		};
	}

	public Editor getEditor() {
		return context.getBean(Editor.class);
	}

	public RootPreferencesNode getRootNode() {
		return rootNode;
	}

	public DocumentPreferencesNode getDocumentsNode() {
		try {
			handleMovedNode(computeDocumentNodeName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentsNode;
	}

	public PreferencesContext nodeContext(Object instance, String name) {
		
		return new PreferencesContext(
				context, 
				new RootPreferencesNode() {
					
					@Override
					public Preferences getNode() {
						if (PreferencesContext.this.isDocumentScope(instance.getClass())) {
							return rootNode.getNode();
						} else {
							return rootNode.getNode().node(name);
						}
					}
				}, 
				new DocumentPreferencesNode() {
					
					@Override
					public Preferences getNode() {
						if (PreferencesContext.this.isDocumentScope(instance.getClass())) {
							return documentsNode.getNode().node(name);
						} else {
							return documentsNode.getNode();
						}
					}

					@Override
					public void cleanupCorruptedNodes() {
						PreferencesContext.this.getDocumentsNode().cleanupCorruptedNodes();
					}

					@Override
					public void clearAllDocumentNodes() {
						PreferencesContext.this.getDocumentsNode().clearAllDocumentNodes();
					}
					
				}) {

				@Override
				public boolean isDocumentScope(Class<?> cls) {
					return PreferencesContext.this.isDocumentScope(cls);
				}
				
				@Override
				public String computeDocumentNodeName() {
					return PreferencesContext.this.computeDocumentNodeName();
				}
			
		};
	}
	
	/**
     * Generates a document node key for the specified document file name.
     * Preferences keys are limited to 80 chars so we cannot use the document path.
     *
     * To eliminate the need to iterate the nodes and compare the contained path
     * to the file path we use a MD5 checksum of the current document path prepended to 
     * a substring of the document path. Like this we have predictability 
     * and unicity with a decent collision probability
     * 
     * we use it as this document key.
     *
     * @param name The document file name
     * @return
     */
	
    private static String generateKey(String name) {

    	String hash = hash(name);
    	int maxlength = Preferences.MAX_KEY_LENGTH - hash.length() - HASH_PATH_SEPARATOR.length();
    	if (name.length() > maxlength) {
    		name = name.substring(name.length() - maxlength);
    	}
        
        return hash + HASH_PATH_SEPARATOR + name;
    }
    
    private static String hash(String value) {
    	try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(value.getBytes());
			byte[] digest = md.digest();
			//return hexadecimal value
			return String.format("%040x", new BigInteger(1, digest));
		} catch (NoSuchAlgorithmException e) {
			// MD5 is here no panic
			return null;
		}
    }
}
