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
package com.gluonhq.jfxapps.boot.registry.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import com.gluonhq.jfxapps.boot.registry.internal.model.ApplicationEntity;
import com.gluonhq.jfxapps.boot.registry.internal.repository.ApplicationRepository;

@DataJpaTest(showSql = true)
@ContextConfiguration(classes = ApplicationRepositoryTest.Config.class)
class ApplicationRepositoryTest {

    @Autowired
    ApplicationRepository repository;

    @Autowired
    TestEntityManager em;

    @EntityScan(basePackageClasses = ApplicationEntity.class)
    @EnableJpaRepositories(basePackageClasses = ApplicationRepository.class)
    @Configuration
    static class Config {

    }

    private void ensureDatabaseIsUpdated() {
        em.flush();
        em.clear();
    }

    @Test
    void must_set_installed_boolean_true() throws SQLException {
        UUID id = UUID.randomUUID();

        ApplicationEntity app = new ApplicationEntity();
        app.setId(id);
        app.setGroupId("grp");
        app.setArtifactId("art");
        app.setVersion("1.0");
        repository.save(app);

        ensureDatabaseIsUpdated();

        assertFalse(repository.findById(id).get().isInstalled());

        repository.install(id);

        ensureDatabaseIsUpdated();

        assertTrue(repository.findById(id).isPresent());
        assertTrue(repository.findById(id).get().isInstalled());
    }

    @Test
    void must_set_installed_boolean_false() {
        UUID id = UUID.randomUUID();

        ApplicationEntity app = new ApplicationEntity();
        app.setId(id);
        app.setGroupId("grp");
        app.setArtifactId("art");
        app.setVersion("1.0");
        app.setInstalled(true);
        repository.save(app);

        ensureDatabaseIsUpdated();

        assertTrue(repository.findById(id).get().isInstalled());

        repository.uninstall(id);

        ensureDatabaseIsUpdated();

        assertTrue(repository.findById(id).isPresent());
        assertFalse(repository.findById(id).get().isInstalled());
    }

    @Test
    void must_update_version_to_latest_version() {
        UUID id = UUID.randomUUID();
        String version = "1.0";
        String nextVersion = "2.0";

        ApplicationEntity app = new ApplicationEntity();
        app.setId(id);
        app.setGroupId("grp");
        app.setArtifactId("art");
        app.setVersion(version);
        app.setNextVersion(nextVersion);
        app.setInstalled(true);
        repository.save(app);

        ensureDatabaseIsUpdated();

        assertEquals(repository.findById(id).get().getVersion(), version);

        repository.update(id);

        ensureDatabaseIsUpdated();

        assertTrue(repository.findById(id).isPresent());
        assertEquals(nextVersion, repository.findById(id).get().getVersion());
    }

}
