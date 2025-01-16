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
package com.gluonhq.jfxapps.boot.jpa.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.cfg.AvailableSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.jpa.RepositoryFragment;
import com.gluonhq.jfxapps.boot.api.jpa.ResolvablePersistenceManagedTypes;
import com.gluonhq.jfxapps.boot.context.impl.JfxAppContextImpl;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;

class DeferredJpaRepositoryFactoryPostProcessorTest {

    public static class TestConfig {
        @Bean
        DataSource dataSource() {
            // Configure H2 in-memory database
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false");
            dataSource.setUser("sa"); // Default H2 user
            dataSource.setPassword(""); // Default H2 password

            return dataSource;
        }

        @Bean
        EntityManagerFactory entityManagerFactory(DataSource dataSource,
                ResolvablePersistenceManagedTypes persistenceManagedTypes, JfxAppContext ctx) {

            LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
            factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

            // Configure an in-memory database (e.g., H2)
            factoryBean.setDataSource(dataSource);

            // JPA properties
            Map<String, Object> jpaProperties = new HashMap<>();
            jpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");
            jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            factoryBean.setJpaPropertyMap(jpaProperties);

            // JfxApps custom
            factoryBean.setManagedTypes(persistenceManagedTypes);
            factoryBean.setBeanFactory(ctx.getBeanFactory());
            factoryBean.setBeanClassLoader(persistenceManagedTypes.getClassLoader());
            factoryBean.getJpaPropertyMap().put(AvailableSettings.CLASSLOADERS,
                    List.of(persistenceManagedTypes.getClassLoader()));
            // JfxApps custom

            factoryBean.afterPropertiesSet(); // Initialize the factory

            return factoryBean.getObject();

        }
    }

    @Test
    void should_load_repository_mapped_type_and_extension_and_fragment_without_any_scan() {
        Class<?>[] classes = {
                DeferredJpaRepositoryFactoryPostProcessorTest.TestConfig.class,
                JfxAppsJpaRepositorySupport.class,
                TestEntity.class,
                TestRepository.class,
                TestRepositoryImpl.class,
                TestRepositoryFragmentImpl.class,
                TestRepositoryFragmentFactoryBean.class};

        JfxAppContext context = JfxAppContextImpl.fromScratch(classes);

        var repository = context.getBean(TestRepository.class);

        assertNotNull(repository);
        assertEquals("extensionMethod", repository.extensionMethod());
        assertEquals("fragmentMethod", repository.fragmentMethod());
    }

    public static interface TestRepository
            extends JpaRepository<TestEntity, Long>, TestRepositoryCustomization, TestRepositoryFragment {
    }

    public static interface TestRepositoryCustomization {
        String extensionMethod();
    }

    public static class TestRepositoryImpl implements TestRepositoryCustomization {
        @Override
        public String extensionMethod() {
            return "extensionMethod";
        }
    }

    public static interface TestRepositoryFragment {
        String fragmentMethod();
    }

    @RepositoryFragment
    public static class TestRepositoryFragmentImpl implements TestRepositoryFragment {
        @Override
        public String fragmentMethod() {
            return "fragmentMethod";
        }
    }

    @Component
    public static class TestRepositoryFragmentFactoryBean implements FactoryBean<TestRepositoryFragment>, ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Override
        @Nullable
        public TestRepositoryFragment getObject() throws Exception {
            return applicationContext.getBean(TestRepositoryFragmentImpl.class);
        }

        @Override
        @Nullable
        public Class<?> getObjectType() {
            return TestRepositoryFragment.class;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

    }

    @Entity
    public static class TestEntity {
        @Id
        Long id;
        String value;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
}
