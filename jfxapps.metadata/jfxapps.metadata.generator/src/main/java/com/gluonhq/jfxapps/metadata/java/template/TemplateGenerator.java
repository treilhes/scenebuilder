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
package com.gluonhq.jfxapps.metadata.java.template;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class TemplateGenerator {

    private static Logger logger = LoggerFactory.getLogger(TemplateGenerator.class);
    private static TemplateGenerator instance;
    private Configuration cfg;

    private TemplateGenerator() {
        // 1. Configure FreeMarker
        //
        // You should do this ONLY ONCE, when your application starts,
        // then reuse the same Configuration object elsewhere.

        this.cfg = new Configuration(new Version(2, 3, 20));

        // Where do we load the templates from:
        //cfg.setClassForTemplateLoading(TemplateGenerator.class, "");
        cfg.setTemplateLoader(new UrlTemplateLoader());
        // Some other recommended settings:
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setBooleanFormat("c");

    }


    private Template getTemplate(String templateFileName) throws IOException {
        return cfg.getTemplate(templateFileName);
    }

    public static void generate(Map<String, Object> inputs, String templateFileName, File output) throws IOException {

        // 2.2. Get the template

        Template template = getInstance().getTemplate(templateFileName);

        if (!output.getParentFile().exists()) {
            output.getParentFile().mkdirs();
        }
        Map<String, Object> generatorInputs = new HashMap<>(inputs);
        generatorInputs.put("logger", logger);

        // 2.3. Generate the output
        try (Writer fileWriter = new FileWriter(output)) {
            template.process(generatorInputs, fileWriter);
        } catch (TemplateException e) {
            throw new IOException(e);
        }

    }

    public static String generate(Map<String, Object> inputs, String templateFileName) throws IOException {

        Template template = getInstance().getTemplate(templateFileName);
        Map<String, Object> generatorInputs = new HashMap<>(inputs);
        generatorInputs.put("logger", logger);

        StringWriter stringWriter = new StringWriter();
        try {
            template.process(generatorInputs, stringWriter);
            return stringWriter.toString();
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }


    private static TemplateGenerator getInstance() {
        if (instance == null) {
            instance = new TemplateGenerator();
        }
        return instance;
    }

    public static class UrlTemplateLoader extends URLTemplateLoader {

        @Override
        protected URL getURL(String path) {
            return findInFsOrResource(path);
        }

        private URL findInFsOrResource(String componentCustomizationTemplate) {
            File f = new File(componentCustomizationTemplate);
            if (f.exists()) {
                try {
                    return f.toURI().toURL();
                } catch (MalformedURLException e) {}
            }
            return Thread.currentThread().getContextClassLoader().getResource(componentCustomizationTemplate);
        }

    }


}
