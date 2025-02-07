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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity(name = "JFXAPPS_BOOT_REGISTRY_REGISTRY")
public class RegistryEntity extends Description {

	@NotBlank
	private String groupId;

	@NotBlank
	private String artifactId;

	@NotBlank
	private String version;

	@OneToMany(mappedBy = "registry", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private Set<ApplicationEntity> applications = new HashSet<ApplicationEntity>();

	@OneToMany(mappedBy = "registry", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private Set<PluginEntity> plugins = new HashSet<PluginEntity>();

	public RegistryEntity() {
	}

	public RegistryEntity(UUID id, String groupId, String artifactId, String version) {
		super();
		this.setId(id);
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
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

	public Set<ApplicationEntity> getApplications() {
		return Collections.unmodifiableSet(applications);
	}

	private void setApplications(Set<ApplicationEntity> applications) {
		this.applications = applications;
	}

	public void addApplication(ApplicationEntity application) {
		application.setRegistry(this);
		this.applications.add(application);
	}

	public void removeApplication(ApplicationEntity application) {
		if (application == null) {
			return;
		}
		if (application.getRegistry() != this) {
			return;
		}
		if (this.applications.contains(application)) {
			this.applications.remove(application);
		}
	}

	public Set<PluginEntity> getPlugins() {
		return Collections.unmodifiableSet(plugins);
	}

	private void setPlugins(Set<PluginEntity> plugins) {
		this.plugins = plugins;
	}

	public void addPlugin(PluginEntity plugin) {
		plugin.setRegistry(this);
		this.plugins.add(plugin);
	}

	public void removePlugin(PluginEntity plugin) {
		if (plugin == null) {
			return;
		}
		if (plugin.getRegistry() != this) {
			return;
		}
		if (this.plugins.contains(plugin)) {
			this.plugins.remove(plugin);
		}
	}
}
