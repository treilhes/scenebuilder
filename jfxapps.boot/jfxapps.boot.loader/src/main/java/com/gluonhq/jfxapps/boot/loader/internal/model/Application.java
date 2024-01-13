package com.gluonhq.jfxapps.boot.loader.internal.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity(name = "JFXAPPS_BOOT_LOADER_APPLICATION")
public class Application {

    @Id
    private UUID id;

    private String groupId;

    private String artifactId;

    private String version;

    private UUID registry;

    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "parentApplication")
    @JsonManagedReference
    private Set<Extension> extensions = new HashSet<>();

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

    public UUID getRegistry() {
        return registry;
    }

    public void setRegistry(UUID registry) {
        this.registry = registry;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Set<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Set<Extension> extensions) {
        this.extensions = extensions;
    }

}
