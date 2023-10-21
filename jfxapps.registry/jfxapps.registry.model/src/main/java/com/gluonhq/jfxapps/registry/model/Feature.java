package com.gluonhq.jfxapps.registry.model;

import java.util.Set;
import java.util.UUID;

public class Feature {
    private UUID target;
    private UUID uuid;
    private Description description;
    private Set<Extension> extensions;

    public Feature(UUID target, UUID uuid, Description description, Set<Extension> extensions) {
        super();
        this.target = target;
        this.uuid = uuid;
        this.description = description;
        this.extensions = Set.copyOf(extensions);
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

}
