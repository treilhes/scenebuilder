package com.gluonhq.jfxapps.registry.model;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Registry {

    private UUID uuid;
    private Dependency dependency;
    private Set<Application> applications;
    private Set<Extension> extensions;

    public Registry(UUID uuid, Dependency dependency, Set<Application> applications, Set<Extension> extensions) {
        this.uuid = uuid;
        this.dependency = dependency;
        this.applications = Set.copyOf(applications);
        this.extensions = Set.copyOf(extensions);
    }

    public Registry() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public void setApplications(Set<Application> applications) {
        this.applications = applications;
    }

    public Set<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Set<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(applications, dependency, extensions, uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Registry other = (Registry) obj;
        return Objects.equals(applications, other.applications) && Objects.equals(dependency, other.dependency)
                && Objects.equals(extensions, other.extensions) && Objects.equals(uuid, other.uuid);
    }

    @Override
    public String toString() {
        return "Registry [uuid=" + uuid + ", dependency=" + dependency + ", applications=" + applications
                + ", extensions=" + extensions + "]";
    }

}
