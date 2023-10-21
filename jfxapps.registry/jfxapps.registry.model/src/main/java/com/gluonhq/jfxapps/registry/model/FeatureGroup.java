package com.gluonhq.jfxapps.registry.model;

import java.util.Set;
import java.util.UUID;

public class FeatureGroup {
    private UUID uuid;
    private Description description;
    private Set<Feature> features;

    public FeatureGroup(UUID uuid, Description description, Set<Feature> features) {
        super();
        this.uuid = uuid;
        this.description = description;
        this.features = Set.copyOf(features);
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

    public Set<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

}
