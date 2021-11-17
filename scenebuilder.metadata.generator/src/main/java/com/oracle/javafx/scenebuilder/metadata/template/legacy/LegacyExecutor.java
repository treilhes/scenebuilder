package com.oracle.javafx.scenebuilder.metadata.template.legacy;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.oracle.javafx.scenebuilder.metadata.finder.SearchContext;
import com.oracle.javafx.scenebuilder.metadata.finder.api.Executor;
import com.oracle.javafx.scenebuilder.metadata.model.Component;
import com.oracle.javafx.scenebuilder.metadata.model.Property;
import com.oracle.javafx.scenebuilder.metadata.template.TemplateGenerator;

public class LegacyExecutor implements Executor {

    @Override
    public void preExecute(SearchContext searchContext) throws Exception {
       //Resources.addRawTransform(k -> k.endsWith(BundleValues.METACLASS), s -> s.replace(".kit.", ".core."));

    }

    @Override
    public void execute(SearchContext searchContext, Map<Component, Set<Property>> components, Map<Class<?>, Component> descriptorComponents) throws Exception {


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
