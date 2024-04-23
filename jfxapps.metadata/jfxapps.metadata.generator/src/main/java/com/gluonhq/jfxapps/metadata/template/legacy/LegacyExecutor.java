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
package com.gluonhq.jfxapps.metadata.template.legacy;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.metadata.finder.JavaGenerationContext;
import com.gluonhq.jfxapps.metadata.finder.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.finder.SearchContext;
import com.gluonhq.jfxapps.metadata.finder.api.Executor;
import com.gluonhq.jfxapps.metadata.model.Component;
import com.gluonhq.jfxapps.metadata.model.Property;
import com.gluonhq.jfxapps.metadata.template.TemplateGenerator;

public class LegacyExecutor implements Executor {

    @Override
    public void preExecute(SearchContext searchContext) throws Exception {
       //Resources.addRawTransform(k -> k.endsWith(BundleValues.METACLASS), s -> s.replace(".kit.", ".core."));

    }

    @Override
    public void execute(PropertyGenerationContext propertyContext, JavaGenerationContext searchContext,
            Map<Component, Set<Property>> components, Map<Class<?>, Component> descriptorComponents) throws Exception {


        LegacyMetadata inputs = new LegacyMetadata(components);

        inputs.put("package", searchContext.getTargetPackage());

        String path = searchContext.getTargetPackage().replace('.', '/') + "/Metadata.java";
        File out = new File(searchContext.getSourceFolder(), path);

        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        TemplateGenerator.generate(inputs, "legacy/Metadata.ftl", out);
    }

}
