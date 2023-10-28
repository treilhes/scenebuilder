package com.gluonhq.jfxapps.boot.loader.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gluonhq.jfxapps.boot.loader.content.ExtensionContentProvider;

public class AbstractExtension<T extends AbstractExtension<Extension>> implements Cloneable {

    private UUID id;

    @JsonProperty("content")
    private ExtensionContentProvider contentProvider;

    @JsonProperty("state")
    private LoadState loadState = LoadState.Unloaded;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<T> extensions;

    public AbstractExtension(UUID id, ExtensionContentProvider contentProvider) {
        super();
        this.id = id;
        this.contentProvider = contentProvider;
        this.extensions = new HashSet<>();
    }

    protected AbstractExtension() {
        this(null, null);
    }

    public UUID getId() {
        return id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    public ExtensionContentProvider getContentProvider() {
        return contentProvider;
    }

    protected void setContentProvider(ExtensionContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public LoadState getLoadState() {
        return loadState;
    }

    public void setLoadState(LoadState loadState) {
        this.loadState = loadState;
    }

    public Set<T> getExtensions() {
        return Collections.unmodifiableSet(extensions);
    }

    public void addExtension(T extension) {
        extensions.add(extension);
    }

    public void removeExtension(T extension) {
        extensions.remove(extension);
    }

    protected void setExtensions(Set<T> extensions) {
        this.extensions = extensions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensions, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractExtension other = (AbstractExtension) obj;
        return Objects.equals(extensions, other.extensions) && Objects.equals(id, other.id);
    }

    @Override
    protected AbstractExtension<T> clone() throws CloneNotSupportedException {
        return (AbstractExtension<T>) super.clone();
    }
}
