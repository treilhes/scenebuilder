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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.jpa.RepositoryFragment;
import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.boot.context.boot.BootContext;
import com.gluonhq.jfxapps.boot.context.impl.ExtensionContext;
import com.gluonhq.jfxapps.boot.jpa.context.JfxAppsJpaRepositorySupport;
import com.gluonhq.jfxapps.boot.jpa.context.JpaExtensionConfig;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;

class JpaTest {

    public static class FakeExtension implements OpenExtension {

        @Override
        public UUID getId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public UUID getParentId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Class<?>> localContextClasses() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Class<?>> exportedContextClasses() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    @Test
    void must_load_repository_and_entity_without_any_scan_from_boot_context() {
        var bootClasses = new ArrayList<Class<?>>();
        bootClasses.add(BootEntity.class);
        bootClasses.add(BootRepository.class);

        var bootContext = BootContext.create(bootClasses, new String[0]);
        var repository = bootContext.getBean(BootRepository.class);

        assertTrue("repository interface must be loaded", repository != null);

        var entity = new BootEntity();
        entity.setId(1L);
        repository.save(entity);

        assertTrue("entity must be saved", repository.existsById(1L));
    }

    @Test
    void must_load_repository_customization_without_any_scan_from_boot_context() {
        var bootClasses = new ArrayList<Class<?>>();
        bootClasses.add(BootEntity.class);
        bootClasses.add(BootRepositoryCustomized.class);
        bootClasses.add(BootRepositoryCustomizedImpl.class);

        var bootContext = BootContext.create(bootClasses, new String[0]);
        var repository = bootContext.getBean(BootRepositoryCustomized.class);

        assertTrue("repository interface must be loaded", repository != null);

        assertEquals("extension must work", "extensionMethod", repository.extensionMethod());
    }

    @Test
    void must_load_repository_fragment_without_any_scan_from_boot_context() {
        var bootClasses = new ArrayList<Class<?>>();
        bootClasses.add(BootEntity.class);
        bootClasses.add(BootRepositoryWithFragment.class);
        bootClasses.add(BootRepositoryFragmentImpl.class);

        var bootContext = BootContext.create(bootClasses, new String[0]);
        var repository = bootContext.getBean(BootRepositoryWithFragment.class);

        assertTrue("repository interface must be loaded", repository != null);

        assertEquals("fragment must work", "fragmentMethod", repository.fragmentMethod());
    }

    @Test
    void must_load_repository_fragment_using_factory_without_any_scan_from_boot_context() {
        var bootClasses = new ArrayList<Class<?>>();
        bootClasses.add(BootEntity.class);
        bootClasses.add(BootRepositoryWithFragment.class);
        bootClasses.add(BootRepositoryFragmentImpl.class);
        bootClasses.add(BootRepositoryFragmentFactoryBean.class);

        var bootContext = BootContext.create(bootClasses, new String[0]);
        var repository = bootContext.getBean(BootRepositoryWithFragment.class);

        assertTrue("repository interface must be loaded", repository != null);

        assertEquals("fragment must work", "fragmentMethod", repository.fragmentMethod());
    }

    @Test
    void must_generate_a_validation_exception_from_boot_context() {
        var bootClasses = new ArrayList<Class<?>>();
        bootClasses.add(BootEntity.class);
        bootClasses.add(BootRepository.class);

        var bootContext = BootContext.create(bootClasses, new String[0]);
        var repository = bootContext.getBean(BootRepository.class);

        assertTrue("repository interface must be loaded", repository != null);

        BootEntity entity = new BootEntity();
        entity.setId(1L);
        // make entity invalid
        entity.setNonNullValue(null);

        try {
            repository.save(entity);
        } catch (Exception e) {
            assertTrue("entity must be invalid", e.getCause().getCause() instanceof ConstraintViolationException);
        }
    }

    @Test
    void must_load_repository_and_entity_without_any_scan_from_extension_context() {
        var bootClasses = new ArrayList<Class<?>>();

        var extensionClasses = new ArrayList<Class<?>>();
        extensionClasses.add(FakeExtension.class);
        extensionClasses.add(JfxAppsJpaRepositorySupport.class);
        extensionClasses.add(JpaExtensionConfig.class);
        extensionClasses.add(ExtensionEntity.class);
        extensionClasses.add(ExtensionRepository.class);

        var bootContext = BootContext.create(bootClasses, new String[0]);

        var extensionContext = ExtensionContext.create(bootContext, UUID.randomUUID(), extensionClasses,
                JpaTest.class.getClassLoader());
        extensionContext.refresh();

        var repository = extensionContext.getBean(ExtensionRepository.class);

        assertTrue("repository interface must be loaded", repository != null);

        var entity = new ExtensionEntity();
        entity.setId(1L);
        repository.save(entity);

        assertTrue("entity must be saved", repository.existsById(1L));
    }

    @Test
    void must_load_repository_customization_without_any_scan_from_extension_context() {

        var bootClasses = new ArrayList<Class<?>>();

        var extensionClasses = new ArrayList<Class<?>>();
        extensionClasses.add(FakeExtension.class);
        extensionClasses.add(JfxAppsJpaRepositorySupport.class);
        extensionClasses.add(JpaExtensionConfig.class);
        extensionClasses.add(ExtensionEntity.class);
        extensionClasses.add(ExtensionRepositoryCustomized.class);
        extensionClasses.add(ExtensionRepositoryCustomizedImpl.class);

        var bootContext = BootContext.create(bootClasses, new String[0]);

        var extensionContext = ExtensionContext.create(bootContext, UUID.randomUUID(), extensionClasses,
                JpaTest.class.getClassLoader());
        extensionContext.refresh();

        var repository = extensionContext.getBean(ExtensionRepositoryCustomized.class);

        assertTrue("repository interface must be loaded", repository != null);

        assertEquals("extension must work", "extensionMethod", repository.extensionMethod());
    }

    @Test
    void must_load_repository_fragment_without_any_scan_from_extension_context() {

        var bootClasses = new ArrayList<Class<?>>();

        var extensionClasses = new ArrayList<Class<?>>();
        extensionClasses.add(FakeExtension.class);
        extensionClasses.add(JfxAppsJpaRepositorySupport.class);
        extensionClasses.add(JpaExtensionConfig.class);
        extensionClasses.add(ExtensionEntity.class);
        extensionClasses.add(ExtensionRepositoryWithFragment.class);
        extensionClasses.add(ExtensionRepositoryFragmentImpl.class);

        var bootContext = BootContext.create(bootClasses, new String[0]);

        var extensionContext = ExtensionContext.create(bootContext, UUID.randomUUID(), extensionClasses,
                JpaTest.class.getClassLoader());
        extensionContext.refresh();

        var repository = extensionContext.getBean(ExtensionRepositoryWithFragment.class);

        assertTrue("repository interface must be loaded", repository != null);

        assertEquals("extension must work", "fragmentMethod", repository.fragmentMethod());
    }

    @Test
    void must_load_repository_fragment_using_factory_without_any_scan_from_extension_context() {

        var bootClasses = new ArrayList<Class<?>>();

        var extensionClasses = new ArrayList<Class<?>>();
        extensionClasses.add(FakeExtension.class);
        extensionClasses.add(JfxAppsJpaRepositorySupport.class);
        extensionClasses.add(JpaExtensionConfig.class);
        extensionClasses.add(ExtensionEntity.class);
        extensionClasses.add(ExtensionRepositoryWithFragment.class);
        extensionClasses.add(ExtensionRepositoryFragmentImpl.class);
        extensionClasses.add(ExtensionRepositoryFragmentFactoryBean.class);

        var bootContext = BootContext.create(bootClasses, new String[0]);

        var extensionContext = ExtensionContext.create(bootContext, UUID.randomUUID(), extensionClasses,
                JpaTest.class.getClassLoader());
        extensionContext.refresh();

        var repository = extensionContext.getBean(ExtensionRepositoryWithFragment.class);

        assertTrue("repository interface must be loaded", repository != null);

        assertEquals("extension must work", "fragmentMethod", repository.fragmentMethod());
    }

    public static interface BootRepository extends JpaRepository<BootEntity, Long> {
    }

    public static interface BootRepositoryCustomized
            extends JpaRepository<BootEntity, Long>, BootRepositoryCustomization {
    }

    public static interface BootRepositoryCustomization {
        String extensionMethod();
    }

    public static class BootRepositoryCustomizedImpl implements BootRepositoryCustomization {
        @Override
        public String extensionMethod() {
            return "extensionMethod";
        }
    }

    public static interface BootRepositoryWithFragment extends JpaRepository<BootEntity, Long>, BootRepositoryFragment {
    }

    public static interface BootRepositoryFragment {
        String fragmentMethod();
    }

    @RepositoryFragment
    public static class BootRepositoryFragmentImpl implements BootRepositoryFragment {
        @Override
        public String fragmentMethod() {
            return "fragmentMethod";
        }
    }

    @Component
    public static class BootRepositoryFragmentFactoryBean
            implements FactoryBean<BootRepositoryFragment>, ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Override
        @Nullable
        public BootRepositoryFragment getObject() throws Exception {
            return applicationContext.getBean(BootRepositoryFragmentImpl.class);
        }

        @Override
        @Nullable
        public Class<?> getObjectType() {
            return BootRepositoryFragment.class;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

    }

    @Entity
    public static class BootEntity {
        @Id
        Long id;

        @NotNull
        String nonNullValue = "nonNullValue";

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNonNullValue() {
            return nonNullValue;
        }

        public void setNonNullValue(String nonNullValue) {
            this.nonNullValue = nonNullValue;
        }
    }

    public static interface ExtensionRepository extends JpaRepository<ExtensionEntity, Long> {
    }

    public static interface ExtensionRepositoryCustomized
            extends JpaRepository<ExtensionEntity, Long>, ExtensionRepositoryCustomization {
    }

    public static interface ExtensionRepositoryCustomization {
        String extensionMethod();
    }

    public static class ExtensionRepositoryCustomizedImpl implements ExtensionRepositoryCustomization {
        @Override
        public String extensionMethod() {
            return "extensionMethod";
        }
    }

    public static interface ExtensionRepositoryWithFragment
            extends JpaRepository<ExtensionEntity, Long>, ExtensionRepositoryFragment {
    }

    public static interface ExtensionRepositoryFragment {
        String fragmentMethod();
    }

    @RepositoryFragment
    public static class ExtensionRepositoryFragmentImpl implements ExtensionRepositoryFragment {
        @Override
        public String fragmentMethod() {
            return "fragmentMethod";
        }
    }

    @Component
    public static class ExtensionRepositoryFragmentFactoryBean
            implements FactoryBean<ExtensionRepositoryFragment>, ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Override
        @Nullable
        public ExtensionRepositoryFragment getObject() throws Exception {
            return applicationContext.getBean(ExtensionRepositoryFragmentImpl.class);
        }

        @Override
        @Nullable
        public Class<?> getObjectType() {
            return ExtensionRepositoryFragment.class;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

    }

    @Entity
    public static class ExtensionEntity {
        @Id
        Long id;

        @NotNull
        String nonNullValue = "nonNullValue";

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNonNullValue() {
            return nonNullValue;
        }

        public void setNonNullValue(String nonNullValue) {
            this.nonNullValue = nonNullValue;
        }
    }

}
