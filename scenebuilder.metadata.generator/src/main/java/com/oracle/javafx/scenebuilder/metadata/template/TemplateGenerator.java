package com.oracle.javafx.scenebuilder.metadata.template;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class TemplateGenerator {

	public static void generate(Map<String, Object> inputs, String templateFileName,  File output) throws IOException {

		// 1. Configure FreeMarker
        //
        // You should do this ONLY ONCE, when your application starts,
        // then reuse the same Configuration object elsewhere.

        Configuration cfg = new Configuration(new Version(2, 3, 20));

        // Where do we load the templates from:
        cfg.setClassForTemplateLoading(TemplateGenerator.class, "");

        // Some other recommended settings:
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setBooleanFormat("c");

        // 2.2. Get the template

        Template template = cfg.getTemplate(templateFileName);

        if (!output.getParentFile().exists()) {
        	output.getParentFile().mkdirs();
        }
        // 2.3. Generate the output
        try (Writer fileWriter = new FileWriter(output)){
            template.process(inputs, fileWriter);
        } catch (TemplateException e) {
			throw new IOException(e);
		}

	}

}
