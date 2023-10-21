package com.gluonhq.jfxapps.registry.model;

import java.net.URI;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Description {
    private URI icon;
    private String title;
    private String text;

    public Description(URI icon, String title, String text) {
        this.icon = icon;
        this.title = title;
        this.text = text;
    }

    public Description() {

    }

    public URI getIcon() {
        return icon;
    }

    public void setIcon(URI icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, text, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Description other = (Description) obj;
        return Objects.equals(icon, other.icon) && Objects.equals(text, other.text)
                && Objects.equals(title, other.title);
    }

    @Override
    public String toString() {
        return "Description [icon=" + icon + ", title=" + title + ", text=" + text + "]";
    }


}
