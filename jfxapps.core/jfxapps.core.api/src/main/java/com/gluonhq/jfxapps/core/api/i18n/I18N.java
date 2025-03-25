/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.i18n;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.PreferedConstructor;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;

/**
 * Internationalization class that allows to bind keys to values.
 * Bindings are automatically updated when the locale is changed.
 * This class is also a map of keys to values to allow easy access to the values using expression bindings
 * See {@link AbstractFxmlController#i18nProperty()} allowing to bind keys to values in FXML using expression
 * like <b>${controller.i18n.key1.key2.key3}</b> provided the fxml controller extends {@link AbstractFxmlController}
 */
@ApplicationSingleton("i18n")
public class I18N extends SimpleMapOrValueProperty {

    private static final Logger logger = LoggerFactory.getLogger(I18N.class);

    private CombinedResourceBundle combinedBundle;
    private final List<BundleProvider> bundleProviders;
    private final boolean allowUnresolvedKeys;
    private Locale locale = Locale.getDefault();

    //@formatter:off
    public I18N(
            List<BundleProvider> bundleProviders,
            boolean allowUnresolvedKeys) {
        //@formatter:on
        super(null, "");
        this.bundleProviders = bundleProviders;
        this.allowUnresolvedKeys = allowUnresolvedKeys;
        reload();
    }

    @PreferedConstructor
    public I18N(List<BundleProvider> bundleProviders) {
        this(bundleProviders, false);
    }

    public String get(String key) {
        return combinedBundle.getString(key);
    }

    public ResourceBundle getBundle() {
        return combinedBundle;
    }

    public String getString(String key) {
        return get(key);
    }

    public String getStringOrDefault(String key, String defaultValue) {
        try {
            if (!combinedBundle.containsKeyStrict(key)) {
                return defaultValue;
            }
            return get(key);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }

    public String getString(String key, Object... arguments) {
        final var pattern = getString(key);
        return MessageFormat.format(pattern, arguments);
    }

    public Locale getLocale() {
        return locale;
    }

    public void changeLocale(Locale newLocale) {
        var localeBackup = Locale.getDefault();
        Locale.setDefault(newLocale);
        reload();
        Locale.setDefault(localeBackup);
        locale = newLocale;
    }

    private void reload() {
        List<ResourceBundle> bundles = bundleProviders.stream()
                .map(bp -> {
                    try {
                        return bp.getBundle();
                    } catch (IOException e) {
                        logger.error("Unable to load bundle from provider {}", bp.getClass(), e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        combinedBundle = new CombinedResourceBundle(bundles, allowUnresolvedKeys);

        var keys = getBundle().getKeys();
        while (keys.hasMoreElements()) {
            var key = keys.nextElement();
            var value = getString(key);

            var parts = key.split("\\.");

            SimpleMapOrValueProperty map = this;

            for (var i=0;i < parts.length; i++) {
                var part = parts[i];
                final var finalMap = map;
                map = map.computeIfAbsent(part, (k) -> new SimpleMapOrValueProperty(finalMap, part));
                if (i == parts.length - 1) {
                    map.setSimpleValue(value);
                }
            }
        }
    }

    public StringBinding bind(String key) {
        var parts = key.split("\\.");

        SimpleMapOrValueProperty map = this;

        for (var i=0;i < parts.length; i++) {
            var part = parts[i];

            if (i == parts.length - 1) {
                return Bindings.valueAt(map, part).asString();
            } else {
                final var finalMap = map;
                map = map.computeIfAbsent(part, (k) -> new SimpleMapOrValueProperty(finalMap, part));
            }
        }
        throw new NullPointerException("Unknown key : " + key);
    }

    public void addBundleProvider(BundleProvider bundleProvider) {
        bundleProviders.add(bundleProvider);
    }


}
