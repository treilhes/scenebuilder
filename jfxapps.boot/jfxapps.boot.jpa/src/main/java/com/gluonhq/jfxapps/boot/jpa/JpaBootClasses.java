/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.jpa;

import java.util.List;

import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;

import com.gluonhq.jfxapps.boot.api.loader.BootContextConfigClasses;
import com.gluonhq.jfxapps.boot.jpa.context.JfxAppsJpaRepositorySupport;

public class JpaBootClasses implements BootContextConfigClasses {

    @Override
    public List<Class<?>> classes() {
        return List.of(
                org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration.class,
                org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration.class,
                org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration.class,
                //org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration.class,
                org.springframework.boot.jdbc.autoconfigure.XADataSourceAutoConfiguration.class,
                //org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.class,

                org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration.class,
                //org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,

                //FIXME reactivate the H2 console asap
                //org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration.class,
                //FIXME reactivate the H2 console asap

                //tmp
                //org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration.class,
                //org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration.class,
                //org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration.class,

                ProxyTransactionManagementConfiguration.class,
                //end tmp

                //jfxapps
                JfxAppsJpaRepositorySupport.class
                );
    }

}