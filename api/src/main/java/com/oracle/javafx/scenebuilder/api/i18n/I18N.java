package com.oracle.javafx.scenebuilder.api.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("i18n")
@Lazy
public class I18N {
	
	private static I18N instance;
	
	private CombinedResourceBundle combinedBundle;
	
	public I18N(@Autowired List<BundleProvider> bundleProviders) {
		List<ResourceBundle> bundles = bundleProviders.stream().map(BundleProvider::getBundle).collect(Collectors.toList());
		combinedBundle = new CombinedResourceBundle(bundles);
		if (instance == null) {
			instance = this;
		} else {
			throw new RuntimeException("Duplicate instance for class " + getClass().getName());
		}
		
	}
	
//    public ResourceBundle getBundle() {
//		return combinedBundle;
//	}
//
//	public String getString(String key) {
//        return combinedBundle.getString(key);
//    }
//    
//    public String getString(String key, Object... arguments) {
//        final String pattern = getString(key);
//        return MessageFormat.format(pattern, arguments);
//    }
    
    public static ResourceBundle getBundle() {
		return instance.combinedBundle;
	}
    
    public static String getString(String key) {
        return instance.combinedBundle.getString(key);
    }
    
    public static String getString(String key, Object... arguments) {
        final String pattern = getString(key);
        return MessageFormat.format(pattern, arguments);
    }
}
