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
package com.oracle.javafx.scenebuilder.sb.actions;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.preferences.CssPreference;
import com.oracle.javafx.scenebuilder.api.preferences.CssPreference.CssProperty;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.sb.spi.InMemoryFileRegistry;
import com.oracle.javafx.scenebuilder.sb.spi.InMemoryFileURLStreamHandlerProvider;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ApplyToolCssPreferencesExtension extends AbstractActionExtension<ApplyToolCssAction>
        implements InitWithDocument {

    private final static String inMemorySessionId = "toolthemecss";
    private final static String inMemoryUrlFormat = String.format("%s:%s/%%s",
            InMemoryFileURLStreamHandlerProvider.PROTOCOL_NAME, inMemorySessionId);
    private final static String cssClassFormat = "%s {%s\n}\n\n";
    private final static String cssPropertyFormat = "\n   %s: %s;";

    private final ApplicationContext context;
    private final List<CssPreference<?>> cssPreferences;
    private final Subject<Boolean> throttledUpdate = PublishSubject.create();

    public ApplyToolCssPreferencesExtension(@Autowired ApplicationContext context,
            @Autowired @Lazy List<CssPreference<?>> cssPreferences) {
        super();
        this.context = context;
        this.cssPreferences = cssPreferences;
    }

    @Override
    public boolean canPerform() {
        return cssPreferences != null && !cssPreferences.isEmpty();
    }

    @Override
    public void prePerform() {

        String cssContent = buildContent();

        try {
            // StyleManager cache all css files by name, but StyleManager check modification
            // only if the url
            // use the 'file' protocol, so to be sure the right content is applied the name
            // is a checksum of the content
            // It means StyleManager will keep in the cache every different content
            // generated by this class which is some kind of leak
            // So to mitigate excessive memory consumption the we use checksum as name
            MessageDigest digest = MessageDigest.getInstance("MD5");
            String md5 = new BigInteger(1, digest.digest(cssContent.getBytes())).toString(16);
            String virtualPath = md5 + ".css";

            InMemoryFileRegistry.clearSession("toolthemecss");
            InMemoryFileRegistry.addFile(inMemorySessionId, virtualPath, cssContent);
            String url = String.format(inMemoryUrlFormat, virtualPath);

            getExtendedAction().getActionConfig().getStylesheets().add(url);

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void initWithDocument() {
        throttledUpdate.throttleWithTimeout(1, TimeUnit.SECONDS)
            .observeOn(JavaFxScheduler.platform())
            .subscribe( b -> context.getBean(ApplyToolCssAction.class).extend().perform());

        cssPreferences.forEach(p -> {
            p.getObservableValue()
                    .addListener((ob, o, n) -> throttledUpdate.onNext(true));
        });
    }

    private String buildContent() {

        // rationalize css, group by class, no check for duplicate property
        Map<String, List<String>> classes = new HashMap<>();
        cssPreferences.stream().flatMap(p -> p.getClasses().stream())
                .forEach(c -> classes.computeIfAbsent(c.getClassName(), k -> new ArrayList<>())
                        .addAll(transformProperties(c.getProperties())));

        // create the content
        StringBuilder cssContent = new StringBuilder();
        for (Map.Entry<String, List<String>> e : classes.entrySet()) {
            String properties = String.join("", e.getValue());
            String className = e.getKey();

            cssContent.append(String.format(cssClassFormat, className, properties));
        }
        return cssContent.toString();
    }

    private List<String> transformProperties(List<CssProperty> properties) {
        return properties.stream().map(p -> String.format(cssPropertyFormat, p.getPropertyName(), p.getPropertyValue()))
                .collect(Collectors.toList());
    }

}