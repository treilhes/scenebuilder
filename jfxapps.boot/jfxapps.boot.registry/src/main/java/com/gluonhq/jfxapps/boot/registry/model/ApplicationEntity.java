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
package com.gluonhq.jfxapps.boot.registry.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity(name = "JFXAPPS_BOOT_REGISTRY_APPLICATION")
public class ApplicationEntity {

    @Id
    private UUID id;

	@NotBlank
    private String groupId;

	@NotBlank
    private String artifactId;

	@NotBlank
    private String version;

	@Embedded
	private Description description;

    private String splash;

	private boolean installed;

    @OneToMany(mappedBy = "parentApplication", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<ExtensionEntity> extensions = new HashSet<ExtensionEntity>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @NotFound(action = NotFoundAction.IGNORE)
    private RegistryEntity registry;

    public ApplicationEntity() {
        super();
    }

    public ApplicationEntity(UUID id, String groupId, String artifactId, String version) {
        super();
        this.setId(id);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isInstalled() {
		return installed;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

    public String getSplash() {
		return splash;
	}

	public void setSplash(String splash) {
		this.splash = splash;
	}

	public Set<ExtensionEntity> getExtensions() {
        return extensions;
    }

    public void setExtensions(Set<ExtensionEntity> extensions) {
        this.extensions = extensions;
    }

    public void addExtension(ExtensionEntity extension) {
		extension.setParentApplication(this);
		this.extensions.add(extension);
	}

	public void removeExtension(ExtensionEntity extension) {
		if (extension == null) {
			return;
		}
		if (extension.getParentApplication() != this) {
			return;
		}
		if (this.extensions.contains(extension)) {
			this.extensions.remove(extension);
		}
	}
    public RegistryEntity getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryEntity registry) {
        this.registry = registry;
    }

}
