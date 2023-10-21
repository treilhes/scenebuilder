package com.gluonhq.jfxapps.registry.model;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Extension {

    private UUID uuid;
    private Dependency dependency;
    private Description description;
    private Set<Extension> extensions;

    public Extension(UUID uuid, Dependency dependency, Description description, Set<Extension> extensions) {
        this.uuid = uuid;
        this.dependency = dependency;
        this.description = description;
        this.extensions = Set.copyOf(extensions);
    }

    public Extension() {
    }

    public UUID getUuid() {
        return uuid;
    }

//    private void setUuid(UUID uuid) {
//        this.uuid = uuid;
//    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Set<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Set<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependency, description, extensions, uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Extension other = (Extension) obj;
        return Objects.equals(dependency, other.dependency) && Objects.equals(description, other.description)
                && Objects.equals(extensions, other.extensions) && Objects.equals(uuid, other.uuid);
    }

    @Override
    public String toString() {
        return "Extension [uuid=" + uuid + ", dependency=" + dependency + ", description=" + description
                + ", extensions=" + extensions + "]";
    }

}
