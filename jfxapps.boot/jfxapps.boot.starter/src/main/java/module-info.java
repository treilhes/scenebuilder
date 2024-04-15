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
module jfxapps.boot.starter {

    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.annotation;

    requires transitive io.swagger.v3.core;
    requires transitive io.swagger.v3.oas.models;

    requires transitive java.compiler;
    requires transitive java.desktop;
    requires transitive java.instrument;// required by javafx
    requires transitive java.json;
    requires transitive java.logging;
    requires transitive java.net.http;
    requires transitive java.prefs;
    requires transitive java.scripting; // required by javafx
    requires transitive java.sql;

    requires transitive jdk.xml.dom; // required by javafx
    requires transitive jdk.jsobject; // required by javafx
    requires transitive jdk.unsupported; // required by javafx

    requires transitive jakarta.annotation;
    requires transitive jakarta.inject;
    requires transitive jakarta.persistence;
    requires transitive jakarta.validation;

    requires transitive org.apache.tomcat.embed.core;
    requires transitive org.aspectj.weaver;
    requires transitive org.hibernate.orm.core;
    requires transitive org.hibernate.validator;
    requires transitive org.mapstruct;
    requires transitive org.slf4j;
    requires transitive org.springdoc.openapi.ui;
    requires transitive org.springdoc.openapi.common;
    requires transitive org.springdoc.openapi.webmvc.core;

    requires transitive spring.aop;
    requires transitive spring.aspects;
    requires transitive spring.beans;
    requires transitive spring.boot;
    requires transitive spring.boot.autoconfigure;
    requires transitive spring.core;
    requires transitive spring.context;
    requires transitive spring.data.jpa;
    requires transitive spring.data.commons;
    requires transitive spring.expression;
    requires transitive spring.orm;
    requires transitive spring.tx;
    requires transitive spring.web;
    requires transitive spring.webmvc;

    requires transitive thymeleaf;
    requires transitive thymeleaf.spring6;
}