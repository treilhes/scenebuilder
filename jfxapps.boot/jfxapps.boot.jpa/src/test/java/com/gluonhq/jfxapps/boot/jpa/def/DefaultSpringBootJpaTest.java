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
package com.gluonhq.jfxapps.boot.jpa.def;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

class DefaultSpringBootJpaTest {

    public static void main(String[] args) {
        new DefaultSpringBootJpaTest().test();
    }
    @SpringBootApplication(exclude = { DataSourceTransactionManagerAutoConfiguration.class })
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackageClasses = { SimpleTestRepository.class }, considerNestedRepositories = true)
    @EntityScan(basePackageClasses = { TestEntity.class })
    @ComponentScan(basePackageClasses = { SimpleService.class })
    public static class Config {
//        @Bean(name = "transactionManager")
//        public HibernateTransactionManager getTransactionManager(
//                SessionFactory sessionFactory) {
//            return new HibernateTransactionManager(sessionFactory);
//        }
//        @Bean
//        public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//                DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
//            LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
//            factoryBean.setDataSource(dataSource);
//            factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
//            factoryBean.setPackagesToScan("com.example.entity"); // Adjust to your package
//            return factoryBean;
//        }

//      @Bean
//      @Primary
//      public JpaTransactionManager transactionManager(EntityManagerFactory sessionFactory) {
//          return new JpaTransactionManager(sessionFactory);
//      }
    }

    @Test
    void test() {
        var bootClasses = new ArrayList<>();//new JpaBootClasses().bootClasses());
        bootClasses.add(DefaultSpringBootJpaTest.Config.class);
        bootClasses.add(SimpleTestRepository.class);
        bootClasses.add(TestEntity.class);

        SpringApplication application = new SpringApplication(bootClasses.toArray(Class<?>[]::new));
        application.setWebApplicationType(WebApplicationType.SERVLET);

        var bootContext = application.run(new String[] {
                "--server.port=0",
                "--spring.jpa.hibernate.ddl-auto=update",
                "--spring.jpa.open-in-view=false",
                "--spring.h2.console.enabled=true",
                "--spring.h2.console.path=/h2-console",
                "--spring.datasource.driverClassName=org.h2.Driver",
                "--spring.datasource.url=jdbc:h2:file:./target/jfxapps-h2-db",
                "--spring.datasource.username=sa",
                "--spring.datasource.password="
        });

        var service = bootContext.getBean(SimpleService.class);

        // Start transaction and use EntityManager directly
        var entity = new TestEntity();
        entity.setId(1L);
        entity.setValue("test1");
        service.save(entity);

        System.out.println("Transaction successful. Retrieved entities:");
        System.out.println("XXXXXXXXXXXXXXXXXXXX");
        for (var e : service.getAll()) {
            System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYYY");
            System.out.println(e.getId() + " " + e.getValue());
        }
        System.out.println("XXXXXXXXXXXXXXXXXXXX");

        //FIXME reactivate the H2 console asap
        //H2ConsoleAutoConfiguration h2ConsoleAutoConfiguration = null;

    }

    @Repository
    public static interface SimpleTestRepository extends JpaRepository<TestEntity, Long> {
    }

    @Service
    public static class SimpleService {
        @Autowired
        SimpleTestRepository repository;

        public void save(TestEntity entity) {
            repository.save(entity);
            repository.flush();
        }

        public List<TestEntity> getAll() {
            return repository.findAll();
        }
    }

    @Entity
    public static class TestEntity {
        @Id
        Long id;
        @Column(name = "VALL")
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
