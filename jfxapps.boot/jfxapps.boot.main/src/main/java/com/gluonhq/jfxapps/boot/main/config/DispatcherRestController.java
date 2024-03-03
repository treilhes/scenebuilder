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
package com.gluonhq.jfxapps.boot.main.config;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.DispatcherServlet;

import com.gluonhq.jfxapps.boot.context.ContextManager;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@RestController
@RequestMapping("/" + JfxAppsPlatform.EXTENSION_REST_PATH_PREFIX)
public class DispatcherRestController {

    private static final String JFXAPPS_ATTRIBUTE = "JFXAPPS-ATTRIBUTE";

    private static final Logger logger = LoggerFactory.getLogger(DispatcherRestController.class);

    private final ContextManager ctxManager;

    DispatcherServlet dso;

    public DispatcherRestController(ContextManager ctxManager, DispatcherServlet ds) {
        super();
        this.ctxManager = ctxManager;
        this.dso = ds;
    }

    @GetMapping("/{contextId}/{*remains}")
    public void getCall(@PathVariable(name = "contextId") String contextId,
            @PathVariable(name = "remains") String remains, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        contextCall(contextId, request, response);
    }

    @PostMapping("/{contextId}/{*remains}")
    public void postCall(@PathVariable(name = "contextId") String contextId,
            @PathVariable(name = "remains") String remains, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        contextCall(contextId, request, response);
    }

    @PutMapping("/{contextId}/{*remains}")
    public void putCall(@PathVariable(name = "contextId") String contextId,
            @PathVariable(name = "remains") String remains, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        contextCall(contextId, request, response);
    }

    @DeleteMapping("/{contextId}/{*remains}")
    public void deleteCall(@PathVariable(name = "contextId") String contextId,
            @PathVariable(name = "remains") String remains, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        contextCall(contextId, request, response);
    }

    @PatchMapping("/{contextId}/{*remains}")
    public void patchCall(@PathVariable(name = "contextId") String contextId,
            @PathVariable(name = "remains") String remains, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        contextCall(contextId, request, response);
    }

    public void contextCall(String contextId, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        var id = UUID.fromString(contextId);
        var ctx = ctxManager.get(id);
        try {

            // check if we are looping
            if (request.getAttribute(JFXAPPS_ATTRIBUTE) != null) {
                throw new IllegalStateException("Looping request, JfxApps attribute already set");
            }
            request.setAttribute(JFXAPPS_ATTRIBUTE, contextId);

            DispatcherServlet ds = (DispatcherServlet) ctx.getBean("redirector");

            //DispatcherServlet ds = ctx.getBean(DispatcherServlet.class);

//            RequestPath path = (RequestPath) request.getAttribute("org.springframework.web.util.ServletRequestPathUtils.PATH");
//            path.contextPath();
//
//            Enumeration<String> e = request.getAttributeNames();
//            while (e.hasMoreElements()) {
//                request.removeAttribute(e.nextElement());
//            }

            ds.service(request, response);


            System.out.println();
        } catch (Exception e) {
            logger.error("Error in context call", e);

            if (e.getCause() instanceof RuntimeException) {
                throw new RuntimeException(e);
            }
            throw e;
        }
    }
}
