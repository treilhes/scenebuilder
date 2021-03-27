/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.api.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class I18N {
	
    public static void initForTest() {
        new I18N();
    }
	private static I18N instance;
	
	private CombinedResourceBundle combinedBundle;
	
	private boolean testing = false;
	
	private I18N() {
	    testing = true;
	    instance = this;
	}
	
	@Autowired
	public I18N(@Autowired List<BundleProvider> bundleProviders) {
		List<ResourceBundle> bundles = bundleProviders.stream().map(BundleProvider::getBundle).collect(Collectors.toList());
		combinedBundle = new CombinedResourceBundle(bundles);
		if (instance == null) {
			instance = this;
		} else {
			throw new RuntimeException("Duplicate instance for class " + getClass().getName());
		}
		
	}
	
	public String get(String key) {
	    if (testing) {
	        return key;
	    }
		return combinedBundle.getString(key);
	}
    
    public static ResourceBundle getBundle() {
		return instance.combinedBundle;
	}
    
    public static String getString(String key) {
        return instance.get(key);
    }
    
    public static String getStringOrDefault(String key, String defaultValue) {
        try {
            return instance.get(key);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }
    
    public static String getString(String key, Object... arguments) {
        final String pattern = getString(key);
        return MessageFormat.format(pattern, arguments);
    }
}
