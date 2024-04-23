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
package com.gluonhq.jfxapps.metadata.template.scenebuilderx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.metadata.bean.BundleValues;
import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData;
import com.gluonhq.jfxapps.metadata.bean.QualifierMetaData;
import com.gluonhq.jfxapps.metadata.finder.JavaGenerationContext;
import com.gluonhq.jfxapps.metadata.finder.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.finder.SearchContext;
import com.gluonhq.jfxapps.metadata.finder.api.Executor;
import com.gluonhq.jfxapps.metadata.model.Component;
import com.gluonhq.jfxapps.metadata.model.Descriptor;
import com.gluonhq.jfxapps.metadata.model.Property;
import com.gluonhq.jfxapps.metadata.model.Property.Type;
import com.gluonhq.jfxapps.metadata.template.TemplateGenerator;
import com.gluonhq.jfxapps.metadata.util.Report;
import com.gluonhq.jfxapps.metadata.util.Resources;

public class ScenebuilderxExecutor implements Executor {

    @Override
    public void preExecute(SearchContext searchContext) throws Exception {
        Resources.addRawTransform(k -> k.endsWith(BundleValues.METACLASS), s -> s.replace(".kit.", ".core."));
    }

    @Override
    public void execute(
            PropertyGenerationContext propertyContext,
            JavaGenerationContext javaContext, Map<Component, Set<Property>> components, Map<Class<?>, Component> descriptorComponents) throws Exception {

        Set<String> packages = new HashSet();
        Descriptor descriptor = new Descriptor();

        SbxMetadata mainInputs = new SbxMetadata(components);

        Map<Component, Set<Class>> metaParams = (Map<Component, Set<Class>>) mainInputs
                .get(SbxMetadata.COMPONENT_META_PARAMS_KEY);

        String javaPackage = javaContext.getTargetPackage();
        String packagePath = javaPackage.replace('.', File.separatorChar);

        mainInputs.put("package", javaPackage);
        mainInputs.put("uuid", UUID.randomUUID().toString()); // need to get uuid from maven config
        mainInputs.put("components", components);
        mainInputs.put("metadataPrefix", javaContext.getMetadataPrefix());

        generateSource(javaContext, mainInputs, "scenebuilderx/PropertyNames.ftl", packagePath + "/PropertyNames.java");

        Map<Class<?>, Component> classToComponents = new HashMap<>(descriptorComponents);

        for (Component c : components.keySet()) {
            String category = sanitizeCategory(c.getRaw().getCategory());
            String componentPackage = javaPackage + "." + category.toLowerCase();
            String componentMetadataClassName = componentPackage + "."+ javaContext.getMetadataPrefix() + c.getRaw().getName() + "Metadata";

            c.getCustom().put("package", componentPackage);
            c.getCustom().put("className", componentMetadataClassName);
            c.getCustom().put("category", category);
            c.getCustom().put("metadataPrefix", javaContext.getMetadataPrefix());
            c.getCustom().put("propertyNamesClass", javaPackage + ".PropertyNames");

            classToComponents.put(c.getRaw().getType(), c);
            packages.add(componentPackage);
        }


        for (Entry<Component, Set<Property>> entry : components.entrySet()) {
            Map<String, Object> inputs = new HashMap<>();

            String componentPackage = entry.getKey().getCustom().get("package").toString();
            String componentPackagePath = componentPackage.replace('.', File.separatorChar);

            Component cmp = entry.getKey();

            Set<Component> metadataTypesComponent = metaParams.get(cmp).stream()
                    .map(cls -> classToComponents.get(cls))
                    .sorted(Comparator.comparing((Component c) -> c.getRaw().getName()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            inputs.put("package", componentPackage);
            inputs.put("component", cmp);
            inputs.put("properties", entry.getValue());
            inputs.put("metadataComponents", metadataTypesComponent);
            inputs.put("metadataPrefix", javaContext.getMetadataPrefix());

            String fileName = javaContext.getMetadataPrefix() + entry.getKey().getRaw().getType().getSimpleName() + "Metadata.java";

            generateSource(javaContext, inputs, "scenebuilderx/ComponentClassMetadata.ftl", componentPackagePath + "/" + fileName);

            for (Property property:entry.getValue()) {
                PropertyMetaData pmeta = property.getRaw();
                if (property.getType() == Type.COMPONENT) {
                    handlePropertyResourceCopy(propertyContext, cmp, pmeta, pmeta::getImage, "image");
                    handlePropertyResourceCopy(propertyContext, cmp, pmeta, pmeta::getImageX2, "imageX2");
                }
            }
            for (QualifierMetaData qualifier:cmp.getRaw().getQualifiers()) {
                handleQualifierResourceCopy(propertyContext, cmp, qualifier, qualifier::getFxml, "fxml");
                handleQualifierResourceCopy(propertyContext, cmp, qualifier, qualifier::getImage, "image");
                handleQualifierResourceCopy(propertyContext, cmp, qualifier, qualifier::getImageX2, "imageX2");
            }
            descriptor.put(cmp.getRaw().getType(), componentPackage + "." + cmp.getRaw().getName() + "Metadata");
        }

        generateDescriptor(propertyContext, descriptor);

        mainInputs.put("uuid", javaContext.getUuid().toString());
        mainInputs.put("extensionName", javaContext.getExtensionName());
        generateExtension(javaContext, mainInputs);

        generateServiceFile(propertyContext, mainInputs);

        if (javaContext.getModuleName() != null) {
            mainInputs.put("packages", packages);
            mainInputs.put("moduleName", javaContext.getModuleName());
            mainInputs.put("moduleRequires", javaContext.getModuleRequires());
            generateModuleInfo(javaContext, mainInputs);
        }


//        LegacyMetadata inputs = new LegacyMetadata(components);
//        inputs.put("package", searchContext.getTargetPackage());
//        String npath = searchContext.getTargetPackage().replace('.', '/') + "/ValuePropertyMetadataCatalog.java";
//        File nout = new File(searchContext.getSourceFolder(), npath);
//        TemplateGenerator.generate(inputs, "scenebuilderx/ValuePropertyMetadataCatalog.ftl", nout);
    }

    private void handlePropertyResourceCopy(PropertyGenerationContext searchContext, Component cmp, PropertyMetaData property, Supplier<String> getter, String label)
            throws IOException {

        String value = getter.get();

        if (value == null) {
            Report.warn(cmp.getRaw().getType(), String.format("For property: %s %s undefined in properties!", property.getName(), label));
            return;
        }

        String resource = cmp.getRaw().getType().getName().toLowerCase().replace("$", "/").replace(".", "/") + "/" + value;
        String targetResource = cmp.getCustom().get("package").toString().replace(".", "/") + "/" + value;

        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource)){
            if (stream == null) {
                Report.error(cmp.getRaw().getType(), String.format("For property: %s %s: %s, not found!", property.getName(), label, value));
            } else {
                try {
                    copyResource(searchContext, stream, targetResource);
                } catch (Exception e) {
                    Report.error(cmp.getRaw().getType(), String.format("For property: %s %s: %s, can't copy!", property.getName(), label, value), e);
                }
            }
        }
    }

    private void handleQualifierResourceCopy(PropertyGenerationContext searchContext, Component cmp, QualifierMetaData qualifier, Supplier<String> getter, String label)
            throws IOException {

        String value = getter.get();

        if (value == null) {
            Report.warn(cmp.getRaw().getType(), String.format("For qualifier: %s %s undefined in properties!", qualifier.getName(), label));
            return;
        }

        String resource = cmp.getRaw().getType().getName().toLowerCase().replace("$", "/").replace(".", "/") + "/" + value;
        String targetResource = cmp.getCustom().get("package").toString().replace(".", "/") + "/" + value;

        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource)){
            if (stream == null) {
                Report.error(cmp.getRaw().getType(), String.format("For qualifier: %s %s: %s, not found!", qualifier.getName(), label, value));
            } else {
                try {
                    copyResource(searchContext, stream, targetResource);
                } catch (Exception e) {
                    Report.error(cmp.getRaw().getType(), String.format("For qualifier: %s %s: %s, can't copy!", qualifier.getName(), label, value), e);
                }
            }
        }
    }

    private void generateDescriptor(PropertyGenerationContext searchContext, Descriptor descriptor) throws IOException {
        String relativePath = Descriptor.DESCRIPTOR_LOCATION;
        String template = "scenebuilderx/Descriptor.ftl";
        generateResource(searchContext, descriptor.getInputs(), template, relativePath);
    }

    private void generateServiceFile(PropertyGenerationContext searchContext, Map<String, Object> inputs) throws IOException {
        String relativePath = "META-INF/services/com.oracle.javafx.scenebuilder.extension.Extension";
        String template = "scenebuilderx/service.ftl";
        generateResource(searchContext, inputs, template, relativePath);
    }

    private void generateExtension(JavaGenerationContext searchContext, Map<String, Object> inputs) throws IOException {
        String javaPackage = searchContext.getTargetPackage();
        String packagePath = javaPackage.replace('.', File.separatorChar);
        String relativePath = packagePath + "/" + searchContext.getExtensionName() + ".java";
        String template = "scenebuilderx/Extension.ftl";
        generateSource(searchContext, inputs, template, relativePath);
    }

    private void generateModuleInfo(JavaGenerationContext searchContext, Map<String, Object> inputs) throws IOException {
        String relativePath = "module-info.java";
        String template = "scenebuilderx/module-info.ftl";
        generateSource(searchContext, inputs, template, relativePath);
    }

    private void copyResource(PropertyGenerationContext searchContext, InputStream stream, String relativePath) throws IOException {
        File targetFile = new File(searchContext.getResourceFolder(), relativePath);

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        Files.copy(stream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void generateResource(PropertyGenerationContext searchContext, Map<String, Object> inputs, String templateFileName, String relativePath) throws IOException {
        File targetFile = new File(searchContext.getResourceFolder(), relativePath);

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        TemplateGenerator.generate(inputs, templateFileName, targetFile);
    }

    private void generateSource(JavaGenerationContext searchContext, Map<String, Object> inputs, String templateFileName, String relativePath) throws IOException {
        File targetFile = new File(searchContext.getSourceFolder(), relativePath);

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        TemplateGenerator.generate(inputs, templateFileName, targetFile);
    }

    private String sanitizeCategory(String category) {
        if (category == null) {
            return "other";
        }

        if (Character.isDigit(category.charAt(0))) {
            return "c" + category;
        }
        return category;
    }

}
