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
    public void execute(SearchContext searchContext, Map<Component, Set<Property>> components, Map<Class<?>, Component> descriptorComponents) throws Exception {

        Set<String> packages = new HashSet();
        Descriptor descriptor = new Descriptor();

        SbxMetadata mainInputs = new SbxMetadata(components);

        Map<Component, Set<Class>> metaParams = (Map<Component, Set<Class>>) mainInputs
                .get(SbxMetadata.COMPONENT_META_PARAMS_KEY);

        String javaPackage = searchContext.getTargetPackage();
        String packagePath = javaPackage.replace('.', File.separatorChar);

        mainInputs.put("package", javaPackage);
        mainInputs.put("uuid", UUID.randomUUID().toString()); // need to get uuid from maven config
        mainInputs.put("components", components);
        mainInputs.put("metadataPrefix", searchContext.getMetadataPrefix());

        generateSource(searchContext, mainInputs, "scenebuilderx/PropertyNames.ftl", packagePath + "/PropertyNames.java");

        Map<Class<?>, Component> classToComponents = new HashMap<>(descriptorComponents);

        for (Component c : components.keySet()) {
            String category = sanitizeCategory(c.getRaw().getCategory());
            String componentPackage = javaPackage + "." + category.toLowerCase();
            String componentMetadataClassName = componentPackage + "."+ searchContext.getMetadataPrefix() + c.getRaw().getName() + "Metadata";

            c.getCustom().put("package", componentPackage);
            c.getCustom().put("className", componentMetadataClassName);
            c.getCustom().put("category", category);
            c.getCustom().put("metadataPrefix", searchContext.getMetadataPrefix());
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
            inputs.put("metadataPrefix", searchContext.getMetadataPrefix());

            String fileName = searchContext.getMetadataPrefix() + entry.getKey().getRaw().getType().getSimpleName() + "Metadata.java";

            generateSource(searchContext, inputs, "scenebuilderx/ComponentClassMetadata.ftl", componentPackagePath + "/" + fileName);

            for (Property property:entry.getValue()) {
                PropertyMetaData pmeta = property.getRaw();
                if (property.getType() == Type.COMPONENT) {
                    handlePropertyResourceCopy(searchContext, cmp, pmeta, pmeta::getImage, "image");
                    handlePropertyResourceCopy(searchContext, cmp, pmeta, pmeta::getImageX2, "imageX2");
                }
            }
            for (QualifierMetaData qualifier:cmp.getRaw().getQualifiers()) {
                handleQualifierResourceCopy(searchContext, cmp, qualifier, qualifier::getFxml, "fxml");
                handleQualifierResourceCopy(searchContext, cmp, qualifier, qualifier::getImage, "image");
                handleQualifierResourceCopy(searchContext, cmp, qualifier, qualifier::getImageX2, "imageX2");
            }
            descriptor.put(cmp.getRaw().getType(), componentPackage + "." + cmp.getRaw().getName() + "Metadata");
        }

        generateDescriptor(searchContext, descriptor);

        mainInputs.put("uuid", searchContext.getUuid().toString());
        mainInputs.put("extensionName", searchContext.getExtensionName());
        generateExtension(searchContext, mainInputs);

        generateServiceFile(searchContext, mainInputs);

        if (searchContext.getModuleName() != null) {
            mainInputs.put("packages", packages);
            mainInputs.put("moduleName", searchContext.getModuleName());
            mainInputs.put("moduleRequires", searchContext.getModuleRequires());
            generateModuleInfo(searchContext, mainInputs);
        }


//        LegacyMetadata inputs = new LegacyMetadata(components);
//        inputs.put("package", searchContext.getTargetPackage());
//        String npath = searchContext.getTargetPackage().replace('.', '/') + "/ValuePropertyMetadataCatalog.java";
//        File nout = new File(searchContext.getSourceFolder(), npath);
//        TemplateGenerator.generate(inputs, "scenebuilderx/ValuePropertyMetadataCatalog.ftl", nout);
    }

    private void handlePropertyResourceCopy(SearchContext searchContext, Component cmp, PropertyMetaData property, Supplier<String> getter, String label)
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

    private void handleQualifierResourceCopy(SearchContext searchContext, Component cmp, QualifierMetaData qualifier, Supplier<String> getter, String label)
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

    private void generateDescriptor(SearchContext searchContext, Descriptor descriptor) throws IOException {
        String relativePath = Descriptor.DESCRIPTOR_LOCATION;
        String template = "scenebuilderx/Descriptor.ftl";
        generateResource(searchContext, descriptor.getInputs(), template, relativePath);
    }

    private void generateServiceFile(SearchContext searchContext, Map<String, Object> inputs) throws IOException {
        String relativePath = "META-INF/services/com.oracle.javafx.scenebuilder.extension.Extension";
        String template = "scenebuilderx/service.ftl";
        generateResource(searchContext, inputs, template, relativePath);
    }

    private void generateExtension(SearchContext searchContext, Map<String, Object> inputs) throws IOException {
        String javaPackage = searchContext.getTargetPackage();
        String packagePath = javaPackage.replace('.', File.separatorChar);
        String relativePath = packagePath + "/" + searchContext.getExtensionName() + ".java";
        String template = "scenebuilderx/Extension.ftl";
        generateSource(searchContext, inputs, template, relativePath);
    }

    private void generateModuleInfo(SearchContext searchContext, Map<String, Object> inputs) throws IOException {
        String relativePath = "module-info.java";
        String template = "scenebuilderx/module-info.ftl";
        generateSource(searchContext, inputs, template, relativePath);
    }

    private void copyResource(SearchContext searchContext, InputStream stream, String relativePath) throws IOException {
        File targetFile = new File(searchContext.getResourceFolder(), relativePath);

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        Files.copy(stream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void generateResource(SearchContext searchContext, Map<String, Object> inputs, String templateFileName, String relativePath) throws IOException {
        File targetFile = new File(searchContext.getResourceFolder(), relativePath);

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        TemplateGenerator.generate(inputs, templateFileName, targetFile);
    }

    private void generateSource(SearchContext searchContext, Map<String, Object> inputs, String templateFileName, String relativePath) throws IOException {
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
