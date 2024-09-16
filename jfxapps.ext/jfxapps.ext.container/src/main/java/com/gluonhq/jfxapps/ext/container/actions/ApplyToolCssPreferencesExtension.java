/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.ext.container.actions;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import org.springframework.context.annotation.Lazy;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.action.AbstractActionExtension;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.lifecycle.InitWithDocument;
import com.gluonhq.jfxapps.core.api.tooltheme.CssPreference;
import com.gluonhq.jfxapps.core.api.tooltheme.CssPreference.CssProperty;
import com.gluonhq.jfxapps.util.URLUtils;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

@Prototype
public class ApplyToolCssPreferencesExtension extends AbstractActionExtension<ApplyToolCssAction>
        implements InitWithDocument {

    private final static String cssClassFormat = "%s {%s\n}\n\n";
    private final static String cssPropertyFormat = "\n   %s: %s;";

    private final ActionFactory actionFactory;
    private final List<CssPreference<?>> cssPreferences;
    private final Subject<Boolean> throttledUpdate = PublishSubject.create();

    public ApplyToolCssPreferencesExtension(
            ActionFactory actionFactory,
            @Lazy List<CssPreference<?>> cssPreferences) {
        super();
        this.actionFactory = actionFactory;
        this.cssPreferences = cssPreferences;
    }

    @Override
    public boolean canPerform() {
        return cssPreferences != null && !cssPreferences.isEmpty();
    }

    @Override
    public void prePerform() {
        String cssContent = buildContent();
        URI binaryUri = URLUtils.toDataURI(cssContent);
        getExtendedAction().getActionConfig().getStylesheets().add(binaryUri.toString());
    }

    @Override
    public void initWithDocument() {
        throttledUpdate.throttleWithTimeout(1, TimeUnit.SECONDS)
            .observeOn(JavaFxScheduler.platform())
            .subscribe( b -> actionFactory.create(ApplyToolCssAction.class).perform());

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
