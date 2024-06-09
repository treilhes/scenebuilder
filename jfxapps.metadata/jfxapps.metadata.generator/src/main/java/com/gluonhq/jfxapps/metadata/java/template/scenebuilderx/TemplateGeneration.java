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
package com.gluonhq.jfxapps.metadata.java.template.scenebuilderx;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.metadata.finder.api.Descriptor;
import com.gluonhq.jfxapps.metadata.java.api.ClassCustomization;
import com.gluonhq.jfxapps.metadata.java.api.JavaGenerationContext;
import com.gluonhq.jfxapps.metadata.java.model.Component;
import com.gluonhq.jfxapps.metadata.java.model.Context;
import com.gluonhq.jfxapps.metadata.java.template.TemplateGenerator;

public class TemplateGeneration {

    public void generateComponentClass(JavaGenerationContext context, Context templateContext,  Component<?, ?, ?> component, ClassCustomization customization) throws IOException {
        String classPath = component.getMetadataClassName().replace('.', File.separatorChar);
        String relativePath = String.format("%s.java", classPath);
        String template = "/com/gluonhq/jfxapps/metadata/java/template/scenebuilderx/ComponentClassMetadata.ftl";

        Map<String, Object> inputs = Map.of("context", templateContext, "component", component, "customization", customization);

        generateSource(context, inputs, template, relativePath);
    }

    public void generatePropertyNamesClass(JavaGenerationContext context, Context templateContext, Map<Class<?>, Component<?, ?, ?>> localComponents) throws IOException {
        String javaPackage = context.getTargetPackage();
        String packagePath = javaPackage.replace('.', File.separatorChar);
        String relativePath = packagePath + "/PropertyNames.java";
        String template = "/com/gluonhq/jfxapps/metadata/java/template/scenebuilderx/PropertyNames.ftl";

        var propertyMap = new HashMap<String, Object>();
        for (var c:localComponents.values()) {
            c.getComponentProperties().values().stream().forEach(p -> {
                propertyMap.put(p.getMemberName(), p);
            });
            c.getValueProperties().values().stream().forEach(p -> {
                propertyMap.put(p.getMemberName(), p);
            });
            c.getStaticValueProperties().values().stream().forEach(p -> {
                propertyMap.put(p.getMemberName(), p);
            });
        }

        Map<String, Object> inputs = Map.of("context", templateContext, "properties", propertyMap);

        generateSource(context, inputs, template, relativePath);
    }
    public void generateDescriptor(JavaGenerationContext javaContext, Context templateContext, Descriptor descriptor) throws IOException {
        String relativePath = Descriptor.DESCRIPTOR_LOCATION;
        String template = "/com/gluonhq/jfxapps/metadata/java/template/scenebuilderx/Descriptor.ftl";

        Map<String, Object> inputs = Map.of("context", templateContext, "classToMetaMap", descriptor.getClassToMetaClass());

        generateResource(javaContext, inputs, template, relativePath);
    }

    public void generateServiceFile(JavaGenerationContext javaContext, Map<String, Object> inputs) throws IOException {
        String relativePath = "META-INF/services/com.oracle.javafx.scenebuilder.extension.Extension";
        String template = "/com/gluonhq/jfxapps/metadata/java/template/scenebuilderx/service.ftl";
        generateResource(javaContext, inputs, template, relativePath);
    }

    public void generateExtension(JavaGenerationContext javaContext, Context templateContext, Collection<Component<?, ?, ?>> components) throws IOException {
        String javaPackage = javaContext.getTargetPackage();
        String packagePath = javaPackage.replace('.', File.separatorChar);
        String relativePath = packagePath + "/" + javaContext.getExtensionName() + ".java";
        String template = "/com/gluonhq/jfxapps/metadata/java/template/scenebuilderx/Extension.ftl";

        Map<String, Object> inputs = Map.of("context", templateContext, "components", components);

        generateSource(javaContext, inputs, template, relativePath);
    }

    public void generateModuleInfo(JavaGenerationContext searchContext, Context templateContext, Set<String> packages) throws IOException {
        String relativePath = "module-info.java";
        String template = "/com/gluonhq/jfxapps/metadata/java/template/scenebuilderx/module-info.ftl";

        Map<String, Object> inputs = Map.of("context", templateContext, "packages", packages);

        generateSource(searchContext, inputs, template, relativePath);
    }

    public String generate(Map<String, Object> inputs, String templateFileName) throws IOException {
        return TemplateGenerator.generate(inputs, templateFileName);
    }

    private void generateResource(JavaGenerationContext javaContext, Map<String, Object> inputs, String templateFileName, String relativePath) throws IOException {
        File targetFile = new File(javaContext.getSourceFolder(), relativePath);

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        TemplateGenerator.generate(inputs, templateFileName, targetFile);
    }

    private void generateSource(JavaGenerationContext javaContext, Map<String, Object> inputs, String templateFileName, String relativePath) throws IOException {
        File targetFile = new File(javaContext.getSourceFolder(), relativePath);

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        TemplateGenerator.generate(inputs, templateFileName, targetFile);
    }

}
